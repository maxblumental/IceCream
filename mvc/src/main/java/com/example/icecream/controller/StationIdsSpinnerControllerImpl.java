package com.example.icecream.controller;

import com.example.firebasedb.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.ObservableAssessmentRecord;

public class StationIdsSpinnerControllerImpl implements StationIdsSpinnerController {

    private AssessmentRecordsManager recordsManager;
    private ObservableAssessmentRecord record;

    public StationIdsSpinnerControllerImpl(AssessmentRecordsManager recordsManager,
                                           ObservableAssessmentRecord record) {
        this.recordsManager = recordsManager;
        this.record = record;
    }

    @Override
    public void onItemSelected(String stationId) {
        AssessmentRecord record = recordsManager.getRecord(stationId);
        this.record.fillFrom(record);
    }
}
