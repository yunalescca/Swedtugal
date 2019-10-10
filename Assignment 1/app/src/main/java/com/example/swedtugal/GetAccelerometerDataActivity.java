package com.example.swedtugal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public class GetAccelerometerDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_accelerometer_data);
    }

    public void getAccelerometerScanData(View view) {
        Intent intent = new Intent(this,  GetAccelerometerScanActivity.class);
        startActivity(intent);
    }

    public void addAccelerometer(View view) {
        Intent intent = new Intent(this,  AddAccelerometerActivity.class);
        startActivity(intent);
    }
}