@echo off
REM Script para generar una imagen de aplicacion de Adsobalin

REM Configuracion de variables
set "APP_NAME=Adsobalin"
set "APP_VERSION=1.0.0"
set "VENDOR=Omwekiatl"
set "MAIN_JAR=Adsobalin.jar"
set "MAIN_CLASS=logic.interfaz.Adsobalin"
set "ICON_PATH=src\assets\icono.ico"
set "JAVA_FX_PATH=C:\Program Files\Java\javafx-sdk-17.0.13\lib"
set "INPUT_DIR=dist"
set "OUTPUT_DIR=result"

REM Crear la imagen de aplicacion
jpackage ^
  --type app-image ^
  --name "%APP_NAME%" ^
  --input "%INPUT_DIR%" ^
  --main-jar "%MAIN_JAR%" ^
  --main-class "%MAIN_CLASS%" ^
  --icon "%ICON_PATH%" ^
  --java-options "--module-path \"%JAVA_FX_PATH%\" --add-modules javafx.controls,javafx.fxml,javafx.media" ^
  --dest "%OUTPUT_DIR%" ^
  --vendor "%VENDOR%" ^
  --app-version "%APP_VERSION%" ^
  --description "shooter 2D multijugador LAN con NPCs"

pause
