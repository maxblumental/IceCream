package com.example.icecream.model

import com.example.firebasedb.AssessmentRecord
import com.example.firebasedb.RxRemoteStorage
import io.reactivex.Completable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.*

@Suppress("IllegalIdentifier")
class ModelImplTest {

    @Rule @JvmField val rule: MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var remoteStorage: RxRemoteStorage

    private lateinit var model: Model

    @Before
    fun setUp() {
        model = ModelImpl(remoteStorage, null)
    }

    @Test
    fun `load records - call remote storage - data is not cached`() {
        mockRemoteStorageGetRecords()

        model.loadRecords().subscribe()

        verify(remoteStorage).records
    }

    @Test
    fun `load records - do not call remote storage - data is cached`() {
        mockRemoteStorageGetRecords()
        model.loadRecords().subscribe()
        clearInvocations(remoteStorage)

        model.loadRecords().subscribe()

        verify(remoteStorage, never()).records
    }

    @Test
    fun `reload records - call remote storage - data is not cached`() {
        mockRemoteStorageGetRecords()

        model.reloadRecords().subscribe()

        verify(remoteStorage).records
    }

    @Test
    fun `reload records - call remote storage - data is cached`() {
        mockRemoteStorageGetRecords()
        model.loadRecords().subscribe()
        clearInvocations(remoteStorage)

        model.reloadRecords().subscribe()

        verify(remoteStorage).records
    }

    @Test
    fun `update - call update in remote storage`() {
        val record = AssessmentRecord()
        mockRemoteStorageUpdate(record)

        model.update(record).subscribe()

        verify(remoteStorage).update(record)
    }

    @Test
    fun `get record - return record - record is present`() {
        val record = AssessmentRecord().apply { stationId = STATION_ID }
        mockRemoteStorageGetRecords(record)
        model.loadRecords().subscribe()

        val retrievedRecord = model.getRecord(STATION_ID)

        assertThat(retrievedRecord, `is`(record))
    }

    @Test
    fun `get record - return null - record is absent`() {
        val retrievedRecord = model.getRecord(STATION_ID)

        assertThat(retrievedRecord, Is(nullValue()))
    }

    @Test
    fun `calculate variance - return difference of actual and target`() {
        val (actual, target) = with(Random()) { nextInt() to nextInt() }

        val variance = model.calculateVariance(actual, target)

        assertThat(variance, equalTo(actual - target))
    }

    private fun mockRemoteStorageGetRecords(record: AssessmentRecord
                                            = AssessmentRecord().apply { stationId = STATION_ID }) {
        val map = mapOf(record.stationId to record)
        Mockito.`when`(remoteStorage.records).thenReturn(Single.just(map))
    }

    private fun mockRemoteStorageUpdate(record: AssessmentRecord) {
        `when`(remoteStorage.update(record)).thenReturn(Completable.create { it.onComplete() })
    }

}

private const val STATION_ID = "stationId"
