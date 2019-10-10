package com.example.swedtugal;

import android.os.Environment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    private static final String DIRECTORY_NAME = "Swedtugal";
    private DataOutputStream fileOutStream;

    Writer(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIRECTORY_NAME, fileName);
        try {
            fileOutStream = new DataOutputStream(new FileOutputStream(file,true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void writeToFile(String msg) {
        //if (!isExternalStorageWritable())
        try {
            fileOutStream.write(msg.getBytes());
            fileOutStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void closeFile() {
        try {
            fileOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
