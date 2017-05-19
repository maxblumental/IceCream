package com.example.icecream.di.assessment_record;

import android.os.Bundle;

import com.example.firebasedb.RemoteStorage;
import com.example.icecream.di.PerActivity;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.AssessmentRecordsManagerImpl;
import com.example.icecream.model.ObservableAssessmentRecord;
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
    ObservableAssessmentRecord provideObservableAssessmentRecord() {
        return new ObservableAssessmentRecord();
    }

    @Provides
    @PerActivity
    AssessmentRecordsManager provideAssessmentRecordsManager(NetworkStateMonitor networkStateMonitor,
                                                             RemoteStorage remoteStorage) {
        return new AssessmentRecordsManagerImpl(networkStateMonitor, remoteStorage, savedState);
    }
}
