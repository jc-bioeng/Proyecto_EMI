/** Especificación editorial; sin dependencia de PptxGenJS ni generación de slides. */
const width = 40 / 3;
const marginX = 0.6;
const gutter = 0.2;
const contentWidth = width - 2 * marginX;

export const theme = Object.freeze({
  width,
  height: 7.5,
  fontFace: 'Arial',
  marginX,
  marginY: 0.4,
  columns: 12,
  gutter,
  columnWidth: (contentWidth - 11 * gutter) / 12,
  title: Object.freeze({ x: marginX, y: 0.4, w: contentWidth, h: 1.05, fontSize: 32 }),
  body: Object.freeze({ x: marginX, y: 1.7, w: contentWidth, h: 4.95, fontSize: 22 }),
  footer: Object.freeze({ x: marginX, y: 6.85, w: contentWidth, h: 0.25, fontSize: 12 }),
  minimumFont: Object.freeze({ body: 22, label: 18, evidence: 12 }),
  colors: Object.freeze({
    background: 'FAFCF8', text: '203C2B', secondary: '4D5E52', accent: '357A3E',
    closed: '286535', progress: '92400E', pending: '536255', limit: '9F1239',
  }),
});
