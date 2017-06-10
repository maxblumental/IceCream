package com.example.modelviewintent

import android.os.Bundle

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.commonui.AssessmentRecordField
import com.example.firebasedb.AssessmentRecord
import com.example.modelviewintent.di.assessment_record.ModelModule
import com.example.modelviewintent.di.assessment_record.PresenterModule
import com.example.modelviewintent.presenter.AssessmentRecordPresenter
import com.example.modelviewintent.utils.parseIntSafe
import com.example.modelviewintent.utils.textChanges
import com.example.modelviewintent.view.AssessmentRecordView
import com.example.modelviewintent.view_state.ViewState
import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AssessmentRecordActivity : AppCompatActivity(), AssessmentRecordView {
    private lateinit var stationsSpinner: Spinner
    private lateinit var stationIdField: AssessmentRecordField
    private lateinit var targetField: AssessmentRecordField
    private lateinit var actualField: AssessmentRecordField
    private lateinit var varianceField: AssessmentRecordField
    private lateinit var dateField: AssessmentRecordField
    private lateinit var sendButton: Button
    private lateinit var refreshLayout: SwipeRefreshLayout

    @Inject lateinit var presenter: AssessmentRecordPresenter

    private val simpleDateFormat = SimpleDateFormat("HH:mm MM/dd/yy", Locale.ENGLISH)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IceCreamApplication.component
                .plus(ModelModule(), PresenterModule())
                .inject(this)

        findViews()

        presenter.attachView(this)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
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

    override fun render(viewState: ViewState) {
        stationsSpinner.adapter =
                ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, viewState.recordIds)
        with(viewState.record) {
            stationIdField.text = stationId
            dateField.text = if (date != null) {
                simpleDateFormat.format(date)
            } else {
                ""
            }
            targetField.text = target.toString()
            actualField.text = actual.toString()
            varianceField.text = variance.toString()
        }
        refreshLayout.isRefreshing = viewState.isLoading
    }

    override val sendRecordIntent: Observable<Unit>
        get() = RxView.clicks(sendButton).map { Unit }

    override val selectRecordIntent: Observable<String>
        get() = RxAdapterView.itemSelections(stationsSpinner)
                .filter { it != AdapterView.INVALID_POSITION }
                .map { stationsSpinner.adapter.getItem(it) as String }

    override val actualChangeIntent: Observable<Int>
        get() = actualField.textChanges()
                .map { it.toString().parseIntSafe() }

    override val targetValue: Observable<Int>
        get() = Observable.just(targetField.text).map(String::parseIntSafe)

    override val refreshIntent: Observable<Unit>
        get() = RxSwipeRefreshLayout.refreshes(refreshLayout).map { Unit }

    override val record: Observable<AssessmentRecord>
        get() = Observable.just(AssessmentRecord(
                stationIdField.text,
                targetField.text.parseIntSafe(),
                Date(),
                actualField.text.parseIntSafe(),
                varianceField.text.parseIntSafe()
        ))

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
