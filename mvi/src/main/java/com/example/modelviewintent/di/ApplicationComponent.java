package com.example.modelviewintent.di;

import com.example.modelviewintent.di.assessment_record.AssessmentRecordSubcomponent;
import com.example.modelviewintent.di.assessment_record.ModelModule;
import com.example.modelviewintent.di.assessment_record.PresenterModule;

import javax.inject.Singleton;

import dagger.Component;

@Component
@Singleton
public interface ApplicationComponent {
    AssessmentRecordSubcomponent plus(ModelModule modelModule, PresenterModule presenterModule);
}
