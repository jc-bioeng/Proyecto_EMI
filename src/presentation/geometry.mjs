/** Control rectangular; no comprueba glifos, editabilidad ni render. Unidades: pulgadas. */
import { theme } from './theme.mjs';

export function assertBox(box, bounds = { x: 0, y: 0, w: theme.width, h: theme.height }) {
  for (const [name, rect] of [['box', box], ['bounds', bounds]]) {
    if (!rect || !['x', 'y', 'w', 'h'].every(key => Number.isFinite(rect[key]))) {
      throw new TypeError(`${name}: se requieren x, y, w, h finitos`);
    }
    if (rect.w <= 0 || rect.h <= 0) throw new RangeError(`${name}: dimensiones no positivas`);
  }
  const epsilon = 1e-8;
  if (box.x < bounds.x - epsilon || box.y < bounds.y - epsilon ||
      box.x + box.w > bounds.x + bounds.w + epsilon ||
      box.y + box.h > bounds.y + bounds.h + epsilon) {
    throw new RangeError('Caja fuera de los límites permitidos');
  }
  return box;
}

export function overlaps(a, b) {
  assertBox(a);
  assertBox(b);
  return Math.min(a.x + a.w, b.x + b.w) - Math.max(a.x, b.x) > 1e-8 &&
    Math.min(a.y + a.h, b.y + b.h) - Math.max(a.y, b.y) > 1e-8;
}
