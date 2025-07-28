# Copy with Context VS Code Extension

**A robust, feature-rich way to export code context to a markdown file (`paste.txt`) for AI/LLM workflows, with advanced filtering, .gitignore support, history/undo and full customization.**

## Features

- **Multi-select**: Select any files and/or folders in VS Code Explorer.
- **LLM-friendly Markdown**: Each file block is clearly marked (with path, code block, timestamp).
- **Smart filtering**: Excludes binary files, images, large files, and recognizes `.gitignore`.
- **User configuration**: Change output file, folder, append/overwrite, excludes, separators, etc., via settings.
- **One-click undo**: Restore a previous paste.txt from history.
- **Clipboard mode**: Optionally copy context directly to clipboard.
- **Progress & warnings**: Notifies if selection is very large (lots of files or total MBs).
- **Output channel logging**: For debugging and traceability.
- **Production-ready**: Handles errors, permissions, and race conditions robustly.

## Usage

1. **Select files/folders** in the VS Code Explorer.
2. **Right-click**, choose **Copy with Context: Save to paste.txt**.
3. Output will appear in `paste.txt` (or your chosen name/folder).
4. **Undo**: Use the `Undo last paste.txt Save` command to restore a recent backup.
5. **Clipboard**: Use the `Copy with Context: Copy to Clipboard` command/menu.

## Configuration (settings)

Set these in your workspace or global settings:

| Setting                         | Default                 | Description                                 |
|----------------------------------|-------------------------|---------------------------------------------|
| outputFileName                   | "paste.txt"             | Name/path for output file                   |
| appendMode                       | false                   | Append or overwrite                         |
| includeTimestamp                 | true                    | Add timestamp to each file block            |
| filteredExtensions               | `.png`, `.exe` etc.     | Array of extensions to skip                 |
| maxFileSize                      | 5242880 (5MB)           | Per-file max size                           |
| outputSubfolder                  | "" (root)               | Subfolder for output (optional)             |
| openAfterSave                    | true                    | Open file after writing                     |
| separator                        | "\n---\n"               | Markdown separator between file blocks      |
| useGitignore                     | true                    | Use `.gitignore` for filtering              |
| historyFolder                    | `.llm-context-history`  | History folder for backups                  |

## Keybindings

- Save to paste.txt: `Ctrl+Alt+Shift+S`
- Copy to clipboard: `Ctrl+Alt+Shift+C`
- Undo last save: `Ctrl+Alt+Shift+U`

## Troubleshooting

- If no files appear: Check filteredExtensions, .gitignore, and file size limits.
- If output isn't updated: Confirm permissions and path.
- For debugging: Run the "Copy with Context: Show Log" command to see output logs.

## Contributing

1. Fork + branch.
2. Run/test via `F5` in VS Code.
3. PRs welcome; see file header comments and `outputChannel` logs for tracing/debug.

---

MIT License  
Inspired by [Daniel Ronson's original repo](https://github.com/Daniel-Ronson/copy-with-context)
