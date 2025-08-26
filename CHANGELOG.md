# Change Log

All notable changes to the "copy-with-context" extension will be documented in this file.

## [2.3.1] - 08-26-2025

### Added

- **Update/Resave commands**: Added the ability to regenerate your last paste file or ZIP archive based on the previous selection.  Two new commands are available:
  - `copyWithContext.updatePasteFile` (“CC: Update paste file”) regenerates the paste file using the most recent selection and current settings.
  - `copyWithContext.updateZipFile` (“CC: Update zip file”) regenerates the ZIP archive from the previous selection.
- Selections used by the save/zip commands are now recorded for the duration of the session so that update commands can re-use them.

## [2.2.4] - 08-15-2025

- Cursor support for the extension

## [1.5.0] - 05-06-2025

### Added

- Interactive UI for editing the excluded files/folders. from command pallete (shift+cmd+p) and look for "Copy Context: Edit Excluded Paths", this will open the quick pick dialogue to add/remove files
- Reduced extension bundle size by 93%
- Performance optimizations with caching of excluded paths