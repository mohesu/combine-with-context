# Combine with Context - JetBrains Plugin

This is the JetBrains IDE plugin version of Combine with Context, providing the same functionality as the VS Code extension for IntelliJ IDEA, WebStorm, PyCharm, Android Studio, and other JetBrains IDEs.

## Features

- **Multi-select**: Select any files and/or folders in Project View
- **Export to Markdown**: Create LLM-friendly markdown with syntax highlighting
- **Export to ZIP**: Create ZIP archives of selected files
- **Copy to clipboard**: Copy formatted context directly to clipboard
- **Smart filtering**: Excludes binary files, respects .gitignore, file size limits
- **File tree generation**: Optional file tree and analysis in output
- **Content compression**: Optional minification and comment removal
- **History and undo**: Backup and restore functionality
- **Configurable**: Extensive customization options

## Installation

### From Source

1. Clone this repository
2. Navigate to the `jetbrains-plugin` directory
3. Run `./gradlew buildPlugin`
4. Install the generated plugin file from `build/distributions/`

### From JetBrains Marketplace

*Coming soon - plugin will be published to the marketplace*

## Usage

The plugin provides multiple ways to access its functionality for maximum convenience:

### From Project View
1. **Select files/folders** in the Project View
2. **Right-click** and choose from the "Combine with Context" submenu

### From Editor
1. **Right-click** in any open file editor
2. Choose from the "Combine with Context" submenu (works with current file)

### From Edit Menu
1. Access via **Edit > Combine with Context** in the main menu bar
2. Actions work with currently selected files or active editor file

### From Editor Tabs
1. **Right-click** on any editor tab
2. Choose from the "Combine with Context" submenu

### Available Actions
- **CC: Copy to Clipboard** - Copy formatted context to clipboard
- **CC: Save to Paste File** - Save as markdown file  
- **CC: Save as ZIP** - Create ZIP archive
- **CC: Update Last Output** - Regenerate last action
- **CC: Undo Last Save** - Restore from backup

**Smart Context Detection**: Actions automatically work with:
- Selected files/folders in Project View (when available)
- Current file in active editor (fallback)
- All open files in editor (last resort)

## Keyboard Shortcuts

- **Copy to Clipboard**: `Ctrl+Alt+Shift+C`
- **Save to Paste File**: `Ctrl+Alt+Shift+P`
- **Save as ZIP**: `Ctrl+Alt+Shift+O`
- **Update Last Output**: `Ctrl+Alt+Shift+U`
- **Undo Last Save**: `Ctrl+Alt+Shift+Z`

## Configuration

Access settings via **File → Settings → Tools → Combine with Context**

### Output Settings
- **Output file name**: Name of the markdown file (default: `paste.md`)
- **ZIP file name**: Name of the ZIP archive (default: `context.zip`)
- **Output subfolder**: Subfolder for output files
- **History folder**: Folder for backup files (default: `.llm-context-history`)
- **Append mode**: Append to existing files instead of overwriting
- **Open after save**: Automatically open files after saving

### Content Settings
- **Include timestamp**: Add timestamp to each file block
- **Include file tree**: Include file tree in output
- **Include file analysis**: Include file type and count analysis
- **Compress content**: Remove whitespace and comments
- **Separator**: String between file blocks in output

### Filtering Settings
- **Filtered extensions**: File extensions to exclude (e.g., `.png`, `.jpg`)
- **Max file size**: Maximum file size in MB
- **Use .gitignore**: Respect .gitignore rules
- **Symlink handling**: How to handle symbolic links (`skip` or `resolve`)

## Supported IDEs

- IntelliJ IDEA (Community & Ultimate) - 2023.2 to 2025.3
- WebStorm - 2023.2 to 2025.3  
- PyCharm (Community & Professional) - 2023.2 to 2025.3
- Android Studio - 2025.2+ (based on IntelliJ platform)
- PhpStorm - 2023.2 to 2025.3
- CLion - 2023.2 to 2025.3
- RubyMine - 2023.2 to 2025.3
- DataGrip - 2023.2 to 2025.3
- Rider - 2023.2 to 2025.3
- GoLand - 2023.2 to 2025.3
- And other JetBrains IDEs based on IntelliJ Platform

## Development

### Prerequisites
- JDK 17 or later
- Gradle (included via wrapper)

### Building
```bash
./gradlew buildPlugin
```

### Running in IDE
```bash
./gradlew runIde
```

### Testing
```bash
./gradlew test
```

## Version Compatibility

- **Platform Version**: 232-252.*
- **JetBrains IDEs**: 2023.2 - 2025.3
- **JDK**: 17+

## License

[MIT License](../LICENSE.md) - Same as the main project