# Change Log

All notable changes to the "combine-with-context" extension will be documented in this file.

## [2.5.0] - 08-26-2025

### Added

- **Ignored-parent overrides**: When you explicitly select a text-based file or folder, its contents are now processed even if the parent directory is ignored by `.gitignore`.  Recognized file types defined in the Markdown mapping are always included regardless of ignore rules.
- **Markdown mapping enforcement**: Files with extensions recognized by your markdown mapping (e.g. `.md`, `.js`, `.ts`, `.json`, `.py`, `.css`, `.sh`, `.yml`) are always selected even if they reside under ignored folders, ensuring essential content is never skipped.

### Changed

- **Collect function signature**: Internal changes were made to pass explicit root selections and override ignore checks when necessary.
- **Version bump**: Incremented the extension version to 2.5.0 to reflect these enhancements.


## [2.4.2] - 08-26-2025

### Changed

- **README.md**: Updated the README.md file to be more interactive and scannable.
- **package.json**: Updated the package.json file to include the new keywords.
- **CHANGELOG.md**: Updated the CHANGELOG.md file to include the new changes.

## [2.4.1] - 08-26-2025

### Changed

- refactor: update command labels and keybindings for consistency in package.json

## [2.4.0] - 08-26-2025

### Changed

- **Submenu for saving outputs**: Added a new **Save As** submenu in the Explorer context menu.  You can now choose **Save to paste file** or **Save selection as ZIP** from a single entry.
- **Unified update command**: Consolidated the update actions into a single `combineWithContext.updateLast` command (“CC: Update last output”).  This regenerates whichever output (paste or ZIP) you saved most recently.
- **Undo improvements**: Undo now restores either the paste file or ZIP archive depending on which one you saved last, rather than only handling Markdown files.
- **Combine branding**: Renamed all user‑facing messages and the output channel to **Combine with Context** for consistency.  The configuration title in `package.json` was updated accordingly.
- **Command and configuration renames**: Updated all command IDs, keybindings, submenu IDs and configuration keys to use the `combineWithContext` prefix instead of `copyWithContext`.
- **Ignore history folder**: Updated `.gitignore` and `.vscodeignore` to exclude `.llm-context-history/` and the output files (`paste.md`, `context.zip`) in line with the default history folder.

## [2.3.3] - 08-26-2025

### Added

- **Save selection as ZIP**: Added ability to package selected files and folders into a ZIP archive (`context.zip` by default).  The command `combineWithContext.saveAsZip` (“CC: Save selection as ZIP”) can be invoked from the context menu or via keybinding.
- **Update/Resave commands**: Added the ability to regenerate your last paste file or ZIP archive based on the previous selection.  Two commands were introduced:
  - `combineWithContext.updatePasteFile` (“CC: Update paste file”) regenerates the paste file using the most recent selection and current settings.
  - `combineWithContext.updateZipFile` (“CC: Update zip file”) regenerates the ZIP archive from the previous selection.
- Selections used by the save/zip commands are recorded for the duration of the session so that update commands can re‑use them.

## [2.2.4] - 08-15-2025

- Cursor support for the extension

## [1.5.0] - 05-06-2025

### Added

- Interactive UI for editing the excluded files/folders. from command pallete (shift+cmd+p) and look for "Copy Context: Edit Excluded Paths", this will open the quick pick dialogue to add/remove files
- Reduced extension bundle size by 93%
- Performance optimizations with caching of excluded paths