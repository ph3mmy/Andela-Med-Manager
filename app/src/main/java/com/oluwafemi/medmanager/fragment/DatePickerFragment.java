package com.oluwafemi.medmanager.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.oluwafemi.medmanager.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by phemi-mint on 4/4/18.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    OnDateSet onDateSetListener;
    OnCancelled onCancelled;

    public static final String PICKER_TITLE = "picker_title";
    boolean isMonthYear;

    public DatePickerFragment() {
        super();
    }

    public static DatePickerFragment newInstance(String pickerTitle, boolean isMonthYear) {

        Bundle args = new Bundle();
        args.putString(PICKER_TITLE, pickerTitle);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public OnDateSet getOnDateSetListener() {
        return onDateSetListener;
    }

    public void setOnDateSetListener(OnDateSet onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public OnCancelled getOnCancelled() {
        return onCancelled;
    }

    public void setOnCancelled(OnCancelled onCancelled) {
        this.onCancelled = onCancelled;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        Bundle args = getArguments();
        String titleStr = args.getString(PICKER_TITLE);

        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), R.style.datepicker,this, year, month, day);
        datePicker.setTitle(titleStr);
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePicker;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getOnCancelled().onCancelClicked();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        getOnDateSetListener().onDateSetListener(calendar.getTime());
    }

    public interface OnDateSet{
        void onDateSetListener(Date date);
    }public interface OnCancelled{
        void onCancelClicked();
    }
}
