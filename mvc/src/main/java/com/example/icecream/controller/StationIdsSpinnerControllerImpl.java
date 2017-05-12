package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.Model;
import com.example.icecream.databinding.ActivityAssessmentRecordBinding;

public class StationIdsSpinnerControllerImpl implements StationIdsSpinnerController {

    private ActivityAssessmentRecordBinding binding;

    public StationIdsSpinnerControllerImpl(ActivityAssessmentRecordBinding binding) {
        this.binding = binding;
    }

    @Override
    public void onItemSelected(int position) {
        Model model = binding.getModel();
        String stationId = model.getStationIds().get(position);
        AssessmentRecord record = model.getStationIdToRecord().get(stationId);
        binding.setRecord(record);
    }
}
