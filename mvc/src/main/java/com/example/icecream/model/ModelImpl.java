package com.example.icecream.model;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableArrayMap;
import android.databinding.ObservableList;
import android.databinding.ObservableMap;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ModelImpl implements Model {

    private ObservableMap<String, AssessmentRecord> stationIdToRecord = new ObservableArrayMap<>();
    private ObservableList<String> stationIds = new ObservableArrayList<>();

    @Override
    public void loadRecords() {
        Log.d("investigation", "loadRecords()");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("records")
                .addListenerForSingleValueEvent(new RecordsValueEventListener());
    }

    @Override
    public void save(int position) {
        AssessmentRecord record = stationIdToRecord.get(stationIds.get(position));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> map = Collections.<String, Object>singletonMap(record.stationId, record);
        database.getReference("records")
                .updateChildren(map, new RecordUpdateCompletionListener(record));
    }

    public ObservableList<String> getStationIds() {
        return stationIds;
    }

    public ObservableMap<String, AssessmentRecord> getStationIdToRecord() {
        return stationIdToRecord;
    }

    private class RecordsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d("investigation", "onDataChange");
            stationIdToRecord.clear();
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
            }

            ArrayList<String> list = new ArrayList<>(stationIdToRecord.keySet());
            Collections.sort(list);
            stationIds.clear();
            stationIds.addAll(list);
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
