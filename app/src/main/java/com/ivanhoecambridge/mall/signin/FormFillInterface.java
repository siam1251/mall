package com.ivanhoecambridge.mall.signin;

import com.ivanhoecambridge.mall.views.AppcompatEditTextWithWatcher;

/**
 * Created by Kay on 2017-01-31.
 */

public interface FormFillInterface {

    interface OnFieldFilledListener{
        void isFieldFilled(AppcompatEditTextWithWatcher aet, boolean filled);
    }

    void isFieldsCompletelyFilled(boolean filled);

}
