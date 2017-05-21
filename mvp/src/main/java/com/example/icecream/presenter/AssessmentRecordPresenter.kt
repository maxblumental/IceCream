package com.example.icecream.presenter

import android.os.Bundle
import android.support.annotation.ColorRes
import com.example.firebasedb.AssessmentRecord
import com.example.icecream.R
import com.example.icecream.model.Model
import com.example.icecream.view.AssessmentRecordView
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*

interface AssessmentRecordPresenter {

    fun attachView(view: AssessmentRecordView)

    fun detachView()

    fun onActualChanged()

    fun onRefresh()

    fun send()

    fun onStationSelected(stationId: String)

    fun onSaveState(): Bundle

}

class AssessmentRecordPresenterImpl(private val model: Model) : AssessmentRecordPresenter {
    private var view: AssessmentRecordView? = null

    private val disposable: CompositeDisposable = CompositeDisposable()
    private val simpleDateFormat = SimpleDateFormat("HH:mm MM/dd/yy", Locale.ENGLISH)
    override fun attachView(view: AssessmentRecordView) {
        this.view = view

        disposable += model.loadRecords()
                .compose(addRefreshing())
                .subscribe(
                        { list -> view.stationIds = list },
                        { view.showToast("Failed to load records: ${it.message}") }
                )
    }

    override fun detachView() {
        disposable.clear()
        view = null
    }

    override fun onActualChanged() = updateVariance()

    override fun onRefresh() {
        disposable += model.reloadRecords()
                .compose(addRefreshing())
                .subscribe(
                        { list -> view?.stationIds = list },
                        { view?.showToast("Failed to reload records: ${it.message}") }
                )
    }

    override fun send() {
        val view = view ?: return
        val record = AssessmentRecord()
                .apply {
                    stationId = view.stationId
                    date = Date()
                    target = view.target.parseInt()
                    actual = view.actual.parseInt()
                    variance = view.variance.parseInt()
                }
        disposable += model.update(record)
                .subscribe {
                    view.showToast("Updated record for ${record.stationId}")
                }
    }

    override fun onStationSelected(stationId: String) {
        val record = model.getRecord(stationId) ?: return
        view?.fillFrom(record)
    }

    override fun onSaveState() = model.onSaveState()

    private fun AssessmentRecordView.fillFrom(record: AssessmentRecord) {
        stationId = record.stationId
        date = simpleDateFormat.format(record.date)
        target = record.target.toString()
        actual = record.actual.toString()
        variance = record.actual.toString()
        updateVariance()
    }

    @ColorRes
    private fun determineVarianceColor(target: Int, variance: Int): Int {
        val percent = Math.abs(variance.toFloat() / target)
        return if (variance > 0 && percent > 0.05f) {
            R.color.green
        } else if (variance < 0 && percent > 0.1f) {
            R.color.red
        } else {
            R.color.black
        }
    }

    private fun String.parseInt() = try {
        toInt()
    } catch (e: NumberFormatException) {
        0
    }

    private fun updateVariance() {
        val view = view ?: return
        val actual = view.actual.parseInt()
        val target = view.target.parseInt()

        val newVariance = model.calculateVariance(actual, target)
        view.variance = newVariance.toString()

        val varianceColor = determineVarianceColor(target, newVariance)
        view.updateVarianceColor(varianceColor)
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }

    private fun addRefreshing(): (Single<List<String>>) -> Single<List<String>> {
        return { upstream ->
            upstream.doOnSubscribe { view?.isRefreshing = true }
                    .doFinally { view?.isRefreshing = false }
        }
    }
}

