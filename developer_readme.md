# developer_readme.md

## Setup
- Install VS Code, Node.js, pnpm.
- Run `pnpm install`.

## Build
- Compile: `pnpm run compile`.
- Package: `pnpm exec vsce package --no-dependencies`.

## Test
- Open in VS Code: Press F5 for debug.
- Commands: `copyWithContext.saveToPasteFile`, `copyWithContext.saveAsZip`, `copyWithContext.copyToClipboard`, `copyWithContext.updateLast`, `copyWithContext.undoLastSave`, `copyWithContext.showLog`.

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

Refer to the top-level `README.md` for detailed documentation.  This extension is now called **Combine with Context** and supports both saving to Markdown or ZIP as well as updating and undoing your last output.  See the main README for features, usage, configuration and license information.