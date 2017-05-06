package com.example.mvblyumental.icecream;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Locale;

public class AssessmentRecordActivity extends AppCompatActivity {

    private Spinner stationsSpinner;

    private EditText targetEditText;

    private EditText actualEditText;

    private EditText varianceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_record);

        findViews();

        actualEditText.setOnClickListener(new ActualFieldClickListener());
    }

    private void findViews() {
        stationsSpinner = (Spinner) findViewById(R.id.spinner);
        targetEditText = (EditText) findViewById(R.id.target);
        actualEditText = (EditText) findViewById(R.id.actual);
        varianceEditText = (EditText) findViewById(R.id.variance);
    }

    private class ActualFieldClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int variance = calculateNewVariance();
            setVarianceValue(variance);
            updateVarianceColor(variance);
        }

        private int calculateNewVariance() {
            int actual = readIntegerField(actualEditText);
            int target = readIntegerField(targetEditText);
            return actual - target;
        }

        private int readIntegerField(EditText field) {
            String actualText = field.getText().toString();
            return Integer.parseInt(actualText);
        }

        private void setVarianceValue(int variance) {
            String varianceText = String.format(Locale.ENGLISH, "%d", variance);
            varianceEditText.setText(varianceText);
        }

        private void updateVarianceColor(int variance) {
            int target = readIntegerField(targetEditText);
            int color = determineVarianceColor(variance, target);
            varianceEditText.setTextColor(color);
        }

        private int determineVarianceColor(int variance, int target) {
            float percent = Math.abs(((float) variance) / target);
            int color;
            if (variance > 0 && percent > 0.05f) {
                color = Color.GREEN;
            } else if (variance < 0 && percent > 0.1f) {
                color = Color.RED;
            } else {
                color = Color.BLACK;
            }
            return color;
        }
    }
}
