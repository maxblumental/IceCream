package com.example.icecream.di.assessment_record

import android.os.Bundle
import com.example.firebasedb.RxFirebaseRemoteStorage
import com.example.firebasedb.RxRemoteStorage
import com.example.icecream.di.PerActivity
import com.example.icecream.model.Model
import com.example.icecream.model.ModelImpl
import dagger.Module
import dagger.Provides

@Module
class ModelModule(private val savedState: Bundle?) {

    @Provides
    @PerActivity
    internal fun provideModel(remoteStorage: RxRemoteStorage): Model {
        return ModelImpl(remoteStorage, savedState)
    }

    @Provides
    @PerActivity
    internal fun provideRxRemoteStorage(): RxRemoteStorage {
        return RxFirebaseRemoteStorage()
    }
}
