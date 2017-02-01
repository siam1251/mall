package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.signin.FormFillInterface;
import com.ivanhoecambridge.mall.signin.FormValidation;

/**
 * Created by Kay on 2017-01-31.
 */

public class AppcompatEditTextWithWatcher extends AppCompatEditText implements TextWatcher {

    private OnValidateListener onValidateListener;

    private FormFillInterface.OnFieldFilledListener onFieldFilledListener;

    public interface OnValidateListener {
        void validate();
    }

    public AppcompatEditTextWithWatcher(Context context) {
        super(context);
        this.setOnFocusChangeListener(onFocusChangeListener);
    }

    public AppcompatEditTextWithWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnFocusChangeListener(onFocusChangeListener);
    }

    public AppcompatEditTextWithWatcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void setOnValidateListener(OnValidateListener onValidateListener){
        this.onValidateListener = onValidateListener;
    }

    public OnValidateListener getOnValidateListener() {
        return onValidateListener;
    }

    public void setOnFieldFilledListener(FormFillInterface.OnFieldFilledListener onFieldFilledListener) {
        this.onFieldFilledListener = onFieldFilledListener;
    }

    public FormFillInterface.OnFieldFilledListener getOnFieldFilledListener() {
        return onFieldFilledListener;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        this.setTag(charSequence);
        if(onFieldFilledListener != null) {
            if(!charSequence.toString().equals("")) onFieldFilledListener.isFieldFilled(this, true);
            else onFieldFilledListener.isFieldFilled(this, false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(onValidateListener != null && !b) {
                onValidateListener.validate();
            }
        }
    };

}
