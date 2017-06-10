package com.example.icecream.controller;

import com.example.firebasedb.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.ObservableAssessmentRecord;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StationIdsSpinnerControllerImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    ObservableAssessmentRecord record;

    @Mock
    AssessmentRecordsManager recordsManager;

    private StationIdsSpinnerController controller;

    @Before
    public void setUp() {
        controller = new StationIdsSpinnerControllerImpl(recordsManager, record);
    }

    @Test
    public void onItemSelected_getAccordingRecordFromRecordsManager() throws Exception {
        String stationId = "stationId";

        controller.onItemSelected(stationId);

        verify(recordsManager).getRecord(stationId);
    }

    @Test
    public void onItemSelected_updateCurrentRecord() throws Exception {
        String stationId = "stationId";
        AssessmentRecord assessmentRecord = new AssessmentRecord();
        mockGetRecord(stationId, assessmentRecord);

        controller.onItemSelected(stationId);

        verify(record).fillFrom(assessmentRecord);
    }

    private void mockGetRecord(String stationId, AssessmentRecord assessmentRecord) {
        when(recordsManager.getRecord(stationId)).thenReturn(assessmentRecord);
    }
}