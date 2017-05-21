package com.example.icecream

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.icecream.di.assessment_record.ModelModule
import com.example.icecream.di.assessment_record.PresenterModule
import com.example.icecream.presenter.AssessmentRecordPresenter

import com.example.icecream.view.AssessmentRecordView
import com.example.icecream.view.AssessmentRecordViewImpl
import javax.inject.Inject

class AssessmentRecordActivity : AppCompatActivity() {

    private lateinit var view: AssessmentRecordView

    @Inject lateinit var presenter: AssessmentRecordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        val viewGroup = inflater.inflate(R.layout.activity_record_assessment, null) as ViewGroup
        setContentView(viewGroup)

        val savedState = savedInstanceState?.getBundle(KEY_STATE)

        IceCreamApplication.component
                .plus(ModelModule(savedState), PresenterModule())
                .inject(this)

        view = AssessmentRecordViewImpl(presenter, viewGroup)

        presenter.attachView(view)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isChangingConfigurations) {
            outState.putBundle(KEY_STATE, presenter.onSaveState())
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}

private const val KEY_STATE = "key_state"