package com.example.icecream.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.icecream.BR;

import java.util.Date;

public class AssessmentRecord extends BaseObservable {

    public String stationId;

    public int target;

    public Date date;

    public int actual;

    public int variance;

    public VarianceDegree varianceDegree;

    public AssessmentRecord() {
    }

    @Bindable
    public String getStationId() {
        return stationId;
    }

    @Bindable
    public int getTarget() {
        return target;
    }

    @Bindable
    public Date getDate() {
        return date;
    }

    @Bindable
    public int getActual() {
        return actual;
    }

    @Bindable
    public int getVariance() {
        return variance;
    }

    @Bindable
    public VarianceDegree getVarianceDegree() {
        return varianceDegree;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
        notifyPropertyChanged(BR.stationId);
    }

    public void setDate(Date date) {
        this.date = date;
        notifyPropertyChanged(BR.date);
    }

    public void setActual(int actual) {
        this.actual = actual;
        notifyPropertyChanged(BR.actual);
        updateVariance();
    }

    public void setTarget(int target) {
        this.target = target;
        notifyPropertyChanged(BR.target);
        updateVariance();
    }

    public void setVariance(int variance) {
        this.variance = variance;
        notifyPropertyChanged(BR.variance);
        updateVarianceDegree();
    }

    public void setVarianceDegree(VarianceDegree varianceDegree) {
        this.varianceDegree = varianceDegree;
        notifyPropertyChanged(BR.varianceDegree);
    }

    public AssessmentRecord copy() {
        AssessmentRecord copy = new AssessmentRecord();
        copy.fillFrom(this);
        return copy;
    }

    public enum VarianceDegree {
        GOOD, NORMAL, BAD
    }

    private void updateVarianceDegree() {
        if (target == 0) {
            setVarianceDegree(VarianceDegree.NORMAL);
            return;
        }
        float percent = Math.abs(((float) variance) / target);
        VarianceDegree degree;
        if (variance > 0 && percent > 0.05f) {
            degree = VarianceDegree.GOOD;
        } else if (variance < 0 && percent > 0.1f) {
            degree = VarianceDegree.BAD;
        } else {
            degree = VarianceDegree.NORMAL;
        }
        setVarianceDegree(degree);
    }

    public void fillFrom(AssessmentRecord record) {
        setStationId(record.stationId);
        setDate(record.date);
        setTarget(record.target);
        setActual(record.actual);
        setVariance(record.variance);
    }

    private void updateVariance() {
        int newVariance = actual - target;
        setVariance(newVariance);
    }
}