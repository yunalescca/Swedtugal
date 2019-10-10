package com.example.swedtugal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CalibratingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
    }

    public void saveWifiData(View view) {
        Intent intent = new Intent(this,  AddWifiActivity.class);
        startActivity(intent);
    }

    public void saveAccData(View view) {
        Intent intent = new Intent(this, AddAccelerometerActivity.class);
        startActivity(intent);
    }

}
