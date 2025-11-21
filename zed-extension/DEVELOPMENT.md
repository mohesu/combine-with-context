# Development Guide for Zed Extension

This guide covers development, building, testing, and publishing the Combine with Context extension for Zed.

## Prerequisites

- Rust 1.70 or later
- Cargo (comes with Rust)
- Zed IDE (for testing)

## Building the Extension

1. Navigate to the `zed-extension` directory:
   ```bash
   cd zed-extension
   ```

2. Check that the code compiles:
   ```bash
   cargo check
   ```

3. Build the extension:
   ```bash
   cargo build --release
   ```

   The compiled extension will be in `target/release/libcombine_with_context.so` (Linux), `libcombine_with_context.dylib` (macOS), or `combine_with_context.dll` (Windows).

## Testing the Extension Locally

1. Build the extension (see above)

2. Link the extension directory to Zed's extensions directory:
   
   **Linux/macOS:**
   ```bash
   mkdir -p ~/.config/zed/extensions
   ln -s $(pwd) ~/.config/zed/extensions/combine-with-context
   ```
   
   **Windows:**
   ```powershell
   New-Item -ItemType Directory -Force -Path "$env:APPDATA\Zed\extensions"
   New-Item -ItemType SymbolicLink -Path "$env:APPDATA\Zed\extensions\combine-with-context" -Target (Get-Location)
   ```

3. Restart Zed or reload the window

4. Open a project in Zed

5. Open the AI assistant panel

6. Type `/combine` and press Enter

7. The extension should generate a formatted context output

## Code Structure

```
zed-extension/
├── Cargo.toml          # Rust dependencies and metadata
├── extension.toml      # Zed extension metadata
├── src/
│   └── lib.rs         # Main extension implementation
├── README.md          # User documentation
└── DEVELOPMENT.md     # This file
```

### Key Components

- **extension.toml**: Defines the extension metadata, including the `/combine` slash command
- **Cargo.toml**: Rust project configuration with `zed_extension_api` dependency
- **lib.rs**: 
  - `CombineWithContextExtension` struct implementing `zed::Extension` trait
  - `run_slash_command()`: Handles the `/combine` command
  - `combine_files()`: Main logic for collecting and formatting files
  - `process_directory()`: Recursively processes directories
  - Helper functions for filtering and formatting

## Making Changes

1. Edit the code in `src/lib.rs`

2. Run checks:
   ```bash
   cargo check
   cargo clippy  # For linting
   cargo fmt     # For formatting
   ```

3. Rebuild:
   ```bash
   cargo build --release
   ```

4. Test in Zed (the extension should reload automatically)

## Publishing to Zed Extension Registry

(Note: Publishing process may evolve as Zed's extension ecosystem matures)

1. Ensure all code is committed and tagged with a version:
   ```bash
   git tag -a v0.1.0 -m "Release version 0.1.0"
   git push origin v0.1.0
   ```

2. Update the version in both:
   - `extension.toml` (version field)
   - `Cargo.toml` (version field)

3. Build the release:
   ```bash
   cargo build --release
   ```

4. Follow Zed's extension publishing guidelines at:
   https://zed.dev/docs/extensions/developing-extensions

## Debugging

1. Check Zed's extension logs:
   - Open Zed's developer console
   - Look for messages from the extension

2. Use `cargo check` frequently during development

3. Test with various project types to ensure compatibility

## Features to Potentially Add

- Configuration options (file size limits, exclusion patterns)
- Support for custom gitignore patterns
- Better error messages
- Progress indicators for large projects
- Export to file options
- Additional slash commands (e.g., `/combine-file` for single files)

## Troubleshooting

### Extension doesn't load
- Ensure the symlink is correct
- Check that Zed can read the extension directory
- Look for errors in Zed's logs

### Compilation errors
- Update Rust: `rustup update`
- Clean and rebuild: `cargo clean && cargo build --release`
- Check that `zed_extension_api` version is compatible

### Slash command not appearing
- Verify `extension.toml` has the correct slash_commands section
- Restart Zed completely
- Check that the extension is enabled in Zed's settings

## Resources

- [Zed Extension Documentation](https://zed.dev/docs/extensions/developing-extensions)
- [Zed Extension API](https://github.com/zed-industries/zed/tree/main/crates/extension_api)
- [Main Repository](https://github.com/mohesu/combine-with-context)
