package com.example.icecream.di;

import com.example.icecream.di.assessment_record.AssessmentRecordModule;
import com.example.icecream.di.assessment_record.AssessmentRecordSubcomponent;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {CommonModule.class})
@Singleton
public interface ApplicationComponent {
    AssessmentRecordSubcomponent plus(AssessmentRecordModule module);
}
