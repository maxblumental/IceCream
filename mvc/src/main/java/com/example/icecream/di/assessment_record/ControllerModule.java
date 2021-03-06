package com.example.icecream.di.assessment_record;

import com.example.icecream.controller.ActualFieldController;
import com.example.icecream.controller.ActualFieldControllerImpl;
import com.example.icecream.controller.RefreshLayoutController;
import com.example.icecream.controller.RefreshLayoutControllerImpl;
import com.example.icecream.controller.SendButtonController;
import com.example.icecream.controller.SendButtonControllerImpl;
import com.example.icecream.controller.StationIdsSpinnerController;
import com.example.icecream.controller.StationIdsSpinnerControllerImpl;
import com.example.icecream.di.PerActivity;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.ObservableAssessmentRecord;

import dagger.Module;
import dagger.Provides;

@Module
public class ControllerModule {

    @Provides
    @PerActivity
    ActualFieldController providesActualFieldController(ObservableAssessmentRecord record) {
        return new ActualFieldControllerImpl(record);
    }

    @Provides
    @PerActivity
    RefreshLayoutController providesRefreshLayoutController(AssessmentRecordsManager recordsManager) {
        return new RefreshLayoutControllerImpl(recordsManager);
    }

    @Provides
    @PerActivity
    SendButtonController providesSendButtonController(AssessmentRecordsManager recordsManager,
                                                      ObservableAssessmentRecord record) {
        return new SendButtonControllerImpl(recordsManager, record);
    }

    @Provides
    @PerActivity
    StationIdsSpinnerController providesStationIdsSpinnerController(AssessmentRecordsManager recordsManager,
                                                                    ObservableAssessmentRecord record) {
        return new StationIdsSpinnerControllerImpl(recordsManager, record);
    }
}
