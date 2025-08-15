import { getConfig } from './settings';

interface MarkdownMapping {
  [ext: string]: { language: string; };
}

export function getMarkdownLangForFile(filename: string): string {
  const config = getConfig();
  const mapping: MarkdownMapping = config.markdownMapping as any || {};
  for (const [key, value] of Object.entries(mapping)) {
    if (filename.endsWith(key)) {return value.language || '';}
  }
  const ext = filename.split('.').pop()?.toLowerCase();
  switch (ext) {
    case "js": return "javascript";
    case "ts": return "typescript";
    case "md": return "markdown";
    case "json": return "json";
    case "sh": return "bash";
    case "py": return "python";
    case "java": return "java";
    case "css": return "css";
    case "dart": return "dart";
    case "yml": 
    case "yaml": return "yaml";
    default: return '';
  }
}
