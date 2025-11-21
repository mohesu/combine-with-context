use zed_extension_api::{self as zed, Result, SlashCommand, SlashCommandOutput, SlashCommandOutputSection};
use std::collections::HashMap;
use std::fs;
use std::path::PathBuf;

struct CombineWithContextExtension;

impl zed::Extension for CombineWithContextExtension {
    fn new() -> Self {
        Self
    }

    fn run_slash_command(
        &self,
        command: SlashCommand,
        _args: Vec<String>,
        worktree: Option<&zed::Worktree>,
    ) -> Result<SlashCommandOutput> {
        match command.name.as_str() {
            "combine" => {
                if let Some(wt) = worktree {
                    self.combine_files(wt)
                } else {
                    Err("No worktree available".into())
                }
            }
            _ => Err("Unknown command".into()),
        }
    }
}

impl CombineWithContextExtension {
    fn combine_files(&self, worktree: &zed::Worktree) -> Result<SlashCommandOutput> {
        let root_path_str = worktree.root_path();
        let root_path = PathBuf::from(root_path_str);
        
        // Collect all text files from the worktree
        let mut files = Vec::new();
        let mut file_tree = String::new();
        let mut file_analysis = HashMap::new();
        
        // Read .gitignore if it exists
        let gitignore_path = root_path.join(".gitignore");
        let gitignore_content = if gitignore_path.exists() {
            fs::read_to_string(gitignore_path).unwrap_or_default()
        } else {
            String::new()
        };
        
        // Build file tree and collect files
        if let Ok(entries) = fs::read_dir(&root_path) {
            for entry in entries.flatten() {
                self.process_entry(
                    &entry,
                    &root_path,
                    &gitignore_content,
                    0,
                    &mut files,
                    &mut file_tree,
                    &mut file_analysis,
                );
            }
        }
        
        // Build the output markdown
        let mut output = String::new();
        output.push_str("# Combined Context for AI\n\n");
        
        // Add file tree
        output.push_str("## File Tree\n\n```\n");
        output.push_str(&file_tree);
        output.push_str("```\n\n");
        
        // Add file analysis
        if !file_analysis.is_empty() {
            output.push_str("## File Analysis\n\n");
            for (ext, count) in file_analysis.iter() {
                output.push_str(&format!("- {}: {} files\n", ext, count));
            }
            output.push_str("\n");
        }
        
        // Add file contents
        output.push_str("## File Contents\n\n");
        for (rel_path, content) in files {
            let ext = rel_path
                .split('.')
                .last()
                .unwrap_or("txt");
            
            output.push_str("---\n\n");
            output.push_str(&format!("### {}\n\n", rel_path));
            output.push_str(&format!("```{}\n", self.get_language_for_extension(ext)));
            output.push_str(&content);
            output.push_str("\n```\n\n");
        }
        
        let output_len = output.len() as u32;
        Ok(SlashCommandOutput {
            text: output,
            sections: vec![SlashCommandOutputSection {
                range: zed_extension_api::Range {
                    start: 0,
                    end: output_len,
                },
                label: "Combined Context".to_string(),
            }],
        })
    }
    
    fn process_entry(
        &self,
        entry: &fs::DirEntry,
        base_path: &PathBuf,
        gitignore: &str,
        depth: usize,
        files: &mut Vec<(String, String)>,
        file_tree: &mut String,
        file_analysis: &mut HashMap<String, usize>,
    ) {
        let path = entry.path();
        let file_name = entry.file_name().to_string_lossy().to_string();
        
        // Skip hidden files and common ignored directories
        if file_name.starts_with('.') 
            || file_name == "node_modules" 
            || file_name == "target" 
            || file_name == "dist"
            || file_name == "build"
            || self.is_ignored(&path, gitignore) {
            return;
        }
        
        let indent = "  ".repeat(depth);
        
        if path.is_dir() {
            file_tree.push_str(&format!("{}📁 {}/\n", indent, file_name));
            
            if let Ok(sub_entries) = fs::read_dir(&path) {
                for sub_entry in sub_entries.flatten() {
                    self.process_entry(
                        &sub_entry,
                        base_path,
                        gitignore,
                        depth + 1,
                        files,
                        file_tree,
                        file_analysis,
                    );
                }
            }
        } else if path.is_file() && !self.is_binary_file(&path) {
            file_tree.push_str(&format!("{}📄 {}\n", indent, file_name));
            
            // Read file content
            if let Ok(content) = fs::read_to_string(&path) {
                // Skip empty files
                if content.trim().is_empty() {
                    return;
                }
                
                // Skip very large files (> 5MB)
                if content.len() > 5_242_880 {
                    return;
                }
                
                let rel_path = path
                    .strip_prefix(base_path)
                    .unwrap_or(&path)
                    .to_string_lossy()
                    .to_string();
                
                files.push((rel_path.clone(), content));
                
                // Track file extension
                if let Some(ext) = path.extension() {
                    let ext_str = ext.to_string_lossy().to_string();
                    *file_analysis.entry(ext_str).or_insert(0) += 1;
                }
            }
        }
    }
    
    fn is_binary_file(&self, path: &PathBuf) -> bool {
        // First check by extension
        if let Some(ext) = path.extension() {
            let ext_str = ext.to_string_lossy().to_lowercase();
            if matches!(
                ext_str.as_str(),
                "png" | "jpg" | "jpeg" | "gif" | "exe" | "dll" | "ico" 
                | "svg" | "webp" | "bmp" | "tiff" | "zip" | "tar" | "gz" 
                | "bin" | "so" | "dylib" | "a" | "o" | "pdf" | "mp4" | "mp3"
                | "avi" | "mov" | "wav" | "ttf" | "otf" | "woff" | "woff2"
                | "eot" | "pyc" | "class" | "jar" | "war" | "ear"
            ) {
                return true;
            }
        }
        
        // Check file content for null bytes (indicates binary)
        if let Ok(mut file) = fs::File::open(path) {
            use std::io::Read;
            let mut buffer = [0; 512];
            if let Ok(bytes_read) = file.read(&mut buffer) {
                return buffer[..bytes_read].contains(&0);
            }
        }
        
        false
    }
    
    fn is_ignored(&self, _path: &PathBuf, _gitignore: &str) -> bool {
        // TODO: Implement proper gitignore parsing
        // Currently only excludes common directories in process_entry()
        // For full .gitignore support, consider adding the 'ignore' crate dependency
        false
    }
    
    fn get_language_for_extension(&self, ext: &str) -> &str {
        match ext {
            "rs" => "rust",
            "js" => "javascript",
            "ts" => "typescript",
            "py" => "python",
            "java" => "java",
            "c" | "h" => "c",
            "cpp" | "cc" | "cxx" | "hpp" => "cpp",
            "go" => "go",
            "rb" => "ruby",
            "php" => "php",
            "swift" => "swift",
            "kt" => "kotlin",
            "scala" => "scala",
            "cs" => "csharp",
            "html" => "html",
            "css" => "css",
            "scss" | "sass" => "scss",
            "json" => "json",
            "xml" => "xml",
            "yaml" | "yml" => "yaml",
            "toml" => "toml",
            "md" => "markdown",
            "sh" | "bash" => "bash",
            "sql" => "sql",
            "r" => "r",
            "dart" => "dart",
            _ => "text",
        }
    }
}

zed::register_extension!(CombineWithContextExtension);
