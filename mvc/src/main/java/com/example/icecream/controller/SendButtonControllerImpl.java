package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.ObservableAssessmentRecord;

import java.util.Date;

public class SendButtonControllerImpl implements SendButtonController {

    private AssessmentRecordsManager recordsManager;
    private ObservableAssessmentRecord record;

    public SendButtonControllerImpl(AssessmentRecordsManager recordsManager,
                                    ObservableAssessmentRecord record) {
        this.recordsManager = recordsManager;
        this.record = record;
    }

    @Override
    public void onClick() {
        record.setDate(new Date());
        recordsManager.update(record.createRecordCopy());
    }
}
