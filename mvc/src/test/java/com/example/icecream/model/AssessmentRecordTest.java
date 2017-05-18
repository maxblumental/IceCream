package com.example.icecream.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static com.example.icecream.model.AssessmentRecord.VarianceDegree.BAD;
import static com.example.icecream.model.AssessmentRecord.VarianceDegree.GOOD;
import static com.example.icecream.model.AssessmentRecord.VarianceDegree.NORMAL;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AssessmentRecordTest {

    private AssessmentRecord record;

    @Before
    public void setUp() {
        record = new AssessmentRecord();
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

        AssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(GOOD));
    }

    @Test
    public void varianceDegree_isNormal_varianceIsPositiveAndLessThanFivePercentAboveTarget() {
        record.setTarget(100);
        record.setActual(103);

        AssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(NORMAL));
    }

    @Test
    public void varianceDegree_isNormal_varianceIsPositiveAndLessThanTenPercentBelowTarget() {
        record.setTarget(100);
        record.setActual(95);

        AssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

        assertThat(varianceDegree, is(NORMAL));
    }

    @Test
    public void varianceDegree_isBad_varianceIsPositiveAndMoreThanTenPercentBelowTarget() {
        record.setTarget(100);
        record.setActual(89);

        AssessmentRecord.VarianceDegree varianceDegree = record.getVarianceDegree();

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
        fillRecordWithSampleData(record);

        AssessmentRecord copy = record.copy();

        assertFieldsAreEqual(record, copy);
    }

    private void fillRecordWithSampleData(AssessmentRecord otherRecord) {
        otherRecord.setStationId("stationId");
        otherRecord.setDate(new Date(1L));
        otherRecord.setTarget(42);
        otherRecord.setActual(45);
    }

    private void assertFieldsAreEqual(AssessmentRecord record, AssessmentRecord otherRecord) {
        assertThat(record.stationId, is(otherRecord.stationId));
        assertThat(record.date, is(otherRecord.date));
        assertThat(record.target, is(otherRecord.target));
        assertThat(record.actual, is(otherRecord.actual));
        assertThat(record.variance, is(otherRecord.variance));
        assertThat(record.varianceDegree, is(otherRecord.varianceDegree));
    }
}