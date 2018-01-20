import datetime
import sqlite3
import sys
import subprocess
import time
import json
import pprint


#based on 
#    https://github.com/keencave/LBridge/blob/master/LBridge_RFduino_170729_1647.ino, line 2164.
#    https://github.com/UPetersen/LibreMonitor/wiki


def RunCommand(params_string):
    params = params_string.split()
    
    #prarms = ['adb', 'push', '2017_12_29_01_17_42.bin', '/data/local/tmp/']
    print(type (params), params)
    p1 = subprocess.Popen( params,stdout=subprocess.PIPE)
    #p1 = subprocess.Popen(['adb', 'push', '2017_12_29_01_17_42.bin', '/data/local/tmp/'],stdout=subprocess.PIPE)
    out = p1.communicate()[0]
    #print(out)
    return out.decode("utf-8") 


def WriteBinFile(file_name, bin_data):
    newFile = open(file_name, "wb")
    newFile.write(bin_data)    
    

def getGlucose(byte0, byte1):
    return ((256 * (byte0 & 0xFF) + (byte1 & 0xFF)) & 0x0FFF) / 10
    
def PrintBytesAsHex(bytearray, text_file):
    i = 0
    for byte in bytearray:
        # print leading line number
        if not i % 8:
            print( format(i, '0002x'), ': ',end='', sep='', file=text_file)
        print(format(byte, '02x'), end='', file=text_file)
        i+=1
        # move line or space as needed
        if i % 8:
            print(' ',end='', file=text_file)
        else:
            print('', file=text_file)
    if i % 8:
        print('', file=text_file)

    
def WriteTxtFile(text_file, data, watchTime, json):

    indexTrend = data[26] & 0xFF;

    indexHistory = data[27] & 0xFF;

    sensorTime = 256 * (data[317] & 0xFF) + (data[316] & 0xFF);
    MINUTE = 60

    sensorStartTime = watchTime - sensorTime * MINUTE;
    print('Header of sensor:', file=text_file)
    PrintBytesAsHex(data[0:24], text_file)

    
    # loads trend values (ring buffer, starting at index_trent. byte 28-123)
    print ('per minute data', file=text_file)
    for index in range (0, 16):
        i = indexTrend - index - 1;
        if (i < 0): i += 16;
        print('index', i, file=text_file )
        PrintBytesAsHex(data[i * 6 + 28 : (i + 1) * 6 + 28], text_file)
        
        glucoseLevel = getGlucose(data[(i * 6 + 29)], data[(i * 6 + 28)]);
        time = max(0, sensorTime - index);
        print ('glucoseLevel = ', glucoseLevel, 'time = ', time, file=text_file)

        

        #        glucoseData.realDate = sensorStartTime + time * MINUTE;
        #        glucoseData.sensorTime = time;

    
    # loads history values (ring buffer, starting at index_trent. byte 124-315)
    print ('Historical data', file=text_file)
    for index in range (0, 32):
        i = indexHistory - index - 1;
        if (i < 0): i += 32;
        #GlucoseData glucoseData = new GlucoseData();
        glucoseLevel = getGlucose(data[(i * 6 + 125)], data[i * 6 + 124]);
        
        print('index', i, file=text_file )
        PrintBytesAsHex(data[i * 6 + 28 : (i + 1) * 6 + 28], text_file)

        time = max(0, abs((sensorTime - 3) / 15) * 15 - index * 15);
        print ('glucoseLevel = ', glucoseLevel, 'time = ', time, file=text_file)

        # glucoseData.realDate = sensorStartTime + time * MINUTE;
        # glucoseData.sensorId = tagId;
        # glucoseData.sensorTime = time;
        # historyList.add(glucoseData);

    print('Footer of sensor:', file=text_file)
    PrintBytesAsHex(data[320:344], text_file)
    print('OOP result', file=text_file)
    print(pprint.pformat(json), file=text_file)

# Take the output of adb logcat -d and look for the string starting with gson, if it exists.
def GetGson(out_string):
    for line in out_string.split():
        if 'gson:' in line:
            #print('found line', line[5:])
            if len(line) < 10:
                return json.loads('{\"result\": \"No data\"}')
            data = json.loads(line[5:])
            pprint.pprint(data)
            return data
    return None
    
def getAlgorithmResult(file_name):
    RunCommand('adb push ' + file_name + ' /data/local/tmp/')
    RunCommand('adb logcat -c')
    RunCommand('adb shell am broadcast -a com.eveningoutpost.dexdrip.LIBRE_DATA --es packet ' + '/data/local/tmp/' +file_name)
    for i in range (1, 20):
        time.sleep(0.1)
        out = RunCommand('adb logcat -d')
        #print(out)
        gson = GetGson(out)
        if gson != None:
            return gson
    # 20 experiments have passed. Probably there will not be a reply.
    # please run adb logcat and find out why the result does not contain the string 'gson:'
    print('Error, adb logcat does not find \'gson\' - exiting')
    sys.exit(1)

def ReadxDripDb(file_name):
    # gets the latest n non commited objects
    ret = []
    conn = sqlite3.connect(file_name)
    with conn:
        cursor = conn.execute("SELECT timestamp, blockbytes, bytestart, byteend from LibreBlock  where bytestart == 0 and byteend >=344 ORDER BY timestamp")
    
        for raw in cursor:
            #print (raw)
            raw_dict = dict()
            raw_dict['timestamp'] = raw[0]
            raw_dict['blockbytes'] = raw[1][0:344]
            # reverse the list to get is ASC but from the end.
            ret.insert(0,raw_dict)
    for raw in ret:
        print(raw)
        time_string = datetime.datetime.fromtimestamp(raw['timestamp'] / 1000.0).strftime('%Y_%m_%d_%H_%M_%S')
        print(time_string)
        
        WriteBinFile(time_string + '.bin', raw['blockbytes'])
        
        json = getAlgorithmResult(time_string + '.bin')
        
        file_name = time_string + '.txt'
        with open(file_name, "w") as text_file:
            WriteTxtFile(text_file, raw['blockbytes'], raw['timestamp'], json)
            
        #sys.exit(3) # only print first

    return ret
    
json.loads('{\"result\": \"No data\"}') 
if len(sys.argv) != 2:
    print ("Need to supply DB file Name")
    print('a\r\n') 
    sys.exit()

file_name = sys.argv[1]
ReadxDripDb(file_name)


