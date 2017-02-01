package com.ivanhoecambridge.mall.signin;

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

}
