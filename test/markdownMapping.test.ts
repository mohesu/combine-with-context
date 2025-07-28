import { getMarkdownLangForFile } from '../src/markdownMapping';

jest.mock('../src/settings', () => ({
  getConfig: () => ({
    markdownMapping: {
      '.sh': { language: 'bash-script' },
      '.foo': { language: 'foobar' }
    }
  })
}));

describe('markdownMapping.ts', () => {
  it('maps custom extensions', () => {
    expect(getMarkdownLangForFile('mytest.sh')).toBe('bash-script');
    expect(getMarkdownLangForFile('hello.FOO')).toBe('foobar');
  });
  it('falls back to default for .js', () => {
    expect(getMarkdownLangForFile('something.js')).toBe('javascript');
  });
  it('returns blank for unknown', () => {
    expect(getMarkdownLangForFile('README.unknown')).toBe('');
  });
});
