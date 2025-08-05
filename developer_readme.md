# developer_readme.md

## Setup
- Install VS Code, Node.js, pnpm.
- Run `pnpm install`.

## Build
- Compile: `pnpm run compile`.
- Package: `pnpm exec vsce package --no-dependencies`.

## Test
- Open in VS Code: Press F5 for debug.
- Commands: copyWithContext.saveToPasteFile, copyWithContext.copyToClipboard, copyWithContext.undoLastSave.

## Config
- Edit `settings.ts` for defaults.
- Use `extension.ts` for core logic.

## Enhance
- Add features in `extension.ts`, `util.ts`.
- Test symlink: Set `symlinkHandling` to 'resolve'.
- Compress: Enable `compressContent`.

## Deploy
- Publish: `pnpm exec vsce publish --no-dependencies`.

# README.md

# Copy with Context

VS Code extension to aggregate files into formatted text for LLM context.

## Features
- Collect files, respect .gitignore.
- Output to paste.md or clipboard.
- Include file tree, analysis.
- Configurable: timestamps, compression, symlinks.

## Install
From VS Code Marketplace.

## Usage
- Select files/folders.
- Run "Copy with Context: Save to Paste.md" or "Copy to Clipboard".
- Undo last save.

## Config
- `copyWithContext.outputFileName`: 'paste.md'.
- `copyWithContext.useGitignore`: true.
- `copyWithContext.compressContent`: false.

## License
MIT.