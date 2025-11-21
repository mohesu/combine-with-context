# Combine with Context - Zed Extension

Export project code context in clean Markdown format for LLMs with smart filtering.

## Features

- **Slash Command**: Use `/combine` in Zed's assistant to generate formatted markdown with your project context
- **File Tree**: Automatically generates a visual file tree of your project
- **File Analysis**: Shows file type distribution and counts
- **Smart Filtering**: 
  - Excludes common build directories (`node_modules`, `target`, `dist`, `build`)
  - Skips binary files (detected by extension and content analysis)
  - Filters hidden files (starting with `.`)
  - Ignores files larger than 5MB
  - Skips empty files
  - Note: Full `.gitignore` pattern support is planned for a future release
- **Syntax Highlighting**: Automatically detects and applies appropriate language syntax for code blocks

## Installation

### From Zed Extension Registry (Coming Soon)

Once published to the Zed extension registry, you can install it directly from Zed:

1. Open Zed
2. Press `Cmd+Shift+X` (Mac) or `Ctrl+Shift+X` (Linux/Windows) to open extensions
3. Search for "Combine with Context"
4. Click Install

### Manual Installation (Development)

For local development or testing:

1. Clone this repository
2. Navigate to the `zed-extension` directory
3. Build the extension:
   ```bash
   cargo build --release
   ```
4. Link the extension to Zed's extensions directory:
   ```bash
   ln -s $(pwd) ~/.config/zed/extensions/combine-with-context
   ```
5. Restart Zed or reload the window

## Usage

### Slash Command

In Zed's AI assistant panel:

1. Type `/combine` and press Enter
2. The extension will scan your workspace and generate a formatted markdown document
3. The output includes:
   - A file tree showing project structure
   - File type analysis (counts by extension)
   - Full content of each file with syntax highlighting

The generated context is perfect for:
- Sharing project context with AI assistants
- Code reviews
- Documentation generation
- Project analysis
- Onboarding new team members

## Configuration

The extension uses sensible defaults and doesn't require configuration. It automatically:

- Filters out binary files (by extension and content analysis)
- Excludes common build artifacts (`node_modules`, `target`, `dist`, `build`)
- Skips hidden files (starting with `.`)
- Limits file size to prevent huge outputs (5MB max)
- Applies appropriate syntax highlighting based on file extensions

**Note:** Full `.gitignore` pattern support is planned for a future release. Currently, common directories are filtered, but custom gitignore patterns are not yet implemented.

## Supported File Types

The extension automatically detects and applies syntax highlighting for:

- Rust, JavaScript, TypeScript, Python, Java, C/C++, Go
- Ruby, PHP, Swift, Kotlin, Scala, C#
- HTML, CSS, SCSS/SASS
- JSON, XML, YAML, TOML
- Markdown, Bash/Shell scripts, SQL
- R, Dart
- And more...

## Examples

### Basic Usage

```
/combine
```

This will generate output like:

```markdown
# Combined Context for AI

## File Tree

📁 src/
  📄 main.rs
  📄 lib.rs
📁 tests/
  📄 integration_test.rs
📄 Cargo.toml
📄 README.md

## File Analysis

- rs: 3 files
- toml: 1 files
- md: 1 files

## File Contents

---

### src/main.rs

\`\`\`rust
fn main() {
    println!("Hello, world!");
}
\`\`\`

...
```

## Compatibility

- **Minimum Zed Version**: 0.120.0
- **Platform**: All platforms supported by Zed (macOS, Linux, Windows)

## Related Extensions

This extension is also available for:

- **VS Code**: [Visual Studio Marketplace](https://marketplace.visualstudio.com/items?itemName=mohesu.combine-with-context)
- **JetBrains IDEs**: [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/28309-combine-with-context)

## Contributing

Contributions are welcome! Please see the [main repository](https://github.com/mohesu/combine-with-context) for contribution guidelines.

## License

[MIT License](https://github.com/mohesu/combine-with-context/blob/main/LICENSE.md)

## Support

- **Issues**: [GitHub Issues](https://github.com/mohesu/combine-with-context/issues)
- **Repository**: [mohesu/combine-with-context](https://github.com/mohesu/combine-with-context)
