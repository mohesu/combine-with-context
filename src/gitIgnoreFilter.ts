import ignore from 'ignore';
import * as fs from 'fs';
import * as path from 'path';

export async function getGitIgnore(workspaceRoot: string): Promise<ReturnType<typeof ignore> | null> {
  const gitignorePath = path.join(workspaceRoot, '.gitignore');
  if (fs.existsSync(gitignorePath)) {
    try {
      const raw = fs.readFileSync(gitignorePath, 'utf8');
      return ignore().add(raw.split('\n'));
    } catch {
      return null;
    }
  }
  return null;
}
