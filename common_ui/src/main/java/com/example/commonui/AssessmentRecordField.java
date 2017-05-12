package com.example.commonui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AssessmentRecordField extends LinearLayout {

    private EditText field;
    private TextView fieldName;

    public AssessmentRecordField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
        setAttributes(context, attrs);
    }

    public void setText(String text) {
        field.setText(text);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        field.setOnFocusChangeListener(listener);
    }

    private void initialize(Context context) {
        inflate(context, R.layout.assessment_record_field, this);
        field = (EditText) findViewById(R.id.field);
        fieldName = (TextView) findViewById(R.id.fieldName);
    }

    private void setAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AssessmentRecordField,
                0, 0);

        try {
            fieldName.setText(a.getString(R.styleable.AssessmentRecordField_fieldName));
            field.setEnabled(a.getBoolean(R.styleable.AssessmentRecordField_enabled, true));
            field.setText(a.getString(R.styleable.AssessmentRecordField_text));
            field.setInputType(
                    a.getInteger(R.styleable.AssessmentRecordField_inputType, 0) == 0
                            ? EditorInfo.TYPE_CLASS_TEXT
                            : EditorInfo.TYPE_CLASS_NUMBER
            );
        } finally {
            a.recycle();
        }
    }
}
