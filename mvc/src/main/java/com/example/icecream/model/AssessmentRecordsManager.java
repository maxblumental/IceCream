package com.example.icecream.model;

import android.databinding.ObservableList;

public interface AssessmentRecordsManager {

    void loadRecords();

    void save(AssessmentRecord record);

    ObservableList<String> getStationIds();

    AssessmentRecord getRecord(String stationId);
}
