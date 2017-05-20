package com.example.icecream

import android.app.Application

import com.example.icecream.di.ApplicationComponent
import com.example.icecream.di.DaggerApplicationComponent

class IceCreamApplication : Application() {

    companion object {
        lateinit var component: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.builder().build()
    }
}
