package com.ivanhoecambridge.mall.signin;

import android.support.v7.widget.AppCompatEditText;

import com.ivanhoecambridge.mall.views.TextInputLayoutListener;

/**
 * Created by Kay on 2017-01-31.
 */

public interface FormFillInterface {

    interface OnFieldFilledListener{
        void isFieldFilled(TextInputLayoutListener aet, boolean filled);
    }

    void isFieldsCompletelyFilled(boolean filled);

}
