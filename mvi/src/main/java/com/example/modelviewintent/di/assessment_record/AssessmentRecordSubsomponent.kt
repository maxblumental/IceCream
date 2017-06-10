package com.example.modelviewintent.di.assessment_record

import com.example.modelviewintent.di.PerActivity
import com.example.modelviewintent.di.assessment_record.ModelModule
import com.example.modelviewintent.AssessmentRecordActivity
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(ModelModule::class, PresenterModule::class))
@PerActivity
interface AssessmentRecordSubcomponent {
    fun inject(activity: AssessmentRecordActivity)
}