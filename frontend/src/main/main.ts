import path from 'path';
import { app, BrowserWindow, ipcMain, shell } from 'electron';
import { spawn } from 'child_process';
import MenuBuilder from './menu';
import { resolveHtmlPath } from './util';

let mainWindow: BrowserWindow | null = null;

// Handle creating/removing shortcuts on Windows when installing/uninstalling.
if (require('electron-squirrel-startup')) {
  app.quit();
}

const createWindow = async () => {
  const RESOURCES_PATH = app.isPackaged
    ? path.join(process.resourcesPath, 'assets')
    : path.join(__dirname, '../../assets');

  const getAssetPath = (...paths: string[]): string => {
    return path.join(RESOURCES_PATH, ...paths);
  };

  mainWindow = new BrowserWindow({
    show: false,
    width: 1024,
    height: 728,
    icon: getAssetPath('icon.png'),
    title: 'Family Tree',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
    },
  });

  mainWindow.loadURL(resolveHtmlPath('index.html'));

  mainWindow.on('ready-to-show', () => {
    if (!mainWindow) {
      throw new Error('"mainWindow" is not defined');
    }
    if (process.env.START_MINIMIZED) {
      mainWindow.minimize();
    } else {
      mainWindow.show();
    }
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  const menuBuilder = new MenuBuilder(mainWindow);
  menuBuilder.buildMenu();

  // Open urls in the user's browser
  mainWindow.webContents.setWindowOpenHandler((edata) => {
    shell.openExternal(edata.url);
    return { action: 'deny' };
  });

  if (process.env.NODE_ENV === 'development') {
    mainWindow.webContents.openDevTools();
  }
};

const startBackend = () => {
  const jarPath = path.join(app.getAppPath(), 'backend', 'family-1.0.jar');
  const backend = spawn('java', ['-jar', jarPath]);

  backend.stdout.on('data', (data) => {
    console.log(`stdout: ${data}`);
  });

  backend.stderr.on('data', (data) => {
    console.error(`stderr: ${data}`);
  });

  backend.on('close', (code) => {
    console.log(`backend process exited with code ${code}`);
  });
};

/**
 * Add event listeners...
 */

ipcMain.on('open-person-form', (event, person) => {
  if (!mainWindow) {
    return;
  }

  let personWindow: BrowserWindow | null = new BrowserWindow({
    width: 650,
    height: 450,
    minWidth: 650,
    minHeight: 450,
    parent: mainWindow,
    title: person ? 'Edit Person' : 'Add Person',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
    },
  });

  personWindow.loadURL(resolveHtmlPath('index.html', 'person-form'));

  personWindow.webContents.on('did-finish-load', () => {
    personWindow?.webContents.send('person-data', person);
  });

  personWindow.on('closed', () => {
    personWindow = null;
  });

  personWindow.webContents.openDevTools();
});

ipcMain.on('open-partner-form', (event, personId) => {
  if (!mainWindow) {
    return;
  }

  let partnerWindow: BrowserWindow | null = new BrowserWindow({
    width: 650,
    height: 650,
    minWidth: 650,
    minHeight: 650,
    parent: mainWindow,
    title: 'Add Partner',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
    },
  });

  partnerWindow.loadURL(resolveHtmlPath('index.html', 'partner-form'));

  partnerWindow.webContents.on('did-finish-load', () => {
    partnerWindow?.webContents.send('partnership-data', personId);
  });

  partnerWindow.on('closed', () => {
    partnerWindow = null;
  });

  partnerWindow.webContents.openDevTools();
});

ipcMain.on('submit-person-form', (event, personId) => {
  if (mainWindow) {
    mainWindow.webContents.send('person-submitted', personId);
  }
});

ipcMain.on('submit-partner-form', (event, personId, partnerId) => {
  if (mainWindow) {
    mainWindow.webContents.send('partner-submitted', personId, partnerId);
  }
});

app.on('window-all-closed', () => {
  // Respect the OSX convention of having the application in memory even
  // after all windows have been closed
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app
  .whenReady()
  .then(() => {
    createWindow();
    startBackend();
    app.on('activate', () => {
      // On macOS, it's common to re-create a window in the app when the
      // dock icon is clicked and there are no other windows open.
      if (mainWindow === null) createWindow();
    });
  })
  .catch(console.log);
