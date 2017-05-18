package com.example.icecream.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.icecream.BR;
import com.example.icecream.network.NetworkStateMonitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssessmentRecordsManagerImpl extends BaseObservable implements AssessmentRecordsManager {

    private final String KEY_RECORDS = "key_records";

    private NetworkStateMonitor networkStateMonitor;
    private RemoteStorage remoteStorage;

    private Map<String, AssessmentRecord> stationIdToRecord;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private ObservableField<Error> error = new ObservableField<>();

    @SuppressWarnings("unchecked")
    public AssessmentRecordsManagerImpl(NetworkStateMonitor networkStateMonitor,
                                        RemoteStorage remoteStorage,
                                        @Nullable Bundle savedState) {
        this.networkStateMonitor = networkStateMonitor;
        this.remoteStorage = remoteStorage;
        if (savedState != null && savedState.containsKey(KEY_RECORDS)) {
            stationIdToRecord = (Map<String, AssessmentRecord>) savedState.getSerializable(KEY_RECORDS);
        }
    }

    @Override
    public void loadRecords() {
        if (stationIdToRecord == null) {
            loadRecordsFromServer();
        }
    }

    @Override
    public void reloadRecords() {
        loadRecordsFromServer();
    }

    private void loadRecordsFromServer() {
        isLoading.set(true);
        if (!networkStateMonitor.checkNetworkAvailability()) {
            isLoading.set(false);
            return;
        }
        remoteStorage.getRecords(new RecordsQueryListenerImpl());
    }

    @Override
    public void update(AssessmentRecord record) {
        if (!networkStateMonitor.checkNetworkAvailability()) {
            return;
        }
        remoteStorage.updateRecord(record, new RecordUpdateCompletionListenerImpl());
    }

    @Override
    public List<String> getStationIds() {
        if (stationIdToRecord == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(stationIdToRecord.keySet());
    }

    @Override
    public ObservableBoolean isLoading() {
        return isLoading;
    }

    @Override
    public AssessmentRecord getRecord(String stationId) {
        return stationIdToRecord.get(stationId);
    }

    @Override
    public Bundle getSavedState() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_RECORDS, (Serializable) stationIdToRecord);
        return bundle;
    }

    @Override
    public ObservableField<Error> getError() {
        return error;
    }

    private class RecordsQueryListenerImpl implements RemoteStorage.RecordsQueryListener {

        @Override
        public void onResult(Map<String, AssessmentRecord> recordMap) {
            isLoading.set(false);
            stationIdToRecord = recordMap;
            notifyPropertyChanged(BR.stationIds);
        }

        @Override
        public void onError(Exception exception) {
            isLoading.set(false);
            setError(Error.RECORDS_LOAD_ERROR, exception);
        }
    }

    private class RecordUpdateCompletionListenerImpl implements RemoteStorage.RecordUpdateCompletionListener {

        @Override
        public void onComplete(AssessmentRecord record) {
            stationIdToRecord.put(record.stationId, record);
        }

        @Override
        public void onError(Exception exception) {
            setError(Error.UPDATE_RECORD_ERROR, exception);
        }
    }

    private void setError(Error error, Exception exception) {
        error.setException(exception);
        this.error.set(error);
    }
}
