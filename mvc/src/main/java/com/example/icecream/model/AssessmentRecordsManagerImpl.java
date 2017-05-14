package com.example.icecream.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

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

    private Map<String, AssessmentRecord> stationIdToRecord = new HashMap<>();
    private ObservableList<String> stationIds = new ObservableArrayList<>();

    @Override
    public void loadRecords() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("records")
                .addListenerForSingleValueEvent(new RecordsValueEventListener());
    }

    @Override
    public void save(AssessmentRecord record) {
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
    public AssessmentRecord getRecord(String stationId) {
        return stationIdToRecord.get(stationId);
    }

    private class RecordsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            stationIdToRecord.clear();
            stationIds.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                AssessmentRecord record;
                try {
                    record = snapshot.getValue(AssessmentRecord.class);
                } catch (DatabaseException e) {
                    //todo: handle snapshot conversion to assessment record
                    return;
                }
                stationIdToRecord.put(key, record);
                stationIds.add(key);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //todo: handle records reading cancellation
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
                //todo: handle error
                return;
            }
            updateSessionState();
        }

        private void updateSessionState() {
            stationIdToRecord.put(record.stationId, record);
        }
    }
}
