package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;

public class StationIdsSpinnerControllerImpl implements StationIdsSpinnerController {

    private AssessmentRecordsManager model;
    private AssessmentRecord assessmentRecord;

    public StationIdsSpinnerControllerImpl(AssessmentRecordsManager model, AssessmentRecord assessmentRecord) {
        this.model = model;
        this.assessmentRecord = assessmentRecord;
    }

    @Override
    public void onItemSelected(int position) {
        String stationId = model.getStationIds().get(position);
        AssessmentRecord record = model.getStationIdToRecord().get(stationId);
        assessmentRecord.fillFrom(record);
    }
}
