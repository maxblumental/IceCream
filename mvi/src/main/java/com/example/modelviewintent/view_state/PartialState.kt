package com.example.modelviewintent.view_state

import com.example.firebasedb.AssessmentRecord
import com.example.modelviewintent.presenter.AssessmentRecordPresenter

interface NextStateProducer {
    fun produceNext(viewState: ViewState): ViewState
}

sealed class PartialState : NextStateProducer {

    class Variance(val variance: Int,
                   val varianceDegree: AssessmentRecordPresenter.VarianceDegree) : PartialState() {
        override fun produceNext(viewState: ViewState) =
                viewState.builder()
                        .setVariance(variance)
                        .setVarianceDegree(varianceDegree)
                        .build()

    }

    class Record(val record: AssessmentRecord?) : PartialState() {
        override fun produceNext(viewState: ViewState) =
                if (record != null) {
                    with(record) {
                        viewState.builder()
                                .setStationId(stationId)
                                .setDate(date)
                                .setActual(actual)
                                .setTarget(target)
                                .setVariance(variance)
                                .build()
                    }
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

    class RecordsLoaded(private val records: List<AssessmentRecord>) : PartialState() {
        override fun produceNext(viewState: ViewState): ViewState {
            val record = records.first()
            return viewState.builder()
                    .setStationId(record.stationId)
                    .setDate(record.date)
                    .setActual(record.actual)
                    .setTarget(record.target)
                    .setVariance(record.variance)
                    .setLoading(false)
                    .setRecordIds(records.map { it.stationId })
                    .setError(null)
                    .build()
        }
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