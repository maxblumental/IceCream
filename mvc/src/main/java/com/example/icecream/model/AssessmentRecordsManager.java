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

    void save(AssessmentRecord record);

    @Bindable
    List<String> getStationIds();

    ObservableBoolean isLoading();

    AssessmentRecord getRecord(String stationId);

    Bundle getSavedState();

    ObservableField<Error> getError();

    enum Error {
        RECORDS_LOAD_ERROR,
        SAVE_RECORD_ERROR;

        private Exception exception;

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }
}
