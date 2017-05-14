package com.example.icecream.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

public interface AssessmentRecordsManager {

    void loadRecords();

    void save(AssessmentRecord record);

    ObservableList<String> getStationIds();

    ObservableBoolean isLoading();

    AssessmentRecord getRecord(String stationId);
}
