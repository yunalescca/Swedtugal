package com.example.swedtugal;

import android.os.Environment;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Reader {

    private static final String DIRECTORY_NAME = "Swedtugal";

    public Reader(String fileName) {
        try {
            DataInputStream fileInStream = new DataInputStream(new FileInputStream(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            DIRECTORY_NAME + "/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
