package com.example.icecream.presenter

import com.example.firebasedb.AssessmentRecord
import com.example.icecream.R
import com.example.icecream.model.Model
import com.example.icecream.view.AssessmentRecordView
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.isA
import io.reactivex.Completable
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.*

@Suppress("IllegalIdentifier")
class AssessmentRecordPresenterImplTest {

    @Rule @JvmField val rule: MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var model: Model
    @Mock private lateinit var view: AssessmentRecordView

    private lateinit var presenter: AssessmentRecordPresenterImpl

    @Before
    fun setUp() {
        presenter = AssessmentRecordPresenterImpl(model)
        presenter.attachView(view)
    }

    @Test
    fun `initialize - ask model to load records`() {
        mockLoadRecords()

        presenter.initialize()

        verify(model).loadRecords()
    }

    @Test
    fun `initialize - view show progress`() {
        mockLoadRecords()

        presenter.initialize()

        with(inOrder(view)) {
            verify(view).isRefreshing = true
            verify(view).isRefreshing = false
        }
    }

    @Test
    fun `initialize - set stations ids to view - model completes load successfully`() {
        val list = listOf(STATION_ID)
        mockLoadRecords(list)

        presenter.initialize()

        verify(view).stationIds = list
    }

    @Test
    fun `initialize - view shows toast - model completes load with error`() {
        mockLoadRecordsError(Exception())

        presenter.initialize()

        verify(view).showToast(anyString())
    }

    @Test
    fun `on actual changed - ask view for actual`() {
        presenter.onActualChanged()

        verify(view).actual
    }

    @Test
    fun `on actual changed - ask view for target`() {
        presenter.onActualChanged()

        verify(view).target
    }

    @Test
    fun `on actual changed - ask model to calculate variance`() {
        val (actual, target) = with(Random()) { nextInt() to nextInt() }
        mockActualField(actual)
        mockTargetField(target)

        presenter.onActualChanged()

        verify(model).calculateVariance(actual, target)
    }

    @Test
    fun `on actual changed - set variance field`() {
        val (actual, target, variance) = with(Random()) { listOf(nextInt(), nextInt(), nextInt()) }
        mockActualField(actual)
        mockTargetField(target)
        mockCalculateVariance(actual, target, variance)

        presenter.onActualChanged()

        verify(view).variance = variance.toString()
    }

    @Test
    fun `on actual changed - set variance field text color to red - variance is more than 10 percent below the target`() {
        val (target, actual) = listOf(100, 89)
        mockActualField(actual)
        mockTargetField(target)
        mockCalculateVariance(actual, target, actual - target)

        presenter.onActualChanged()

        verify(view).updateVarianceColor(R.color.red)
    }

    @Test
    fun `on actual changed - set variance field text color to black - variance is less than 10 percent below the target`() {
        val (target, actual) = listOf(100, 95)
        mockActualField(actual)
        mockTargetField(target)
        mockCalculateVariance(actual, target, actual - target)

        presenter.onActualChanged()

        verify(view).updateVarianceColor(R.color.black)
    }

    @Test
    fun `on actual changed - set variance field text color to black - variance is less than 5 percent above the target`() {
        val (target, actual) = listOf(100, 103)
        mockActualField(actual)
        mockTargetField(target)
        mockCalculateVariance(actual, target, actual - target)

        presenter.onActualChanged()

        verify(view).updateVarianceColor(R.color.black)
    }

    @Test
    fun `on actual changed - set variance field text color to green - variance is more than 5 percent above the target`() {
        val (target, actual) = listOf(100, 106)
        mockActualField(actual)
        mockTargetField(target)
        mockCalculateVariance(actual, target, actual - target)

        presenter.onActualChanged()

        verify(view).updateVarianceColor(R.color.green)
    }

    @Test
    fun `on refresh - ask model to reload records`() {
        mockReloadRecords()

        presenter.onRefresh()

        verify(model).reloadRecords()
    }

    @Test
    fun `on refresh - view shows progress`() {
        mockReloadRecords()

        presenter.onRefresh()

        with(inOrder(view)) {
            verify(view).isRefreshing = true
            verify(view).isRefreshing = false
        }
    }

    @Test
    fun `on refresh - set stations ids to view - model completes load successfully`() {
        val list = listOf(STATION_ID)
        mockReloadRecords(Single.just(list))

        presenter.onRefresh()

        verify(view).stationIds = list
    }

    @Test
    fun `on refresh - view shows toast - model completes load with error`() {
        mockReloadRecords(Single.error(Exception()))

        presenter.onRefresh()

        verify(view).showToast(anyString())
    }

    @Test
    fun `send - read record entries from view`() {
        mockSendRecord()

        presenter.send()

        verify(view).stationId
        verify(view).target
        verify(view).actual
        verify(view).variance
    }

