package com.ivanhoecambridge.mall.signin;

import android.support.v7.widget.AppCompatEditText;

import com.ivanhoecambridge.mall.views.TextInputLayoutListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kay on 2017-01-31.
 */

public class FormFillChecker implements FormFillInterface.OnFieldFilledListener {

    private Map<TextInputLayoutListener, Boolean> editTextmap = new HashMap<TextInputLayoutListener, Boolean>();
    private FormFillInterface formFillInterface;

    public FormFillChecker(FormFillInterface formFillInterface) {
        this.formFillInterface = formFillInterface;
    }

    public Map<TextInputLayoutListener, Boolean> getEditTextmap() {
        return editTextmap;
    }

    public void addEditText(TextInputLayoutListener... appCompatEditTexts){
        for(TextInputLayoutListener appCompatEditText : appCompatEditTexts){
            editTextmap.put(appCompatEditText, false);
        }
    }

    @Override
    public void isFieldFilled(TextInputLayoutListener aet, boolean filled) {
        if(editTextmap.containsKey(aet)) editTextmap.put(aet, filled);
        for (boolean value : editTextmap.values()) {
            if(!value) {
                formFillInterface.isFieldsCompletelyFilled(false);
                return;
            }
        }
        formFillInterface.isFieldsCompletelyFilled(true);
    }
}
