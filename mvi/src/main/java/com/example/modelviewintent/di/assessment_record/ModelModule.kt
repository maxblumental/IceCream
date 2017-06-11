package com.example.modelviewintent.di.assessment_record

import android.os.Bundle
import com.example.firebasedb.RxFirebaseRemoteStorage
import com.example.firebasedb.RxRemoteStorage
import com.example.modelviewintent.di.PerActivity
import com.example.modelviewintent.interactor.RecordsInteractor
import com.example.modelviewintent.interactor.RecordsInteractorImpl
import dagger.Module
import dagger.Provides

@Module
class ModelModule(private val savedState: Bundle?) {

    @Provides
    @PerActivity
    internal fun provideRxRemoteStorage(): RxRemoteStorage {
        return RxFirebaseRemoteStorage()
    }

    @Provides
    @PerActivity
    internal fun provideRecordsInteractor(storage: RxRemoteStorage): RecordsInteractor {
        return RecordsInteractorImpl(storage, savedState)
    }

}
