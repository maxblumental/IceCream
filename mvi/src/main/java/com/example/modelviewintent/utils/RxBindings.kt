package com.example.modelviewintent.utils

import android.widget.TextView
import com.example.commonui.AssessmentRecordField
import com.example.modelviewintent.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

fun AssessmentRecordField.textChanges(): Observable<CharSequence> {
    val field = findViewById(R.id.field) as TextView
    return RxTextView.textChanges(field)
}