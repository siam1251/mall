package com.kineticcafe.kcpmall.views.CustomFontView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;

public class CustomFontTextView extends TextView {

	private String mFontFamily;

	public CustomFontTextView(Context context) {
		super(context);
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs){
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
			mFontFamily = a.getString(R.styleable.CustomFontTextView_fontFamily);
			Typeface tf = Typeface.createFromAsset(context.getAssets(), mFontFamily);
			this.setTypeface(tf);
		}
	}

	protected void onDraw (Canvas canvas) {
		super.onDraw(canvas);
	}
}
