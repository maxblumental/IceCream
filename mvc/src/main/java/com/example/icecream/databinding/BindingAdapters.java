package com.example.icecream.databinding;

import android.databinding.BindingAdapter;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.commonui.AssessmentRecordField;
import com.example.icecream.R;
import com.example.icecream.model.AssessmentRecordsManager;
import com.example.icecream.model.ObservableAssessmentRecord;
import com.example.icecream.view.ErrorHandlingView;
import com.example.icecream.view.NetworkObserverView;

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
        void onItemSelected(Object item);
    }

    @BindingAdapter("itemSelectedListener")
    public static void setItemSelectedListener(Spinner spinner,
                                               final OnItemSelectedListener itemSelectedListener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (itemSelectedListener != null) {
                    itemSelectedListener.onItemSelected(parent.getSelectedItem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @BindingAdapter("valueColor")
    public static void setValueColor(AssessmentRecordField field,
                                     ObservableAssessmentRecord.VarianceDegree varianceDegree) {
        if (varianceDegree == null) {
            return;
        }
        @ColorRes int color;
        switch (varianceDegree) {
            case GOOD:
                color = R.color.goodVarianceDegree;
                break;
            case BAD:
                color = R.color.badVarianceDegree;
                break;
            default:
                color = R.color.normalVarianceDegree;
        }
        field.setValueColor(color);
    }

    @BindingAdapter("handleError")
    public static void handleError(ErrorHandlingView view, AssessmentRecordsManager.Error error) {
        view.handleError(error);
    }

    @BindingAdapter("networkAvailability")
    public static void setNetworkAvailability(NetworkObserverView view, boolean networkAvailable) {
        view.setNetworkAvailable(networkAvailable);
    }
}
