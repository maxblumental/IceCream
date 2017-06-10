package com.example.icecream.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.firebasedb.AssessmentRecord;
import com.example.firebasedb.AssessmentRecordBuilder;
import com.example.icecream.BR;

import java.util.Date;

public class ObservableAssessmentRecord extends BaseObservable {

    private AssessmentRecord record = new AssessmentRecord();

    private VarianceDegree varianceDegree;

    @Bindable
    public String getStationId() {
        return record.getStationId();
    }

    @Bindable
    public int getTarget() {
        return record.getTarget();
    }

    @Bindable
    public Date getDate() {
        return record.getDate();
    }

    @Bindable
    public int getActual() {
        return record.getActual();
    }

    @Bindable
    public int getVariance() {
        return record.getVariance();
    }

    @Bindable
    public VarianceDegree getVarianceDegree() {
        return varianceDegree;
    }

    public void setStationId(String stationId) {
        record.setStationId(stationId);
        notifyPropertyChanged(BR.stationId);
    }

    public void setDate(Date date) {
        record.setDate(date);
        notifyPropertyChanged(BR.date);
    }

    public void setActual(int actual) {
        record.setActual(actual);
        notifyPropertyChanged(BR.actual);
        updateVariance();
    }

    public void setTarget(int target) {
        record.setTarget(target);
        notifyPropertyChanged(BR.target);
        updateVariance();
    }

    public void setVariance(int variance) {
        record.setVariance(variance);
        notifyPropertyChanged(BR.variance);
        updateVarianceDegree();
    }

    public void setVarianceDegree(VarianceDegree varianceDegree) {
        this.varianceDegree = varianceDegree;
        notifyPropertyChanged(BR.varianceDegree);
    }

    public AssessmentRecord createRecordCopy() {
        AssessmentRecord copy = new AssessmentRecord();
        copy.setStationId(record.getStationId());
        copy.setDate(record.getDate());
        copy.setTarget(record.getTarget());
        copy.setActual(record.getActual());
        copy.setVariance(record.getVariance());
        return copy;
    }

    public enum VarianceDegree {
        GOOD, NORMAL, BAD
    }

    private void updateVarianceDegree() {
        int target = record.getTarget();
        if (target == 0) {
            setVarianceDegree(VarianceDegree.NORMAL);
            return;
        }
        int variance = record.getVariance();
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
        setStationId(record.getStationId());
        setDate(record.getDate());
        setTarget(record.getTarget());
        setActual(record.getActual());
    }

    private void updateVariance() {
        int actual = record.getActual();
        int target = record.getTarget();
        int newVariance = actual - target;
        setVariance(newVariance);
    }
}