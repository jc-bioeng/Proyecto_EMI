# Producción editorial EMI

Directorio separado del código Java. Esta preparación no genera slides ni PPTX y no añade dependencias a Maven.

Archivos disponibles:

- `theme.mjs`: medidas, fuentes y colores acordes con `presentation/deck_spec.md`.
- `geometry.mjs`: validación de cajas; no estima texto ni renderiza.

Uso desde la raíz, con Node disponible:

```powershell
node --check src/presentation/theme.mjs
node --check src/presentation/geometry.mjs
```

PptxGenJS 4.0.1 está disponible en el runtime de artefactos inspeccionado, no instalado como dependencia del repositorio. Al producir, localizar el runtime con `load_workspace_dependencies` o preparar una dependencia editorial aislada; comprobar versión y API local. No guardar una ruta personal absoluta en código portable ni copiar `node_modules`.

Flujo posterior: revalidar fuentes → actualizar matriz/storyboard → ensamblar con PptxGenJS → añadir notas y fuentes → exportar a `presentation/build/` → renderizar todas las slides → corregir y verificar editabilidad → entregar en `presentation/output/`. Leer los cinco skills enlazados desde AGENTS. En el encargo actual no hay ensamblador ni comando de exportación que ejecute accidentalmente esa fase.

`geometry.mjs` verifica límites rectangulares y solapamientos de área. Los conectores de ancho/alto cero requieren otro tratamiento. Una caja dentro del lienzo puede contener texto cortado: revisar el render en producción. Excluir explícitamente solapamientos intencionales, por ejemplo texto contenido en una forma, sin silenciar otros.
