import * as vscode from 'vscode';

export function getConfig() {
  const config = vscode.workspace.getConfiguration('copyWithContext');
  return {
    zipFileName: config.get<string>('zipFileName', 'context.zip')!,
    outputFileName: config.get<string>('outputFileName', 'paste.md')!,
    appendMode: config.get<boolean>('appendMode', false),
    includeTimestamp: config.get<boolean>('includeTimestamp', true),
    filteredExtensions: config.get<string[]>('filteredExtensions', [".png", ".jpg", ".jpeg", ".gif", ".exe", ".dll", ".ico", ".svg"]),
    maxFileSize: config.get<number>('maxFileSize', 5242880),
    outputSubfolder: config.get<string>('outputSubfolder', ''),
    openAfterSave: config.get<boolean>('openAfterSave', true),
    separator: config.get<string>('separator', '\n---\n')!,
    useGitignore: config.get<boolean>('useGitignore', true),
    historyFolder: config.get<string>('historyFolder', '.llm-context-history'),
    markdownMapping: config.get('markdownMapping', {}),
    includeFileTree: config.get<boolean>('includeFileTree', true),
    includeFileAnalysis: config.get<boolean>('includeFileAnalysis', true),
    symlinkHandling: config.get<string>('symlinkHandling', 'skip')!, // 'skip' | 'resolve'
    compressContent: config.get<boolean>('compressContent', false)
  };
}
