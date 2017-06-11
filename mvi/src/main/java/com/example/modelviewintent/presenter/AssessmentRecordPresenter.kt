package com.example.modelviewintent.presenter

import android.os.Bundle
import android.util.Log
import com.example.modelviewintent.interactor.RecordsInteractor
import com.example.modelviewintent.presenter.AssessmentRecordPresenter.VarianceDegree.BAD
import com.example.modelviewintent.presenter.AssessmentRecordPresenter.VarianceDegree.GOOD
import com.example.modelviewintent.presenter.AssessmentRecordPresenter.VarianceDegree.NORMAL
import com.example.modelviewintent.view.AssessmentRecordView
import com.example.modelviewintent.view_state.PartialState
import com.example.modelviewintent.view_state.ViewState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

interface AssessmentRecordPresenter {

    fun attachView(view: AssessmentRecordView)

    fun detachView()

    fun onSaveState(): Bundle

    enum class VarianceDegree {GOOD, NORMAL, BAD }
}

class AssessmentRecordPresenterImpl(private val interactor: RecordsInteractor) : AssessmentRecordPresenter {

    private var view: AssessmentRecordView? = null
    private var disposable: CompositeDisposable? = null

    override fun attachView(view: AssessmentRecordView) {
        this.view = view
        disposable = CompositeDisposable()

        val actualChange = view.actualChangeIntent
                .doOnNext { Log.d(javaClass.simpleName, "intent: actual change") }
                .flatMap { actual ->
                    view.targetValue
                            .map { target ->
                                target to interactor.calculateVariance(actual, target)
                            }
                }
                .map {
                    val (target, variance) = it
                    variance to assessVarianceDegree(target, variance)
                }
                .map {
                    val (variance, varianceDegree) = it
                    PartialState.Variance(variance, varianceDegree)
                }

        val recordSelection = view.selectRecordIntent
                .doOnNext { Log.d(javaClass.simpleName, "intent: record selection") }
                .map { stationId -> interactor.getRecord(stationId) }
                .map { record -> PartialState.Record(record) }
                .distinctUntilChanged()

        val refreshes = view.refreshIntent
                .doOnNext { Log.d(javaClass.simpleName, "intent: refresh") }
                .flatMap { load() }

        disposable += view.sendRecordIntent
                .doOnNext { Log.d(javaClass.simpleName, "intent: send record") }
                .flatMap { view.record }
                .flatMap { record ->
                    interactor.sendRecord(record)
                            .toSingleDefault(record)
                            .toObservable()
                }
                .subscribe({ view.showToast("Sent record for ${it.stationId}") },
                        { Log.e(javaClass.simpleName, it.message, it) })


        disposable += Observable.merge(actualChange, load(), recordSelection, refreshes)
                .doOnNext { Log.d(javaClass.simpleName, "Partial=${it.javaClass.simpleName}") }
                .scan(ViewState(), { prev, partial -> partial.produceNext(prev) })
                .distinctUntilChanged()
                .subscribe({ view.render(it) },
                        { Log.e(javaClass.simpleName, it.message, it) })
    }

    override fun detachView() {
        disposable?.dispose()
        view = null
    }

    override fun onSaveState(): Bundle = interactor.saveState()

    private fun load(): Observable<PartialState> {
        return interactor.loadRecords()
                .map { PartialState.RecordsLoaded(it) as PartialState }
                .onErrorReturn { PartialState.Error(it as Exception) }
                .toObservable()
                .startWith(PartialState.Loading())
    }

    private operator fun CompositeDisposable?.plusAssign(disposable: Disposable) {
        this?.add(disposable)
    }

    private fun assessVarianceDegree(target: Int, variance: Int): AssessmentRecordPresenter.VarianceDegree {
        val percent = Math.abs(variance.toFloat()) / target
        return if (variance > 0 && percent > 0.05f) {
            GOOD
        } else if (variance < 0 && percent > 0.1f) {
            BAD
        } else {
            NORMAL
        }
    }
}
