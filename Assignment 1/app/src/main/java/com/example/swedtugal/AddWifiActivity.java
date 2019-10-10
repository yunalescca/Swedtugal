package com.example.swedtugal;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.util.List;

public class AddWifiActivity extends AppCompatActivity {

    private WifiManager wifiManager;

    private EditText cellNameInput, sampleNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);

        cellNameInput = findViewById(R.id.cellNameInput);
        cellNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        cellNameInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        sampleNumberInput = findViewById(R.id.sampleNumberInput);
        sampleNumberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        sampleNumberInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
    }

    public void saveWifi(View view) {

        String FILENAME = "wifiSamples.csv";

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        wifiManager.startScan();

        List<ScanResult> scanResultList = wifiManager.getScanResults();

        String cellName = cellNameInput.getText().toString();
        String sampleNumber = sampleNumberInput.getText().toString();

        String wifiData;

        try {
            FileOutputStream out = openFileOutput( FILENAME, Context.MODE_APPEND);
            for (ScanResult wifi: scanResultList) {
                wifiData = cellName + ";" + sampleNumber + ";" + wifi.SSID +
                        ";" + wifi.BSSID + ";" + wifi.level + "\n";
                out.write(wifiData.getBytes());
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}