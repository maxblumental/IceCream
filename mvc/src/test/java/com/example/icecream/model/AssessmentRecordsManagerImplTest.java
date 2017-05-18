package com.example.icecream.model;

import com.example.icecream.network.NetworkStateMonitor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.icecream.model.AssessmentRecordsManager.Error.RECORDS_LOAD_ERROR;
import static com.example.icecream.model.AssessmentRecordsManager.Error.UPDATE_RECORD_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class AssessmentRecordsManagerImplTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    NetworkStateMonitor networkStateMonitor;

    @Mock
    RemoteStorage remoteStorage;

    private AssessmentRecordsManager recordsManager;

    @Before
    public void setUp() {
        recordsManager = new AssessmentRecordsManagerImpl(networkStateMonitor, remoteStorage, null);
        mockNetworkAvailability(true);
    }

    @Test
    public void loadRecords_remoteStorageIsCalled() throws Exception {
        recordsManager.loadRecords();

        verify(remoteStorage).getRecords(any(RemoteStorage.RecordsQueryListener.class));
    }

    @Test
    public void loadRecords_remoteStorageIsNotCalled_dataIsCached() throws Exception {
        mockLoadedRecords(Collections.singletonList(new AssessmentRecord()));
        clearInvocations(remoteStorage);

        recordsManager.loadRecords();

        verify(remoteStorage, never()).getRecords(any(RemoteStorage.RecordsQueryListener.class));
    }

    @Test
    public void loadRecords_remoteStorageIsNotCalled_networkIsNotAvailable() throws Exception {
        mockNetworkAvailability(false);

        recordsManager.loadRecords();

        verify(remoteStorage, never()).getRecords(any(RemoteStorage.RecordsQueryListener.class));
    }

    @Test
    public void reloadRecords_remoteStorageIsCalled() throws Exception {
        recordsManager.reloadRecords();

        verify(remoteStorage).getRecords(any(RemoteStorage.RecordsQueryListener.class));
    }

    @Test
    public void reloadRecords_remoteStorageIsCalled_dataIsCached() throws Exception {
        mockLoadedRecords(Collections.singletonList(new AssessmentRecord()));
        clearInvocations(remoteStorage);

        recordsManager.reloadRecords();

        verify(remoteStorage).getRecords(any(RemoteStorage.RecordsQueryListener.class));
    }

    @Test
    public void reloadRecords_remoteStorageIsNotCalled_networkIsNotAvailable() throws Exception {
        mockNetworkAvailability(false);

        recordsManager.reloadRecords();

        verify(remoteStorage, never()).getRecords(any(RemoteStorage.RecordsQueryListener.class));
    }

    @Test
    public void update_remoteStorageIsCalled_networkIsAvailable() throws Exception {
        AssessmentRecord record = new AssessmentRecord();

        recordsManager.update(record);

        verify(remoteStorage)
                .updateRecord(eq(record), any(RemoteStorage.RecordUpdateCompletionListener.class));
    }

    @Test
    public void update_remoteStorageIsNotCalled_networkIsNotAvailable() throws Exception {
        mockNetworkAvailability(false);

        recordsManager.update(new AssessmentRecord());

        verify(remoteStorage, never())
                .updateRecord(any(AssessmentRecord.class), any(RemoteStorage.RecordUpdateCompletionListener.class));
    }

    @Test
    public void update_updatedRecordCanBeRetrievedViaGetRecord() throws Exception {
        mockLoadedRecords();
        String stationId = "stationId";
        AssessmentRecord updatedRecord = createRecord(stationId);

        recordsManager.update(updatedRecord);
        captureRecordUpdateCompletionListener().onComplete();

        assertEquals(updatedRecord, recordsManager.getRecord(stationId));
    }

    @Test
    public void getRecord_loadedRecordsCanBeAccessedViaGetRecord() throws Exception {
        String stationId = "stationId";
        AssessmentRecord record = createRecord(stationId);
        mockLoadedRecords(Collections.singletonList(record));

        AssessmentRecord retrievedRecord = recordsManager.getRecord(stationId);

        assertEquals(record, retrievedRecord);
    }

    @Test
    public void isLoading_true_loadRecordsIsCalled() throws Exception {
        recordsManager.loadRecords();

        boolean isLoading = recordsManager.isLoading().get();

        assertTrue(isLoading);
    }

    @Test
    public void isLoading_false_loadRecordsIsCalledAndNetworkIsNotAvailable() throws Exception {
        mockNetworkAvailability(false);
        recordsManager.loadRecords();

        boolean isLoading = recordsManager.isLoading().get();

        assertFalse(isLoading);
    }

    @Test
    public void isLoading_false_loadRecordsIsCalledAndCompletesSuccessfully() throws Exception {
        recordsManager.loadRecords();
        captureRecordsQueryListener().onResult(Collections.<String, AssessmentRecord>emptyMap());

        boolean isLoading = recordsManager.isLoading().get();

        assertFalse(isLoading);
    }

    @Test
    public void isLoading_false_loadRecordsIsCalledAndCompletesWithAnError() throws Exception {
        recordsManager.loadRecords();
        captureRecordsQueryListener().onError(new Exception());

        boolean isLoading = recordsManager.isLoading().get();

        assertFalse(isLoading);
    }

    @Test
    public void error_recordsLoadErrorIsSet_recordsLoadCompletesWithAnError() throws Exception {
        recordsManager.loadRecords();
        captureRecordsQueryListener().onError(new Exception());

        AssessmentRecordsManager.Error error = recordsManager.getError().get();

        assertThat(error, is(RECORDS_LOAD_ERROR));
    }

    @Test
    public void error_exceptionIsTakenFromTheCallback_recordsLoadCompletesWithAnError() throws Exception {
        recordsManager.loadRecords();
        Exception exception = new Exception();
        captureRecordsQueryListener().onError(exception);

        AssessmentRecordsManager.Error error = recordsManager.getError().get();

        assertEquals(exception, error.getException());
    }

    @Test
    public void error_updateRecordErrorIsSet_recordUpdateCompletesWithAnError() throws Exception {
        recordsManager.update(new AssessmentRecord());
        captureRecordUpdateCompletionListener().onError(new Exception());

        AssessmentRecordsManager.Error error = recordsManager.getError().get();

        assertThat(error, is(UPDATE_RECORD_ERROR));
    }

    @Test
    public void error_exceptionIsTakenFromTheCallback_recordUpdateCompletesWithAnError() throws Exception {
        recordsManager.update(new AssessmentRecord());
        Exception exception = new Exception();
        captureRecordUpdateCompletionListener().onError(exception);

        AssessmentRecordsManager.Error error = recordsManager.getError().get();

        assertEquals(exception, error.getException());
    }

    private AssessmentRecord createRecord(String stationId) {
        AssessmentRecord record = new AssessmentRecord();
        record.setStationId(stationId);
        return record;
    }

    private void mockLoadedRecords(List<AssessmentRecord> records) {
        recordsManager.loadRecords();
        RemoteStorage.RecordsQueryListener listener = captureRecordsQueryListener();
        Map<String, AssessmentRecord> map = toRecordsMap(records);
        listener.onResult(map);
    }

    private void mockLoadedRecords() {
        mockLoadedRecords(Collections.<AssessmentRecord>emptyList());
    }

    private Map<String, AssessmentRecord> toRecordsMap(List<AssessmentRecord> records) {
        HashMap<String, AssessmentRecord> map = new HashMap<>();
        for (AssessmentRecord record : records) {
            map.put(record.stationId, record);
        }
        return map;
    }

    private RemoteStorage.RecordsQueryListener captureRecordsQueryListener() {
        ArgumentCaptor<RemoteStorage.RecordsQueryListener> listenerCaptor =
                ArgumentCaptor.forClass(RemoteStorage.RecordsQueryListener.class);
        verify(remoteStorage).getRecords(listenerCaptor.capture());
        return listenerCaptor.getValue();
    }

    private RemoteStorage.RecordUpdateCompletionListener captureRecordUpdateCompletionListener() {
        ArgumentCaptor<RemoteStorage.RecordUpdateCompletionListener> listenerCaptor =
                ArgumentCaptor.forClass(RemoteStorage.RecordUpdateCompletionListener.class);
        verify(remoteStorage).updateRecord(any(AssessmentRecord.class), listenerCaptor.capture());
        return listenerCaptor.getValue();
    }

    private void mockNetworkAvailability(boolean networkAvailability) {
        Mockito.when(networkStateMonitor.checkNetworkAvailability()).thenReturn(networkAvailability);
    }
}