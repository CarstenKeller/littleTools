// Rendert SVG-Dateien per Chromium (Playwright) zu PNG mit Transparenz.
const fs = require('fs');
const { chromium } = require('playwright');
(async () => {
  const jobs = JSON.parse(fs.readFileSync(process.argv[2], 'utf8'));
  const b = await chromium.launch();
  const pg = await b.newPage();
  for (const j of jobs) {
    await pg.setViewportSize({ width: j.w, height: j.h });
    await pg.setContent(`<html><body style="margin:0;background:transparent">${fs.readFileSync(j.svg, 'utf8')}</body></html>`);
    await pg.locator('svg').first().screenshot({ path: j.png, omitBackground: true });
  }
  await b.close();
})();
