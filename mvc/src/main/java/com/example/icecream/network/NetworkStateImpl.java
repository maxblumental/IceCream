package com.example.icecream.network;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateImpl implements NetworkState {

    private Context context;
    private ObservableBoolean isNetworkAvailable;

    public NetworkStateImpl(Context context) {
        this.context = context;
        isNetworkAvailable = new ObservableBoolean();
        checkNetworkAvailability();
    }

    @Override
    public boolean checkNetworkAvailability() {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        isNetworkAvailable.set(networkInfo != null && networkInfo.isConnected());
        return isNetworkAvailable.get();
    }

    @Override
    public ObservableBoolean isNetworkAvailable() {
        return isNetworkAvailable;
    }
}
