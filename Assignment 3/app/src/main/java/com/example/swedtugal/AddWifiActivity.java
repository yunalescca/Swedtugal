package com.example.swedtugal;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AddWifiActivity extends AppCompatActivity {

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
            public boolean onKey(View v, int keyCode, KeyEvent event) { return false;
            }
        });
    }

    private void timerMethod() {
        String filename = "wifiSamples.csv";
        Writer writer = new Writer(filename);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        List<ScanResult> scanResultList = wifiManager.getScanResults();

        String cellName = cellNameInput.getText().toString();
        String sampleNumber = sampleNumberInput.getText().toString();

        try {
            for (ScanResult wifi: scanResultList) {
                String wifiData = cellName + ";" + sampleNumber + ";" + wifi.SSID +
                        ";" + wifi.BSSID + ";" + wifi.level + ";" + "\n";
                writer.writeToFile(wifiData);
            }
            writer.closeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sampleNumberInput.setText(Integer.toString(Integer.valueOf(sampleNumber) + 1));
    }

    public void saveWifi(View view) {
        sampleNumberInput.setFocusable(false);
        cellNameInput.setFocusable(false);
        CountDownTimer myTimer = new CountDownTimer(64000,4000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerMethod();
            }

            @Override
            public void onFinish() {
                sampleNumberInput.setFocusable(true);
                cellNameInput.setFocusable(true);
            }
        }.start();
    }
}