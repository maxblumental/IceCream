package com.example.icecream.di;

import com.example.icecream.di.assessment_record.AssessmentRecordSubsomponent;
import com.example.icecream.di.assessment_record.ModelModule;
import com.example.icecream.di.assessment_record.ViewModelModule;

import javax.inject.Singleton;

import dagger.Component;

@Component
@Singleton
public interface ApplicationComponent {

    AssessmentRecordSubsomponent plus(ModelModule modelModule, ViewModelModule viewModelModule);
}
