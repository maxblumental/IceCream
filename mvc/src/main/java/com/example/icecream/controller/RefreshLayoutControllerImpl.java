package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecordsManager;

public class RefreshLayoutControllerImpl implements RefreshLayoutController {

    private AssessmentRecordsManager recordsManager;

    public RefreshLayoutControllerImpl(AssessmentRecordsManager recordsManager) {
        this.recordsManager = recordsManager;
    }

    @Override
    public void onRefresh() {
        recordsManager.loadRecords();
    }
}