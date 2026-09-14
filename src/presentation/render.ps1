$ErrorActionPreference='Stop'
$app=New-Object -ComObject PowerPoint.Application
$deck=$null
try {
 $deck=$app.Presentations.Open('C:\Proyecto_EMI\output\Proyecto_EMI_Avance_Asesor.pptx',-1,0,0)
 $deck.SaveAs('C:\Proyecto_EMI\output\Proyecto_EMI_Avance_Asesor.pdf',32)
 $deck.Export('C:\Proyecto_EMI\output\rendered_slides','PNG',1600,900)
 $checks=@()
 foreach($s in $deck.Slides){
  foreach($shape in $s.Shapes){
   if($shape.HasTextFrame -eq -1 -and $shape.TextFrame.HasText -eq -1){
    $range=$shape.TextFrame.TextRange
    $checks += [pscustomobject]@{slide=$s.SlideIndex;text=$range.Text;left=$shape.Left;top=$shape.Top;width=$shape.Width;height=$shape.Height;boundHeight=$range.BoundHeight;boundWidth=$range.BoundWidth}
   }
  }
 }
 $checks | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath 'C:\Proyecto_EMI\presentation\build\powerpoint_text_bounds.json' -Encoding utf8
 Write-Output ('Exported '+$deck.Slides.Count+' slides and PDF via PowerPoint')
} finally {
 if($null -ne $deck){$deck.Close()}
 $app.Quit()
 [System.Runtime.InteropServices.Marshal]::ReleaseComObject($app) | Out-Null
}
