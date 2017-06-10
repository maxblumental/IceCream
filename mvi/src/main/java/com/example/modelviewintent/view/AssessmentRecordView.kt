package com.example.modelviewintent.view

import com.example.firebasedb.AssessmentRecord
import com.example.modelviewintent.view_state.ViewState
import io.reactivex.Observable

interface AssessmentRecordView {
    fun render(viewState: ViewState)

    val sendRecordIntent: Observable<Unit>

    val selectRecordIntent: Observable<String>

    val actualChangeIntent: Observable<Int>

    val refreshIntent: Observable<Unit>

    val targetValue: Observable<Int>

    val record: Observable<AssessmentRecord>

    fun showToast(message: String)
}