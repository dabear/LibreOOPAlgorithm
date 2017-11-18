#!/usr/bin/env bash
function download-librelink {
  wget -O "$1" "https://download.apkpure.com/b/apk/Y29tLmxpYnJlbGluay5hcHBfMTA2NTdfNzlkZGIyZDc?_fn=TGlicmVMaW5rX3YxLjMuMi40X2Fwa3B1cmUuY29tLmFwaw%3D%3D&k=322c052d0f9c34b28c1f67bc88de53c15a0fe122&as=2f14426570424edf58d775b630faf2645a0d3e9a&_p=Y29tLmxpYnJlbGluay5hcHA%3D&c=1%7CMEDICAL"
  sha1sum=$(shasum "$1" |awk  '{print $1}')
  #if [ "$sha1sum" == "6baf72651def0e562590b406893e4f0e315b1cf" ] ; then
  if [ "$sha1sum" == "56baf72651def0e562590b406893e4f0e315b1cf" ] ; then
    return 0
  else
    return -1
  fi
}

bdir=$(mktemp -d)

mkdir -p "$bdir/LibreLink/apk"
download-librelink "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk"

if [ $? -ne 0 ]; then
  echo "Aborting, could not download librelink with sha1sum 6baf72651def0e562590b406893e4f0e315b1cf"
  exit -1
fi

unzip "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk" -d "$bdir/LibreLink/apk"

mkdir -p "$bdir/temp/dir/lib/arm64-v8a"
mkdir -p "$bdir/temp/dir/lib/armeabi"
mkdir -p "$bdir/temp/dir/lib/armeabi-v7a"
mkdir -p "$bdir/temp/dir/lib/x86"
mkdir -p "$bdir/temp/dir/res/raw/"
cp "apk/app-debug.apk" "$bdir/temp/"
unzip  "$bdir/temp/app-debug.apk" -d "$bdir/temp/dir"
rm -rf "$bdir/temp/dir/META-INF"

cp "$bdir/LibreLink/apk/lib/arm64-v8a/libDataProcessing.so" "$bdir/temp/dir/lib/arm64-v8a/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/armeabi/libDataProcessing.so" "$bdir/temp/dir/lib/armeabi/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/armeabi-v7a/libDataProcessing.so" "$bdir/temp/dir/lib/armeabi-v7a/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/x86/libDataProcessing.so" "$bdir/temp/dir/lib/x86/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/x86_64/libDataProcessing.so" "$bdir/temp/dir/lib/x86_64/libDataProcessing.so"

cp "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk" "$bdir/temp/dir/res/raw/"

cd "$bdir/temp/dir"
zip -r -X apk.aunaligned.zip .
cd "$bdir"

echo "moving modified apk!"
set -x
mv "$bdir/temp/dir/apk.aunaligned.zip" "$bdir/temp"

##
## TODO:
## 
#"C:\Program Files\Android\Android Studio\jre\bin\jarsigner.exe"  -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore tools\windows\debug.keystore  -storepass android  temp\apk.aunaligned.zip androiddebugkey
#move temp\apk.aunaligned.zip apk.aunaligned.apk
#rm -rf "$bdir"
