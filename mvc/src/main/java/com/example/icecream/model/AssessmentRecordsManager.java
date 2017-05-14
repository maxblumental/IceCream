package com.example.icecream.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

public interface AssessmentRecordsManager {

    void loadRecords();

    void save(AssessmentRecord record);

    ObservableList<String> getStationIds();

    ObservableBoolean isLoading();

    ObservableBoolean isNetworkAvailable();

    AssessmentRecord getRecord(String stationId);

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
