package com.example.icecream;

import android.databinding.ObservableBoolean;

public interface NetworkState {

    boolean checkNetworkAvailability();

    ObservableBoolean isNetworkAvailable();
}
