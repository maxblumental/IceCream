package com.example.icecream.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Bundle;

import com.example.icecream.BR;
import com.example.icecream.NetworkState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AssessmentRecordsManagerImpl extends BaseObservable implements AssessmentRecordsManager {

    private final String KEY_RECORDS = "key_records";

    private NetworkState networkState;

    private LinkedHashMap<String, AssessmentRecord> stationIdToRecord;
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private ObservableField<Error> error = new ObservableField<>();

    @SuppressWarnings("unchecked")
    public AssessmentRecordsManagerImpl(NetworkState networkState, Bundle savedState) {
        this.networkState = networkState;
        if (savedState != null) {
            stationIdToRecord = (LinkedHashMap<String, AssessmentRecord>) savedState.getSerializable(KEY_RECORDS);
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
        if (!networkState.checkNetworkAvailability()) {
            isLoading.set(false);
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("records")
                .addListenerForSingleValueEvent(new RecordsValueEventListener());
    }

    @Override
    public void save(AssessmentRecord record) {
        if (!networkState.checkNetworkAvailability()) {
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> map = Collections.<String, Object>singletonMap(record.stationId, record);
        database.getReference("records")
                .updateChildren(map, new RecordUpdateCompletionListener(record));
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
        bundle.putSerializable(KEY_RECORDS, stationIdToRecord);
        return bundle;
    }

    @Override
    public ObservableField<Error> getError() {
        return error;
    }

    private class RecordsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            isLoading.set(false);
            stationIdToRecord = new LinkedHashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                AssessmentRecord record;
                try {
                    record = snapshot.getValue(AssessmentRecord.class);
                } catch (DatabaseException e) {
                    setRecordsLoadError(e);
                    return;
                }
                stationIdToRecord.put(key, record);
            }
            notifyPropertyChanged(BR.stationIds);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            isLoading.set(false);
            setRecordsLoadError(databaseError.toException());
        }

        private void setRecordsLoadError(Exception exception) {
            setError(Error.RECORDS_LOAD_ERROR, exception);
        }
    }

    private class RecordUpdateCompletionListener implements DatabaseReference.CompletionListener {

        private AssessmentRecord record;

        RecordUpdateCompletionListener(AssessmentRecord record) {
            this.record = record;
        }

        @Override
        public void onComplete(DatabaseError error, DatabaseReference reference) {
            if (error != null) {
                setSaveRecordError(error.toException());
                return;
            }
            updateSessionState();
        }

        private void updateSessionState() {
            stationIdToRecord.put(record.stationId, record);
        }

        private void setSaveRecordError(Exception exception) {
            setError(Error.SAVE_RECORD_ERROR, exception);
        }
    }

    private void setError(Error error, Exception exception) {
        error.setException(exception);
        this.error.set(error);
    }
}
