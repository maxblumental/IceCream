package com.example.commonui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
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

    public String getText() {
        return field.getText().toString();
    }

    public interface ValueChangeListener {
        void onChange(String value);
    }

    public void setOnValueChangeListener(final ValueChangeListener listener) {
        field.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText editText = (EditText) v;
                    String text = editText.getText().toString();
                    listener.onChange(text);
                }
            }
        });
    }

    public void setValueColor(@ColorRes int color) {
        field.setTextColor(ResourcesCompat.getColor(getResources(), color, null));
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
