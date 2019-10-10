package com.example.swedtugal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class GetWifiDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_wifi_data);
    }

    public void getWifiScanData(View view){
        Intent intent = new Intent(this,  GetWifiScanActivity.class);
        startActivity(intent);
    }

    public void addWifi(View view){
        Intent intent = new Intent(this, AddWifiActivity.class);
        startActivity(intent);
    }
}