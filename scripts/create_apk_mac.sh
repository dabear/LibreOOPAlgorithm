#!/usr/bin/env bash

#
#Requirements:
# 1) download jdk first to make sure the "jarsigner" command  is available
# 2) Make sure wget command is installed. If not, install homebrew then do "brew install wget"
#
RED='\033[0;31m'
NC='\033[0m' # No Color

function download-librelink {
  wget -O "$1" "https://download.apkpure.com/b/apk/Y29tLmxpYnJlbGluay5hcHBfMTA2NTdfNzlkZGIyZDc?_fn=TGlicmVMaW5rX3YxLjMuMi40X2Fwa3B1cmUuY29tLmFwaw%3D%3D&k=322c052d0f9c34b28c1f67bc88de53c15a0fe122&as=2f14426570424edf58d775b630faf2645a0d3e9a&_p=Y29tLmxpYnJlbGluay5hcHA%3D&c=1%7CMEDICAL"
  sha1sum=$(shasum "$1" |awk  '{print $1}')
  if [ "$sha1sum" == "56baf72651def0e562590b406893e4f0e315b1cf" ] ; then
    return 0
  else
    return -1
  fi
}

function find-local-librelink {
  find $HOME -type f -not -path '*/\.*' -name '*.apk' -exec shasum {} + | grep '^56baf72651def0e562590b406893e4f0e315b1cf' | awk '{print $2}'
  
  
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

mkdir -p "$bdir/LibreLink/apk"




apk=$(find-local-librelink)
if [ "x${#apk}" == "x0" ] ; then
    echo "Could not find librelink in $HOME, trying to download from remote server"
    #not found, trying to redownload
    apk="$HOME/LibreLink_v1.3.2.4.apk"
    download-librelink "$apk"
    
    if [ $? -ne 0 ]; then
      doexit "Could not find local LibreLink apk in $HOME. Download of librelink with sha1sum 56baf72651def0e562590b406893e4f0e315b1cf also failed!"

    fi
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

cp "$bdir/LibreLink/LibreLink_v1.3.2.4_apkpure.com.apk" "$bdir/temp/dir/res/raw/"

pushd "$bdir/temp/dir"
zip -r -X apk.aunaligned.zip .
popd 

echo "Moving unaligned apk!"

mv "$bdir/temp/dir/apk.aunaligned.zip" "$bdir/temp"

jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore "tools/windows/debug.keystore"  -storepass android  "$bdir/temp/apk.aunaligned.zip" androiddebugkey


if [ $? -ne 0 ]; then
    doexit "Could not sign jar! See error message above!"

fi

mv $bdir/temp/apk.aunaligned.zip $HOME/apk.aunaligned.apk
echo "Finished creating modified apk,  please install this file on your phone: $HOME/apk.aunaligned.apk"



rm -rf "$bdir"
