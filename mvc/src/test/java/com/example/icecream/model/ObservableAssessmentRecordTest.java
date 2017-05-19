package com.example.icecream.model;

import com.example.firebasedb.AssessmentRecord;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.example.icecream.model.ObservableAssessmentRecord.VarianceDegree.BAD;
import static com.example.icecream.model.ObservableAssessmentRecord.VarianceDegree.GOOD;
import static com.example.icecream.model.ObservableAssessmentRecord.VarianceDegree.NORMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ObservableAssessmentRecordTest {

    private ObservableAssessmentRecord record;

    @Before
    public void setUp() {
        record = new ObservableAssessmentRecord();
    }

    @Test
    public void variance_equalsActualMinusTarget() {
        record.setTarget(10);
        record.setActual(15);

        int variance = record.getVariance();

        assertThat(variance, is(5));
    }

    @Test
    public void varianceDegree_isGood_varianceIsPositiveAndMoreThanFivePercentAboveTarget() {
        record.setTarget(100);
        record.setActual(106);

        ObservableAssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(GOOD));
    }

    @Test
    public void varianceDegree_isNormal_varianceIsPositiveAndLessThanFivePercentAboveTarget() {
        record.setTarget(100);
        record.setActual(103);

        ObservableAssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(NORMAL));
    }

    @Test
    public void varianceDegree_isNormal_varianceIsPositiveAndLessThanTenPercentBelowTarget() {
        record.setTarget(100);
        record.setActual(95);

        ObservableAssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(NORMAL));
    }

    @Test
    public void varianceDegree_isBad_varianceIsPositiveAndMoreThanTenPercentBelowTarget() {
        record.setTarget(100);
        record.setActual(89);

        ObservableAssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(BAD));
    }

    @Test
    public void fillFrom_fieldsBecomeEqualToThoseOfTheArgument() {
        AssessmentRecord otherRecord = new AssessmentRecord();
        fillRecordWithSampleData(otherRecord);

        record.fillFrom(otherRecord);

        assertFieldsAreEqual(record, otherRecord);
    }

    @Test
    public void copy_fieldsOfTheRecordAndOfItsCopyAreEqual() {
        fillObservableRecordWithSampleData(record);

        AssessmentRecord copy = record.createRecordCopy();

        assertFieldsAreEqual(record, copy);
    }

    private void fillRecordWithSampleData(AssessmentRecord record) {
        record.setStationId("stationId");
        record.setDate(new Date(1L));
        record.setTarget(42);
        record.setActual(45);
        record.setVariance(3);
    }

    private void fillObservableRecordWithSampleData(ObservableAssessmentRecord record) {
        AssessmentRecord assessmentRecord = new AssessmentRecord();
        fillRecordWithSampleData(assessmentRecord);
        record.fillFrom(assessmentRecord);
    }

    private void assertFieldsAreEqual(ObservableAssessmentRecord record, AssessmentRecord otherRecord) {
        assertThat(record.getStationId(), is(otherRecord.getStationId()));
        assertThat(record.getDate(), is(otherRecord.getDate()));
        assertThat(record.getTarget(), is(otherRecord.getTarget()));
        assertThat(record.getActual(), is(otherRecord.getActual()));
        assertThat(record.getVariance(), is(otherRecord.getVariance()));
    }
}