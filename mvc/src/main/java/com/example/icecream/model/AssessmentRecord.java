package com.example.icecream.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import java.util.Date;

public class AssessmentRecord extends BaseObservable {

    public String stationId;

    public int target;

    public Date date;

    public int actual;

    public int variance;

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

    public void setDate(Date date) {
        this.date = date;
        notifyPropertyChanged(com.example.icecream.BR.date);
    }

    public void setActual(int actual) {
        this.actual = actual;
        notifyPropertyChanged(com.example.icecream.BR.actual);
        updateVariance();
    }

    private void setVariance(int variance) {
        this.variance = variance;
        notifyPropertyChanged(com.example.icecream.BR.variance);
    }

    public void setTarget(int target) {
        this.target = target;
        notifyPropertyChanged(BR.target);
        updateVariance();
    }

    private void updateVariance() {
        int newVariance = actual - target;
        setVariance(newVariance);
    }
}