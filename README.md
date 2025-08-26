# Combine with Context

**A robust, feature-rich way to export code context to a markdown file (`paste.md`) or zip file `context.zip` (for agent mode) for AI/LLM workflows, with advanced filtering, .gitignore support, history/undo, file tree/analysis, content compression, symlink handling, and full customization.**

## Features

- **Multi-select**: Select any files and/or folders in VS Code Explorer.
- **Re-save/Update outputs**: Once you've created a paste file or ZIP archive, you can regenerate it later using the same selection without reselecting files.
- **LLM-friendly Markdown**: Each file block is clearly marked (with path, code block, timestamp); optional file tree and type-wise analysis.
- **Smart filtering**: Excludes binary files, images, large/empty files, and recognizes `.gitignore`.
- **Content compression**: Minifies code (trim whitespace, remove comments for JS/TS, Python, CSS) when enabled.
- **Symlink handling**: Configurable to skip or resolve symlinks.
- **User configuration**: Change output file, folder, append/overwrite, excludes, separators, etc., via settings.
- **One-click undo**: Restore a previous paste.md from history.
- **Clipboard mode**: Optionally copy context directly to clipboard.
- **Progress & warnings**: Notifies if selection is very large (lots of files or total MBs).
- **Output channel logging**: For debugging and traceability.
- **Production-ready**: Handles errors, permissions, and race conditions robustly.

## Usage

1. **Select files/folders** in the VS Code Explorer.
2. **Right-click**, choose **CC: Save to paste.md**.
3. Output will appear in `paste.md` (or your chosen name/folder).
4. **Undo**: Use the `Undo last paste.md Save` command to restore a recent backup.
5. **Clipboard**: Use the `CC: Copy to Clipboard` command/menu.

6. **Update**: After you've saved a paste file or ZIP once, you can regenerate it with the latest contents of the same files/folders. Right‑click and choose **CC: Update paste file** or **CC: Update zip file**.

## Configuration (settings)

Set these in your workspace or global settings:

| Setting                         | Default                 | Description                                 |
|----------------------------------|-------------------------|---------------------------------------------|
| outputFileName                   | "paste.md"              | Name/path for output file                   |
| appendMode                       | false                   | Append or overwrite                         |
| includeTimestamp                 | true                    | Add timestamp to each file block            |
| filteredExtensions               | `.png`, `.exe` etc.     | Array of extensions to skip                 |
| maxFileSize                      | 5242880 (5MB)           | Per-file max size                           |
| outputSubfolder                  | "" (root)               | Subfolder for output (optional)             |
| openAfterSave                    | true                    | Open file after writing                     |
| separator                        | "\n\n"                  | Markdown separator between file blocks      |
| useGitignore                     | true                    | Use `.gitignore` for filtering              |
| historyFolder                    | `.llm-context-history`  | History folder for backups                  |
| includeFileTree                  | true                    | Include file tree in output                 |
| includeFileAnalysis              | true                    | Include file count/type analysis            |
| symlinkHandling                  | "skip"                  | Handle symlinks: "skip" or "resolve"        |
| compressContent                  | false                   | Minify content and remove comments          |

## Keybindings

- Save to paste.md: `Ctrl+Alt+Shift+S`
- Copy to clipboard: `Ctrl+Alt+Shift+C`
- Save to context.zip: `Ctrl+Alt+Shift+Z`
- Undo last save: `Ctrl+Alt+Shift+U`

## Troubleshooting

- If no files appear: Check filteredExtensions, .gitignore, file size limits, or empty files.
- If output isn't updated: Confirm permissions and path.
- For debugging: Run the "Copy with Context: Show Log" command to see output logs.

## Contributing

1. Fork + branch.
2. Run/test via `F5` in VS Code.
3. PRs welcome; see file header comments and `outputChannel` logs for tracing/debug.

---

MIT License  