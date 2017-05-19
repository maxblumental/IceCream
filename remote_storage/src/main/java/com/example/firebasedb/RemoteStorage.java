package com.example.firebasedb;

import java.util.Map;

public interface RemoteStorage {

    void getRecords(RecordsQueryListener listener);

    void updateRecord(AssessmentRecord record, RecordUpdateCompletionListener listener);

    interface RecordsQueryListener {

        void onResult(Map<String, AssessmentRecord> recordMap);

        void onError(Exception e);
    }

    interface RecordUpdateCompletionListener {

        void onComplete();

        void onError(Exception e);
    }
}
