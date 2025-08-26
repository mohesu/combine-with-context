# Change Log

All notable changes to the "combine-with-context" extension will be documented in this file.

## [2.4.0] - 08-26-2025

### Changed

- **Submenu for saving outputs**: Added a new **Save As** submenu in the Explorer context menu.  You can now choose **Save to paste file** or **Save selection as ZIP** from a single entry.
- **Unified update command**: Consolidated the update actions into a single `copyWithContext.updateLast` command (“CC: Update last output”).  This regenerates whichever output (paste or ZIP) you saved most recently.
- **Undo improvements**: Undo now restores either the paste file or ZIP archive depending on which one you saved last, rather than only handling Markdown files.
- **Combine branding**: Renamed all user‑facing messages and the output channel to **Combine with Context** for consistency.  The configuration title in `package.json` was updated accordingly.
- **Ignore history folder**: Updated `.vscodeignore` to exclude `.llm-context-history/` and `.llm-context/` in line with the default history folder.

## [2.3.3] - 08-26-2025

### Added

- **Save selection as ZIP**: Added ability to package selected files and folders into a ZIP archive (`context.zip` by default).  The command `copyWithContext.saveAsZip` (“CC: Save selection as ZIP”) can be invoked from the context menu or via keybinding.
- **Update/Resave commands**: Added the ability to regenerate your last paste file or ZIP archive based on the previous selection.  Two commands were introduced:
  - `copyWithContext.updatePasteFile` (“CC: Update paste file”) regenerates the paste file using the most recent selection and current settings.
  - `copyWithContext.updateZipFile` (“CC: Update zip file”) regenerates the ZIP archive from the previous selection.
- Selections used by the save/zip commands are recorded for the duration of the session so that update commands can re‑use them.

## [2.2.4] - 08-15-2025

- Cursor support for the extension

## [1.5.0] - 05-06-2025

### Added

- Interactive UI for editing the excluded files/folders. from command pallete (shift+cmd+p) and look for "Copy Context: Edit Excluded Paths", this will open the quick pick dialogue to add/remove files
- Reduced extension bundle size by 93%
- Performance optimizations with caching of excluded paths