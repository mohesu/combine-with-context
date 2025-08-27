# Release Workflows

This document describes the GitHub Actions workflows for creating releases.

## Workflows

### 1. `release.yml` — Unified Release (Recommended)
Creates a single GitHub release with both the VS Code extension (.vsix) and JetBrains plugin (.zip), uploads checksums, and optionally publishes to marketplaces.

Trigger: Push tags matching `v*.*.*` (e.g., `v2.5.0`)

Artifacts:
- VS Code extension: `combine-with-context-{version}.vsix` (+ `.sha256`)
- JetBrains plugin: `combine-with-context-{version}.zip` (+ `.sha256`)

Optional publishing:
- Open VSX: requires `OPEN_VSX_TOKEN` secret
- Visual Studio Marketplace: requires `VS_MARKETPLACE_TOKEN` secret
- JetBrains Marketplace: requires `PUBLISH_TOKEN` env var for Gradle

### 2. `release-vscode.yml` - VS Code Only
Creates a release with only the VS Code extension.

### 3. `release-intellij.yml` - JetBrains Only
Creates a release with only the JetBrains plugin.

### 4. `publish.yml` — Version bump + tag
Manually or automatically bumps the version, commits, and pushes a `v{version}` tag which triggers `release.yml`.

Triggers:
- Push to `main` with commit message containing `#release`, `#publish`, or `#version`
- Manual dispatch with input `bump`: `patch|minor|major|prepatch|preminor|premajor`

## How to Create a Release

1. **Update versions** (optional if using `publish.yml`):
   - `package.json`: `version`
   - `jetbrains-plugin/build.gradle.kts`: `version` (plugin xml version now syncs with project version)

2. **Commit and push changes**:
   ```bash
   git add .
   git commit -m "Bump version to 2.5.1"
   git push origin main
   ```

3. **Create and push tag** (skip if `publish.yml` was used):
   ```bash
   git tag v2.5.1
   git push origin v2.5.1
   ```

4. **Wait for workflows**:
   - Check the Actions tab in GitHub
   - The release will be created automatically

## Installation Instructions

### VS Code Extension
1. Download the `.vsix` file from the release
2. In VS Code: Command Palette → "Extensions: Install from VSIX..."
3. Or via command line: `code --install-extension filename.vsix`

### JetBrains Plugin
1. Download the `.zip` file from the release
2. In your IDE: **File** → **Settings** → **Plugins**
3. Click gear icon → **Install Plugin from Disk...**
4. Choose the `.zip` file and restart IDE

## CI

`ci.yml` runs lint, typecheck, unit tests, and packaging on PRs and pushes to `main` (excluding tags). It builds the VS Code extension on Ubuntu and Windows, and the JetBrains plugin on Ubuntu.

## Secrets / Tokens

- `OPEN_VSX_TOKEN` — token for publishing to Open VSX
- `VS_MARKETPLACE_TOKEN` — token for publishing to Visual Studio Marketplace
- `PUBLISH_TOKEN` — token for JetBrains Marketplace (read by Gradle from env)