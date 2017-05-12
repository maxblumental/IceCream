package com.example.icecream.databinding;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.commonui.AssessmentRecordField;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BindingAdapters {

    private static final Locale locale = Locale.ENGLISH;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm MM/dd/yy", locale);

    @BindingAdapter("dateText")
    public static void setDateText(AssessmentRecordField field, Date date) {
        if (date != null) {
            field.setText(formatter.format(date));
        }
    }

    @BindingAdapter("numberDecimalText")
    public static void setNumberDecimal(AssessmentRecordField field, int oldNumber, int newNumber) {
        if (newNumber != oldNumber) {
            field.setText(String.format(locale, "%d", newNumber));
        }
    }

    @BindingAdapter("adapter")
    public static void setStringArrayAdapter(Spinner spinner, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_list_item_1, list);
        spinner.setAdapter(adapter);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    @BindingAdapter("itemSelectedListener")
    public static void setItemSelectedListener(Spinner spinner,
                                               final OnItemSelectedListener itemSelectedListener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (itemSelectedListener != null) {
                    itemSelectedListener.onItemSelected(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public interface OnFieldFocusChangeListener {
        void onChanged(View v, boolean hasFocus);
    }

    @BindingAdapter("fieldChangedListener")
    public static void setFieldFocusChangeListener(AssessmentRecordField field,
                                                   final OnFieldFocusChangeListener listener) {
        field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                listener.onChanged(view, hasFocus);
            }
        });
    }
}