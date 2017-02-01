package com.ivanhoecambridge.mall.signin;

import com.ivanhoecambridge.mall.views.AppcompatEditTextWithWatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kay on 2017-01-31.
 */

public class FormFillChecker implements FormFillInterface.OnFieldFilledListener {

    private Map<AppcompatEditTextWithWatcher, Boolean> editTextmap = new HashMap<AppcompatEditTextWithWatcher, Boolean>();
    private FormFillInterface formFillInterface;

    public FormFillChecker(FormFillInterface formFillInterface) {
        this.formFillInterface = formFillInterface;
    }

    public Map<AppcompatEditTextWithWatcher, Boolean> getEditTextmap() {
        return editTextmap;
    }

    public void addEditText(AppcompatEditTextWithWatcher... appCompatEditTexts){
        for(AppcompatEditTextWithWatcher appCompatEditText : appCompatEditTexts){
            editTextmap.put(appCompatEditText, false);
        }
    }

    @Override
    public void isFieldFilled(AppcompatEditTextWithWatcher aet, boolean filled) {
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
