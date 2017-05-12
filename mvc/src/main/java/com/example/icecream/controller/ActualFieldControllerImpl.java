package com.example.icecream.controller;

import android.view.View;
import android.widget.EditText;

import com.example.icecream.databinding.ActivityAssessmentRecordBinding;

public class ActualFieldControllerImpl implements ActualFieldController {

    private ActivityAssessmentRecordBinding binding;

    public ActualFieldControllerImpl(ActivityAssessmentRecordBinding binding) {
        this.binding = binding;
    }

    @Override
    public void onChanged(View view, boolean hasFocus) {
        if (hasFocus) {
            return;
        }

        String text = ((EditText) view).getText().toString();
        int value;
        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            value = 0;
        }
        binding.getRecord().setActual(value);
    }
}
