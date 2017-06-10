package com.example.modelviewintent.view_state

import com.example.modelviewintent.presenter.AssessmentRecordPresenter
import java.util.*

data class ViewState(val stationId: ViewStateParameter<String>,
                     val date: ViewStateParameter<Date>,
                     val actual: ViewStateParameter<Int>,
                     val target: ViewStateParameter<Int>,
                     val variance: ViewStateParameter<Int>,
                     val varianceDegree: ViewStateParameter<AssessmentRecordPresenter.VarianceDegree>,
                     val recordIds: ViewStateParameter<List<String>>,
                     val isLoading: ViewStateParameter<Boolean>,
                     val error: ViewStateParameter<Exception?>) {

    constructor() : this("".toViewStateParameter(),
            Date().toViewStateParameter(),
            0.toViewStateParameter(),
            0.toViewStateParameter(),
            0.toViewStateParameter(),
            AssessmentRecordPresenter.VarianceDegree.NORMAL.toViewStateParameter(),
            emptyList<String>().toViewStateParameter(),
            false.toViewStateParameter(),
            null.toViewStateParameter<Exception?>())
}

data class ViewStateParameter<out T>(val isUpdated: Boolean, val value: T)

private fun <T> T.toViewStateParameter(isUpdated: Boolean = true) = ViewStateParameter(isUpdated, this)

class ViewStateBuilder(viewState: ViewState) {

    private var stationId: ViewStateParameter<String>
    private var date: ViewStateParameter<Date>
    private var actual: ViewStateParameter<Int>
    private var target: ViewStateParameter<Int>
    private var variance: ViewStateParameter<Int>
    private var varianceDegree: ViewStateParameter<AssessmentRecordPresenter.VarianceDegree>
    private var recordIds: ViewStateParameter<List<String>>
    private var isLoading: ViewStateParameter<Boolean>
    private var error: ViewStateParameter<Exception?>

    init {
        stationId = viewState.stationId.value.toViewStateParameter(false)
        date = viewState.date.value.toViewStateParameter(false)
        actual = viewState.actual.value.toViewStateParameter(false)
        target = viewState.target.value.toViewStateParameter(false)
        variance = viewState.variance.value.toViewStateParameter(false)
        varianceDegree = viewState.varianceDegree.value.toViewStateParameter(false)
        recordIds = viewState.recordIds.value.toViewStateParameter(false)
        isLoading = viewState.isLoading.value.toViewStateParameter(false)
        error = viewState.error.value.toViewStateParameter(false)
    }

    fun setStationId(stationId: String) = apply { this.stationId = stationId.toViewStateParameter(true) }

    fun setDate(date: Date) = apply { this.date = date.toViewStateParameter(true) }

    fun setActual(actual: Int) = apply { this.actual = actual.toViewStateParameter(true) }

    fun setTarget(target: Int) = apply { this.target = target.toViewStateParameter(true) }

    fun setVariance(variance: Int) = apply { this.variance = variance.toViewStateParameter(true) }

    fun setVarianceDegree(varianceDegree: AssessmentRecordPresenter.VarianceDegree) =
            apply { this.varianceDegree = varianceDegree.toViewStateParameter(true) }

    fun setRecordIds(recordIds: List<String>) = apply { this.recordIds = recordIds.toViewStateParameter(true) }

    fun setLoading(isLoading: Boolean) = apply { this.isLoading = isLoading.toViewStateParameter(true) }

    fun setError(error: Exception?) = apply { this.error = error.toViewStateParameter(true) }

    fun build(): ViewState =
            ViewState(stationId, date, actual, target, variance, varianceDegree, recordIds, isLoading, error)
}

fun ViewState.builder() = ViewStateBuilder(this)