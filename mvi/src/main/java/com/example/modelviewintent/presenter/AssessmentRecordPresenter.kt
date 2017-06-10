package com.example.modelviewintent.presenter

import android.util.Log
import com.example.firebasedb.AssessmentRecord
import com.example.modelviewintent.interactor.RecordsInteractor
import com.example.modelviewintent.view.AssessmentRecordView
import com.example.modelviewintent.view_state.PartialState
import com.example.modelviewintent.view_state.ViewState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

interface AssessmentRecordPresenter {

    fun attachView(view: AssessmentRecordView)

    fun detachView()
}

class AssessmentRecordPresenterImpl(private val interactor: RecordsInteractor) : AssessmentRecordPresenter {

    private var view: AssessmentRecordView? = null
    private var disposable: CompositeDisposable? = null

    override fun attachView(view: AssessmentRecordView) {
        this.view = view
        disposable = CompositeDisposable()

        val actualChange = view.actualChangeIntent
                .doOnNext { Log.d(javaClass.simpleName, "intent: actual change") }
                .zipWith(view.targetValue,
                        BiFunction<Int, Int, Int> { actual, target ->
                            interactor.calculateVariance(actual, target)
                        })
                .map { variance -> PartialState.Variance(variance) }

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


        val initialState = ViewState(AssessmentRecord(), emptyList(), false, null)
        disposable += Observable.merge(load(), actualChange, recordSelection, refreshes)
                .doOnNext { Log.d(javaClass.simpleName, "Partial=${it.javaClass.simpleName}") }
                .scan(initialState, { prev, partial -> partial.produceNext(prev) })
                .subscribe({ view.render(it) },
                        { Log.e(javaClass.simpleName, it.message, it) })
    }

    override fun detachView() {
        disposable?.dispose()
        view = null
    }

    private fun load(): Observable<PartialState> {
        return interactor.loadRecords()
                .doOnSuccess { Log.d(this@AssessmentRecordPresenterImpl.javaClass.simpleName, it.javaClass.simpleName) }
                .map { PartialState.RecordsLoaded(it) as PartialState }
                .onErrorReturn { PartialState.Error(it as Exception) }
                .toObservable()
                .startWith(PartialState.Loading())
    }

    private operator fun CompositeDisposable?.plusAssign(disposable: Disposable) {
        this?.add(disposable)
    }
}
