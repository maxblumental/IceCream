package com.example.firebasedb;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface RxRemoteStorage {

    Single<Map<String, AssessmentRecord>> getRecords();

    Completable update(AssessmentRecord record);
}
