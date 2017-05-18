package com.example.icecream.di.assessment_record;

import android.os.Bundle;

import com.example.icecream.model.DatabaseWrapper;
import com.example.icecream.network.NetworkState;
import com.example.icecream.di.PerActivity;
import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.AssessmentRecordsManagerImpl;
import com.example.icecream.network.NetworkStateMonitor;

import dagger.Module;
import dagger.Provides;

@Module
public class ModelModule {

    private Bundle savedState;

    public ModelModule(Bundle savedState) {
        this.savedState = savedState;
    }

    @Provides
    @PerActivity
    AssessmentRecord provideAssessmentRecord() {
        return new AssessmentRecord();
    }

    @Provides
    @PerActivity
    AssessmentRecordsManager provideAssessmentRecordsManager(NetworkStateMonitor networkStateMonitor,
                                                             DatabaseWrapper databaseWrapper) {
        return new AssessmentRecordsManagerImpl(networkStateMonitor, databaseWrapper, savedState);
    }
}
