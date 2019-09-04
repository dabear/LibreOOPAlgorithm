
var ApkPureStartPage = "https://apkpure.com/freestyle-librelink-us/com.freestylelibre.app.us/download?from=details";
var ApkPureLocalPage = "LibreLink\\LibreLink.latest.html";
var LibreLinkApk = "LibreLink\\LibreLink_us_latest_apkpure.com.apk";

function GetStartingPage() {
    // Does wget on https://apkpure.com/librelink/com.librelink.app/download?from=details
    // This brings a page that has a link to the place that we are looking for.
    var oShell = WScript.CreateObject("WScript.Shell");
    oExec = oShell.Exec("cmd /c tools\\windows\\wget.exe -O " + ApkPureLocalPage +" --no-check-certificate " + ApkPureStartPage );
    var strOutput = oExec.Stderr.ReadAll();
    WScript.Echo("StdOut "+strOutput);
    
}
// read the first file and look for it to the download_link
function  GetLine() {

  var forReading = 1, forWriting = 2, forAppending = 8;
  // Create the object 
  fs = new ActiveXObject("Scripting.FileSystemObject");
  f = fs.GetFile(ApkPureLocalPage);
  // Open the file 
  is = f.OpenAsTextStream( forReading, 0 );
  // start and continue to read until we hit
  // the end of the file. 
  while( !is.AtEndOfStream ){
     line = is.ReadLine();
     if (line.indexOf("download_link") != -1) {
         //WScript.Echo(line);
         return line;
     }
  }
  // Close the stream 
  is.Close();
  WScript.Echo("Error - Could not find download_link in file");
  WScript.Quit(1);
}

// Split the line and look for the word starts with "href=\"https"
function GetFileTag() { 
    line = GetLine()
    //WScript.Echo(line.split());
    words = line.split(" ")
    for (var i in words) {
        // WScript.Echo(words[i])
        if (words[i].indexOf("href=\"https") != -1) {
            return words[i];
        }
    }
    return null;
}

function DownloadApk(tag) {
    start = tag.indexOf("href=\"");
    end = tag.indexOf("\">click");
    if(start == -1 || end == -1) {
        WScript.Echo("Error - Bad tag found in file tag = " + tag);
        WScript.Quit(2);
    }
    filename = tag.substring(start + 6 , end)
    WScript.Echo("Downloading file " + filename)

    var oShell = WScript.CreateObject("WScript.Shell");
    oExec = oShell.Exec('cmd /c tools\\windows\\wget.exe -O '+LibreLinkApk + ' --no-check-certificate \"' + filename +"\"" );
    var strOutput = oExec.StdErr.ReadAll();
    WScript.Echo("StdOut "+strOutput);
}

function CheckApkSize() {
    fs = new ActiveXObject("Scripting.FileSystemObject");
    try {
        f = fs.GetFile(LibreLinkApk);
    } catch (err) {
        WScript.Echo("Error - failed to downloaded " + err);
        WScript.Quit(3);    
    }
    
    if(f.size < 12*1000*1000) {
        // File seems too small...
        WScript.Echo("Error - file that was downloaded is too small");
        WScript.Quit(4);
    }
    WScript.Echo("Successfully downloaded "+ LibreLinkApk + " file size = " + f.size );

}

function GetFile() {
    GetStartingPage();
    tag = GetFileTag()
    if (tag == null) {
        WScript.Echo("Error - Could not find the word href=\"https in file");
        WScript.Quit(2);
    }
    DownloadApk(tag);
    CheckApkSize();
}

GetFile()
