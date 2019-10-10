package com.example.swedtugal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class GetAccelerometerScanActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float xValue, yValue, zValue;

    private TextView currentXValue, currentYValue, currentZValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_accelerometer_scan);

        currentXValue = (TextView) findViewById(R.id.currentXValue);
        currentYValue = (TextView) findViewById(R.id.currentYValue);
        currentZValue = (TextView) findViewById(R.id.currentZValue);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{

        }
    }

    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xValue = event.values[0];
        yValue = event.values[1];
        zValue = event.values[2];

        currentXValue.setText(Float.toString(xValue));
        currentYValue.setText(Float.toString(yValue));
        currentZValue.setText(Float.toString(zValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void saveData(View view) {
        String FILENAME = "file";
        List<String> data = new ArrayList<String>();
        data.add("India"+";"+"New Delhi" +"\n");
        data.add("United States"+ ";" +"Washington D.C\n");
        data.add("Germany;Berlin\n");
        try {
            FileOutputStream out = new FileOutputStream( new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.pathSeparator + FILENAME + ".csv"), true);
            for (String d: data) {
                out.write(d.getBytes());
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}