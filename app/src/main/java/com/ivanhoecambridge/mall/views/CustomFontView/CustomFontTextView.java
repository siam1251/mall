package com.ivanhoecambridge.mall.views.CustomFontView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;

public class CustomFontTextView extends TextView {

	private String mFontFamily;
	private Drawable compoundDrawable;
	private int drawableColour;

	public CustomFontTextView(Context context) {
		super(context);
		if(!isInEditMode()){}
	}

	public CustomFontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) init(context, attrs);
	}

	public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs){
		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
			mFontFamily = a.getString(R.styleable.CustomFontTextView_fontFamily);
			if(mFontFamily == null) mFontFamily = context.getResources().getString(R.string.fontFamily_roboto_regular);
			drawableColour = a.getColor(R.styleable.CustomFontTextView_drawableFilterColor, ContextCompat.getColor(context, R.color.white));
			Typeface tf = Typeface.createFromAsset(context.getAssets(), mFontFamily);
			this.setTypeface(tf);
			a.recycle();
		}
		compoundDrawable = getFirstAvailableDrawable();
		if (compoundDrawable != null) {
			compoundDrawable.setColorFilter(drawableColour, PorterDuff.Mode.SRC_ATOP);
		}
	}

	public void setFont(Context context, String font){
		Typeface tf = Typeface.createFromAsset(context.getAssets(), font);
		this.setTypeface(tf);
		invalidate();
	}

	protected void onDraw (Canvas canvas) {
		super.onDraw(canvas);
	}

    /**
     * Returns the first available compound drawable.
     * <br /> Order of array:
     * <ul>
     *     <li>top</li>
     *     <li>left</li>
     *     <li>bottom</li>
     *     <li>right</li>
     * </ul>
     * @return first non-null drawable or null if none is set.
     */
	private Drawable getFirstAvailableDrawable() {
		for (Drawable drawable : getCompoundDrawables()) {
			if (drawable != null) return drawable;
		}
		return null;
	}
}
