package com.example.icecream.model;

import android.databinding.ObservableList;
import android.databinding.ObservableMap;

public interface Model {

    void loadRecords();

    void save(int position);

    ObservableList<String> getStationIds();

    ObservableMap<String, AssessmentRecord> getStationIdToRecord();
}
