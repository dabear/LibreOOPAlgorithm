#!/usr/bin/env bash

#
# Requirements:
# 1) curl must be installed


RED='\033[0;31m'
NC='\033[0m' # No Color

function do-sha1-check {
  shasum "$1" 2>/dev/null || sha1sum "$1"
}

function doexit {
  printf "${RED}Aborting!${NC} $1"
  exit -1
}

export -f do-sha1-check

apkPureStartPage='https://apkpure.com/librelink/com.librelink.app/download?from=details';
apkPureLocalPage='LibreLink/LibreLink.latest.html';
apkSystemLocation="$HOME/LibreLink_v1.3.2.4_apkpure.com.apk"
function download-start-page() {
    # Does wget on https://apkpure.com/librelink/com.librelink.app/download?from=details
    # This brings a page that has a link to the place that we are looking for.
    curl $apkPureStartPage --create-dirs -o $apkPureLocalPage  2> /dev/null
}

function get-download-location(){
  cat $apkPureLocalPage|grep 'id="download_link"'| grep '">click' | sed -E 's/.*href="(.*)".*/\1/'
}

function download-apk(){
  url=$1
  curl -L -o  "$apkSystemLocation" "$url"
}

function verify-filesize() {
  filename=$1
  bdir=$(dirname "$filename")
  bname=$(basename "$filename")
  find "$bdir" -maxdepth 1 -name "$bname" -size +12M
}

tmpdir=$(mktemp -d)
function finish {
  rm -rf "$tmpdir"
}
trap finish EXIT

#set -x
function runit(){
  pushd "$tmpdir"
  download-start-page
  location=$(get-download-location)
  echo "Downloading apk"
  download-apk "$location"
  apk=$(verify-filesize "$apkSystemLocation")
  if [ "x${#apk}" == "x0" ] ; then
    doexit "Could not download local LibreLink apk in $HOME. Please manually download LibreLink_v1.3.2.4_apkpure.com.apk with sha1sum 56baf72651def0e562590b406893e4f0e315b1cf Before continuing!"
  else
    echo  "Found local librelink on path $apk"
  fi
  popd

}

runit
