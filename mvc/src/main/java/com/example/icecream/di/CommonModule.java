package com.example.icecream.di;

import android.content.Context;

import com.example.icecream.NetworkState;
import com.example.icecream.NetworkStateImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CommonModule {

    private Context context;

    public CommonModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    NetworkState provideNetworkState() {
        return new NetworkStateImpl(context);
    }
}
