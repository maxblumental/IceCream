package com.example.icecream;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.icecream.databinding.ActivityAssessmentRecordBinding;
import com.example.icecream.di.assessment_record.AssessmentRecordModule;
import com.example.icecream.di.assessment_record.AssessmentRecordSubcomponent;
import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;

import javax.inject.Inject;

public class AssessmentRecordActivity extends AppCompatActivity {

    @Inject
    AssessmentRecordsManager recordsManager;

    @Inject
    AssessmentRecord record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAssessmentRecordBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_assessment_record);
        initialize(binding);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordsManager.loadRecords();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void initialize(ActivityAssessmentRecordBinding binding) {
        AssessmentRecordSubcomponent subcomponent = IceCreamApplication.component
                .plus(new AssessmentRecordModule());

        subcomponent.inject(this);

        binding.setRecordsManager(recordsManager);
        binding.setRecord(record);
        binding.setActualController(subcomponent.actualFieldController());
        binding.setStationIdsController(subcomponent.stationIdsSpinnerController());
        binding.setSendButtonController(subcomponent.sendButtonController());
        binding.setRefreshController(subcomponent.refreshLayoutController());
    }
}