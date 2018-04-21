package com.hg4.oopalgorithm.oopalgorithm;

import com.abbottdiabetescare.flashglucose.sensorabstractionservice.dataprocessing.GlucoseValue;
import com.abbottdiabetescare.flashglucose.sensorabstractionservice.TrendArrow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

class HistoricBg {

    HistoricBg(GlucoseValue historicGlucose) {
        quality = historicGlucose.getDataQuality() == 0 ? 0 : 1; // TODO:
        time = historicGlucose.getId();
        bg = historicGlucose.getValue();
    }

    public int quality;
    public int time;
    public double bg;
}

class OOPResults {
    double currentBg;
    int currentTime;
    int currentTrend;
    HistoricBg [] historicBg;
    long timestamp;
    String serialNumber;

    byte[] newState;

    OOPResults(long timestamp, int currentBg, int currentTime, TrendArrow currenTrend) {

        this( timestamp, currentBg, currentTime, currenTrend, new byte[] {0,1,2});
    }

    OOPResults(long timestamp, int currentBg, int currentTime, TrendArrow currenTrend, byte[] newState) {

        this.currentBg = currentBg;
        this.currentTrend = 0;// Translate currenTrend TODO:
        this.timestamp = timestamp;
        this.currentTime = currentTime;
        this.newState = newState;
        serialNumber="";
   }
    void setHistoricBg(final List<GlucoseValue> historicGlucose) {
        if(historicGlucose == null) {
            return;
        }
        historicBg = new HistoricBg[historicGlucose.size()];
        for (int i = 0; i < historicGlucose.size(); i++) {
            historicBg[i] = new HistoricBg(historicGlucose.get(i));
        }
    }

    String toGson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    static public void HandleData(String oopData) {
        //Log.i(TAG, "HandleData called with " + oopData);
        final Gson gson = new GsonBuilder().create();
        final OOPResultsContainer oOPResults = gson.fromJson(oopData, OOPResultsContainer.class);

    }
}

public class OOPResultsContainer {
    OOPResultsContainer() {
        oOPResultsArray = new OOPResults[0];
        version = 1;
    }

    String toGson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    int version;
    String message;

    OOPResults[] oOPResultsArray;
}