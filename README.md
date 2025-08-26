# 🚀 Combine with Context

> A robust, feature-rich way to export code context to a markdown file (`paste.md`) or zip file (`context.zip`) for AI/LLM workflows, with advanced filtering, .gitignore support, history/undo, file tree/analysis, content compression, symlink handling, and full customization.

---

## 📑 Table of Contents

- [✨ Features](#-features)
- [🛠 Usage](#-usage)
- [⚙️ Configuration (settings)](#️-configuration-settings)
- [⌨️ Keybindings](#️-keybindings)
- [🐞 Troubleshooting](#-troubleshooting)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)

---

<details open>
<summary><h2>✨ Features</h2></summary>

- Instantly copy selected code and context to the clipboard for quick sharing or use.
- Save selections directly as Markdown files for documentation or collaboration.
- Export chosen files and folders as a ZIP archive for easy transfer or backup.
- Effortlessly resave your last selection, streamlining repeated workflows.
- Supports advanced filtering (e.g., `.gitignore`, binary/image exclusion) for clean outputs.
- Handles large projects efficiently with progress notifications and error handling.
- Designed for reliability, traceability, and ease of use in production environments.

- **Multi-select**: Select any files and/or folders in VS Code Explorer.
- **Re-save/Update last output**: Once you've created a paste file or ZIP archive, you can regenerate it later using the same selection without reselecting files.  A single **Update last output** command determines which format to rebuild based on your most recent save.
- **LLM-friendly Markdown**: Each file block is clearly marked (with path, code block, timestamp); optional file tree and type-wise analysis.
- **Smart filtering**: Excludes binary files, images, large/empty files, and recognizes `.gitignore`.
  If you explicitly select a file or folder, it will be processed even if its parent directory is ignored by `.gitignore`.  Recognized file types (via the Markdown mapping) are always included.
- **Content compression**: Minifies code (trim whitespace, remove comments for JS/TS, Python, CSS) when enabled.
- **Symlink handling**: Configurable to skip or resolve symlinks.
- **User configuration**: Change output file, folder, append/overwrite, excludes, separators, etc., via settings.
- **One-click undo**: Restore a previous paste or ZIP file from history.
- **Clipboard mode**: Optionally copy context directly to clipboard.
- **Progress & warnings**: Notifies if selection is very large (lots of files or total MBs).
- **Output channel logging**: For debugging and traceability.
- **Production-ready**: Handles errors, permissions, and race conditions robustly.

</details>

---

<details open>
<summary><h2>🛠 Usage</h2></summary>

1. **Select files/folders** in the VS Code Explorer.
2. **Right‑click** and choose **Save As → Save to paste file** or **Save As → Save selection as ZIP**.  The selected files will be aggregated and saved to your configured Markdown (`paste.md` by default) or ZIP (`context.zip` by default) file.
3. The output will appear in the designated file (or your chosen name/folder).
4. **Undo**: Use the **CC: Undo last save** command to restore the most recent backup of your paste or ZIP file.
5. **Clipboard**: Use the **CC: Copy to Clipboard** command/menu to copy the formatted context directly.
6. **Update**: After you've saved a paste or ZIP once, you can regenerate the output with the latest contents of the same files/folders. Right‑click and choose **CC: Update last output**; the extension will determine which format to regenerate based on your most recent save.

</details>

---

<details open>
<summary><h2>⚙️ Configuration (settings)</h2></summary>

Set these in your workspace or global settings:

| Setting             | Default                | Description                            |
| ------------------- | ---------------------- | -------------------------------------- |
| outputFileName      | "paste.md"             | Name/path for output file              |
| appendMode          | false                  | Append or overwrite                    |
| includeTimestamp    | true                   | Add timestamp to each file block       |
| filteredExtensions  | `.png`, `.exe` etc.    | Array of extensions to skip            |
| maxFileSize         | 5242880 (5MB)          | Per-file max size                      |
| outputSubfolder     | "" (root)              | Subfolder for output (optional)        |
| openAfterSave       | true                   | Open file after writing                |
| separator           | "\n---\n"              | Markdown separator between file blocks |
| useGitignore        | true                   | Use `.gitignore` for filtering         |
| historyFolder       | `.llm-context-history` | History folder for backups             |
| includeFileTree     | true                   | Include file tree in output            |
| includeFileAnalysis | true                   | Include file count/type analysis       |
| symlinkHandling     | "skip"                 | Handle symlinks: "skip" or "resolve"   |
| compressContent     | false                  | Minify content and remove comments     |

</details>

---

<details open>
<summary><h2>⌨️ Keybindings</h2></summary>

- Copy to clipboard: `Ctrl+Alt+Shift+C`
- Save to paste.md: `Ctrl+Alt+Shift+P`
- Save to context.zip: `Ctrl+Alt+Shift+O`
- Undo last save: `Ctrl+Alt+Shift+Z`
- Update/Resave Output: `Ctrl+Alt+Shift+U`

</details>

---

<details open>
<summary><h2>🐞 Troubleshooting</h2></summary>

- If no files appear: Check filteredExtensions, .gitignore, file size limits, or empty files.
- If output isn't updated: Confirm permissions and path.
- For debugging: Run the **"CC: Show Log"** command to see output logs.

</details>

---

<details open>
<summary><h2>🤝 Contributing</h2></summary>

1. Fork + branch.
2. Run/test via `F5` in VS Code.
3. PRs welcome; see file header comments and `outputChannel` logs for tracing/debug.

</details>

---

## 📜 License

[MIT License](https://github.com/mohesu/combine-with-context/blob/main/LICENSE.md)
