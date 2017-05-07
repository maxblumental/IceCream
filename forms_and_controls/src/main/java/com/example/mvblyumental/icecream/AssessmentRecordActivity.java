package com.example.mvblyumental.icecream;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AssessmentRecordActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Spinner stationsSpinner;
    private EditText stationIdEditText;
    private EditText targetEditText;
    private EditText actualEditText;
    private EditText varianceEditText;
    private EditText dateEditText;
    private Button sendButton;
    private SwipeRefreshLayout refreshLayout;

    private Map<String, AssessmentRecord> stationIdToRecord = new HashMap<>();
    private ArrayList<String> stationIds = new ArrayList<>();
    private final Locale locale = Locale.ENGLISH;

    private final String KEY_ASSESSMENT_RECORDS = "key_assessment_records";
    private final String KEY_STATION_IDS = "key_station_ids";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_record);

        findViews();
        setListeners();
        initializeScreen(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isChangingConfigurations()) {
            ArrayList<AssessmentRecord> list = new ArrayList<>(stationIdToRecord.values());
            outState.putParcelableArrayList(KEY_ASSESSMENT_RECORDS, list);
            outState.putStringArrayList(KEY_STATION_IDS, stationIds);
        }
    }

    @Override
    public void onRefresh() {
        loadRecords();
    }

    private void findViews() {
        stationsSpinner = (Spinner) findViewById(R.id.spinner);
        stationIdEditText = (EditText) findViewById(R.id.stationId);
        targetEditText = (EditText) findViewById(R.id.target);
        actualEditText = (EditText) findViewById(R.id.actual);
        varianceEditText = (EditText) findViewById(R.id.variance);
        dateEditText = (EditText) findViewById(R.id.date);
        sendButton = (Button) findViewById(R.id.sendButton);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
    }

    private void setListeners() {
        actualEditText.addTextChangedListener(new ActualFieldTextWatcher());
        sendButton.setOnClickListener(new SendButtonClickListener());
        stationsSpinner.setOnItemSelectedListener(new StationsSpinnerItemSelectedListener());
        refreshLayout.setOnRefreshListener(this);
    }

    private void initializeScreen(Bundle savedInstanceState) {
        if (savedInstanceState == null
                || !savedInstanceState.containsKey(KEY_ASSESSMENT_RECORDS)
                || !savedInstanceState.containsKey(KEY_STATION_IDS)) {
            loadRecords();
        } else {
            restoreSessionState(savedInstanceState);
        }
    }

    private void loadRecords() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("records")
                .addListenerForSingleValueEvent(new RecordsValueEventListener());
    }

    @SuppressWarnings("ConstantConditions")
    private void restoreSessionState(Bundle savedInstanceState) {
        stationIds = savedInstanceState.getStringArrayList(KEY_STATION_IDS);
        ArrayList<AssessmentRecord> records = savedInstanceState.getParcelableArrayList(KEY_ASSESSMENT_RECORDS);
        for (AssessmentRecord record : records) {
            stationIdToRecord.put(record.stationId, record);
        }
        initializeStationsSpinner();
    }

    private class ActualFieldTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int variance = calculateNewVariance();
            setVarianceValue(variance);
            updateVarianceColor(variance);
        }

        private int calculateNewVariance() {
            int actual = readIntegerField(actualEditText);
            int target = readIntegerField(targetEditText);
            return actual - target;
        }

        private void setVarianceValue(int variance) {
            String varianceText = String.format(locale, "%d", variance);
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

    private class SendButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AssessmentRecord record = readAssessmentRecord();
            updateRecordState(record);
        }

        private AssessmentRecord readAssessmentRecord() {
            String stationId = stationIdEditText.getText().toString();
            int actual = readIntegerField(actualEditText);
            int variance = readIntegerField(varianceEditText);
            return new AssessmentRecord(stationId, actual, variance);
        }

        private void updateRecordState(final AssessmentRecord record) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            Map<String, Object> map = Collections.<String, Object>singletonMap(record.stationId, record);
            database.getReference("records")
                    .updateChildren(map, new RecordUpdateCompletionListener(record));
        }

        private class RecordUpdateCompletionListener implements DatabaseReference.CompletionListener {

            private AssessmentRecord record;

            RecordUpdateCompletionListener(AssessmentRecord record) {
                this.record = record;
            }

            @Override
            public void onComplete(DatabaseError error, DatabaseReference reference) {
                if (error == null) {
                    String text = String.format(locale, "Updated record for %s", record.stationId);
                    showToast(text);
                    updateSessionState(record);
                } else {
                    showErrorToast("Failed to update record", error);
                }
            }
        }

        private void updateSessionState(AssessmentRecord record) {
            stationIdToRecord.put(record.stationId, record);
        }

    }

    private class RecordsValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            refreshLayout.setRefreshing(false);
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                AssessmentRecord record;
                try {
                    record = snapshot.getValue(AssessmentRecord.class);
                } catch (DatabaseException e) {
                    showErrorToast("Failed to get assessment record from snapshot", e);
                    return;
                }
                stationIdToRecord.put(key, record);
            }

            stationIds = new ArrayList<>(stationIdToRecord.keySet());
            Collections.sort(stationIds);

            initializeStationsSpinner();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            showErrorToast("Records retrieval was cancelled", databaseError);
        }

    }

    private class StationsSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String stationId = stationIds.get(position);
            AssessmentRecord record = stationIdToRecord.get(stationId);
            updateFields(record);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private void updateFields(AssessmentRecord record) {
        String dateText = new SimpleDateFormat("HH:mm MM/dd/yy", locale).format(record.date);

        stationIdEditText.setText(record.stationId);
        dateEditText.setText(dateText);
        targetEditText.setText(String.format(locale, "%d", record.target));
        actualEditText.setText(String.format(locale, "%d", record.actual));
        varianceEditText.setText(String.format(locale, "%d", record.variance));
    }

    private int readIntegerField(EditText field) {
        String actualText = field.getText().toString();
        int value;
        try {
            value = Integer.parseInt(actualText);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }

    private void initializeStationsSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AssessmentRecordActivity.this,
                android.R.layout.simple_list_item_1, stationIds);
        stationsSpinner.setAdapter(adapter);
    }

    private void showErrorToast(String message, Exception e) {
        showErrorToast(message, e.getMessage());
    }

    private void showErrorToast(String message, DatabaseError e) {
        showErrorToast(message, e.getMessage());
    }

    private void showErrorToast(String message, String errorMessage) {
        String text = String.format("%s: %s", message, errorMessage);
        showToast(text);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
