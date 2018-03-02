# LibreOOPAlgorithm
A framework for testing and implementing algorithms for libre.

## TLDR: To create the apk, please do the following:

Make sure that your pc has android studio instaled. follow the movie at https://developer.android.com/studio/install.html for instructions on how to install android studio. <br/>

git clone https://github.com/tzachi-dar/LibreOOPAlgorithm.git<br/>
cd LibreOOPAlgorithm<br/>
scripts\create_apk.cmd<br/>

The apk that you need to install is apk.aunaligned.apk
Before doing it, please go to settings->security and turn on "unknown sources"

You can install the apk in anyway you like, for example: adb install apk.aunaligned.apk

You can also copy it to your phone and install it from there.<br/>

If you see a message saying "The algorithm worked successfully" all is done. If you see an error message please let me know. <br/>

## Setting xDrip to work with the oop Algorithm.

Currently only the blukon supports this oop Algorithm (other ways of scanning will be added soon). <br/>

xDrip has to be at a version after  23rd Nov nightly. (This probably means that you have to download the latest nightly).<br/>

Please move xDrip to engineering mode: <br/>
* From the xDrip home screen press the Treatment button Syringe icon.
* Now either tap the Microphone icon to speak or long press it to type text.
* Speak or Type the command enable engineering mode.

On xdrip go to setting->less common settings->other misc options-> and set out of process blukon algorithm. 
(If this option is not set, this means that you have not enabled engineering mode). <br/>

## The propose of this project is to allow opensource projects to use the same algorithm for bg calculation.
This is a specific example that simulates official results, but anyone can implement whatever algorithm that he wants. <br/>

Please see the class IntentsReceiver for how to send and receive intents with raw data from the libre sensor. <br/>