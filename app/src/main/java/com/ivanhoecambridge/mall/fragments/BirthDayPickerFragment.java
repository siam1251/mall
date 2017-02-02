package com.ivanhoecambridge.mall.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class BirthDayPickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DateSelectedListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

//        return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
        return new DatePickerDialog(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert, this, year, month, day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DateSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DateSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        listener.onBirthdaySelected(new GregorianCalendar(year, month, day));
    }

    public interface DateSelectedListener {
        public void onBirthdaySelected(GregorianCalendar birthday);
    }
}