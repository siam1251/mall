package com.ivanhoecambridge.mall.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.ivanhoecambridge.mall.R;

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

        return new DatePickerDialog(getActivity(), R.style.DialogTheme_BirthdayPicker, this, year, month, day);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DateSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(listener.getClass().toString()
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
        void onBirthdaySelected(GregorianCalendar birthday);
    }
}