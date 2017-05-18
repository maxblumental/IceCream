package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecord;
import com.example.icecream.model.AssessmentRecordsManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SendButtonControllerImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AssessmentRecord record;

    @Mock
    AssessmentRecordsManager recordsManager;

    private SendButtonController controller;

    @Before
    public void setUp() {
        controller = new SendButtonControllerImpl(recordsManager, record);
    }

    @Test
    public void onClick_updateTheRecordDate() throws Exception {
        controller.onClick();

        verify(record).setDate(any(Date.class));
    }

    @Test
    public void onClick_takeACopyOfTheRecord() throws Exception {
        controller.onClick();

        verify(record).copy();
    }

    @Test
    public void onClick_saveTheCopyOfTheRecord() throws Exception {
        AssessmentRecord copy = new AssessmentRecord();
        mockRecordCopy(copy);

        controller.onClick();

        verify(recordsManager).update(copy);
    }

    private void mockRecordCopy(AssessmentRecord copy) {
        when(record.copy()).thenReturn(copy);
    }
}