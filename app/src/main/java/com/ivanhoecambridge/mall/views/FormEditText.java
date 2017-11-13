package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;

import com.ivanhoecambridge.mall.R;

/**
 * Created by petar on 2017-10-30.
 */

public class FormEditText extends AppCompatEditText {

    private final static String TAG = "FormEditText";

    private final static int DEFAULT_EMPTY_COLOR = -1;
    public final static int DRAWABLE_START = 0;
    public final static int DRAWABLE_TOP = 1;
    public final static int DRAWABLE_END = 2;
    public final static int DRAWABLE_BOTTOM = 3;

    private Context context;
    private PorterDuffColorFilter defaultColorFilter;
    private int drawableFilterColor = DEFAULT_EMPTY_COLOR;
    private String fontFamily;
    private Typeface typeface;
    private int drawableIndex = 0;
    private Drawable drawable;



    public FormEditText(Context context) {
        super(context);
        if (!isInEditMode()) init(context, null);
    }

    public FormEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) init(context, attrs);
    }

    public FormEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FormEditText);
            drawableFilterColor = a.getColor(R.styleable.FormEditText_drawableFilterColor, DEFAULT_EMPTY_COLOR);
            fontFamily = a.getString(R.styleable.FormEditText_fontFamily);
            a.recycle();
        } else {
            initDefault(context);
        }
        setFont(fontFamily);
        drawable = getFirstDrawable();
        if (drawable != null) {
            defaultColorFilter = new PorterDuffColorFilter(drawableFilterColor, PorterDuff.Mode.SRC_IN);
            if (hasFilter()) {
                setColorFilter(defaultColorFilter);
            }
        }
    }

    private void initDefault(Context context) {
        fontFamily = context.getResources().getString(R.string.font_fontFamily_medium);
    }


    public void setFont(String fontFamily) {
        typeface = Typeface.createFromAsset(context.getAssets(), fontFamily);
        setTypeface(typeface);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * Gets the drawable at the given index.
     * @param drawableIndex Drawable index. (top = 0, start/left = 1, bottom = 2, right/end = 3)
     * @return requested Drawable or null if no drawable at that index exists.
     */
    @Nullable
    public Drawable getDrawable(int drawableIndex) {
        return getCompoundDrawables()[drawableIndex];

    }

    /**
     * Returns the set drawable if there is one.
     * @return Set drawable or null if none are set.
     */
    @Nullable
    public Drawable getDrawable() {
        return  drawable;
    }

    /**
     * Gets the first non-null drawable set on the EditText.
     * @return first available drawable or null if none are set.
     * <br /> Order of drawables
     * <ul>
     *     <li>left/start</li>
     *     <li>top</li>
     *     <li>right/end</li>
     *     <li>bottom</li>
     * </ul>
     */
    private Drawable getFirstDrawable() {
        for (Drawable image : getCompoundDrawables()) {
            if (image != null) {
                return image;
            }
            drawableIndex++;
        }
        drawableIndex = 0;
        return null;
    }


    public void clearColorFilter() {
        setColorFilter(null);
    }

    /**
     * Set the drawable FilterColor using PorterDuff.Mode allows Mode and color changes.
     * @param colorFilter PorterDuff.Mode ColorFilter
     */
    public void setColorFilter(ColorFilter colorFilter) {
        drawable.setColorFilter(colorFilter);
        invalidate();
    }

    /**
     * Sets the drawable color and applies it using SRC_IN.
     * <br />To use a diferent mode use {@link #setColorFilter(ColorFilter colorFilter)}
     * @param color Color to apply to drawable.
     */
    public void setDrawableFilterColor(@ColorRes int color) {
        drawableFilterColor = color;
        setColorFilter(defaultColorFilter);
    }

    private boolean hasFilter() {
        return drawableFilterColor != DEFAULT_EMPTY_COLOR;
    }

    /**
     * Sets the view's error state, along with an error message that is displayed underneath the view.
     * @param isErrorState sets the ErrorState flag, if true the view will be in error state otherwise normal state.
     */
    public void setErrorState(boolean isErrorState) {
        if (isErrorState) {
            setBackground(ContextCompat.getDrawable(context, R.drawable.cardview_error_bg));
        } else {
            setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        }
        invalidate();
    }

    /**
     * Sets the view's focus state for the drawable if one exists.
     * @param hasFocus flag to check if view has focus.
     */
    public void setFocusStateDrawable(boolean hasFocus) {
        if (hasFocus) {
            setDrawableFilterColor(drawableFilterColor);
        } else {
            clearColorFilter();
        }
    }

    /**
     * Toggles the drawable visibility.
     * @param isVisible flag to set visibility. true for VISIBLE false for GONE
     */
    public void toggleDrawableVisibility(boolean isVisible) {
        if (drawable != null) {
            if (isVisible) {
                setCompoundDrawables((drawableIndex == DRAWABLE_START) ? drawable : null,
                        (drawableIndex == DRAWABLE_TOP) ? drawable : null,
                        (drawableIndex == DRAWABLE_END) ? drawable : null,
                        (drawableIndex == DRAWABLE_BOTTOM) ? drawable : null);
            } else {
                setCompoundDrawables(null, null, null, null);
            }
        }
    }


}
