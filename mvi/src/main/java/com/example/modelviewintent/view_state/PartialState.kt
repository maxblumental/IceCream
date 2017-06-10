package com.example.modelviewintent.view_state

import com.example.firebasedb.AssessmentRecord
import com.example.modelviewintent.utils.builder

interface NextStateProducer {
    fun produceNext(viewState: ViewState): ViewState
}

sealed class PartialState : NextStateProducer {

    class Variance(val variance: Int) : PartialState() {
        override fun produceNext(viewState: ViewState) =
                with(viewState) {
                    builder()
                            .setRecord(
                                    record.builder()
                                            .setVariance(variance)
                                            .build()
                            )
                            .build()
                }

    }

    class Record(val record: AssessmentRecord?) : PartialState() {
        override fun produceNext(viewState: ViewState) =
                if (record != null) {
                    viewState.builder()
                            .setRecord(record)
                            .build()
                } else {
                    viewState
                }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Record

            if (record != other.record) return false

            return true
        }

        override fun hashCode(): Int {
            return record?.hashCode() ?: 0
        }

    }

    class Empty : PartialState() {
        override fun produceNext(viewState: ViewState) =
                viewState.builder()
                        .setRecord(AssessmentRecord())
                        .setRecordIds(emptyList())
                        .setError(null)
                        .setLoading(false)
                        .build()
    }

    class RecordsLoaded(private val records: List<AssessmentRecord>) : PartialState() {
        override fun produceNext(viewState: ViewState) = viewState.builder()
                .setLoading(false)
                .setRecordIds(records.map { it.stationId })
                .setRecord(records.first())
                .setError(null)
                .build()
    }

    class Error(private val exception: Exception) : PartialState() {
        override fun produceNext(viewState: ViewState) = viewState.builder()
                .setLoading(false)
                .setError(exception)
                .build()
    }

    class Loading : PartialState() {
        override fun produceNext(viewState: ViewState) = viewState.builder()
                .setLoading(true)
                .build()
    }
}