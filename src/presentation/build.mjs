import fs from 'node:fs';
import path from 'node:path';
import {createRequire} from 'node:module';
import {theme} from './theme.mjs';
import {assertBox} from './geometry.mjs';
import {decisions,requirements} from './catalogs.mjs';
const require=createRequire(import.meta.url);
const PptxGenJS=require(process.env.EMI_NODE_MODULES ? path.join(process.env.EMI_NODE_MODULES,'pptxgenjs') : 'pptxgenjs');
const pptx=new PptxGenJS(); pptx.layout='LAYOUT_WIDE';
pptx.author='Juan José Cortés Fajardo';pptx.subject='Seguimiento académico del Proyecto EMI';pptx.title='Proyecto EMI · Avance al asesor';pptx.company='Proyecto EMI · Bioingeniería';pptx.lang='es-CO';
pptx.theme={headFontFace:'Arial',bodyFontFace:'Arial',lang:'es-CO'};
const C=theme.colors; const manifest=[];let slide;
const sourceNotes=fs.readFileSync('presentation/speaker_notes.md','utf8').split(/^## N\d{2} — /m).slice(1);
const evidence=fs.readFileSync('presentation/evidence_matrix.md','utf8');
const catalog=evidence.split('## Catálogo de fuentes')[1].split('## Afirmaciones')[0];
const titles=fs.readFileSync('presentation/storyboard.md','utf8').matchAll(/^## (\d{2}) — (.+)$/gm);
const T=[...titles].map(m=>m[2]);
function text(t,x,y,w,h,size=22,color=C.text,bold=false,extra={}){assertBox({x,y,w,h});slide.addText(t,{x,y,w,h,fontFace:'Arial',fontSize:size,color,bold,margin:0,breakLine:false,paraSpaceAfterPt:0,valign:'mid',...extra});manifest.push({slide:pptx._slides.length,type:'text',text:t,x,y,w,h,size});}
function line(x1,y1,x2,y2,color=C.accent,dash=false,arrow=false){slide.addShape(pptx.ShapeType.line,{x:Math.min(x1,x2),y:Math.min(y1,y2),w:Math.abs(x2-x1),h:Math.abs(y2-y1),flipH:x2<x1,flipV:y2<y1,line:{color,width:1.8,...(dash?{dashType:'dash'}:{}),...(arrow?{endArrowType:'triangle'}:{})}});}
function box(x,y,w,h,fill='FFFFFF',stroke='DCE6D9',dash=false){assertBox({x,y,w,h});slide.addShape(pptx.ShapeType.rect,{x,y,w,h,fill:{color:fill},line:{color:stroke,width:1,...(dash?{dashType:'dash'}:{})}});}
function title(t){t=t.replace('El stack elegido permite desarrollar y probar el MVP local','El stack elegido sostiene el desarrollo y las pruebas del MVP').replace('El punto de control debe delimitarse antes de congelar la configuración','La configuración física depende de delimitar el punto');text(t,.6,.38,12.13,1.05,32,C.text,true);}
function subtitle(t,y=1.5){text(t,.6,y,12.13,.45,22,C.secondary);}
function footer(source){line(.6,6.72,12.73,6.72,'DCE6D9');text(source,.6,6.86,10.75,.24,12,C.secondary);text(String(pptx._slides.length).padStart(2,'0'),11.65,6.84,1.08,.28,12,C.secondary,false,{align:'right'});}
function phase(t='DISEÑO'){box(0,0,13.333333,.075,C.accent,C.accent);text('BIOINGENIERÍA · UNIVERSIDAD DE ANTIOQUIA',.6,7.15,8.9,.2,12,C.accent);text(t,10.1,7.15,2.63,.2,12,C.secondary,false,{align:'right'});}
function badge(t,x,y,w=2.4,color=C.pending){text(t,x,y,w,.35,18,color,true);}
function card(head,body,x,y,w,h=1.5,{pending=false,color=C.accent}={}){box(x,y,w,h,pending?'F1F5ED':'FFFFFF',pending?'98AB93':'DCE6D9',pending);text(head,x+.18,y+.1,w-.36,.4,22,color,true);text(body,x+.18,y+.55,w-.36,h-.65,18,C.secondary);}
function node(t,x,y,w,h=.85,pending=false){box(x,y,w,h,pending?'F1F5ED':'FFFFFF',pending?'98AB93':C.accent,pending);text(t,x+.12,y+.1,w-.24,h-.2,20,C.text,true,{align:'center'});}
function flow(labels,x,y,w,{pending=false}={}){const gap=.3,nw=(w-gap*(labels.length-1))/labels.length; labels.forEach((t,i)=>{node(t,x+i*(nw+gap),y,nw,.9,pending);if(i<labels.length-1)line(x+i*(nw+gap)+nw,y+.45,x+(i+1)*(nw+gap)-.03,y+.45,C.accent,pending,true);});}
function label(t,x,y,w,color=C.secondary){text(t,x,y,w,.4,18,color);}
function comparison(left,right){box(.6,1.85,4.05,4.53,'E9F2E5','E9F2E5');box(4.95,1.85,7.78,4.53,'F0F4EA','F0F4EA');text(left,.85,2.02,3.6,.45,24,C.closed,true);text(right,5.2,2.02,7.2,.45,24,C.text,true);}
function timeline(items){const xs=[.6,3.73,6.86,9.99];line(.9,2.9,12.25,2.9,'CDDAC7');items.forEach((a,i)=>{text(a[0],xs[i],1.87,2.74,.85,22,C.text,true);slide.addShape(pptx.ShapeType.ellipse,{x:xs[i]+.04,y:2.78,w:.23,h:.23,fill:{color:C.accent},line:{color:C.accent}});text(a[1],xs[i],3.17,2.74,.8,18,C.secondary);text(a[2],xs[i],4.0,2.74,.65,18,i===0?C.text:C.closed,true);});}
function imagePlano(x,y,w,h){slide.addImage({path:'presentation/build/assets/plano_aportado.png',x,y,w,h});}
function techLogo(name,x,y,w,h){const p=`presentation/build/assets/logos/${name}.svg`;const svg=fs.readFileSync(p,'utf8');const vb=svg.match(/viewBox=["']([^"']+)/);const vals=vb? vb[1].trim().split(/[ ,]+/).map(Number):[0,0,128,128];const ratio=Math.min(w/vals[2],h/vals[3]);slide.addImage({path:p,x:x+(w-vals[2]*ratio)/2,y:y+(h-vals[3]*ratio)/2,w:vals[2]*ratio,h:vals[3]*ratio});}
function pptxgenContain(x,y,w,h){const iw=1122,ih=1402,r=Math.min(w/iw,h/ih);return{x:x+(w-iw*r)/2,y:y+(h-ih*r)/2,w:iw*r,h:ih*r};}
function start(i,source){slide=pptx.addSlide();slide.background={color:C.background};title(T[i-1]);footer(source);phase();let notes=sourceNotes[i-1].replace(/\*\*/g,'');
const eids=[...new Set(notes.match(/E\d{2}/g)||[])];notes+='\n\nREFERENCIAS DOCUMENTALES\n'+catalog+'\n'+evidence.split('\n').filter(l=>eids.some(id=>l.startsWith('| '+id+' |'))).join('\n');
if([4,12].includes(i))notes+='\nIMAGEN APORTADA POR EL USUARIO: codex-clipboard-50e2632b-143c-4559-b9a3-ba98940e4f3e.png. Procedencia técnica/escala y fidelidad dimensional no verificadas. Apoyo interpretativo; cotejo contextual con informe AM/remodelación pp.5–7 y DEC-023. No constituye instalación ni cobertura RF demostrada. Los puntos rojos no se interpretan como antenas.\n';
notes=notes.replace(/El usuario aportará una imagen mejor y su explicación; la lectura espacial se ajustará a ese material antes de producir la slide\./g,'Se recibió una imagen de mejor calidad; se usa como apoyo interpretativo, sin asumir escala o validación institucional.').replace(/Se reservó la composición para la imagen mejorada y explicación que enviará el usuario\./g,'Se incorpora la imagen aportada por el usuario como apoyo interpretativo; no se recibió explicación operativa adicional.').replace(/Nuevo asset\/explicación: aún no recibidos\./g,'Imagen recibida; explicación operativa adicional no recibida.').replace(/nuevo asset\/explicación pendientes\./g,'imagen recibida; explicación adicional pendiente.');
slide.addNotes(notes);}

start(1,'Propuesta aprobada, p.2 · Maestro v1.9.2, pp.1–2 · Corte 10/09/2026');
text('PROYECTO EMI',.6,1.83,8,.45,22,C.accent,true);
text('Desarrollo y validación de un prototipo de trazabilidad basado en RFID pasivo para equipos biomédicos administrados por EMI Medellín',.6,2.65,10.6,1.7,28,C.text,true);
line(.6,4.75,3.1,4.75,C.accent);text('Juan José Cortés Fajardo\nBioingeniería · Universidad de Antioquia',.6,5.02,9,.8,22);
badge('Validación física (RF_REAL) y piloto: PENDIENTES',.6,6.11,11.9,C.pending);

function glossary(titleText,rows,source,notes){slide=pptx.addSlide();slide.background={color:C.background};title(titleText);footer(source);phase();rows.forEach((r,i)=>{const y=1.8+i*(4.65/rows.length);text(r[0].replace(" · ","\n"),.6,y,3.05,.85,22,C.accent,true);text(r[1].replace(/ (Indispensable|Deseable) ·/,"\n$1 ·"),3.9,y,8.83,.82,22);if(i<rows.length-1)line(.6,y+.94,12.73,y+.94,'DCE6D9');});slide.addNotes(notes+'\nFuente: '+source);}
glossary('Los puntos P0–P3 son condiciones de avance, no fases del cronograma',[
 ['P0 · Requisitos','Base suficiente para diseñar; requisitos indispensables definidos. CERRADO.'],
 ['P1 · Hardware UHF','Lector autorizado, configuración y lectura física reproducible. EN CURSO.'],
 ['P2 · Punto operativo','Un único punto, rutas, responsables, geometría y permisos. EN CURSO.'],
 ['P3 · Aceptación','Fijar umbrales tras prepruebas y antes del piloto. PENDIENTE.']
],'Maestro v1.9.2, §2 / §9 · Matriz v1.9.2, §5','P significa aquí punto de control de avance o gate interno. No equivale a una fase metodológica ni a una fecha. P0 autoriza Diseño; no cierra MVP o P1. P1 exige EPC UHF RF_REAL desde hardware autorizado. P2 no autoriza piloto sin Pruebas. P3 no sustituye RF_REAL. La documentación vigente define P0 a P3; no se inventan P4 u otros puntos.');
glossary('Los incrementos entregan partes verificables del software',[
 ['I1 · Identidad','Asociar equipo y etiqueta, corregir y conservar historial. CERRADO: 32 pruebas.'],
 ['I2 · Entrada simulada','Contrato común y generación de lecturas simuladas. CERRADO: 25 pruebas nuevas.'],
 ['I3 · Siguiente trabajo','Diseñar persistencia de lecturas y relación con sesión antes de programar. PENDIENTE.'],
 ['Después de I3','Eventos, historial operativo, verificación, sustitución y contingencia; integración real condicionada.']
],'Maestro v1.9.2, §2 / §4 / §9 · Informes de I1 e I2','I significa incremento: bloque técnico de implementación y comprobación. I1 e I2 no son fases completas del proyecto. Total documentado: 57 pruebas, cero fallos, errores u omitidas. I3 es el siguiente incremento por diseñar; no se da por cerrada su especificación. No se asignan números I4 o posteriores a trabajos que el control vigente no numera. La integración UHF real depende de P1 y no se acredita por simulación.');
glossary('Cada sigla identifica un concepto y cada código permite rastrearlo',[
 ['RFID / EPC','Identificación por radiofrecuencia / código electrónico de la etiqueta.'],
 ['UHF / HF','Bandas de ultraalta / alta frecuencia; el proyecto usa RFID UHF pasivo.'],
 ['MVP / SDK','Producto mínimo viable / librerías y herramientas del fabricante.'],
 ['RF-001 / DEC-022','Requisito funcional / decisión registrada. El número es un identificador, no un resultado.']
],'Matriz y Registro v1.9.2 · Glosario explicativo','RF, cuando aparece solo, significa radiofrecuencia; el prefijo RF- en la matriz significa requisito funcional. RF_REAL identifica evidencia obtenida con hardware RFID físico; SIMULACION identifica datos generados por software. AM es el sistema institucional existente de gestión de activos/mantenimiento; se mantiene la denominación documental sin inventar una expansión oficial. Must significa indispensable y Should deseable en la priorización vigente: prioridad no equivale a implementación. Los requisitos están en la Matriz; las decisiones DEC están en el Registro. Los anexos explican todos sus identificadores y estados.');

start(2,'Maestro v1.9.2, §2 · I1 v0.2 · Informe I2, §9');
timeline([['Propuesta','Línea base formal','APROBADA / congelada'],['Requisitos suficientes','P0 · cierre 03/09','CERRADO'],['Asociación e historial','I1 · informe 06/09','CERRADO'],['Entrada simulada','I2 · evidencia 07/09\nConsolidación 09/09','CERRADO técnicamente']]);
label('Incremento = bloque técnico acotado y verificable',.6,4.8,12.1);
text('Diagnóstico → Requisitos → DISEÑO → MVP → Pruebas → Piloto → Validación',.6,5.4,12.13,.65,22,C.text,true);
label('Fase formal: Diseño. Producto mínimo viable (MVP): aún incompleto.',.6,6.3,12.1,C.accent);

start(3,'Propuesta, pp.3–4 · Registro v1.9.2: DEC-005 / DEC-008');
flow(['Movimiento físico','Identificación','Registro oportuno'],1.0,2.1,11.3);
text('La correspondencia debe poder verificarse',1,3.3,11.3,.55,26,C.text,true,{align:'center'});
box(.6,4.23,12.13,.85,'E8F1E2','E8F1E2');text('UHF pasivo   ·   Equipos biomédicos   ·   Un punto autorizado',.85,4.4,11.6,.48,24,C.accent,true);
text('AM (sistema de activos): sin integración automática autorizada',.6,5.42,12.13,.45,22,C.text,true);
label('Fuera del alcance: localización en tiempo real, insumos y despliegue institucional',.6,6.1,12.13);

start(4,'Maestro v1.9.2, §6 · Registro: DEC-023 · Imagen aportada por el usuario');
imagePlano(.6,1.75,6.7,4.5324);
text('Recepción → preparación\n→ dotación',7.65,1.8,5.08,.8,22,C.accent,true);
card('AM: sistema existente','Tiene “Ubicación Física”; su uso dinámico está pendiente.',7.65,2.85,5.08,1.25);
card('Corroborado en el control','Ventanilla y picking; no demuestra instalación RFID.',7.65,4.2,5.08,1.25);
text('Por confirmar: rutas y operador',7.65,5.7,5.08,.65,22,C.progress,true);
label('Detalle ampliado del plano; sin escala verificada',.6,6.3,7.1);

start(5,'Propuesta, pp.3–6 · Matriz v1.9.2, RF-001 / RF-003 / RF-006');
const cols=[.6,3.08,5.57,8.05,10.54],cw=2.19;
['Necesidad','Requisito','Diseño','Implementación','Prueba'].forEach((t,i)=>label(t,cols[i],1.72,cw,C.accent));
[['Activo ↔\netiqueta','RF-001\nAsociar','Relación\nhistórica','Implementado','Corrección\ny rollback'],['Interpretar\nmovimiento','RF-003\nContexto','Sesión\ny evento','PENDIENTE','Por ejecutar'],['Gestionar\nexcepción','RF-006\nManual','Contingencia','PENDIENTE','Por ejecutar']].forEach((row,j)=>row.forEach((t,i)=>{node(t,cols[i],2.4+j*1.13,cw,.9,j>0);if(i<4)line(cols[i]+cw,2.85+j*1.13,cols[i+1]-.04,2.85+j*1.13,C.secondary,j>0,true);}));
text('Requisitos existentes; los hallazgos posteriores refinan condiciones',.6,5.99,12.13,.4,22,C.text,true);label('RF-006 conserva Should / Aprobado. Aprobado ≠ implementado.',.6,6.39,12.13);

start(6,'Maestro v1.9.2, §4 / §9 · Matriz, pp.2–4 · Informe I2, §3');
badge('IMPLEMENTADO',.6,1.72,4,C.closed);badge('PROCESAMIENTO PENDIENTE',6,1.72,6.7);
card('I1 · Identidad y asociaciones','Equipo ↔ Asignación ↔ Etiqueta\nSQLite / JDBC',.6,2.35,4.85,1.55);
card('I2 · Entrada de lecturas','Fuente común → LecturaEntradaRFID',.6,4.25,4.85,1.4);
node('LecturaRFID persistida + sesión',6.05,2.35,6.65,.9,true);node('Eventos / deduplicación / historial',6.05,3.73,6.65,.9,true);node('Verificación / sustitución / contingencia',6.05,5.1,6.65,.9,true);
line(5.45,3.12,5.75,3.12,C.pending,true);line(5.45,4.95,5.75,4.95,C.pending,true);line(5.75,4.95,5.75,2.8,C.pending,true);line(5.75,2.8,6.05,2.8,C.pending,true,true);line(9.38,3.25,9.38,3.69,C.pending,true,true);line(9.38,4.63,9.38,5.06,C.pending,true,true);
label('EPC: identificador de etiqueta · Sesión: contexto de una operación',.6,6.27,12.13);

start(7,'Registro v1.9.2: DEC-022 · pom.xml · Informe I1, pp.3–4');
label('EN EJECUCIÓN · LÓGICA Y DATOS',.6,1.65,12.13,C.accent);
const techX=[.6,4.79,8.98],techW=3.75;
const runtimeTech=[['Java 21 LTS','Lógica y casos de uso','LTS: soporte prolongado'],['JDBC','Conexión a la base de datos','Acceso desde Java'],['SQLite','Persistencia local','Base de datos en archivo']];
runtimeTech.forEach((a,i)=>{const x=techX[i],dark=i===0;box(x,2.16,techW,1.95,dark?C.text:'FFFFFF',dark?C.text:'DCE6D9');box(x+.18,2.29,.64,.64,'FFFFFF','FFFFFF');techLogo(i===2?'sqlite':'java',x+.23,2.34,.54,.54);text(a[0],x+.98,2.37,techW-1.2,.5,24,dark?'FFFFFF':C.text,true);text(a[1],x+.22,3.0,techW-.44,.65,20,dark?'FFFFFF':C.text);text(i===1?'API de conexión desde Java':a[2],x+.22,3.69,techW-.44,.32,18,dark?'DDEBD2':C.secondary);if(i<2)line(x+techW,3.13,techX[i+1]-.02,3.13,C.accent,false,true);});
label('PARA CONSTRUIR Y VERIFICAR',.6,4.33,12.13,C.accent);
const supportTech=[['Maven','Compilar y empaquetar'],['JUnit 5','Pruebas automatizadas'],['Flyway','Versionar migraciones']];
supportTech.forEach((a,i)=>{const x=techX[i];box(x,4.84,techW,1.12,'E8F1E2','E8F1E2');techLogo(['maven','junit','flyway'][i],x+.18,4.95,1.05,.43);text(a[0],x+1.4,4.97,techW-1.6,.42,24,C.accent,true);text(a[1],x+.22,5.48,techW-.44,.3,18,C.text);});
label('Stack implementado en software; integración física RFID pendiente',.6,6.34,12.13,C.accent);

start(8,'Propuesta, p.4 · Maestro v1.9.2, §4 · Informe I2, §§6–9');
['EPC A','EPC A','EPC A'].forEach((t,i)=>node(t,.6+i*1.55,2.0,1.35,.8));
text('Entrada: tres observaciones',5.28,2.07,7.45,.6,26,C.accent,true);
flow(['Persistencia','Contexto','Deduplicación','Evento'],.6,3.24,12.13,{pending:true});
label('Procesamiento operativo PENDIENTE',.6,4.35,12.13);
text('La entrada conserva repeticiones; el evento exige una regla',.6,4.91,12.13,.65,26,C.text,true);
label('Verificación: esperados vs. detectados · Sustitución: reemplazo con cierre',.6,5.77,12.13);
label('Contingencia: excepción manual explícita · Ejemplo didáctico',.6,6.24,12.13);

start(9,'Informe de cierre I1 v0.2, pp.2–5 · Matriz: RF-001 · SOFTWARE');
flow(['Equipo','Asignación\nEtiqueta','EtiquetaRFID'],.6,1.85,8.45);
node('Anterior: cerrada en t',.6,3.28,3.9,.85);node('Nueva: vigente desde t',5.15,3.28,3.9,.85);line(4.5,3.71,5.1,3.71,C.accent,false,true);
text('32',9.6,2.08,3.1,1.12,64,C.accent,true);text('pruebas automatizadas\nSOFTWARE',9.6,3.1,3.1,1.3,22,C.text,true);
box(.6,4.71,12.13,1.18,'E8F1E2','E8F1E2');text('Cerrar anterior + crear nueva\nUna transacción · Misma fecha/hora · Rollback probado',.85,4.86,11.6,.85,24,C.text,true);
label('I1 CERRADO · RF-001 IMPLEMENTADO · Historial de asociaciones, no de movimientos',.6,6.2,12.13);

start(10,'Maestro v1.9.2, §4 · Matriz: TEC-002 · Informe I2, §§4–9');
node('FuenteLecturasRFID · interfaz implementada',3.18,1.8,7,.72);
node('FuenteSimulada',.6,3.1,5.42,.8);node('Fuente UHF real',7.3,3.1,5.43,.8,true);
label('implementa',1.8,2.57,3,C.accent);label('implementará',10.35,2.57,2.38);line(3.31,3.08,4.3,2.54,C.accent,false,true);line(10.0,3.08,9.3,2.54,C.pending,true,true);
node('LecturaEntradaRFID',3.18,4.57,7,.72);line(3.3,3.9,4.5,4.53,C.accent,false,true);line(10,3.9,8.8,4.53,C.pending,true,true);
label('EPC · timestamp · origen · metadata opcional',3.18,5.48,8);
text('I2 CERRADO · SIMULACIÓN',.6,6.05,6.9,.42,22,C.closed,true);
text('TEC-002 EN DESARROLLO:\nfalta segunda fuente',7.45,5.88,5.28,.7,18,C.secondary);

start(11,'Maestro v1.9.2, §5 / §7 · Registro: DEC-019 / DEC-021');
subtitle('P1 = hardware e integración UHF · EN CURSO');
card('SDK inspeccionado','Librerías del fabricante\nAnálisis estático completado',.6,2.35,3.82,1.67);
card('Prueba funcional','Java 21 + unidad/firmware\nConfiguración PENDIENTE',4.75,2.35,3.82,1.67,{pending:true});
card('EPC reproducible','Hardware autorizado\nRF_REAL PENDIENTE',8.9,2.35,3.82,1.67,{pending:true});
text('RC522 · HF',.6,4.62,4.8,.48,26,C.limit,true);text('Proyecto · UHF pasivo',6.88,4.62,5.84,.48,26,C.accent,true);
text('Incompatible con las\netiquetas UHF del proyecto',.6,5.3,5.65,.78,22);text('U300: candidato provisional\nSelección final CONDICIONADA',6.88,5.3,5.84,.78,22);

start(12,'Maestro v1.9.2, §6 / §9.1 · Imagen aportada: apoyo interpretativo');
imagePlano(.6,1.72,6.7,4.5324);
text('P2 · Definir el punto\nEN CURSO',7.65,1.75,5.08,.9,26,C.accent,true);
text('Ventanilla → preparación\n→ dotación',7.65,2.95,5.08,.75,22,C.text,true);
text('Flujo preliminar; rutas por confirmar',7.65,3.85,5.08,.7,22,C.progress);
text('Banda + puerta: dos antenas solo si forman un único punto autorizado',7.65,4.75,5.08,1.13,22,C.text,true);
label('Antena ≠ dirección del movimiento',7.65,6.02,5.08);
label('Detalle sin escala verificada; no demuestra cobertura RF',.6,6.34,12.13);

start(13,'Maestro v1.9.2, pp.2–5 · Matriz, pp.3–5 · Informe I2, §9');
comparison('YA DEMOSTRADO','TODAVÍA PENDIENTE');
text('SOFTWARE\nAsociación, historial y rollback',.85,2.65,3.55,1.25,22,C.text,true);
text('SIMULACIÓN\nContrato, orden, repeticiones y origen',.85,3.99,3.55,1.08,22,C.text,true);
text('32 + 25 = 57',.85,5.38,3.55,.5,30,C.accent,true);label('0 fallos / errores / omitidas',.85,5.93,3.55);
[['Captura','EPC UHF RF_REAL reproducible'],['RF físico','Omisiones, lecturas externas, interferencia y superficies/materiales relevantes'],['Preparación','Arquitectura física congelada;\nP3 = criterios de aceptación'],['Ejecución','Pruebas controladas físicas y piloto']].forEach((a,i)=>{const y=[2.65,3.35,4.62,5.6][i];text(a[0],5.2,y,1.9,.55,20,C.accent,true);text(a[1],7.17,y,5.33,[.65,1.1,.75,.65][i],20);});
label('Evidencia previa de software/simulación; fase formal de Pruebas pendiente',.6,6.34,12.13);

start(14,'Propuesta, pp.5–9 · Maestro v1.9.2, §9 · Matriz: VAL-003 a VAL-006');
label('SOFTWARE',.6,1.72,2.5,C.accent);flow(['Diseñar lectura–sesión','Completar MVP'],3.2,1.7,9.53,{pending:true});
text('CONDICIONES\nFÍSICAS',.6,2.98,2.5,.8,18,C.accent);flow(['P1 / P2','Prepruebas RF_REAL','P3: criterios'],3.2,2.96,9.53,{pending:true});
label('Ambos frentes preparan las pruebas:',.6,4.02,12.13);
flow(['Pruebas controladas','Piloto único','Validación'],.6,4.61,12.13,{pending:true});
label('≥10 repeticiones + verdad de terreno · Cero eventos duplicados: criterio',.6,5.91,12.13);
label('Metas RF/tiempo tras prepruebas · Piloto pendiente, condicionado a hardware y permisos',.6,6.31,12.13);

slide=pptx.addSlide();slide.background={color:C.background};
title('La red conecta requisitos, desarrollo y evaluación');
footer('Red de precedencias · Maestro v1.9.2, §2 / §9 · Matriz, requisitos y P0–P3');phase();
label('REQUISITOS',.6,1.78,2.15,C.accent);
label('DISEÑO → MVP',3.0,1.78,5.95,C.accent);
label('EVALUACIÓN',9.5,1.78,3.23,C.accent);
function projectNode(head,detail,x,y,w,h,state){const done=state==='closed';box(x,y,w,h,done?'E8F1E2':'FFFFFF',done?C.accent:'98AB93',!done);text(head,x+.12,y+.12,w-.24,.65,20,C.text,true,{align:'center'});text(detail,x+.12,y+h-.43,w-.24,.32,18,done?C.closed:C.secondary,false,{align:'center'});}
// One fork separates software work from physical readiness; one join precedes tests.
line(2.45,3.77,2.7,3.77,C.accent);line(2.7,3.08,2.7,4.8,C.accent);line(2.7,3.08,3,3.08,C.accent,false,true);line(2.7,4.8,3,4.8,C.pending,true,true);
line(5.6,3.08,6.1,3.08,C.pending,true,true);line(5.6,4.8,6.1,4.8,C.pending,true,true);
line(8.95,3.08,9.2,3.08,C.pending,true);line(8.95,4.8,9.2,4.8,C.pending,true);line(9.2,3.08,9.2,4.8,C.pending,true);line(9.2,3.77,9.5,3.77,C.pending,true,true);
line(10.2,4.39,10.2,5.03,C.pending,true,true);line(10.7,5.48,11,5.48,C.pending,true,true);
projectNode('P0\nRequisitos','CERRADO',.6,3.14,1.85,1.27,'closed');
projectNode('I1 + I2\nSoftware','CERRADOS',3,2.45,2.6,1.27,'closed');
projectNode('I3 + MVP\nLectura y sesión','PENDIENTE',6.1,2.45,2.85,1.27,'pending');
projectNode('P1 hardware\nP2 punto','EN CURSO',3,4.16,2.6,1.27,'progress');
projectNode('Prepruebas → P3\nFijar criterios','PENDIENTE',6.1,4.16,2.85,1.27,'pending');
projectNode('Pruebas controladas\nSoftware + físicas','PENDIENTE',9.5,3.14,3.23,1.27,'pending');
node('Piloto',9.5,5.05,1.2,.85,true);node('Validación',11,5.05,1.73,.85,true);
label('Red tipo PERT/CPM: dependencias generales; sin duraciones ni ruta crítica calculada',.6,6.31,12.13);
slide.addNotes('La red se lee de izquierda a derecha. P0 representa la base suficiente de requisitos para Diseño, ya cerrada. La bifurcación muestra dos frentes de preparación: el software, con I1/I2 cerrados y el diseño dirigido de I3 y funciones restantes del MVP pendientes; y las condiciones físicas P1/P2, todavía en curso. La rama física conduce a prepruebas y P3, que fija umbrales después de prepruebas. Ambas condiciones confluyen antes de las pruebas controladas, seguidas de piloto único y validación. El ramal no obliga a esperar al cierre completo de software para trabajar en hardware ni representa una fecha de comienzo del frente físico. Diseño → MVP → Pruebas → Piloto → Validación es la secuencia formal; la fase vigente sigue siendo Diseño. Requisitos funcionales: asociación, captura, contexto/eventos, historial, verificación, sustitución y contingencia; requisitos técnicos/datos: fuente común, hardware, persistencia y separación lectura-evento; validación/operación: criterios, verdad de terreno y punto autorizado. Los nodos agregan tareas para dar una vista general, no fijan nuevas precedencias de detalle. PERT es una técnica de evaluación y revisión de programas; CPM es el método del camino crítico. Esta lámina adopta la representación por dependencias y no calcula PERT probabilístico, duraciones, holguras ni camino crítico: faltan estimaciones de actividades y precedencias detalladas validadas. No atribuir criticidad matemática al color. Fuente: Maestro v1.9.2 §§2/9; Matriz v1.9.2 §§2/5 y Propuesta, secuencia metodológica.');

start(15,'Maestro v1.9.2, §9 · Propuesta, pp.6 y 9 · Propuestas para la reunión');
[['Diseño I3','Revisar LecturaRFID–SesionOperacion'],['P1 / P2','Concretar responsables, hardware y único punto'],['Validación','Revisar protocolo y criterios tras prepruebas']].forEach((a,i)=>{text(a[0],.6,1.98+i*1.13,2.65,.65,26,C.accent,true);text(a[1],3.53,1.98+i*1.13,9.2,.65,24);line(.6,2.85+i*1.13,12.73,2.85+i*1.13,'DCE6D9');});
text('Hay avance de software verificable;\nla validez física y operativa aún debe demostrarse',.6,5.56,12.13,.94,28,C.text,true);

for(let i=0;i<decisions.length;i+=5)glossary('Anexo · Decisiones: qué se aprobó y con qué alcance',decisions.slice(i,i+5),'Registro de Decisiones v1.9.2, p.2 · Consulta: '+(i+1)+'–'+Math.min(i+5,decisions.length),'DEC significa decisión del Registro. Son resúmenes explicativos del índice compacto vigente; prevalecen el texto y las condiciones del Registro. Aprobada no significa ejecutada. DEC-002 y DEC-004 fueron reemplazadas, respectivamente, por DEC-007 y DEC-008. El presupuesto es la línea base, no gasto realizado. DEC-023 no aprueba otra arquitectura, otro punto ni integración AM.');
for(let i=0;i<requirements.length;i+=5)glossary('Anexo · Requisitos: qué debe cumplir el proyecto',requirements.slice(i,i+5),'Matriz de Requisitos v1.9.2, pp.2–3 · Consulta: '+(i+1)+'–'+Math.min(i+5,requirements.length),'Indispensable corresponde a Must; deseable a Should. La prioridad no modifica el estado. Aprobado significa requisito aceptado, no implementado. Las condiciones completas de evidencia están en la Matriz v1.9.2, §§2–7. TEC-001 incluye banda/protocolo, SDK, puertos, firmware, cables, etiquetas y disponibilidad real. OPE-002 incluye horario, usuarios, equipos, red, energía, autorización, regla de evento y contingencia. VAL-005 requiere RF_REAL y verdad de terreno. VAL-006 orienta el piloto; cualquier ajuste por restricciones debe justificarse antes de iniciarlo. El piloto y la evidencia física RFID siguen pendientes.');
fs.mkdirSync('output',{recursive:true});
fs.writeFileSync('presentation/build/layout_manifest.json',JSON.stringify(manifest,null,2));
await pptx.writeFile({fileName:'output/Proyecto_EMI_Avance_Asesor.pptx'});
// Native, reversible PowerPoint crop: original image pixels remain embedded.
const JSZip=require(process.env.EMI_NODE_MODULES ? path.join(process.env.EMI_NODE_MODULES,'jszip') : 'jszip');
const deckPath='output/Proyecto_EMI_Avance_Asesor.pptx';
const zip=await JSZip.loadAsync(fs.readFileSync(deckPath));
for(const name of ['ppt/slides/slide7.xml','ppt/slides/slide15.xml']){let xml=await zip.file(name).async('string');xml=xml.replace(/<a:stretch>/g,'<a:srcRect l="1783" t="8203" r="7308" b="42582"/><a:stretch>');zip.file(name,xml);}
fs.writeFileSync(deckPath,await zip.generateAsync({type:'nodebuffer'}));
console.log('Generated editable slides with speaker notes and native plan crop');
