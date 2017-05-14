package com.example.icecream.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.icecream.AssessmentRecordActivity;

public class NetworkObserverView extends View {

    public NetworkObserverView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setNetworkAvailable(boolean networkAvailable) {
        if (!networkAvailable) {
            AssessmentRecordActivity activity = (AssessmentRecordActivity) getContext();
            activity.showToast("Network is unavailable!");
        }
    }
}
