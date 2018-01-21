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
    cheksum_ok = CheckCRC16(data, 0 ,24)
    print('Header of sensor: checksum_ok =', cheksum_ok , file=text_file)
    PrintBytesAsHex(data[0:24], text_file)

    # loads trend values (ring buffer, starting at index_trent. byte 28-123)
    cheksum_ok = CheckCRC16(data, 24 ,296)
    print ('per minute data: checksum _ok =', cheksum_ok , file=text_file)
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

    cheksum_ok = CheckCRC16(data, 320 ,24)
    print('Footer of sensor: checksum_ok =', cheksum_ok, file=text_file)
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

crc16table = [
        0, 4489, 8978, 12955, 17956, 22445, 25910, 29887, 35912,
    40385, 44890, 48851, 51820, 56293, 59774, 63735, 4225, 264,
    13203, 8730, 22181, 18220, 30135, 25662, 40137, 36160, 49115,
    44626, 56045, 52068, 63999, 59510, 8450, 12427, 528, 5017,
    26406, 30383, 17460, 21949, 44362, 48323, 36440, 40913, 60270,
    64231, 51324, 55797, 12675, 8202, 4753, 792, 30631, 26158,
    21685, 17724, 48587, 44098, 40665, 36688, 64495, 60006, 55549,
    51572, 16900, 21389, 24854, 28831, 1056, 5545, 10034, 14011,
    52812, 57285, 60766, 64727, 34920, 39393, 43898, 47859, 21125,
    17164, 29079, 24606, 5281, 1320, 14259, 9786, 57037, 53060,
    64991, 60502, 39145, 35168, 48123, 43634, 25350, 29327, 16404,
    20893, 9506, 13483, 1584, 6073, 61262, 65223, 52316, 56789,
    43370, 47331, 35448, 39921, 29575, 25102, 20629, 16668, 13731,
     9258, 5809, 1848, 65487, 60998, 56541, 52564, 47595, 43106,
    39673, 35696, 33800, 38273, 42778, 46739, 49708, 54181, 57662,
    61623, 2112, 6601, 11090, 15067, 20068, 24557, 28022, 31999,
    38025, 34048, 47003, 42514, 53933, 49956, 61887, 57398, 6337,
     2376, 15315, 10842, 24293, 20332, 32247, 27774, 42250, 46211,
    34328, 38801, 58158, 62119, 49212, 53685, 10562, 14539, 2640,
     7129, 28518, 32495, 19572, 24061, 46475, 41986, 38553, 34576,
    62383, 57894, 53437, 49460, 14787, 10314, 6865, 2904, 32743,
    28270, 23797, 19836, 50700, 55173, 58654, 62615, 32808, 37281,
    41786, 45747, 19012, 23501, 26966, 30943, 3168, 7657, 12146,
    16123, 54925, 50948, 62879, 58390, 37033, 33056, 46011, 41522,
    23237, 19276, 31191, 26718, 7393, 3432, 16371, 11898, 59150,
    63111, 50204, 54677, 41258, 45219, 33336, 37809, 27462, 31439,
    18516, 23005, 11618, 15595, 3696, 8185, 63375, 58886, 54429,
    50452, 45483, 40994, 37561, 33584, 31687, 27214, 22741, 18780,
    15843, 11370, 7921, 3960 ]
    
# first two bytes = crc16 included in data
def computeCRC16(data, start, size):
    crc = 0xffff;
    for i in range (start + 2, start + size):
        crc = ((crc >> 8) ^ crc16table[(crc ^ data[i]) & 0xff]);
  
    reverseCrc = 0;
    for i in range (0,16):
        reverseCrc = (reverseCrc << 1) | (crc & 1)
        crc >>= 1
    print ('CRC', reverseCrc, data[start + 1] * 256 + data[start])
    return reverseCrc

def CheckCRC16(data, start, size):
    crc = computeCRC16(data, start, size)
    if crc == (data[start+1] * 256 + data[start]) : 
        return True
    return False
    
json.loads('{\"result\": \"No data\"}') 
if len(sys.argv) != 2:
    print ("Need to supply DB file Name")
    print('a\r\n') 
    sys.exit()

file_name = sys.argv[1]
ReadxDripDb(file_name)


