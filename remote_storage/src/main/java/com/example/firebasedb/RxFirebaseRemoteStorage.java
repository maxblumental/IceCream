package com.example.firebasedb;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.NonNull;

public class RxFirebaseRemoteStorage implements RxRemoteStorage {

    private RemoteStorage remoteStorage;

    public RxFirebaseRemoteStorage() {
        remoteStorage = new FirebaseRemoteStorage();
    }

    @Override
    public Single<Map<String, AssessmentRecord>> getRecords() {
        return Single.create(new SingleOnSubscribe<Map<String, AssessmentRecord>>() {
            @Override
            public void subscribe(@NonNull final SingleEmitter<Map<String, AssessmentRecord>> emitter) throws Exception {
                remoteStorage.getRecords(new RemoteStorage.RecordsQueryListener() {
                    @Override
                    public void onResult(Map<String, AssessmentRecord> recordMap) {
                        emitter.onSuccess(recordMap);
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.onError(e);
                    }
                });
            }
        });
    }

    @Override
    public Completable update(final AssessmentRecord record) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter emitter) throws Exception {
                remoteStorage.updateRecord(record, new RemoteStorage.RecordUpdateCompletionListener() {
                    @Override
                    public void onComplete() {
                        emitter.onComplete();
                    }

                    @Override
                    public void onError(Exception e) {
                        emitter.onError(e);
                    }
                });
            }
        });
    }
}
