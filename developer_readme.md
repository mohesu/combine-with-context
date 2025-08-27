# developer_readme.md

## Setup
- Install VS Code, Node.js, pnpm.
- Run `pnpm install`.

## Build
- Compile: `pnpm run compile`.
- Package: `pnpm exec vsce package --no-dependencies`.

## Test
- Open in VS Code: Press F5 for debug.
- Commands: `combineWithContext.saveToPasteFile`, `combineWithContext.saveAsZip`, `combineWithContext.copyToClipboard`, `combineWithContext.updateLast`, `combineWithContext.undoLastSave`, `combineWithContext.showLog`.

## Config
- Edit `settings.ts` for defaults.
- Use `extension.ts` for core logic.

## Enhance
- Add features in `extension.ts`, `util.ts`.
- Test symlink: Set `symlinkHandling` to 'resolve'.
- Compress: Enable `compressContent`.

## Deploy
- Publish: `pnpm exec vsce publish --no-dependencies`.

## Release
The project includes automated GitHub Actions workflows for creating releases:

### Creating a Release

#### Automated Release (Recommended)
Simply include one of these keywords in your commit message when pushing to main:
- `#release` or `#Release`
- `#publish` or `#Publish`

The `auto-version-increment.yml` workflow will automatically:
1. Increment the patch version in both `package.json` and `jetbrains-plugin/build.gradle.kts`
2. Commit the version changes
3. Create and push a version tag (e.g., `v25.8.2706`)
4. Trigger the release workflows

#### Manual Release
1. Update version in `package.json` and `jetbrains-plugin/build.gradle.kts`
2. Commit and push changes
3. Create and push a version tag: `git tag v2.5.1 && git push origin v2.5.1`

### Available Workflows
- **auto-version-increment.yml**: Automatically increments version and creates tags on commit keywords
- **release.yml**: Creates a unified release with both VS Code (.vsix) and JetBrains (.zip) artifacts
- **release-vscode.yml**: Creates VS Code extension release only
- **release-intellij.yml**: Creates JetBrains plugin release only
- **publish-extension.yml**: Publishes VS Code extension to marketplaces (on main branch push)

All release workflows are triggered by version tags matching `v*.*.*` pattern.

# README.md

Refer to the top-level `README.md` for detailed documentation.  This extension is now called **Combine with Context** and supports both saving to Markdown or ZIP as well as updating and undoing your last output.  See the main README for features, usage, configuration and license information.