import * as fs from 'fs';

export function isBinaryFile(buffer: Buffer): boolean {
  if (buffer.length === 0) {return false;}
  for (let i = 0; i < Math.min(buffer.length, 8192); ++i)
    {if (buffer[i] === 0) {return true;}}
  return false;
}

export function ensureDirExists(dirPath: string) {
  if (!fs.existsSync(dirPath))
    {fs.mkdirSync(dirPath, { recursive: true });}
}

export function timestampString(): string {
  return new Date().toISOString().replace(/[^\dT]/g, '_').slice(0, 19);
}

export function stripControlChars(text: string): string {
  return text.replace(/[\u0000-\u001F\u007F-\u009F]/g, '');
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