

rem Download apk file
rem ==============
mkdir LibreLink
if exist LibreLink\apk goto apk_unzipped

if exist LibreLink\LibreLink_v1.3.2.4_apkpure.com.apk goto apk_downloaded

cscript scripts\DownloadApk.js
if errorlevel 1 (
    echo "Error downloading apk"
    goto Exit
)

:apk_downloaded

mkdir LibreLink\apk
tools\windows\7z -oLibreLink\apk x LibreLink\LibreLink_v1.3.2.4_apkpure.com.apk


:apk_unzipped

rem Copy the commited apk, and manipulate it
rem ==========================
rmdir /Q /s temp
mkdir temp

copy apk\app-debug.apk temp

rmdir /Q /s temp\dir
md temp\dir

tools\windows\7z x -otemp\dir temp\app-debug.apk
rmdir /Q /s temp\dir\META-INF

copy LibreLink\apk\lib\arm64-v8a\libDataProcessing.so temp\dir\lib\arm64-v8a\libDataProcessing.so
copy LibreLink\apk\lib\armeabi\libDataProcessing.so temp\dir\lib\armeabi\libDataProcessing.so
copy LibreLink\apk\lib\armeabi-v7a\libDataProcessing.so temp\dir\lib\armeabi-v7a\libDataProcessing.so
copy LibreLink\apk\lib\x86\libDataProcessing.so temp\dir\lib\x86\libDataProcessing.so
copy LibreLink\apk\lib\x86_64\libDataProcessing.so temp\dir\lib\x86_64\libDataProcessing.so



copy LibreLink\LibreLink_v1.3.2.4_apkpure.com.apk temp\dir\res\raw\original_apk

cd temp\dir
..\..\tools\windows\7z -tzip a apk.aunaligned.zip
cd ..\..
move temp\dir\apk.aunaligned.zip temp

"C:\Program Files\Android\Android Studio\jre\bin\jarsigner.exe"  -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore tools\windows\debug.keystore  -storepass android  temp\apk.aunaligned.zip androiddebugkey
move temp\apk.aunaligned.zip LibreOOPAlgorithm.apk

:Exit