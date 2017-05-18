package com.example.icecream.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class FirebaseDatabaseWrapper implements DatabaseWrapper {

    @Override
    public void getRecords(RecordsQueryListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("records")
                .addListenerForSingleValueEvent(new RecordsValueEventListener(listener));
    }

    @Override
    public void updateRecord(AssessmentRecord record, RecordUpdateCompletionListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Map<String, Object> recordUpdate =
                Collections.<String, Object>singletonMap(record.stationId, record);
        database.getReference("records")
                .updateChildren(recordUpdate, new RecordUpdateListener(listener, record));
    }

    private class RecordsValueEventListener implements ValueEventListener {

        private RecordsQueryListener listener;

        RecordsValueEventListener(RecordsQueryListener listener) {
            this.listener = listener;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            LinkedHashMap<String, AssessmentRecord> map = new LinkedHashMap<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                AssessmentRecord record;
                try {
                    record = snapshot.getValue(AssessmentRecord.class);
                } catch (DatabaseException e) {
                    return;
                }
                map.put(key, record);
            }
            listener.onResult(map);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            listener.onError(databaseError.toException());
        }
    }

    private class RecordUpdateListener implements DatabaseReference.CompletionListener {

        private RecordUpdateCompletionListener listener;
        private AssessmentRecord record;

        RecordUpdateListener(RecordUpdateCompletionListener listener, AssessmentRecord record) {
            this.listener = listener;
            this.record = record;
        }

        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            if (databaseError != null) {
                listener.onError(databaseError.toException());
                return;
            }

            listener.onComplete(record);
        }
    }
}
