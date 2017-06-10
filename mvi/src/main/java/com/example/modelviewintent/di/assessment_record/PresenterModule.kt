package com.example.modelviewintent.di.assessment_record

import com.example.modelviewintent.di.PerActivity
import com.example.modelviewintent.interactor.RecordsInteractor
import com.example.modelviewintent.presenter.AssessmentRecordPresenter
import com.example.modelviewintent.presenter.AssessmentRecordPresenterImpl

import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @PerActivity
    @Provides
    fun provideAssessmentRecordPresenter(interactor: RecordsInteractor): AssessmentRecordPresenter {
        return AssessmentRecordPresenterImpl(interactor)
    }
}
