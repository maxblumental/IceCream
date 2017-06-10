package com.example.modelviewintent.di.assessment_record

import com.example.firebasedb.RxFirebaseRemoteStorage
import com.example.firebasedb.RxRemoteStorage
import com.example.modelviewintent.di.PerActivity
import com.example.modelviewintent.interactor.RecordsInteractor
import com.example.modelviewintent.interactor.RecordsInteractorImpl
import dagger.Module
import dagger.Provides

@Module
class ModelModule {

    @Provides
    @PerActivity
    internal fun provideRxRemoteStorage(): RxRemoteStorage {
        return RxFirebaseRemoteStorage()
    }

    @Provides
    @PerActivity
    internal fun provideRecordsInteractor(storage: RxRemoteStorage): RecordsInteractor {
        return RecordsInteractorImpl(storage)
    }

}
