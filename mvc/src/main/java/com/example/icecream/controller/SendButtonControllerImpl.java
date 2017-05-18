package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;

import java.util.Date;

public class SendButtonControllerImpl implements SendButtonController {

    private AssessmentRecordsManager recordsManager;
    private AssessmentRecord record;

    public SendButtonControllerImpl(AssessmentRecordsManager recordsManager,
                                    AssessmentRecord record) {
        this.recordsManager = recordsManager;
        this.record = record;
    }

    @Override
    public void onClick() {
        record.setDate(new Date());
        recordsManager.update(record.copy());
    }
}
