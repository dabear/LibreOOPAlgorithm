package com.hg4.oopalgorithm.oopalgorithm;

import android.content.Context;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

class Utils {

    static final  String TAG = "xOOPAlgorithm";

    // Use reflection print the value of an object
    public static String objectToString (Object obj) {
        if (obj == null) {
            return "{null}";
        }
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append( obj.getClass().getName() );
        result.append( " Object {" );
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = obj.getClass().getDeclaredFields();

        //print field names paired with their values
        for ( Field field : fields  ) {
            result.append("  ");
            try {
                result.append( field.getName() );
                result.append(": ");
                //requires access to private field:
                field.setAccessible(true);

                result.append( field.get(obj) );
            } catch ( IllegalAccessException ex ) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }


    public static String byteArrayToHex(byte[] a) {
        if(a == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("0x%02x ", b));
        return sb.toString();
    }

    public static boolean comparePartialByteArray(byte []b1, byte[] b2) {
        int len = Math.min(b1.length, b2.length);
        for (int i = 0 ; i < len ; i++) {
            if(b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    public static byte [] stringToByte(String str) {
        // This function receives a string in the form "0x1, 0x5" and creates a byte array from it.
        String []str_array = str.split(",");
        byte[] numbers = new byte[str_array.length];
        for(int i = 0;i < str_array.length;i++)
        {
            // Note that this is assuming valid input
            // If you want to check then add a try/catch
            // and another index for the numbers if to continue adding the others (see below)
            try {
                numbers[i] = Byte.decode(str_array[i].trim());
            } catch (NumberFormatException nfe){
                Log.i(TAG,"Invalid value for a byte in " + str);
                Log.i(TAG,"Invalid value index is " + i + " value is " + str_array[i].trim());
                return null;
            }
        }
        return numbers;
    }

    public static byte[] readBinaryFile(String fullPath) {
        if(fullPath == null) {
            return null;
        }
        File file = new File(fullPath);
        byte[] fileData = new byte[(int) file.length()];
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            Log.i(TAG,"File not found " + fullPath);
            return null;
        }
        try {
            dis.readFully(fileData);
            dis.close();
        } catch (IOException e) {
            Log.i(TAG,"Error reading from file " + fullPath);
            return null;
        }
        return fileData;
    }

    public static void writeToFile(Context context, String file, byte []data) {

        Log.i(TAG,"context = " + context);
        String dir = context.getFilesDir().getPath();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        String file_name = dir + '/' + file+ "_" + currentDateandTime + ".dat";
        try {
            Log.i(TAG,"Writing to file " + file_name + ", size = " + (data == null ? 0 : data.length));
            FileOutputStream f = new FileOutputStream(new File(file_name));
            if(data != null) {
                // file will be written with zero length to let the user know what is happening.
                f.write(data);
            }
            f.close();
        }catch (IOException e) {
            Log.i(TAG,"Cought exception when trying to write file" + e);
        }
    }

}
