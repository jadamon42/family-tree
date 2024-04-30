import { URL } from 'url';
import path from 'path';

export function resolveHtmlPath(htmlFileName: string, hash?: string) {
  if (MAIN_WINDOW_VITE_DEV_SERVER_URL) {
    const url = new URL(MAIN_WINDOW_VITE_DEV_SERVER_URL);
    url.pathname = htmlFileName;
    if (hash) {
      url.hash = hash;
    }
    return url.href;
  }
  let htmlPath = `file://${path.join(
    __dirname,
    `../renderer/${MAIN_WINDOW_VITE_NAME}/${htmlFileName}`
  )}`;
  if (hash) {
    htmlPath += `#${hash}`;
  }
  return htmlPath;
}


