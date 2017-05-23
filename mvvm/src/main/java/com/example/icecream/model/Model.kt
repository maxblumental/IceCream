package com.example.icecream.model

import android.os.Bundle
import com.example.firebasedb.AssessmentRecord
import com.example.firebasedb.RxRemoteStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.Serializable

interface Model {

    fun loadRecords(): Single<List<String>>

    fun reloadRecords(): Single<List<String>>

    fun update(record: AssessmentRecord): Completable

    fun getRecord(stationId: String): AssessmentRecord?

    fun calculateVariance(actual: Int, target: Int): Int

    fun saveState(): Bundle

}

class ModelImpl(private val remoteStorage: RxRemoteStorage,
                private val savedState: Bundle?) : Model {

    private var records: Map<String, AssessmentRecord>? = null

    init {
        if (savedState != null && savedState.containsKey(KEY_RECORDS)) {
            @Suppress("UNCHECKED_CAST")
            records = savedState.getSerializable(KEY_RECORDS) as Map<String, AssessmentRecord>
        }
    }

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

    override fun saveState() =
            Bundle().apply { putSerializable(KEY_RECORDS, records as Serializable) }

}

private const val KEY_RECORDS = "key_records"