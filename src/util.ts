import * as fs from 'fs';
import * as path from 'path';

export function isBinaryFile(buffer: Buffer): boolean {
  if (buffer.length === 0) {return false;}
  for (let i = 0; i < Math.min(buffer.length, 8192); ++i)
    {if (buffer[i] === 0) {return true;}}
  return false;
}

export function ensureDirExists(dirPath: string) {
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true });
  }
}

export function timestampString(): string {
  return new Date().toISOString().replace(/[^\dT]/g, '_').slice(0, 19);
}

export function stripControlChars(text: string): string {
  return text.replace(/[\u0000-\u0009\u000B-\u000C\u000E-\u001F\u007F-\u009F]/g, '');
}

export function getBestFence(content: string): string {
  let max = 3;
  let match;
  const regex = /(`{3,})/g;
  while ((match = regex.exec(content)) !== null) {
    if (match[1].length >= max) {max = match[1].length + 1;}
  }
  return '`'.repeat(max);
}

export function buildFileTree(relPaths: string[]): string {
  interface Node {
    children: Map<string, Node>;
  }
  const root: Node = { children: new Map() };
  for (const relPath of relPaths) {
    const parts = relPath.split('/');
    let current = root;
    for (const part of parts) {
      if (!current.children.has(part)) {
        current.children.set(part, { children: new Map() });
      }
      current = current.children.get(part)!;
    }
  }
  const lines: string[] = [];
  function buildLines(node: Node, prefix: string) {
    const entries = Array.from(node.children.entries()).sort((a, b) => a[0].localeCompare(b[0]));
    for (let i = 0; i < entries.length; i++) {
      const [name, child] = entries[i];
      const isLast = i === entries.length - 1;
      lines.push(prefix + (isLast ? '└── ' : '├── ') + name + (child.children.size > 0 ? '/' : ''));
      if (child.children.size > 0) {
        buildLines(child, prefix + (isLast ? '    ' : '│   '));
      }
    }
  }
  buildLines(root, '');
  return lines.join('\n');
}

function removeCommentsJS(code: string): string {
  let output = '';
  let i = 0;
  let inString = false;
  let stringChar = '';
  let inRegex = false;
  let inCommentSingle = false;
  let inCommentMulti = false;
  while (i < code.length) {
    let c = code[i];
    if (inCommentSingle) {
      if (c === '\n') {
        inCommentSingle = false;
      }
      i++;
      continue;
    }
    if (inCommentMulti) {
      if (c === '*' && i + 1 < code.length && code[i + 1] === '/') {
        i += 2;
        inCommentMulti = false;
        continue;
      }
      i++;
      continue;
    }
    if (inString) {
      output += c;
      if (c === stringChar && (i === 0 || code[i - 1] !== '\\')) {
        inString = false;
      }
      i++;
      continue;
    }
    if (inRegex) {
      output += c;
      if (c === '/' && (i === 0 || code[i - 1] !== '\\')) {
        inRegex = false;
      }
      i++;
      continue;
    }
    if (c === '"' || c === "'" || c === '`') {
      inString = true;
      stringChar = c;
      output += c;
      i++;
      continue;
    }
    if (c === '/' && i + 1 < code.length) {
      let next = code[i + 1];
      if (next === '/') {
        inCommentSingle = true;
        i += 2;
        continue;
      } else if (next === '*') {
        inCommentMulti = true;
        i += 2;
        continue;
      } else {
        inRegex = true;
        output += c;
        i++;
        continue;
      }
    }
    output += c;
    i++;
  }
  return output;
}

function removeCommentsPython(code: string): string {
  let output = '';
  let i = 0;
  let inString = false;
  let stringChar = '';
  let isTriple = false;
  while (i < code.length) {
    let c = code[i];
    if (inString) {
      output += c;
      if (isTriple) {
        if (c === stringChar && i + 2 < code.length && code[i + 1] === stringChar && code[i + 2] === stringChar && (i === 0 || code[i - 1] !== '\\')) {
          inString = false;
          output += stringChar + stringChar;
          i += 3;
          continue;
        }
      } else {
        if (c === stringChar && (i === 0 || code[i - 1] !== '\\')) {
          inString = false;
        }
      }
      i++;
      continue;
    }
    if (c === '#') {
      while (i < code.length && code[i] !== '\n') {i++;}
      continue;
    }
    if (c === '"' || c === "'") {
      inString = true;
      stringChar = c;
      isTriple = (i + 2 < code.length && code[i + 1] === c && code[i + 2] === c);
      output += c;
      i++;
      if (isTriple) {
        output += c + c;
        i += 2;
      }
      continue;
    }
    output += c;
    i++;
  }
  return output;
}

function removeCommentsCSS(code: string): string {
  return code.replace(/\/\*[\s\S]*?\*\//g, '');
}

export function minifyContent(content: string, lang: string): string {
  let cleaned = content;
  switch (lang.toLowerCase()) {
    case 'javascript':
    case 'typescript':
      cleaned = removeCommentsJS(content);
      break;
    case 'python':
      cleaned = removeCommentsPython(content);
      break;
    case 'css':
      cleaned = removeCommentsCSS(content);
      break;
  }
  return cleaned.split('\n')
    .map(line => line.trim().replace(/\s+/g, ' '))
    .filter(line => line.length > 0)
    .join('\n');
}