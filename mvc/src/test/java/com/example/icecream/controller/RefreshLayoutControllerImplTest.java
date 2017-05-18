package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecordsManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;

public class RefreshLayoutControllerImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AssessmentRecordsManager recordsManager;

    private RefreshLayoutController controller;

    @Before
    public void setUp() {
        controller = new RefreshLayoutControllerImpl(recordsManager);
    }

    @Test
    public void onRefresh_askRecordsManagerToReload() throws Exception {
        controller.onRefresh();

        verify(recordsManager).reloadRecords();
    }
}