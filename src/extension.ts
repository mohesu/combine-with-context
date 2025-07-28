import ignore from 'ignore';
import * as vscode from 'vscode';
import * as path from 'path';
import * as fs from 'fs';
import { isBinaryFile, ensureDirExists, timestampString, stripControlChars, getBestFence } from './util';
import { getGitIgnore } from './gitIgnoreFilter';
import { getConfig } from './settings';
import { getMarkdownLangForFile } from './markdownMapping';

const WARN_FILE_COUNT = 500;
const WARN_BYTES_TOTAL = 100 * 1048576; // 100 MB
let outputChannel: vscode.OutputChannel | undefined;

function log(message: unknown) {
  if (!outputChannel) {outputChannel = vscode.window.createOutputChannel('Copy with Context');}
  const show = typeof message === 'string' ? message : (message && typeof message === 'object' && 'message' in message) ? String((message as any).message) : JSON.stringify(message, null, 2);
  outputChannel.appendLine(`[${new Date().toLocaleTimeString()}] ${show}`);
}


async function collectFiles(
  uris: vscode.Uri[],
  workspaceRoot: string,
  config: ReturnType<typeof getConfig>,
  gitIgnoreFilter: ReturnType<typeof ignore> | null,
  progress?: vscode.Progress<{ message?: string }>
): Promise<vscode.Uri[]> {
  const toProcess: vscode.Uri[] = [...uris];
  const collected: vscode.Uri[] = [];
  const processed = new Set<string>();
  let filesCount = 0, bytesTotal = 0;
  while (toProcess.length > 0) {
    const uri = toProcess.pop()!;
    if (processed.has(uri.fsPath)) {continue;}
    processed.add(uri.fsPath);
    try {
      const fileStat = await vscode.workspace.fs.stat(uri);
      const relPath = path.relative(workspaceRoot, uri.fsPath);
      if (gitIgnoreFilter && relPath && gitIgnoreFilter.ignores(relPath)) {continue;}
      if (fileStat.type === vscode.FileType.File) {
        if (config.filteredExtensions.some(ext => uri.fsPath.endsWith(ext))) {continue;}
        if (fileStat.size > config.maxFileSize) {continue;}
        let buf: Buffer;
        try { buf = Buffer.from(await vscode.workspace.fs.readFile(uri)); }
        catch (err) { log(err); continue; }
        if (isBinaryFile(buf)) {continue;}
        collected.push(uri); filesCount++; bytesTotal += fileStat.size;
        if (progress && filesCount % 50 === 0) {progress.report({ message: `Indexed ${filesCount} files, ${(bytesTotal / 1048576).toFixed(2)}MB...` });}
        if (filesCount > WARN_FILE_COUNT || bytesTotal > WARN_BYTES_TOTAL) {break;}
      } else if (fileStat.type === vscode.FileType.Directory) {
        if (/node_modules|\.git|dist|out/i.test(uri.fsPath)) {continue;}
        for (const [name] of await vscode.workspace.fs.readDirectory(uri)) {
          toProcess.push(vscode.Uri.joinPath(uri, name));
        }
      }
    } catch (err) { log(err); continue; }
  }
  return collected;
}

async function getFormattedContext(
  files: vscode.Uri[],
  workspaceRoot: string,
  config: ReturnType<typeof getConfig>
): Promise<string> {
  let output = '';
  const now = new Date();
  for (const fileUri of files) {
    let relPath = path.relative(workspaceRoot, fileUri.fsPath).replace(/\\/g, '/');
    let content = '';
    try {
      content = Buffer.from(await vscode.workspace.fs.readFile(fileUri)).toString('utf8');
      content = stripControlChars(content);
      content = content.replace(/\r\n/g, '\n');
    } catch {
      continue;
    }
    const lang = getMarkdownLangForFile(relPath) || '';
    const fence = getBestFence(content);
    output += `#### ${relPath}`;
    if (config.includeTimestamp) {output += ` @ ${now.toLocaleString()}`;}
    output += `\n${fence}${lang}\n${content}\n${fence}\n${config.separator}`;
  }
  return output;
}

