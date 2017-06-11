package com.example.modelviewintent.interactor

import android.os.Bundle
import android.util.Log
import com.example.firebasedb.AssessmentRecord
import com.example.firebasedb.RxRemoteStorage
import io.reactivex.Completable
import io.reactivex.Single
import java.io.Serializable

interface RecordsInteractor {

    fun loadRecords(): Single<List<AssessmentRecord>>

    fun sendRecord(record: AssessmentRecord): Completable

    fun getRecord(stationId: String): AssessmentRecord?

    fun calculateVariance(actual: Int, target: Int): Int

    fun saveState(): Bundle
}

class RecordsInteractorImpl(private val storage: RxRemoteStorage,
                            savedState: Bundle?) : RecordsInteractor {
    private var cache: Map<String, AssessmentRecord> = emptyMap()

    init {
        if (savedState != null && savedState.containsKey(KEY_RECORDS)) {
            @Suppress("UNCHECKED_CAST")
            cache = savedState.getSerializable(KEY_RECORDS) as Map<String, AssessmentRecord>
        }
    }

    private val lock = Any()

    override fun loadRecords(): Single<List<AssessmentRecord>> {
        return storage.records
                .doOnSuccess {
                    synchronized(lock) {
                        cache = it
                    }
                }
                .doOnError {
                    Log.d(javaClass.simpleName, "storage.records error: ${it.message}")
                }
                .map { it.values.toList() }
    }

    override fun sendRecord(record: AssessmentRecord): Completable {
        return storage.update(record)
    }

    override fun getRecord(stationId: String): AssessmentRecord? = synchronized(lock) {
        cache[stationId]
    }

    override fun calculateVariance(actual: Int, target: Int) = actual - target

    override fun saveState(): Bundle = Bundle().apply { putSerializable(KEY_RECORDS, cache as Serializable) }
}

private const val KEY_RECORDS = "key_records"