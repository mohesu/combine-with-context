# Release Workflows

This document describes the GitHub Actions workflows for creating releases.

## Workflows

### 1. `release.yml` - Unified Release (Recommended)
Creates a single GitHub release with both VS Code extension (.vsix) and JetBrains plugin (.zip) files.

**Trigger**: Push tags matching `v*.*.*` (e.g., `v2.5.0`)

**Artifacts**:
- VS Code extension: `combine-with-context-{version}.vsix`
- JetBrains plugin: `combine-with-context-{version}.zip`

### 2. `release-vscode.yml` - VS Code Only
Creates a release with only the VS Code extension.

### 3. `release-intellij.yml` - JetBrains Only
Creates a release with only the JetBrains plugin.

### 4. `publish-extension.yml` - Marketplace Publishing
Publishes the VS Code extension to marketplaces on main branch pushes.

## How to Create a Release

1. **Update versions**:
   - `package.json`: Update `version` field
   - `jetbrains-plugin/build.gradle.kts`: Update `version` field

2. **Commit and push changes**:
   ```bash
   git add .
   git commit -m "Bump version to 2.5.1"
   git push origin main
   ```

3. **Create and push tag**:
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

## Supported Platforms

- **VS Code**: All platforms supported by VS Code
- **JetBrains**: IntelliJ IDEA, WebStorm, PyCharm, Android Studio, PhpStorm, CLion, RubyMine, DataGrip, Rider, GoLand