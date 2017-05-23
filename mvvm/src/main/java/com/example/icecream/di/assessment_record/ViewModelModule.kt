package com.example.icecream.di.assessment_record

import android.content.Context
import com.example.icecream.di.PerActivity
import com.example.icecream.model.Model
import com.example.icecream.view_model.AssessmentRecordViewModel
import com.example.icecream.view_model.AssessmentRecordViewModelImpl
import com.example.icecream.wrapper.Lifecycle
import com.example.icecream.wrapper.LifecycleEventsConsumer
import com.example.icecream.wrapper.LifecycleWrapper
import com.example.icecream.wrapper.ToastMaker
import com.example.icecream.wrapper.ToastMakerImpl

import dagger.Module
import dagger.Provides

@Module
class ViewModelModule(private val context: Context) {

    @PerActivity
    @Provides
    fun provideAssessmentRecordPresenter(model: Model,
                                         toastMaker: ToastMaker,
                                         lifecycle: Lifecycle): AssessmentRecordViewModel {
        return AssessmentRecordViewModelImpl(model, toastMaker, lifecycle)
    }

    @PerActivity
    @Provides
    fun provideLifecycleWrapper(): LifecycleWrapper {
        return LifecycleWrapper()
    }

    @PerActivity
    @Provides
    fun provideLifecycle(lifecycleWrapper: LifecycleWrapper): Lifecycle {
        return lifecycleWrapper
    }

    @PerActivity
    @Provides
    fun provideLifecycleEventsConsumer(lifecycleWrapper: LifecycleWrapper): LifecycleEventsConsumer {
        return lifecycleWrapper
    }

    @PerActivity
    @Provides
    fun provideToastMaker(): ToastMaker {
        return ToastMakerImpl(context)
    }
}
