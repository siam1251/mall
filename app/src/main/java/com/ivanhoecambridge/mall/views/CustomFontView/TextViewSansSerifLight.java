package com.ivanhoecambridge.mall.views.CustomFontView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewSansSerifLight extends TextView {

	public TextViewSansSerifLight(Context context) {
		super(context);

		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		this.setTypeface(tf);
	}

	public TextViewSansSerifLight(Context context, AttributeSet attrs) {
		super(context, attrs);
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		this.setTypeface(tf);
	}

	public TextViewSansSerifLight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
		this.setTypeface(tf);
	}

	protected void onDraw (Canvas canvas) {
		super.onDraw(canvas);
	}

}
