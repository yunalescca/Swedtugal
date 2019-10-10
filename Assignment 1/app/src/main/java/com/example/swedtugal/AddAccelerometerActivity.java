package com.example.swedtugal;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AddAccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final int WINDOWSIZE = 500;
    private final int RECORDINGTIME = 10000;

    private long initialRecordTime, currentRecordTime;
    private int windowSize;
    private String motionStatus;

    //status -> recording
    //!status -> not recording
    private boolean status;
    private static final boolean RECORDING = true;
    private static final boolean STOPPED = false;



    //private float xValue, yValue, zValue;

    //private TextView currentXValue, currentYValue, currentZValue;

    private EditText sampleNumberInput;
    private TextView timeMissing;
    private RadioGroup radioGroup;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accelerometer);

        sampleNumberInput = findViewById(R.id.sampleNumberInput);
        sampleNumberInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        sampleNumberInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });
        timeMissing = findViewById(R.id.timeMissing);
        radioGroup = findViewById(R.id.radioGroup);
        saveButton = findViewById(R.id.saveAccData);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{

        }
        changeStateToStopped();
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
        if (status==RECORDING){
            Date currentTime = new Date();
            currentRecordTime = currentTime.getTime();

            windowSize = ((int)(currentRecordTime-initialRecordTime))/WINDOWSIZE;

            int idMotionStatus = radioGroup.getCheckedRadioButtonId();
            if (idMotionStatus == R.id.stillButton){
                motionStatus="Still";
            }
            else if (idMotionStatus == R.id.walkingButton){
                motionStatus="Walking";
            }
            else{
                motionStatus="Running";
            }

            String FILENAME = "accelerometer.csv";
            String accelerometerData;

            float magnitude = (float)(Math.sqrt( Math.pow(event.values[0], 2) + Math.pow(event.values[1],2) + Math.pow(event.values[2],2)));

            try {
                FileOutputStream out = openFileOutput( FILENAME, Context.MODE_APPEND);
                accelerometerData = sampleNumberInput.getText() + ";" + motionStatus + ";" + currentRecordTime +
                        ";" + windowSize + ";" + event.values[0] + ";" + event.values[1] + ";"
                        + event.values[2] + ";" + magnitude + "\n";
                out.write(accelerometerData.getBytes());
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    public void onClickRecord(View v){
        new CountDownTimer(RECORDINGTIME, 1000) {

            public void onTick(long millisUntilFinished) {
                timeMissing.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timeMissing.setText("");
                changeStateToStopped();
            }

        }.start();
        changeStateToRecording();

    }

    private void changeStateToRecording(){
        status = RECORDING;
        timeMissing.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.INVISIBLE);
        Date currentTime = new Date();
        initialRecordTime = currentTime.getTime();
    }

    private void changeStateToStopped(){
        status = STOPPED;
        timeMissing.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
    }

    public void saveData(View view) {
        String FILENAME = "file.csv";
        List<String> data = new ArrayList<String>();
        data.add("India"+";"+"New Delhi" +"\n");
        data.add("United States"+ ";" +"Washington D.C\n");
        data.add("Germany;Berlin\n");
        try {
            FileOutputStream out = openFileOutput( FILENAME, Context.MODE_APPEND);
            for (String d: data) {
                out.write(d.getBytes());
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}