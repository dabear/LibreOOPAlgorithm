java -jar f:\Users\nirit\Downloads\apktool_2.2.2.jar d -f -o smali/ app\build\outputs\apk\debug\app-debug.apk
copy F:\Users\nirit\take2\libre\smaly\smali\lib\arm64-v8a\libDataProcessing.so dir\lib\arm64-v8a\libDataProcessing.so
copy F:\Users\nirit\take2\libre\smaly\smali\lib\armeabi\libDataProcessing.so dir\lib\armeabi\libDataProcessing.so
copy F:\Users\nirit\take2\libre\smaly\smali\lib\armeabi-v7a\libDataProcessing.so dir\lib\armeabi-v7a\libDataProcessing.so



del MyLibre.unaligned.apk MyLibre.smali.apk
del -f smali/build

java -jar f:\Users\nirit\Downloads\apktool_2.2.2.jar b -f smali/ -o MyLibre.unaligned.apk
"%JAVA_HOME%\bin\jarsigner" -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore debug.keystore  -storepass android  MyLibre.unaligned.apk  androiddebugkey
c:\Users\nirit\AppData\Local\Android\android-studio\sdk\build-tools\22.0.1\zipalign.exe -v 4 MyLibre.unaligned.apk MyLibre.smali.apk
del -f smali/build



copy C:\Users\Nirit\take3\libre\OOPalgorithm\app\build\outputs\apk\debug\app-debug.apk .

rmdir /s dir
md dir
cd dir

7z x ..\app-debug.apk
rmdir /s META-INF

copy F:\Users\nirit\take2\libre\smaly\smali\lib\arm64-v8a\libDataProcessing.so lib\arm64-v8a\libDataProcessing.so
copy F:\Users\nirit\take2\libre\smaly\smali\lib\armeabi\libDataProcessing.so lib\armeabi\libDataProcessing.so
copy F:\Users\nirit\take2\libre\smaly\smali\lib\armeabi-v7a\libDataProcessing.so lib\armeabi-v7a\libDataProcessing.so

7z -tzip a apk.aunaligned.zip
move apk.aunaligned.zip ..
cd ..

"C:\Program Files\Android\Android Studio\jre\bin\jarsigner.exe"  -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore debug.keystore  -storepass android  apk.aunaligned.zip androiddebugkey
move apk.aunaligned.zip apk.aunaligned.apk

