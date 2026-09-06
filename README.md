# Proyecto EMI

Prototipo de trazabilidad RFID UHF pasivo para equipos biomédicos. Este repositorio se encuentra en preparación documental: todavía no contiene código, dependencias, base de datos ni pruebas. I1 requiere autorización posterior del usuario.

Consultar [fuentes de verdad](docs/FUENTES_DE_VERDAD.md) y [reglas de desarrollo](AGENTS.md). Los documentos originales permanecen locales en `docs/control_local`, `docs/soporte` y `docs/anexos`, excluidos de Git. No estarán disponibles al clonar el repositorio.

## Enlace manual con una cuenta personal

Git local y la cuenta del alojamiento son independientes. Este repositorio se inicializa en `main`, sin remoto ni commit inicial. No se cambia la identidad Git del usuario.

En PowerShell, con Git disponible, sustituir los valores de ejemplo:

```powershell
Set-Location C:\Proyecto_EMI
git config --local user.name "TU NOMBRE"
git config --local user.email "TU CORREO DE COMMITS"
git status --short --ignored
git add .gitignore AGENTS.md README.md docs/FUENTES_DE_VERDAD.md
git diff --cached --stat
git diff --cached
git commit -m "Preparar fuentes documentales e instrucciones del proyecto"
```

La identidad de autor no inicia sesión en una cuenta. Para GitHub, crear manualmente un repositorio vacío en la cuenta personal, sin README, licencia ni gitignore iniciales. Después reemplazar USUARIO y REPOSITORIO por los propios:

```powershell
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git remote -v
git push -u origin main
```

Completar la autenticación personal cuando Git la solicite; no guardar tokens dentro del repositorio ni de la URL. Puede usarse en su lugar la URL SSH de la cuenta si ya está configurada.

Antes de publicar, revisar el contenido del commit. `.gitignore` excluye las referencias locales, pero no elimina información que se haya incorporado previamente al historial o copiado a otro archivo.
