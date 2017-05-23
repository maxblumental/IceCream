package com.example.icecream.view_model

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.os.Bundle
import com.example.firebasedb.AssessmentRecord
import com.example.icecream.BR
import com.example.icecream.model.Model
import com.example.icecream.other.BindableProperty
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree.BAD
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree.GOOD
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree.NORMAL
import com.example.icecream.wrapper.Lifecycle
import com.example.icecream.wrapper.ToastMaker
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

interface AssessmentRecordViewModel : Observable {

    @get:Bindable var stationIds: List<String>

    @get:Bindable var stationId: String

    @get:Bindable var date: Date

    @get:Bindable var target: Int

    @get:Bindable var actual: Int

    @get:Bindable val variance: Int

    @get:Bindable val varianceDegree: VarianceDegree

    @get:Bindable var isRefreshing: Boolean

    fun onStationSelected(stationId: String)

    fun onSend()

    fun onRefresh()

    fun onActualChange(actualText: String)

    fun saveState(): Bundle

    enum class VarianceDegree {GOOD, NORMAL, BAD }
}

class AssessmentRecordViewModelImpl(private val model: Model,
                                    private val toastMaker: ToastMaker,
                                    lifecycle: Lifecycle) : BaseObservable(), AssessmentRecordViewModel {
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        subscribeToLifecycleEvents(lifecycle)
    }

    override var stationIds: List<String> by BindableProperty(BR.stationIds, emptyList())

    override var stationId: String by BindableProperty(BR.stationId, "")

    override var date: Date by BindableProperty(BR.date, Date())

    override var target: Int by BindableProperty(BR.target, 0) { notifyVarianceChanged() }

    override var actual: Int by BindableProperty(BR.actual, 0) { notifyVarianceChanged() }

    override val variance: Int
        get() = model.calculateVariance(actual, target)

    override val varianceDegree: VarianceDegree
        get() = assessVarianceDegree()

    override var isRefreshing: Boolean by BindableProperty(BR.refreshing, false)

    override fun onStationSelected(stationId: String) {
        val record = model.getRecord(stationId) ?: return
        fillForm(record)
    }

    override fun onSend() {
        val record = readForm()
        disposable += model.update(record)
                .subscribe({ toastMaker.show("Updated record for ${record.stationId}") },
                        { toastMaker.show("Failed to update record for ${record.stationId}: ${it.message}") })
    }

    override fun onActualChange(actualText: String) {
        this.actual = actualText.parseInt()
    }

    override fun onRefresh() {
        disposable += model.reloadRecords()
                .compose(addRefreshing())
                .subscribe({ list -> stationIds = list },
                        { toastMaker.show("Failed to reload records: ${it.message}") })
    }

    override fun saveState() = model.saveState()

    private fun initialize() {
        disposable += model.loadRecords()
                .compose(addRefreshing())
                .subscribe({ stationIds = it },
                        { toastMaker.show("Failed to load records: ${it.message}") })
    }

    private fun assessVarianceDegree(): VarianceDegree {
        val variance = variance
        val percent = Math.abs(variance.toFloat()) / target
        return if (variance > 0 && percent > 0.05f) {
            GOOD
        } else if (variance < 0 && percent > 0.1f) {
            BAD
        } else {
            NORMAL
        }
    }

    private fun fillForm(record: AssessmentRecord) {
        stationId = record.stationId
        date = record.date
        target = record.target
        actual = record.actual
    }

    private fun readForm(): AssessmentRecord {
        val record = AssessmentRecord()
        record.stationId = stationId
        record.date = Date()
        record.target = target
        record.actual = actual
        record.variance = variance
        return record
    }

    private fun addRefreshing(): (Single<List<String>>) -> Single<List<String>> {
        return { upstream ->
            upstream.doOnSubscribe { isRefreshing = true }
                    .doFinally { isRefreshing = false }
        }
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }

    private fun notifyVarianceChanged() {
        notifyPropertyChanged(BR.variance)
        notifyPropertyChanged(BR.varianceDegree)
    }

    private fun String.parseInt() = try {
        toInt()
    } catch (e: NumberFormatException) {
        0
    }

    private fun subscribeToLifecycleEvents(lifecycle: Lifecycle) {
        disposable += lifecycle.events()
                .subscribe { event ->
                    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                    when (event) {
                        Lifecycle.Event.CREATE -> initialize()
                        Lifecycle.Event.DESTROY -> disposable.clear()
                    }
                }
    }

}
