package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;

public class StationIdsSpinnerControllerImpl implements StationIdsSpinnerController {

    private AssessmentRecordsManager recordsManager;
    private AssessmentRecord record;

    public StationIdsSpinnerControllerImpl(AssessmentRecordsManager recordsManager,
                                           AssessmentRecord record) {
        this.recordsManager = recordsManager;
        this.record = record;
    }

    @Override
    public void onItemSelected(String stationId) {
        AssessmentRecord record = recordsManager.getRecord(stationId);
        this.record.fillFrom(record);
    }
}
