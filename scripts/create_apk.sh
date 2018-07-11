#!/usr/bin/env bash

#
# Requirements:
# 1) download jdk first to make sure the "jarsigner" command  is available
# 1b) on ubuntu: see https://launchpad.net/~webupd8team/+archive/ubuntu/java

RED='\033[0;31m'
NC='\033[0m' # No Color


function find-local-librelink {
  find $HOME -type f -not -path '*/\.*' -iname 'librelink*.apk' -size +12M | head -1
  
}

function doexit {
  printf "${RED}Aborting!${NC} $1"
  exit -1
}

bdir=$(mktemp -d)
function finish {
  rm -rf "$bdir"
}
trap finish EXIT


jarsigner &> /dev/null || doexit "'jarsigner' command not found"

mkdir -p "$bdir/LibreLink/apk"


apk=$(find-local-librelink 2> /dev/null)
if [ "x${#apk}" == "x0" ] ; then
  doexit "Could not find local LibreLink apk in $HOME. Please manually download LibreLink_v1.3.2.4_apkpure.com.apk with sha1sum 56baf72651def0e562590b406893e4f0e315b1cf Before continuing!"
else
  echo  "Found local librelink on path $apk"
fi
  
echo "Copying librelinkapk to $bdir/LibreLink/"
cp "$apk" "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk"


echo "unzipping librelinkapk"
unzip "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk" -d "$bdir/LibreLink/apk"

mkdir -p "$bdir/temp/dir/lib/"{arm64-v8a,armeabi,armeabi-v7a,"x86"}

mkdir -p "$bdir/temp/dir/res/raw/"
cp "apk/app-debug.apk" "$bdir/temp/"
unzip  "$bdir/temp/app-debug.apk" -d "$bdir/temp/dir"
rm -rf "$bdir/temp/dir/META-INF"

  

cp "$bdir/LibreLink/apk/lib/arm64-v8a/libDataProcessing.so" "$bdir/temp/dir/lib/arm64-v8a/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/armeabi/libDataProcessing.so" "$bdir/temp/dir/lib/armeabi/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/armeabi-v7a/libDataProcessing.so" "$bdir/temp/dir/lib/armeabi-v7a/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/x86/libDataProcessing.so" "$bdir/temp/dir/lib/x86/libDataProcessing.so"
cp "$bdir/LibreLink/apk/lib/x86_64/libDataProcessing.so" "$bdir/temp/dir/lib/x86_64/libDataProcessing.so"

cp "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk" "$bdir/temp/dir/res/raw/original_apk"

pushd "$bdir/temp/dir"
zip -r -X apk.aunaligned.zip .
popd 

echo "Moving unaligned apk!"

mv "$bdir/temp/dir/apk.aunaligned.zip" "$bdir/temp"

jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore "tools/windows/debug.keystore"  -storepass android  "$bdir/temp/apk.aunaligned.zip" androiddebugkey


if [ $? -ne 0 ]; then
    doexit "Could not sign jar! See error message above!"

fi

mv $bdir/temp/apk.aunaligned.zip $HOME/LibreOOPAlgorithm.apk
echo "Finished creating modified apk,  please install this file on your phone: $HOME/LibreOOPAlgorithm.apk"



rm -rf "$bdir"
