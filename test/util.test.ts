import { isBinaryFile, stripControlChars, timestampString } from '../src/util';

describe('util.ts', () => {
  it('detects binary files correctly', () => {
    expect(isBinaryFile(Buffer.from([65,66,67,68]))).toBe(false);
    expect(isBinaryFile(Buffer.from([0x00,0x01,0x02]))).toBe(true);
  });
  it('removes control characters from text', () => {
    expect(stripControlChars('abc\x01\x02def')).toBe('abcdef');
  });
  it('generates iso timestamps', () => {
    const ts = timestampString();
    expect(ts).toMatch(/^\d{4}-\d{2}-\d{2}T\d{2}_\d{2}_\d{2}$/);
  });
});