export function activate(context: vscode.ExtensionContext) {
  context.subscriptions.push(
    vscode.commands.registerCommand('copyWithContext.saveToPasteFile', handleSaveToPasteFile),
    vscode.commands.registerCommand('copyWithContext.undoLastSave', handleUndoLastSave),
    vscode.commands.registerCommand('copyWithContext.copyToClipboard', handleCopyToClipboard),
    vscode.commands.registerCommand('copyWithContext.showLog', () => outputChannel?.show(true))
  );
}

export function deactivate() {
  outputChannel?.dispose();
}

async function handleSaveToPasteFile(uri: vscode.Uri, uris?: vscode.Uri[]) {
  try {
    if (!vscode.workspace.workspaceFolders?.length) {
      vscode.window.showWarningMessage('Copy with Context: Please open a folder in VS Code to use this extension.');
      return;
    }
    const workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
    const config = getConfig();
    let outputDir = workspaceRoot;
    if (config.outputSubfolder.trim()) {
      outputDir = path.join(workspaceRoot, config.outputSubfolder.trim());
      ensureDirExists(outputDir);
    }
    const outputFile = path.join(outputDir, config.outputFileName);
    let gitIgnoreFilter: ReturnType<typeof ignore> | null = null;
    if (config.useGitignore) {gitIgnoreFilter = await getGitIgnore(workspaceRoot);}
    const selection = uris && uris.length > 0 ? uris : uri ? [uri] : [];
    if (selection.length === 0) {
      vscode.window.showErrorMessage('No files or folders selected.');
      return;
    }

    await vscode.window.withProgress(
      {
        location: vscode.ProgressLocation.Notification,
        title: 'Copying context to paste.txt...',
        cancellable: false,
      },
      async (progress) => {
        let files: vscode.Uri[] = [];
        try {
          files = await collectFiles(selection, workspaceRoot, config, gitIgnoreFilter, progress);
        } catch (err) {
          vscode.window.showErrorMessage(`Copy with Context: Failed to collect files.`);
          log(err);
          return;
        }
        let totalBytes = 0;
        for (const fileUri of files) {
          const stat = await vscode.workspace.fs.stat(fileUri);
          totalBytes += stat.size;
        }
        if (files.length > WARN_FILE_COUNT || totalBytes > WARN_BYTES_TOTAL) {
          const proceed = await vscode.window.showQuickPick(['Proceed', 'Cancel'], {
            placeHolder: `You are about to aggregate ${files.length} files / ${(totalBytes / 1048576).toFixed(2)}MB. Proceed?`,
          });
          if (proceed !== 'Proceed') {return;}
        }
        if (files.length === 0) {
          vscode.window.showWarningMessage('No eligible files found for context copy. Check your .gitignore and exclusion settings.');
          return;
        }
        const output = await getFormattedContext(files, workspaceRoot, config);

        if (fs.existsSync(outputFile)) {
          try {
            const historyDir = path.join(workspaceRoot, config.historyFolder || '.llm-context-history');
            ensureDirExists(historyDir);
            const ts = timestampString();
            const backupFile = path.join(historyDir, `paste.${ts}.txt`);
            fs.copyFileSync(outputFile, backupFile);
            log(`Backup created: ${backupFile}`);
          } catch (backupErr) {
            vscode.window.showWarningMessage(`Copy with Context: Failed to backup existing paste.txt.`);
            log(backupErr);
          }
        }
        try {
          if (config.appendMode && fs.existsSync(outputFile)) {
            fs.appendFileSync(outputFile, output, 'utf8');
          } else {
            fs.writeFileSync(outputFile, output, 'utf8');
          }
          log(`Wrote context to ${outputFile}`);
        } catch (fileErr) {
          vscode.window.showErrorMessage(`Copy with Context: Error writing to output file.`);
          log(fileErr);
          return;
        }

        vscode.window.showInformationMessage(`Context written to: ${outputFile}`);
        if (config.openAfterSave) {
          try {
            const doc = await vscode.workspace.openTextDocument(outputFile);
            vscode.window.showTextDocument(doc, { preview: false });
          } catch (docErr) {
            log(docErr);
          }
        }
      }
    );
  } catch (err) {
    vscode.window.showErrorMessage('Copy with Context: Unexpected error.');
    log(err);
  }
}

