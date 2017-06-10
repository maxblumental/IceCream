package com.example.modelviewintent;

import android.app.Application;

import com.example.modelviewintent.di.ApplicationComponent;
import com.example.modelviewintent.di.DaggerApplicationComponent;

public class IceCreamApplication extends Application {

    public static ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder().build();
    }
}
