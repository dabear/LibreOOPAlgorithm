
function  GetLine() {

  var forReading = 1, forWriting = 2, forAppending = 8;
  // Create the object 
  fs = new ActiveXObject("Scripting.FileSystemObject");
  f = fs.GetFile("..\\LibreLink\\LibreLink_v1.3.2.4_apkpure.com.apk");
  // Open the file 
  is = f.OpenAsTextStream( forReading, 0 );
  // start and continue to read until we hit
  // the end of the file. 
  while( !is.AtEndOfStream ){
     line = is.ReadLine();

     if (line.indexOf("download_link") != -1) {
         WScript.Echo(line);
         return line;
     }
  }
  // Close the stream 
  is.Close();
  WScript.Echo("Error - Could not find file");
  WScript.Quit(666);
}

function GetFileTag() { 
    line = GetLine()
    WScript.Echo(line.split());
    words = line.split(" ")
    for (var i in words) {
        WScript.Echo(words[i])
        if (words[i].indexOf("href=\"https") != -1) {
            return words[i];
        }
    }
    return null;
}

function GetFile() {
    tag = GetFileTag()
    if (tag == null) {
        WScript.Echo("Error - Could not find tag for file");
        WScript.Quit(666);
    }
    start = tag.indexOf("href=\"");
    end = tag.indexOf("\">click");
    if(start == -1 || end == -1) {
        WScript.Echo("Error - Bad tag for file " + tag);
        WScript.Quit(666);
    }
    filename = tag.substring(start + 6 , end)
    WScript.Echo("Downloading file " + filename +" ")

    var oShell = WScript.CreateObject("WScript.Shell");
    //oShell.Exec('..\tools\windows\wget.exe ' + filename);
    oExec = oShell.Exec('cmd /c ..\\tools\\windows\\wget.exe -O LibreLink\\LibreLink_v1.3.2.4_apkpure.com.apk --no-check-certificate \"' + filename +"\"" );
    var strOutput = oExec.StdErr.ReadAll();
    WScript.Echo("StdOut "+strOutput);


}

GetFile()