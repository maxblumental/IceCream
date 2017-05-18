package com.example.icecream.di;

import android.content.Context;

import com.example.icecream.network.NetworkState;
import com.example.icecream.network.NetworkStateImpl;
import com.example.icecream.model.DatabaseWrapper;
import com.example.icecream.model.FirebaseDatabaseWrapper;
import com.example.icecream.network.NetworkStateIndicator;
import com.example.icecream.network.NetworkStateMonitor;

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

    @Singleton
    @Provides
    NetworkStateMonitor provideNetworkStateMonitor(NetworkState networkState) {
        return networkState;
    }

    @Singleton
    @Provides
    NetworkStateIndicator provideNetworkStateIndicator(NetworkState networkState) {
        return networkState;
    }

    @Singleton
    @Provides
    DatabaseWrapper provideFirebaseWrapper() {
        return new FirebaseDatabaseWrapper();
    }
}
