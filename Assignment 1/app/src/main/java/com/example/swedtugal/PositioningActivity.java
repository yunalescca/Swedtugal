package com.example.swedtugal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

public class PositioningActivity extends AppCompatActivity implements SensorEventListener {

    TableLayout tableLayout1, tableLayout2;
    TableRow tableRow1, tableRow2;
    View cellA, cellB, cellC, cellD;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private final int RECORDINGTIME = 500;
    private boolean status;
    private static final boolean RECORDING = true;
    private static final boolean STOPPED = false;
    List<Float> accelerometerData;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);

        tableLayout1 = (TableLayout) findViewById(R.id.tableLayout1);
        tableLayout2 = (TableLayout) findViewById(R.id.tableLayout2);
        tableRow1 = (TableRow) tableLayout1.findViewById(R.id.tableRow1);
        tableRow2 = (TableRow) tableLayout2.findViewById(R.id.tableRow2);
        cellA = (View) tableRow1.findViewById(R.id.cellA);
        cellB = (View) tableRow2.findViewById(R.id.cellB);
        cellC = (View) tableRow2.findViewById(R.id.cellC);
        cellD = (View) tableRow2.findViewById(R.id.cellD);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else{}
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
        if (status==RECORDING) {
            float magnitude = (float)(Math.sqrt( Math.pow(event.values[0], 2) + Math.pow(event.values[1],2) + Math.pow(event.values[2],2)));
            accelerometerData.add(magnitude);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    private void changeStateToRecording(){
        status = RECORDING;
    }

    private void changeStateToStopped(){
        status = STOPPED;
    }

    public void guessLocation(View view) throws IOException {
        final View v = view;
        changeStateToRecording();
        accelerometerData = new ArrayList<Float>();

        new CountDownTimer(RECORDINGTIME, 100) {

            public void onTick(long millisUntilFinished) {}

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onFinish() {
                changeStateToStopped();
                try {
                    updateStats(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateStats(View view) throws IOException {
        String activity = getCurrentActivity();
        String location = getCurrentLocation();
        View cellToColor;
        switch(location){
            case "c1":
                cellToColor = cellA;
                break;
            case "c2":
                cellToColor = cellB;
                break;
            case "c3":
                cellToColor = cellC;
                break;
            case "c4":
                cellToColor = cellD;
                break;
            default:
                cellToColor = null;
        }

        fillCell(cellToColor);
        putActivityImage(activity);
    }

    private void putActivityImage(String activity) {
        ImageView activityImage = (ImageView) findViewById(R.id.activityView);
        switch(activity){
            case "Still":
                activityImage.setImageResource(R.drawable.standing_man);
                break;
            case "Walking":
                activityImage.setImageResource(R.drawable.walking_man);
                break;
            case "Running":
                activityImage.setImageResource(R.drawable.running_man);
                break;
        }
    }

    private void fillCell(View cellToColor) {
        if(cellToColor != null){
            cellA.setBackgroundResource(R.drawable.emptycell);
            cellB.setBackgroundResource(R.drawable.emptycell);
            cellC.setBackgroundResource(R.drawable.emptycell);
            cellD.setBackgroundResource(R.drawable.emptycell);
            cellToColor.setBackgroundResource(R.drawable.fullcell);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getCurrentLocation() throws IOException {
        StringBuilder ourValue = new StringBuilder();
        InputStream wifiTrainingData = getResources().openRawResource(R.raw.wifi_processed);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(wifiTrainingData, Charset.forName("UTF-8")));
        String line = reader.readLine();
        String[] macs = line.split(",");

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        List<String> scanList = new LinkedList<String>();

        for (ScanResult wifi: scanResultList) {
            scanList.add(wifi.BSSID);
        }

        for (int i=1; i<macs.length; i++){
            if (scanList.contains(macs[i])) {
                ourValue.append("1");
            }
            else {
                ourValue.append("0");
            }
        }

        Map<String, Integer> map = new LinkedHashMap<>();

        try {
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                List<String> listTokens = new LinkedList<>(Arrays.asList(tokens));
                listTokens.remove(0);
                StringBuilder sb = new StringBuilder();
                for (String s : listTokens) {
                    sb.append(s);
                }
                map.put(tokens[0], hammingDist(ourValue.toString(), sb.toString()));
            }

            map = sortMapByValue(map, true);
            String[] keysArray = map.keySet().toArray(new String[map.keySet().size()]);
            Map<String, Integer> topLabels = new LinkedHashMap<>();

            for(int i = 0; i < keysArray.length && i < 5; i++) {
                keysArray[i] = keysArray[i].substring(0, 2);
                if (topLabels.containsKey(keysArray[i])) {
                    topLabels.replace(keysArray[i], topLabels.get(keysArray[i]) + 1);
                } else {
                    topLabels.put(keysArray[i], 1);
                }
            }

            topLabels = sortMapByValue(topLabels, false);
            return topLabels.entrySet().iterator().next().getKey();

        } catch (IOException e) {
            Log.wtf("Error reading data file on line " + line, e);
            e.printStackTrace();
        } finally {
            reader.close();
        }
        return "";
    }

    private int hammingDist(String str1, String str2) {
        int i = 0, count = 0;
        while (i < str1.length()) {
            if (str1.charAt(i) != str2.charAt(i)) count++;
            i++;
        }
        return count;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getCurrentActivity() throws IOException {
        float minList = Collections.min(accelerometerData);
        float maxList = Collections.max(accelerometerData);
        Float ourValue = Math.abs(maxList-minList);
        InputStream accTrainingData = getResources().openRawResource(R.raw.acc_processed);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(accTrainingData, Charset.forName("UTF-8")));
        String line = reader.readLine();
        Map<Float, String> unsortedMap = new HashMap<>();

        try {
            while((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                Float difference = Math.abs(ourValue - Float.valueOf(tokens[3]));
                unsortedMap.put(difference, tokens[1]);
            }

            Map<Float, String> sortedMap = new TreeMap<>(unsortedMap); // Sorted map by key
            Float[] keysArray = sortedMap.keySet().toArray(new Float[sortedMap.keySet().size()]);
            Map<String, Integer> topLabels = new LinkedHashMap<>();

            for(int i = 0; i < keysArray.length && i < 9; i++) {
                if (topLabels.containsKey(sortedMap.get(keysArray[i]))) {
                    topLabels.replace(sortedMap.get(keysArray[i]), topLabels.get(sortedMap.get(keysArray[i])) + 1);
                } else {
                    topLabels.put(sortedMap.get(keysArray[i]), 1);
                }
            }

            topLabels = sortMapByValue(topLabels, false);
            return topLabels.entrySet().iterator().next().getKey();

        } catch (IOException e) {
            Log.wtf("Error reading data file on line " + line, e);
            e.printStackTrace();
        } finally {
            reader.close();
        }
        return "";
    }

    private Map<String, Integer> sortMapByValue(Map<String, Integer> map, final boolean ascending) {
        List<Map.Entry<String, Integer> > list = new LinkedList<>(map.entrySet()); //create list from map
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { //sort list by value
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if (ascending) return (o1.getValue()).compareTo(o2.getValue());
                else return (o2.getValue()).compareTo(o1.getValue());
            }});

        map = new LinkedHashMap<>(); //put back into map
        for (Map.Entry<String, Integer> s : list) {
            map.put(s.getKey(), s.getValue());
        }
        return map;
    }
}
