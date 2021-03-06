package com.example.mvblyumental.icecream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.commonui.AssessmentRecordField;
import com.example.firebasedb.AssessmentRecord;
import com.example.firebasedb.FirebaseRemoteStorage;
import com.example.firebasedb.RemoteStorage;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.example.mvblyumental.icecream.R.id.stationId;

public class AssessmentRecordActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Spinner stationsSpinner;
    private AssessmentRecordField stationIdField;
    private AssessmentRecordField targetField;
    private AssessmentRecordField actualField;
    private AssessmentRecordField varianceField;
    private AssessmentRecordField dateField;
    private Button sendButton;
    private SwipeRefreshLayout refreshLayout;

    private Map<String, AssessmentRecord> stationIdToRecord;
    private final Locale locale = Locale.ENGLISH;

    private RemoteStorage remoteStorage;

    private final String KEY_ASSESSMENT_RECORDS = "key_assessment_records";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_record);
        remoteStorage = new FirebaseRemoteStorage();

        findViews();
        setListeners();
        initializeScreen(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isChangingConfigurations() && stationIdToRecord != null) {
            outState.putSerializable(KEY_ASSESSMENT_RECORDS, (Serializable) stationIdToRecord);
        }
    }

    @Override
    public void onRefresh() {
        loadRecords();
    }

    private void findViews() {
        stationsSpinner = (Spinner) findViewById(R.id.spinner);
        stationIdField = (AssessmentRecordField) findViewById(stationId);
        targetField = (AssessmentRecordField) findViewById(R.id.target);
        actualField = (AssessmentRecordField) findViewById(R.id.actual);
        varianceField = (AssessmentRecordField) findViewById(R.id.variance);
        dateField = (AssessmentRecordField) findViewById(R.id.date);
        sendButton = (Button) findViewById(R.id.sendButton);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
    }

    private void setListeners() {
        actualField.setOnValueChangeListener(new ActualFieldValueChangeListener());
        sendButton.setOnClickListener(new SendButtonClickListener());
        stationsSpinner.setOnItemSelectedListener(new StationsSpinnerItemSelectedListener());
        refreshLayout.setOnRefreshListener(this);
    }

    private void initializeScreen(Bundle savedInstanceState) {
        if (savedInstanceState == null
                || !savedInstanceState.containsKey(KEY_ASSESSMENT_RECORDS)) {
            loadRecords();
        } else {
            restoreSessionState(savedInstanceState);
        }
    }

    private void loadRecords() {
        if (isNetworkUnavailable()) {
            showToast("Network is unavailable.");
            return;
        }
        remoteStorage.getRecords(new RecordsValueEventListener());
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    private void restoreSessionState(Bundle savedInstanceState) {
        stationIdToRecord = (Map<String, AssessmentRecord>) savedInstanceState.getSerializable(KEY_ASSESSMENT_RECORDS);
        initializeStationsSpinner();
    }

    private class ActualFieldValueChangeListener implements AssessmentRecordField.ValueChangeListener {

        @Override
        public void onChange(String value) {
            int variance = calculateNewVariance();
            setVarianceValue(variance);
            updateVarianceColor();
        }

        private int calculateNewVariance() {
            int actual = readIntegerField(actualField);
            int target = readIntegerField(targetField);
            return actual - target;
        }

        private void setVarianceValue(int variance) {
            String varianceText = String.format(locale, "%d", variance);
            varianceField.setText(varianceText);
        }

    }

    private void updateVarianceColor() {
        int variance = readIntegerField(varianceField);
        int target = readIntegerField(targetField);
        int color = determineVarianceColor(variance, target);
        varianceField.setValueColor(color);
    }

    private int determineVarianceColor(int variance, int target) {
        float percent = Math.abs(((float) variance) / target);
        int color;
        if (variance > 0 && percent > 0.05f) {
            color = R.color.green;
        } else if (variance < 0 && percent > 0.1f) {
            color = R.color.red;
        } else {
            color = R.color.black;
        }
        return color;
    }

    private class SendButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            AssessmentRecord record = readAssessmentRecord();
            updateRecordState(record);
        }

        private AssessmentRecord readAssessmentRecord() {
            String stationId = stationIdField.getText();
            int actual = readIntegerField(actualField);
            int target = readIntegerField(targetField);
            int variance = readIntegerField(varianceField);
            return new AssessmentRecord(stationId, target, new Date(), actual, variance);
        }

        private void updateRecordState(AssessmentRecord record) {
            if (isNetworkUnavailable()) {
                showToast("Network is unavailable.");
                return;
            }
            remoteStorage.updateRecord(record, new RecordUpdateCompletionListener(record));
        }

        private class RecordUpdateCompletionListener implements RemoteStorage.RecordUpdateCompletionListener {

            private AssessmentRecord record;

            RecordUpdateCompletionListener(AssessmentRecord record) {
                this.record = record;
            }

            @Override
            public void onComplete() {
                String text = String.format(locale, "Updated record for %s", record.getStationId());
                showToast(text);
                updateSessionState(record);
            }

            @Override
            public void onError(Exception e) {
                showErrorToast("Failed to update record", e);
            }
        }

        private void updateSessionState(AssessmentRecord record) {
            stationIdToRecord.put(record.getStationId(), record);
        }

    }

    private class RecordsValueEventListener implements RemoteStorage.RecordsQueryListener {

        @Override
        public void onResult(Map<String, AssessmentRecord> recordMap) {
            refreshLayout.setRefreshing(false);
            stationIdToRecord = recordMap;
            initializeStationsSpinner();
        }

        @Override
        public void onError(Exception e) {
            showErrorToast("Records retrieval was cancelled", e);
        }
    }

    private class StationsSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String stationId = (String) parent.getSelectedItem();
            AssessmentRecord record = stationIdToRecord.get(stationId);
            updateFields(record);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private void updateFields(AssessmentRecord record) {
        String dateText = new SimpleDateFormat("HH:mm MM/dd/yy", locale).format(record.getDate());

        stationIdField.setText(record.getStationId());
        dateField.setText(dateText);
        targetField.setText(String.format(locale, "%d", record.getTarget()));
        actualField.setText(String.format(locale, "%d", record.getActual()));
        varianceField.setText(String.format(locale, "%d", record.getVariance()));
        updateVarianceColor();
    }

    private int readIntegerField(AssessmentRecordField field) {
        String actualText = field.getText();
        int value;
        try {
            value = Integer.parseInt(actualText);
        } catch (NumberFormatException e) {
            value = 0;
        }
        return value;
    }

    private void initializeStationsSpinner() {
        ArrayList<String> list = new ArrayList<>(stationIdToRecord.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AssessmentRecordActivity.this,
                android.R.layout.simple_list_item_1, list);
        stationsSpinner.setAdapter(adapter);
    }

    private void showErrorToast(String message, Exception e) {
        showErrorToast(message, e.getMessage());
    }

    private void showErrorToast(String message, String errorMessage) {
        String text = String.format("%s: %s", message, errorMessage);
        showToast(text);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private boolean isNetworkUnavailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }
}
