package com.example.icecream.view

import android.support.annotation.ColorRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.commonui.AssessmentRecordField
import com.example.icecream.R
import com.example.icecream.presenter.AssessmentRecordPresenter
import kotlin.reflect.KProperty

interface AssessmentRecordView {

    fun showToast(text: String)

    var stationId: String

    var date: String

    var target: String

    var actual: String

    var variance: String

    var stationIds: List<String>

    fun updateVarianceColor(@ColorRes color: Int)

    var isRefreshing: Boolean

}

class AssessmentRecordViewImpl(private val presenter: AssessmentRecordPresenter,
                               rootView: ViewGroup) : AssessmentRecordView {

    private val stationIdField: AssessmentRecordField
    private val dateField: AssessmentRecordField
    private val targetField: AssessmentRecordField
    private val actualField: AssessmentRecordField
    private val varianceField: AssessmentRecordField
    private val stationIdsSpinner: Spinner
    private val refreshLayout: SwipeRefreshLayout
    private val sendButton: Button

    private val context = rootView.context

    init {

        stationIdField = rootView.findViewById(R.id.stationId) as AssessmentRecordField
        dateField = rootView.findViewById(R.id.date) as AssessmentRecordField
        targetField = rootView.findViewById(R.id.target) as AssessmentRecordField
        actualField = rootView.findViewById(R.id.actual) as AssessmentRecordField
        varianceField = rootView.findViewById(R.id.variance) as AssessmentRecordField
        stationIdsSpinner = rootView.findViewById(R.id.stationIds) as Spinner
        refreshLayout = rootView.findViewById(R.id.refreshLayout) as SwipeRefreshLayout
        sendButton = rootView.findViewById(R.id.sendButton) as Button

        stationIdsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val stationId = parent.selectedItem as String
                presenter.onStationSelected(stationId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        actualField.setOnValueChangeListener { presenter.onActualChanged() }

        refreshLayout.setOnRefreshListener { presenter.onRefresh() }

        sendButton.setOnClickListener { presenter.send() }
    }

    override fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    override var stationId: String by stationIdField

    override var date: String by dateField

    override var target: String by targetField

    override var actual by actualField

    override var variance by varianceField

    override var stationIds: List<String> by stationIdsSpinner

    override fun updateVarianceColor(color: Int) {
        varianceField.setValueColor(color)
    }

    override var isRefreshing: Boolean
        get() = refreshLayout.isRefreshing
        set(value) {
            refreshLayout.isRefreshing = value
        }
}

private operator fun Spinner.setValue(any: Any, property: KProperty<*>, list: List<String>) {
    adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
}

private operator fun Spinner.getValue(any: Any, property: KProperty<*>): List<String> {
    return (0..adapter.count - 1)
            .toList()
            .map { adapter.getItem(it) as String }
}

private operator fun AssessmentRecordField.setValue(any: Any?, property: KProperty<*>, s: String) {
    text = s
}

private operator fun AssessmentRecordField.getValue(any: Any?, property: KProperty<*>): String {
    return text
}