async function handleUndoLastSave() {
  try {
    if (!vscode.workspace.workspaceFolders?.length) {
      vscode.window.showWarningMessage('Copy with Context: Please open a folder in VS Code to use this extension.');
      return;
    }
    const workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
    const config = getConfig();
    const outputDir = config.outputSubfolder.trim()
      ? path.join(workspaceRoot, config.outputSubfolder.trim())
      : workspaceRoot;
    const outputFile = path.join(outputDir, config.outputFileName);
    const historyDir = path.join(workspaceRoot, config.historyFolder || '.llm-context-history');
    if (!fs.existsSync(historyDir)) {
      vscode.window.showWarningMessage('No backup history found to undo.');
      return;
    }
    const backups = fs
      .readdirSync(historyDir)
      .filter((f) => f.startsWith('paste.') && f.endsWith('.txt'))
      .sort()
      .reverse();
    if (backups.length === 0) {
      vscode.window.showWarningMessage('No previous backup found.');
      return;
    }
    const lastBackup = path.join(historyDir, backups[0]);
    try {
      fs.copyFileSync(lastBackup, outputFile);
      log(`Undo: Restored ${outputFile} from ${lastBackup}`);
      vscode.window.showInformationMessage('paste.txt has been restored from most recent backup.');
      if (config.openAfterSave) {
        const doc = await vscode.workspace.openTextDocument(outputFile);
        vscode.window.showTextDocument(doc, { preview: false });
      }
    } catch (cpErr) {
      vscode.window.showErrorMessage('Failed to restore paste.txt from backup.');
      log(cpErr);
    }
  } catch (err) {
    vscode.window.showErrorMessage('Copy with Context: Unexpected error in undo.');
    log(err);
  }
}

async function handleCopyToClipboard(uri: vscode.Uri, uris?: vscode.Uri[]) {
  try {
    if (!vscode.workspace.workspaceFolders?.length) {
      vscode.window.showWarningMessage('Copy with Context: Please open a folder in VS Code to use this extension.');
      return;
    }
    const workspaceRoot = vscode.workspace.workspaceFolders[0].uri.fsPath;
    const config = getConfig();
    let gitIgnoreFilter: ReturnType<typeof ignore> | null = null;
    if (config.useGitignore) {gitIgnoreFilter = await getGitIgnore(workspaceRoot);}
    const selection = uris && uris.length > 0 ? uris : uri ? [uri] : [];
    if (selection.length === 0) {
      vscode.window.showErrorMessage('No files or folders selected.');
      return;
    }

    await vscode.window.withProgress(
      {
        location: vscode.ProgressLocation.Notification,
        title: 'Copying context to clipboard...',
        cancellable: false,
      },
      async (progress) => {
        let files: vscode.Uri[] = [];
        try {
          files = await collectFiles(selection, workspaceRoot, config, gitIgnoreFilter, progress);
        } catch (err) {
          vscode.window.showErrorMessage('Copy with Context: Failed to collect files.');
          log(err);
          return;
        }
        if (files.length === 0) {
          vscode.window.showWarningMessage('No eligible files found for context copy. Check your .gitignore and exclusion settings.');
          return;
        }

        const output = await getFormattedContext(files, workspaceRoot, config);
        try {
          await vscode.env.clipboard.writeText(output);
          vscode.window.showInformationMessage('Context copied to clipboard!');
          log('Context copied to clipboard.');
        } catch (clipErr) {
          vscode.window.showErrorMessage(`Copy with Context: Failed to copy to clipboard.`);
          log(clipErr);
        }
      }
    );
  } catch (err) {
    vscode.window.showErrorMessage('Copy with Context: Unexpected error during clipboard operation.');
    log(err);
  }
}
