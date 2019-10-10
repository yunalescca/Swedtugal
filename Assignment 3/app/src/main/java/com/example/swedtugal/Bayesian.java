package com.example.swedtugal;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Bayesian {

    private static final int NUMBER_CELLS = 16;
    private static final double THRESHOLD = 0.9;
    private Map<String, List<double[]>> macRSSTable;
    private Map<String, List<Integer>> scanResult;
    private Map<String, Integer> scanResultTreated;
    private List<Double> priorProbability, posteriorProbability, vectorProbability;
    private int currentBelief;
    private Activity activity;

    Bayesian (Activity activity) {
        this.activity = activity;
        readPMF();
        setInitialBelief();
    }

    /**
     * Read all existing PMF tables and save in map with MAC address (key) and matrix of
     * corresponding RSS values (value).
     */
    private void readPMF () {
        Field[] fields = R.raw.class.getFields();
        this.macRSSTable = new HashMap<>();

        for (Field field: fields) {
            List<double[]> rssTable = new ArrayList<>();
            int id = activity.getResources().getIdentifier("raw/" +  field.getName(), null, activity.getPackageName());

            InputStream inputStream = activity.getResources().openRawResource(id);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String csvLine;
                bufferedReader.readLine();

                while ((csvLine = bufferedReader.readLine()) != null){
                    String[] tmp = csvLine.split(",");
                    double[] row = new double[tmp.length];

                    for (int i = 0; i < tmp.length; i++) row[i] = Double.valueOf(tmp[i]);

                    rssTable.add(row);
                }
            } catch(IOException e){}
            String macAddress = parseMacColon(field.getName());
            this.macRSSTable.put(macAddress, rssTable);
        }
    }

    /**
     * Reset prior, posterior and vector probabilities, and set current belief to 0.
     */
    private void setInitialBelief() {
        this.priorProbability = new ArrayList<>(NUMBER_CELLS);
        this.posteriorProbability = new ArrayList<>(NUMBER_CELLS);
        this.vectorProbability = new ArrayList<>(NUMBER_CELLS);

        for (int i = 0; i < NUMBER_CELLS; i++) {
            this.priorProbability.add(i,1.0 / NUMBER_CELLS);
            this.posteriorProbability.add(i, 0.0);
        }

        this.currentBelief = 0;
    }

    public List<Double> getScan() {
        setInitialBelief();

        int count = 0;
        double max = 0;

        while (max < THRESHOLD && count < 3) {
            this.scanResult = new HashMap<>();
            for (int i = 0; i < 3; i++) {
                timerMethod();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            treatData();

            if (!this.scanResult.isEmpty()) {
                applyBayes();
            }

            count++;
            max = Collections.max(this.posteriorProbability);
        }

        this.currentBelief = this.posteriorProbability.indexOf(max);
        return posteriorProbability;
    }

    /**
     * Record the MAC addresses and RSS values.
     */
    private void timerMethod() {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        List<ScanResult> scanResultList = wifiManager.getScanResults();
        List<Integer> tmp;

        for (ScanResult wifi: scanResultList) {
            String wifiData = wifi.BSSID;

            if (rawContainsMac(parseMacUnderscore(wifiData))) {
                if (!this.scanResult.containsKey(wifiData)) {
                    tmp = new ArrayList<>();
                    this.scanResult.put(wifiData, tmp);
                }

                tmp = this.scanResult.get(wifiData);
                tmp.add(wifi.level);
                this.scanResult.put(wifiData, tmp);
            }
        }
    }

    /**
     * Put in a map, the MAC address (key) and its median RSS value (value).
     */
    private void treatData() {
        this.scanResultTreated = new HashMap<>();

        for (Map.Entry<String, List<Integer>> entry: scanResult.entrySet()) {
            String key = entry.getKey();
            List<Integer> value = entry.getValue();
            double median = getMedian(value);
            this.scanResultTreated.put(key, (int) median);
        }
    }

    /**
     * Bayes formula. Multiply prior with vector from PMF table, this is new posterior.
     * Keep going until the highest probability in posterior is 0.9 or after 10 iterations.
     */
    private void applyBayes() {
        if (!this.scanResultTreated.isEmpty()) {
            this.scanResultTreated = sortMapByValue(this.scanResultTreated);
            int count = 0;
            double sum, tmp;

            for (Map.Entry<String, Integer> entry: this.scanResultTreated.entrySet()) {
                getColumnForMac(entry.getKey(), entry.getValue());
                sum = 0.0;

                for (int i = 0; i < NUMBER_CELLS; i++) {
                    tmp = this.priorProbability.get(i) * this.vectorProbability.get(i);
                    this.posteriorProbability.set(i, tmp);
                    sum += tmp;
                }

                for (int i = 0; i < NUMBER_CELLS; i++) {
                    tmp = this.posteriorProbability.get(i);
                    this.posteriorProbability.set(i, tmp / sum);
                }

                count++;
                this.priorProbability = this.posteriorProbability;

                if (count == 4){
                    break;
                }
            }
        }
    }

    /**
     * Guess location of user and visualize on the map accordingly.
     */
    int guessLocation() {
        return this.currentBelief + 1;
    }

    private double getMedian(List<Integer> list) {
        Collections.sort(list);

        int midSize = list.size() / 2;
        double middle;

        if (list.size() % 2 == 1) {
            middle = list.get(midSize);
        } else {
            middle = (double) (list.get(midSize) + list.get(midSize - 1)) / 2;
        }

        return middle;
    }

    private Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
        List<Map.Entry<String, Integer> > list = new LinkedList<>(map.entrySet()); //create list from map
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { //sort list by value
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }});

        map = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> s : list) {
            map.put(s.getKey(), s.getValue());
        }

        return map;
    }

    /**
     * Parse MAC address from colon to underscore
     * @param bssid MAC address to be parsed
     * @return MAC address with underscores as separators
     */
    private String parseMacUnderscore(String bssid) {
        String[] macPieces = bssid.split(":");;
        return "mac_" + macPieces[0] + "_" + macPieces[1] + "_" + macPieces[2] + "_"
                + macPieces[3] + "_" + macPieces[4] + "_" + macPieces[5];
    }

    /**
     * Parse MAC address from underscore to colon
     * @param bssid MAC address to be parsed
     * @return MAC address with colons as separators
     */
    private String parseMacColon(String bssid) {
        String[] macPieces = bssid.split("_");;
        return macPieces[1] + ":" + macPieces[2] + ":" + macPieces[3] + ":"
                + macPieces[4] + ":" + macPieces[5] + ":" + macPieces[6];
    }

    /**
     * Get vector from MAC's PMF table for given RSS value
     * @param macAddress PMF table that belongs to this MAC address
     * @param rss RSS value for which column to pick.
     */
    private void getColumnForMac(String macAddress, int rss) {
        this.vectorProbability = new ArrayList<>(NUMBER_CELLS);
        List<double[]> rssTable = this.macRSSTable.get(macAddress);

        for (double[] rssRow: rssTable) {
            this.vectorProbability.add(rssRow[rss + 110]);
        }
    }

    /**
     * Check if PMF table exists for a given MAC address.
     * @param mac The MAC address for which we want corresponding PMF table.
     * @return true if PMF table exists.
     */
    private boolean rawContainsMac(String mac) {
        Field[] fields = R.raw.class.getFields();
        boolean found = false;
        for (Field field: fields) {
            if (field.getName().equals(mac)) {
                found = true;
            }
        }
        return found;
    }


}