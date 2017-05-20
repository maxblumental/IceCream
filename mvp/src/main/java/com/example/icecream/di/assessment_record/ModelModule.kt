package com.example.icecream.di.assessment_record

import com.example.firebasedb.RxFirebaseRemoteStorage
import com.example.firebasedb.RxRemoteStorage
import com.example.icecream.di.PerActivity
import com.example.icecream.model.Model
import com.example.icecream.model.ModelImpl

import dagger.Module
import dagger.Provides

@Module
class ModelModule {

    @Provides
    @PerActivity
    internal fun provideModel(remoteStorage: RxRemoteStorage): Model {
        return ModelImpl(remoteStorage)
    }

    @Provides
    @PerActivity
    internal fun provideRxRemoteStorage(): RxRemoteStorage {
        return RxFirebaseRemoteStorage()
    }
}
