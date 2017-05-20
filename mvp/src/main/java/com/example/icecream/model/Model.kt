package com.example.icecream.model

import com.example.firebasedb.AssessmentRecord
import com.example.firebasedb.RxRemoteStorage
import io.reactivex.Completable
import io.reactivex.Single

interface Model {

    fun loadRecords(): Single<List<String>>

    fun reloadRecords(): Single<List<String>>

    fun update(record: AssessmentRecord): Completable

    fun getRecord(stationId: String): AssessmentRecord?

    fun calculateVariance(actual: Int, target: Int): Int

}

class ModelImpl(val remoteStorage: RxRemoteStorage) : Model {

    private var records: Map<String, AssessmentRecord>? = null

    override fun loadRecords(): Single<List<String>> {
        return if (records != null) {
            Single.just(records!!).map { it.keys.toList() }
        } else {
            reloadRecords()
        }
    }

    override fun reloadRecords(): Single<List<String>> {
        return remoteStorage.records
                .doOnSuccess { records = it }
                .map { it.keys.toList() }
    }

    override fun update(record: AssessmentRecord): Completable {
        return remoteStorage.update(record)
    }

    override fun getRecord(stationId: String): AssessmentRecord? {
        return records?.get(stationId)
    }

    override fun calculateVariance(actual: Int, target: Int): Int = actual - target
}
