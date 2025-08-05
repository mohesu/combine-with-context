function getBestFence(content: string): string {
  let max = 3;
  let match;
  const regex = /(`{3,})/g;
  while ((match = regex.exec(content)) !== null) {
    if (match[1].length >= max) {max = match[1].length + 1;}
  }
  return '`'.repeat(max);
}

function mockGetFormattedContext(mockFiles: { rel: string, content: string }[]) {
  let output = '';
  for (const { rel, content } of mockFiles) {
    const fence = getBestFence(content);
    output += `#### ${rel}\n${fence}\n${content}\n${fence}\n---\n`;
  }
  return output;
}

describe('getFormattedContext', () => {
  it('uses four backticks when code contains three', () => {
    const files = [{ rel: 'snippet.txt', content: 'code\n```' }];
    const md = mockGetFormattedContext(files);
    // Should be "\n````\ncode\n```"
    expect(md).toContain('````\ncode\n```');
  });

  it('uses five if code contains four', () => {
    const files = [{ rel: 'foo.txt', content: 'some\n````\nfoo' }];
    const md = mockGetFormattedContext(files);
    expect(md).toContain('```````\nfoo\n```');
  });

  it('triple backtick for code with none', () => {
    const files = [{ rel: 'plain.md', content: 'abc' }];
    const md = mockGetFormattedContext(files);
    expect(md).toContain('```\nabc\n```');
  });
});
