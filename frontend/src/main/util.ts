/* eslint import/prefer-default-export: off */
import { URL } from 'url';
import path from 'path';

// TODO: refactor after I can test non dev environment
export function resolveHtmlPath(htmlFileName: string, hash?: string) {
  if (process.env.NODE_ENV === 'development') {
    const port = process.env.PORT || 1212;
    const url = new URL(`http://localhost:${port}`);
    url.pathname = htmlFileName;
    if (hash) {
      url.hash = hash;
    }
    return url.href;
  }
  let htmlPath = `file://${path.resolve(
    __dirname,
    '../renderer/',
    htmlFileName,
  )}`;
  if (hash) {
    htmlPath += `#${hash}`;
  }
  return htmlPath;
}
