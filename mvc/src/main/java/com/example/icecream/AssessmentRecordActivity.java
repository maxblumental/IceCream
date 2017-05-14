package com.example.icecream;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.icecream.controller.ActualFieldControllerImpl;
import com.example.icecream.controller.RefreshLayoutControllerImpl;
import com.example.icecream.controller.SendButtonControllerImpl;
import com.example.icecream.controller.StationIdsSpinnerControllerImpl;
import com.example.icecream.databinding.ActivityAssessmentRecordBinding;
import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.AssessmentRecordsManagerImpl;

public class AssessmentRecordActivity extends AppCompatActivity {

    private AssessmentRecordsManager recordsManager = new AssessmentRecordsManagerImpl();
    private AssessmentRecord record = new AssessmentRecord();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAssessmentRecordBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_assessment_record);

        binding.setRecordsManager(recordsManager);
        binding.setRecord(record);
        binding.setActualController(new ActualFieldControllerImpl(record));
        binding.setStationIdsController(new StationIdsSpinnerControllerImpl(recordsManager, record));
        binding.setSendButtonController(new SendButtonControllerImpl(recordsManager, record));
        binding.setRefreshController(new RefreshLayoutControllerImpl(recordsManager));
    }

    @Override
    protected void onResume() {
        super.onResume();
        recordsManager.loadRecords();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}