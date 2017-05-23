package com.example.icecream.wrapper

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

interface Lifecycle {

    fun events(): Observable<Event>

    enum class Event {CREATE, DESTROY }
}

interface LifecycleEventsConsumer {

    fun onCreate()

    fun onDestroy()
}

class LifecycleWrapper : Lifecycle, LifecycleEventsConsumer {

    private val subject: PublishSubject<Lifecycle.Event> = PublishSubject.create()

    override fun onCreate() = subject.onNext(Lifecycle.Event.CREATE)

    override fun onDestroy() = subject.onNext(Lifecycle.Event.DESTROY)

    override fun events(): Observable<Lifecycle.Event> = subject
}