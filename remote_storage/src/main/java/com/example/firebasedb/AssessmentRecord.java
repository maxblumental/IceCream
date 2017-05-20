package com.example.firebasedb;

import java.util.Date;

public class AssessmentRecord {

    private String stationId;

    private int target;

    private Date date;

    private int actual;

    private int variance;

    public AssessmentRecord() {
    }

    public AssessmentRecord(String stationId, int target, Date date, int actual, int variance) {
        this.stationId = stationId;
        this.target = target;
        this.date = date;
        this.actual = actual;
        this.variance = variance;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    public int getVariance() {
        return variance;
    }

    public void setVariance(int variance) {
        this.variance = variance;
    }
}