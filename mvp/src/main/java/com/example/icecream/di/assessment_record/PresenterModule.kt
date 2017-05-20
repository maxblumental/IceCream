package com.example.icecream.di.assessment_record

import com.example.icecream.di.PerActivity
import com.example.icecream.model.Model
import com.example.icecream.presenter.AssessmentRecordPresenter
import com.example.icecream.presenter.AssessmentRecordPresenterImpl

import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @PerActivity
    @Provides
    fun provideAssessmentRecordPresenter(model: Model): AssessmentRecordPresenter {
        return AssessmentRecordPresenterImpl(model)
    }
}
