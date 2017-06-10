package com.example.firebasedb;

import java.util.Date;

public class AssessmentRecordBuilder {
    private String stationId;
    private int target;
    private Date date;
    private int actual;
    private int variance;

    public AssessmentRecordBuilder(AssessmentRecord record) {
        this.stationId = record.getStationId();
        this.target = record.getTarget();
        this.date = record.getDate();
        this.actual = record.getActual();
        this.variance = record.getVariance();
    }

    public AssessmentRecordBuilder setStationId(String stationId) {
        this.stationId = stationId;
        return this;
    }

    public AssessmentRecordBuilder setTarget(int target) {
        this.target = target;
        return this;
    }

    public AssessmentRecordBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public AssessmentRecordBuilder setActual(int actual) {
        this.actual = actual;
        return this;
    }

    public AssessmentRecordBuilder setVariance(int variance) {
        this.variance = variance;
        return this;
    }

    public AssessmentRecord build() {
        return new AssessmentRecord(stationId, target, date, actual, variance);
    }
}