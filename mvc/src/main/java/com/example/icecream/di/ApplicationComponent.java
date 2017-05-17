package com.example.icecream.di;

import com.example.icecream.di.assessment_record.AssessmentRecordSubcomponent;
import com.example.icecream.di.assessment_record.ControllerModule;
import com.example.icecream.di.assessment_record.ModelModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {CommonModule.class})
@Singleton
public interface ApplicationComponent {
    AssessmentRecordSubcomponent plus(ControllerModule controllerModule, ModelModule modelModule);
}
