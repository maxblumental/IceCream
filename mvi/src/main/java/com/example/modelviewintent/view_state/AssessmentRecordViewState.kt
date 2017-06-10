package com.example.modelviewintent.view_state

import com.example.firebasedb.AssessmentRecord

class ViewState(val record: AssessmentRecord,
                val recordIds: List<String>,
                val isLoading: Boolean,
                val error: Exception?)

class ViewStateBuilder(viewState: ViewState) {

    private var record: AssessmentRecord = AssessmentRecord()
    private var recordIds: List<String> = emptyList()
    private var isLoading: Boolean = false
    private var error: Exception? = null

    init {
        record = viewState.record
        recordIds = viewState.recordIds
        isLoading = viewState.isLoading
        error = viewState.error
    }

    fun setRecord(record: AssessmentRecord) = apply { this.record = record }

    fun setRecordIds(recordIds: List<String>) = apply { this.recordIds = recordIds }

    fun setLoading(isLoading: Boolean) = apply { this.isLoading = isLoading }

    fun setError(error: Exception?) = apply { this.error = error }

    fun build(): ViewState = ViewState(record, recordIds, isLoading, error)
}

fun ViewState.builder() = ViewStateBuilder(this)