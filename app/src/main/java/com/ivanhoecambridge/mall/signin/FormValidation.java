package com.ivanhoecambridge.mall.signin;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Kay on 2017-01-31.
 */

public class FormValidation {

    public final static boolean isEmailValid(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isNameLongEnough(CharSequence target) {
        return target.length() > 6;
    }

    /**
     *
     * @param datePicked date to compare to today's date
     * @return false if date picked is equal to today's date or later than today
     */
    public final static boolean isDateBeforeToday(Calendar datePicked){
        Calendar today = Calendar.getInstance();
        if(datePicked.compareTo(today) > -1) return false;
        else {
            int datePickedYear = datePicked.get(Calendar.YEAR);
            int datePickedMonth = datePicked.get(Calendar.MONTH);
            int datePickedDate = datePicked.get(Calendar.DAY_OF_MONTH);

            int todayYear = today.get(Calendar.YEAR);
            int todayMonth = today.get(Calendar.MONTH);
            int todayDate = today.get(Calendar.DAY_OF_MONTH);

            if(datePickedYear == todayYear && datePickedMonth == todayMonth && datePickedDate == todayDate) return false;
            else return true;
        }
    }

}
