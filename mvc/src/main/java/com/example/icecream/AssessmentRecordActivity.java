package com.example.icecream;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.icecream.controller.ActualFieldControllerImpl;
import com.example.icecream.controller.StationIdsSpinnerControllerImpl;
import com.example.icecream.databinding.ActivityAssessmentRecordBinding;
import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.Model;
import com.example.icecream.model.ModelImpl;

public class AssessmentRecordActivity extends AppCompatActivity {

    private Model model = new ModelImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAssessmentRecordBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_assessment_record);

        binding.setModel(model);
        binding.setRecord(new AssessmentRecord());
        binding.setActualController(new ActualFieldControllerImpl(binding));
        binding.setStationIdsController(new StationIdsSpinnerControllerImpl(binding));

        model.loadRecords();
    }
}