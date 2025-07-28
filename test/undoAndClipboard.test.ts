jest.mock('vscode', () => ({
  workspace: {
    getConfiguration: jest.fn(),
    workspaceFolders: [{ uri: { fsPath: '/workspace' } }],
    fs: {
      readFile: jest.fn(),
      stat: jest.fn(),
      readDirectory: jest.fn(),
    },
  },
  window: {
    showErrorMessage: jest.fn(),
    showInformationMessage: jest.fn(),
    showWarningMessage: jest.fn(),
    showQuickPick: jest.fn(),
    withProgress: jest.fn((opts, fn) => fn({ report: jest.fn() }, { isCancellationRequested: false })),
    createOutputChannel: jest.fn(() => ({ appendLine: jest.fn(), show: jest.fn(), dispose: jest.fn() })),
    openTextDocument: jest.fn(),
    showTextDocument: jest.fn(),
  },
  env: {
    clipboard: {
      writeText: jest.fn(),
    },
  },
}));
