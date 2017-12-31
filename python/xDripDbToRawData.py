import datetime
import sqlite3
import sys


def WriteBinFile(file_name, bin_data):
    newFile = open(file_name, "wb")
    newFile.write(bin_data)    

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

    return ret
    
    
if len(sys.argv) != 2:
    print ("Need to supply DB file Name")
    sys.exit()

file_name = sys.argv[1]
ReadxDripDb(file_name)