    @Test
    fun `send - model is asked to update server record with what is on the screen`() {
        val stationId = "stationId"
        val (target, actual, variance) = listOf(10, 15, 5)
        mockFields(stationId, target, actual, variance)
        mockSendRecord()

        presenter.send()

        val record = captureUpdatedRecord()
        assertThat(stationId, `is`(record.stationId))
        assertThat(target, `is`(record.target))
        assertThat(actual, `is`(record.actual))
        assertThat(variance, `is`(record.variance))
    }

    @Test
    fun `send - show toast - model updates the record successfully`() {
        mockSendRecord()

        presenter.send()

        verify(view).showToast(anyString())
    }

    @Test
    fun `send - show toast - model fails to update the record`() {
        mockSendRecord(Completable.error(Exception()))

        presenter.send()

        verify(view).showToast(anyString())
    }

    @Test
    fun `on station selected - get record for selected station from model`() {
        presenter.onStationSelected(STATION_ID)

        verify(model).getRecord(STATION_ID)
    }

    @Test
    fun `on station selected - set station id field from the according record`() {
        val record = createAssessmentRecord()
        mockGetRecord(record)

        presenter.onStationSelected(STATION_ID)

        verify(view).stationId = STATION_ID
    }

    @Test
    fun `on station selected - set target field from the according record`() {
        val record = createAssessmentRecord()
        mockGetRecord(record)

        presenter.onStationSelected(STATION_ID)

        verify(view).target = record.target.toString()
    }

    @Test
    fun `on station selected - set actual field from the according record`() {
        val record = createAssessmentRecord()
        mockGetRecord(record)

        presenter.onStationSelected(STATION_ID)

        verify(view).actual = record.actual.toString()
    }

    @Test
    fun `on station selected - update variance`() {
        val record = createAssessmentRecord()
        mockGetRecord(record)

        presenter.onStationSelected(STATION_ID)

        verify(view).variance = record.variance.toString()
    }

    @Test
    fun `on station selected - set variance color to green - variance is more than 5 percent above the target`() {
        mockGetRecord(createAssessmentRecord(100, 106, 6))

        presenter.onStationSelected(STATION_ID)

        verify(view).updateVarianceColor(R.color.green)
    }

    @Test
    fun `on station selected - set variance color to black - variance is less than 5 percent above the target`() {
        mockGetRecord(createAssessmentRecord(100, 103, 3))

        presenter.onStationSelected(STATION_ID)

        verify(view).updateVarianceColor(R.color.black)
    }

    @Test
    fun `on station selected - set variance color to black - variance is less than 10 percent below the target`() {
        mockGetRecord(createAssessmentRecord(100, 95, -5))

        presenter.onStationSelected(STATION_ID)

        verify(view).updateVarianceColor(R.color.black)
    }

    @Test
    fun `on station selected - set variance color to black - variance is more than 10 percent below the target`() {
        mockGetRecord(createAssessmentRecord(100, 89, -11))

        presenter.onStationSelected(STATION_ID)

        verify(view).updateVarianceColor(R.color.red)
    }

    private fun mockLoadRecords(list: List<String> = listOf(STATION_ID)) {
        mockLoadRecords(Single.just(list))
    }

    private fun mockLoadRecordsError(exception: Exception) {
        mockLoadRecords(Single.error(exception))
    }

    private fun mockLoadRecords(single: Single<List<String>>) {
        `when`(model.loadRecords()).thenReturn(single)
    }

    private fun mockReloadRecords(single: Single<List<String>> = Single.just(listOf(STATION_ID))) {
        `when`(model.reloadRecords()).thenReturn(single)
    }

    private fun mockActualField(actual: Int) {
        `when`(view.actual).thenReturn(actual.toString())
    }

    private fun mockTargetField(target: Int) {
        `when`(view.target).thenReturn(target.toString())
    }

    private fun mockVarianceField(variance: Int) {
        `when`(view.variance).thenReturn(variance.toString())
    }

    private fun mockStationIdField(stationId: String) {
        `when`(view.stationId).thenReturn(stationId)
    }

    private fun mockCalculateVariance(actual: Int, target: Int, variance: Int) {
        `when`(model.calculateVariance(actual, target)).thenReturn(variance)
    }

    private fun mockSendRecord(completable: Completable = Completable.create { it.onComplete() }) {
        `when`(model.update(isA())).thenReturn(completable)
    }

    private fun captureUpdatedRecord(): AssessmentRecord {
        val captor = argumentCaptor<AssessmentRecord>()
        verify(model).update(captor.capture())
        return captor.lastValue
    }

    private fun mockFields(stationId: String, target: Int, actual: Int, variance: Int) {
        mockStationIdField(stationId)
        mockTargetField(target)
        mockActualField(actual)
        mockVarianceField(variance)
    }

    private fun createAssessmentRecord(target: Int = 42, actual: Int = 45, variance: Int = 3): AssessmentRecord {
        return AssessmentRecord().apply {
            stationId = STATION_ID
            date = Date()
            this.target = target
            this.actual = actual
            this.variance = variance
        }
    }

    private fun mockGetRecord(record: AssessmentRecord) {
        `when`(model.getRecord(STATION_ID)).thenReturn(record)
    }

}

private const val STATION_ID = "stationId"
