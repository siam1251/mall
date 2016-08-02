package com.kineticcafe.kcpmall.utility;

/**
 * Created by Kay on 2016-08-02.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.kineticcafe.kcpmall.R;


public class ClearableEditText extends EditText implements OnTouchListener,
        OnFocusChangeListener, TextWatcherAdapter.TextWatcherListener {

    public interface Listener {
        void didClearText();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Drawable xD;
    private Listener listener;

    public ClearableEditText(Context context) {
        super(context);
        init();
        setFont(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setFont(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setFont(context);
    }

    public void setFont(Context context){
//		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/perpetuaStd-italic.otf");
//        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/times-italic.ttf");
//        this.setTypeface(tf);
        this.setIncludeFontPadding(false);
        this.setHintTextColor(Color.WHITE);
    }

    private void init() {
        xD = getCompoundDrawables()[2];
        if (xD == null) {
            xD = getResources()
                    .getDrawable(R.drawable.icn_cancel_nav);
        }

        xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
//        ColorFilter filter = new LightingColorFilter( Color.TRANSPARENT, Color.WHITE);
        ColorFilter filter = new LightingColorFilter( Color.WHITE, Color.TRANSPARENT);
        xD.setColorFilter(filter);
//        xD.setColorFilter(0xFFFFFFFF, PorterDuff.Mode.MULTIPLY);

//        xD.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
//        DrawableCompat.setTint(xD, Color.WHITE);

        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(new TextWatcherAdapter(this, this));

//        SpannableString span = new SpannableString(getHint().toString());
//		span.setSpan(new RelativeSizeSpan(1.35f), 0, getHint().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        span.setSpan(new RelativeSizeSpan(1.15f), 0, getHint().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        this.setHint(span);

//        this.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_search), null, null, null);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.l = l;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener f) {
        this.f = f;
    }

    private OnTouchListener l;
    private OnFocusChangeListener f;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean tappedX = event.getX() > (getWidth()
                        - getPaddingRight() - xD.getIntrinsicWidth());
                if (tappedX) {
                    setText("");
                    if (listener != null) {
                        listener.didClearText();
                    }
                    return true;
                }
            }
        }
        if (l != null) {
            return l.onTouch(v, event);
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(isNotEmpty(getText()));
        } else {
            setClearIconVisible(false);
        }
        if (f != null) {
            f.onFocusChange(v, hasFocus);
        }
    }

    public boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }


    @Override
    public void onTextChanged(EditText view, String text) {
        setClearIconVisible(isNotEmpty(text));
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable x = visible ? xD : null;
//        if(x != null) x.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], x, getCompoundDrawables()[3]);
    }
}
