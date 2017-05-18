package com.example.icecream.model;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;

import java.util.List;

public interface AssessmentRecordsManager extends Observable {

    void loadRecords();

    void reloadRecords();

    void update(AssessmentRecord record);

    AssessmentRecord getRecord(String stationId);

    @Bindable
    List<String> getStationIds();

    Bundle getSavedState();

    ObservableBoolean isLoading();

    ObservableField<Error> getError();

    enum Error {
        RECORDS_LOAD_ERROR,
        UPDATE_RECORD_ERROR;

        private Exception exception;

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }
}
