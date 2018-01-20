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

    int quality;
    int time;
    int bg;
}

public class OOPResults {
    int currentBg;
    int currenTrend;
    HistoricBg [] historicBg;

    OOPResults(int currentBg, TrendArrow currenTrend) {

        this.currentBg = currentBg;
        this.currenTrend = 0;// Translate currenTrend TODO:
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
}
