package com.example.icecream.controller;

import com.example.icecream.model.ObservableAssessmentRecord;

public class ActualFieldControllerImpl implements ActualFieldController {

    private ObservableAssessmentRecord record;

    public ActualFieldControllerImpl(ObservableAssessmentRecord record) {
        this.record = record;
    }

    @Override
    public void onChange(String text) {
        int value = getIntegerValue(text);
        record.setActual(value);
    }

    private int getIntegerValue(String text) {
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }
}
