package com.example.icecream;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.icecream.databinding.ActivityAssessmentRecordBinding;
import com.example.icecream.di.assessment_record.AssessmentRecordSubcomponent;
import com.example.icecream.di.assessment_record.ControllerModule;
import com.example.icecream.di.assessment_record.ModelModule;
import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.network.NetworkStateIndicator;

import javax.inject.Inject;

public class AssessmentRecordActivity extends AppCompatActivity {

    private final String KEY_RECORDS = "key_records";

    @Inject
    AssessmentRecordsManager recordsManager;

    @Inject
    AssessmentRecord record;

    @Inject
    NetworkStateIndicator networkStateIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAssessmentRecordBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_assessment_record);
        initialize(binding, savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordsManager.loadRecords();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isChangingConfigurations()) {
            Bundle savedState = recordsManager.getSavedState();
            outState.putBundle(KEY_RECORDS, savedState);
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void initialize(ActivityAssessmentRecordBinding binding, Bundle savedInstanceState) {
        Bundle modelState = null;
        if (savedInstanceState != null) {
            modelState = savedInstanceState.getBundle(KEY_RECORDS);
        }
        AssessmentRecordSubcomponent subcomponent = IceCreamApplication.component
                .plus(new ControllerModule(), new ModelModule(modelState));

        subcomponent.inject(this);

        binding.setRecordsManager(recordsManager);
        binding.setRecord(record);
        binding.setActualController(subcomponent.actualFieldController());
        binding.setStationIdsController(subcomponent.stationIdsSpinnerController());
        binding.setSendButtonController(subcomponent.sendButtonController());
        binding.setRefreshController(subcomponent.refreshLayoutController());
        binding.setNetwork(networkStateIndicator);
    }
}