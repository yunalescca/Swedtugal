package com.example.swedtugal;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GetWifiScanActivity extends AppCompatActivity {

    private WifiManager wifiManager;

    private ListView wifiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_wifi_scan);

        wifiList = findViewById(R.id.wifiList);
    }

    public void refreshWifi(View view) {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiManager.startScan();

        List<ScanResult> scanResultList = wifiManager.getScanResults();

        List<String> wifiListScan = new ArrayList<String>();
        List<String[]> data = new ArrayList<String[]>();
        for (ScanResult wifi: scanResultList) {
            wifiListScan.add("Name: " + wifi.SSID +
                    "; Mac Address: " + wifi.BSSID +
                    "; Signal Strength: " + wifi.level);
            data.add(new String[] {wifi.SSID, wifi.BSSID, Integer.toString(wifi.level)});
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiListScan);

        wifiList.setAdapter(adapter);
    }
}