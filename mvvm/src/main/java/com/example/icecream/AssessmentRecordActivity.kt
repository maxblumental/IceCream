package com.example.icecream

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.icecream.databinding.ActivityAssessmentRecordBinding
import com.example.icecream.di.assessment_record.ModelModule
import com.example.icecream.di.assessment_record.ViewModelModule
import com.example.icecream.view_model.AssessmentRecordViewModel
import com.example.icecream.wrapper.LifecycleEventsConsumer
import javax.inject.Inject

class AssessmentRecordActivity : AppCompatActivity() {

    @Inject lateinit var viewModel: AssessmentRecordViewModel

    @Inject lateinit var lifecycleEventsConsumer: LifecycleEventsConsumer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAssessmentRecordBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_assessment_record)

        val savedState = savedInstanceState?.getBundle(KEY_STATE)

        IceCreamApplication.component
                .plus(ModelModule(savedState), ViewModelModule(this))
                .inject(this)

        binding.viewModel = viewModel

        lifecycleEventsConsumer.onCreate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(KEY_STATE, viewModel.saveState())
    }

    override fun onDestroy() {
        lifecycleEventsConsumer.onDestroy()
        super.onDestroy()
    }
}

private const val KEY_STATE = "key_state"