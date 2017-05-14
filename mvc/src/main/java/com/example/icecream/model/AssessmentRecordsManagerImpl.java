package com.example.icecream.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.example.icecream.NetworkState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AssessmentRecordsManagerImpl implements AssessmentRecordsManager {

    private NetworkState networkState;

    private Map<String, AssessmentRecord> stationIdToRecord = new HashMap<>();
    private ObservableList<String> stationIds = new ObservableArrayList<>();
    private ObservableBoolean isLoading = new ObservableBoolean(false);
    private ObservableBoolean isNetworkAvailable = new ObservableBoolean(true);
    private ObservableField<Error> error = new ObservableField<>();

    public AssessmentRecordsManagerImpl(NetworkState networkState) {
        this.networkState = networkState;
    }

    @Override
    public void loadRecords() {
        if (!ensureNetworkAvailability()) {
            return;
        }
        isLoading.set(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("records")
                .addListenerForSingleValueEvent(new RecordsValueEventListener());
    }

    @Override
    public void save(AssessmentRecord record) {
        if (!ensureNetworkAvailability()) {
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> map = Collections.<String, Object>singletonMap(record.stationId, record);
        database.getReference("records")
                .updateChildren(map, new RecordUpdateCompletionListener(record));
    }

    @Override
    public ObservableList<String> getStationIds() {
        return stationIds;
    }

    @Override
    public ObservableBoolean isLoading() {
        return isLoading;
    }

    @Override
    public ObservableBoolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    @Override
    public AssessmentRecord getRecord(String stationId) {
        return stationIdToRecord.get(stationId);
    }

    @Override
    public ObservableField<Error> getError() {
        return error;
    }

    private class RecordsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            isLoading.set(false);
            stationIdToRecord.clear();
            stationIds.clear();
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
                stationIds.add(key);
            }
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

    private boolean ensureNetworkAvailability() {
        boolean isAvailable = networkState.isNetworkAvailable();
        isNetworkAvailable.set(isAvailable);
        return isAvailable;
    }
}
