package com.example.icecream.controller;

import com.example.icecream.model.AssessmentRecord;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.verify;

public class ActualFieldControllerImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AssessmentRecord record;

    private ActualFieldController controller;

    @Before
    public void setUp() {
        controller = new ActualFieldControllerImpl(record);
    }

    @Test
    public void onChange_setParsedNumberToRecord_stringRepresentsANumber() throws Exception {
        String actualString = "42";

        controller.onChange(actualString);

        verify(record).setActual(42);
    }

    @Test
    public void onChange_setZeroToRecord_stringRepresentsRubbish() throws Exception {
        String actualString = "rubbish";

        controller.onChange(actualString);

        verify(record).setActual(0);
    }
}