package com.example.icecream.di.assessment_record;

import com.example.icecream.AssessmentRecordActivity;
import com.example.icecream.controller.ActualFieldController;
import com.example.icecream.controller.RefreshLayoutController;
import com.example.icecream.controller.SendButtonController;
import com.example.icecream.controller.StationIdsSpinnerController;
import com.example.icecream.di.PerActivity;

import dagger.Subcomponent;

@Subcomponent(modules = {AssessmentRecordModule.class})
@PerActivity
public interface AssessmentRecordSubcomponent {

    void inject(AssessmentRecordActivity activity);

    ActualFieldController actualFieldController();

    RefreshLayoutController refreshLayoutController();

    SendButtonController sendButtonController();

    StationIdsSpinnerController stationIdsSpinnerController();
}
