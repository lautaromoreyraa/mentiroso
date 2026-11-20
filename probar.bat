@echo off
cd /d "%~dp0"
call "%~dp0compilar.bat" || exit /b 1
java -cp out verificador.PruebaMisEstructuras || exit /b 1
if exist src\pruebas (
    for %%f in (src\pruebas\Prueba*.java) do (
        java -cp out pruebas.%%~nf || exit /b 1
    )
)
