package com.example.modelviewintent

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner

import com.example.commonui.AssessmentRecordField

class AssessmentRecordActivity : AppCompatActivity() {

    private lateinit var stationsSpinner: Spinner
    private lateinit var stationIdField: AssessmentRecordField
    private lateinit var targetField: AssessmentRecordField
    private lateinit var actualField: AssessmentRecordField
    private lateinit var varianceField: AssessmentRecordField
    private lateinit var dateField: AssessmentRecordField
    private lateinit var sendButton: Button
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
    }

    private fun findViews() {
        stationsSpinner = findViewById(R.id.stationIds) as Spinner
        stationIdField = findViewById(R.id.stationId) as AssessmentRecordField
        targetField = findViewById(R.id.target) as AssessmentRecordField
        actualField = findViewById(R.id.actual) as AssessmentRecordField
        varianceField = findViewById(R.id.variance) as AssessmentRecordField
        dateField = findViewById(R.id.date) as AssessmentRecordField
        sendButton = findViewById(R.id.sendButton) as Button
        refreshLayout = findViewById(R.id.refreshLayout) as SwipeRefreshLayout
    }
}
