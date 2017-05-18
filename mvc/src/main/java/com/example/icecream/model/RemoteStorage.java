package com.example.icecream.model;

import java.util.Map;

public interface RemoteStorage {

    void getRecords(RecordsQueryListener listener);

    void updateRecord(AssessmentRecord record, RecordUpdateCompletionListener listener);

    interface RecordsQueryListener {

        void onResult(Map<String, AssessmentRecord> recordMap);

        void onError(Exception e);
    }

    interface RecordUpdateCompletionListener {

        void onComplete(AssessmentRecord updatedRecord);

        void onError(Exception e);
    }
}