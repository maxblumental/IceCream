package com.example.icecream.di.assessment_record

import com.example.icecream.AssessmentRecordActivity
import com.example.icecream.di.PerActivity
import dagger.Subcomponent

@Subcomponent(modules = arrayOf(ModelModule::class, ViewModelModule::class))
@PerActivity
interface AssessmentRecordSubsomponent {

    fun inject(activity: AssessmentRecordActivity)
}