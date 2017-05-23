package com.example.icecream.other

import android.databinding.BaseObservable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BindableProperty<T, in B : BaseObservable>(private val bindingId: Int, initial: T,
                                                 private val after: () -> Unit = {}) : ReadWriteProperty<B, T> {

    private var value: T = initial

    override fun getValue(thisRef: B, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: B, property: KProperty<*>, value: T) {
        this.value = value
        thisRef.notifyPropertyChanged(bindingId)
        after.invoke()
    }
}
