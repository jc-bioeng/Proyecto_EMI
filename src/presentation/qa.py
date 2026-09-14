"""Structural checks supplement (never replace) manual visual/source review."""
import hashlib,json,pathlib,re,zipfile
from lxml import etree
from pypdf import PdfReader
r=pathlib.Path.cwd()
z=zipfile.ZipFile(r/'output/Proyecto_EMI_Avance_Asesor.pptx')
ns={'a':'http://schemas.openxmlformats.org/drawingml/2006/main','p':'http://schemas.openxmlformats.org/presentationml/2006/main'}
s=sorted([n for n in z.namelist() if re.fullmatch(r'ppt/slides/slide\d+.xml',n)],key=lambda n:int(re.search(r'(\d+)\.xml',n)[1]))
n=[n for n in z.namelist() if re.fullmatch(r'ppt/notesSlides/notesSlide\d+.xml',n)]
full=''; shapes=0;pics=0;neg=[];notes=[];cropped=[]
for f in s+n:
 t=etree.fromstring(z.read(f)); text='\n'.join(t.xpath('//a:t/text()',namespaces=ns)); full+=f+'\n'+text+'\n'
 if f in s:
  shapes+=len(t.xpath('//p:sp',namespaces=ns));pics+=len(t.xpath('//p:pic',namespaces=ns))
  neg += [f for e in t.xpath('//a:ext',namespaces=ns) if any(int(e.get(v,'0'))<0 for v in ('cx','cy'))]
  if t.xpath('//a:srcRect',namespaces=ns):cropped.append(f)
 else:notes.append((int(re.search(r'(\d+)\.xml',f)[1]),text))
for f in z.namelist():
 if f.endswith('.xml'):etree.fromstring(z.read(f))
b=json.loads((r/'presentation/build/powerpoint_text_bounds.json').read_text(encoding='utf-8-sig'))
m=json.loads((r/'presentation/source_manifest.json').read_text(encoding='utf-8-sig'))
l=json.loads((r/'presentation/build/layout_manifest.json').read_text(encoding='utf-8'))
phrases=['RFID validado','RFID implementado completamente','piloto realizado','P1 cerrado','RF_REAL probado','integración AM implementada']
res={'slides':len(s),'notes':len(n),'pdf_pages':len(PdfReader(r/'output/Proyecto_EMI_Avance_Asesor.pdf').pages),'rendered_png':len(list((r/'output/rendered_slides').glob('*.PNG'))),'native_shapes':shapes,'picture_placements':pics,'native_crops':cropped,'negative_extents':neg,'text_overflow':[v for v in b if v['boundHeight']>v['height']+2 or v['boundWidth']>v['width']+2],'body_below_18pt':[v for v in l if v.get('type')=='text' and v['size']<18 and v['y']<6.8],'source_count':len(m['sources']),'source_hash_changes':[v['path'] for v in m['sources'] if hashlib.sha256((r/v['path']).read_bytes()).hexdigest()!=v['sha256']],'phrase_hits':{p:[full[max(0,a.start()-80):a.end()+100] for a in re.finditer(re.escape(p),full,re.I)] for p in phrases}}
(r/'presentation/build/final_text.txt').write_text(full,encoding='utf-8')
(r/'presentation/build/qa_checks.json').write_text(json.dumps(res,ensure_ascii=False,indent=2),encoding='utf-8')
(r/'output/speaker_notes_final.md').write_text('# Notas incorporadas a la presentación final\n\n'+ '\n\n'.join('## Diapositiva '+str(i)+'\n\n'+t for i,t in sorted(notes)),encoding='utf-8')
print(json.dumps(res,ensure_ascii=False,indent=2))
assert len(s)==len(n)==res['pdf_pages']==res['rendered_png']==30
assert not neg and not res['text_overflow'] and not res['source_hash_changes']
