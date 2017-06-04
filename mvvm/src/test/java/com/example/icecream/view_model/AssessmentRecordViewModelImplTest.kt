package com.example.icecream.view_model

import com.example.firebasedb.AssessmentRecord
import com.example.icecream.model.Model
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree.BAD
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree.GOOD
import com.example.icecream.view_model.AssessmentRecordViewModel.VarianceDegree.NORMAL
import com.example.icecream.wrapper.Lifecycle
import com.example.icecream.wrapper.ToastMaker
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.isA
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.*

@Suppress("IllegalIdentifier")
class AssessmentRecordViewModelImplTest {

    @Rule @JvmField val rule: MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var model: Model
    @Mock private lateinit var toastMaker: ToastMaker
    @Mock private lateinit var lifecycle: Lifecycle
    private val lifecycleSubject: PublishSubject<Lifecycle.Event> = PublishSubject.create()

    private lateinit var viewModel: AssessmentRecordViewModel

    @Before
    fun setUp() {
        mockLifecycle()
        viewModel = AssessmentRecordViewModelImpl(model, toastMaker, lifecycle)
    }

    @Test
    fun `on create - model is asked to load records`() {
        lifecycleSubject.onNext(Lifecycle.Event.CREATE)

        verify(model).loadRecords()
    }

    @Test
    fun `send - model is asked to update server record with what is on the screen`() {
        mockSendRecord()

        viewModel.onSend()

        verify(model).update(any<AssessmentRecord>())
    }

    @Test
    fun `send - show toast - model updates the record successfully`() {
        mockSendRecord()

        viewModel.onSend()

        verify(toastMaker).show(anyString())
    }

    @Test
    fun `send - show toast - model fails to update the record`() {
        mockSendRecord(completable = Completable.error(Exception()))

        viewModel.onSend()

        verify(toastMaker).show(anyString())
    }

    @Test
    fun `get variance - ask model to calculate variance`() {
        val (actual, target) = with(Random()) { nextInt() to nextInt() }
        viewModel.actual = actual
        viewModel.target = target

        viewModel.variance

        verify(model).calculateVariance(actual, target)
    }

    @Test
    fun `on refresh - ask model to reload records`() {
        mockReloadRecords()

        viewModel.onRefresh()

        verify(model).reloadRecords()
    }

    @Test
    fun `on refresh - loaded station ids can be accessed - model completes load successfully`() {
        val list = listOf(STATION_ID)
        mockReloadRecords(Single.just(list))

        viewModel.onRefresh()

        assertThat(viewModel.stationIds, `is`(list))
    }

    @Test
    fun `on refresh - view shows toast - model completes load with error`() {
        mockReloadRecords(Single.error(Exception()))

        viewModel.onRefresh()

        verify(toastMaker).show(anyString())
    }

    @Test
    fun `get variance degree - return good - variance is more than 10 percent below the target`() {
        val (actual, target, variance) = listOf(106, 100, 6)
        viewModel.actual = actual
        viewModel.target = target
        mockCalculateVariance(actual, target, variance)

        val varianceDegree = viewModel.varianceDegree

        assertThat(varianceDegree, `is`(GOOD))
    }

    @Test
    fun `get variance degree - return normal - variance is less than 10 percent below the target`() {
        val (actual, target, variance) = listOf(103, 100, 3)
        viewModel.actual = actual
        viewModel.target = target
        mockCalculateVariance(actual, target, variance)

        val varianceDegree = viewModel.varianceDegree

        assertThat(varianceDegree, `is`(NORMAL))
    }

    @Test
    fun `get variance degree - return normal - variance is less than 5 percent above the target`() {
        val (actual, target, variance) = listOf(95, 100, -5)
        viewModel.actual = actual
        viewModel.target = target
        mockCalculateVariance(actual, target, variance)

        val varianceDegree = viewModel.varianceDegree

        assertThat(varianceDegree, `is`(NORMAL))
    }

    @Test
    fun `get variance degree - return bad - variance is more than 5 percent above the target`() {
        val (actual, target, variance) = listOf(89, 100, -11)
        viewModel.actual = actual
        viewModel.target = target
        mockCalculateVariance(actual, target, variance)

        val varianceDegree = viewModel.varianceDegree

        assertThat(varianceDegree, `is`(BAD))
    }

    private fun mockLifecycle() {
        `when`(lifecycle.events()).thenReturn(lifecycleSubject)
    }

    private fun mockSendRecord(completable: Completable = Completable.create { it.onComplete() }) {
        `when`(model.update(isA())).thenReturn(completable)
    }

    private fun mockReloadRecords(single: Single<List<String>> = Single.just(listOf(STATION_ID))) {
        `when`(model.reloadRecords()).thenReturn(single)
    }

    private fun mockCalculateVariance(actual: Int, target: Int, variance: Int) {
        `when`(model.calculateVariance(actual, target)).thenReturn(variance)
    }

}

private const val STATION_ID = "stationId"