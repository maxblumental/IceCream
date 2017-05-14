package com.example.icecream.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.icecream.AssessmentRecordActivity;
import com.example.icecream.model.AssessmentRecordsManager;

import java.util.Locale;

public class ErrorHandlingView extends View {

    public ErrorHandlingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void handleError(AssessmentRecordsManager.Error error) {
        if (error == null) {
            return;
        }

        AssessmentRecordActivity activity = (AssessmentRecordActivity) getContext();
        activity.showToast(createErrorMessage(error));
    }

    private String createErrorMessage(AssessmentRecordsManager.Error error) {
        String errorName = error.name();
        String exceptionMessage = error.getException().getMessage();
        return String.format(Locale.ENGLISH, "%s: %s", errorName, exceptionMessage);
    }
}
