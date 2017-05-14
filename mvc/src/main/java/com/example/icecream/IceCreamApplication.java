package com.example.icecream;

import android.app.Application;

import com.example.icecream.di.ApplicationComponent;
import com.example.icecream.di.CommonModule;
import com.example.icecream.di.DaggerApplicationComponent;

public class IceCreamApplication extends Application {

    public static ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .commonModule(new CommonModule(this))
                .build();
    }
}
