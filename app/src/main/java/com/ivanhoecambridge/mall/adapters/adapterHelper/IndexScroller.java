package com.ivanhoecambridge.mall.adapters.adapterHelper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KayShin on 6/30/2015.
 */
public class IndexScroller {

    private float mIndexbarWidth;
    private float mIndexbarMargin;
    private float mPreviewPadding;
    private float mDensity;
    private float mScaledDensity;
    private float mAlphaRate;
    private int mState = STATE_HIDDEN;
    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;
    private boolean mIsIndexing = false;
    private RecyclerView recyclerView = null;
    public List<String> mSections = new ArrayList<>();
    public List<Integer> mSectionPosition = new ArrayList<>();
    private RectF mIndexbarRect;

    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SHOWING = 1;
    private static final int STATE_SHOWN = 2;
    private static final int STATE_HIDING = 3;

    private static final long FADING_DELAY = 2000;


    public IndexScroller(Context context, RecyclerView lv) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        recyclerView = lv;
        recyclerView.setScrollContainer(false);
        mIndexbarWidth = 20 * mDensity;
        mIndexbarMargin = 10 * mDensity;
        mPreviewPadding = 5 * mDensity; //square in the center
    }

    public void draw(Canvas canvas) {
        if (mState == STATE_HIDDEN) {
            return;
        }

        if (mIndexbarRect == null) {
            return;
        }

        if (mSections == null || mSections.isEmpty()) {
            return;
        }

        // mAlphaRate determines the rate of opacity
        Paint indexbarPaint = new Paint();
        indexbarPaint.setColor(Color.BLACK);
        indexbarPaint.setAlpha((int) (64 * mAlphaRate));
        indexbarPaint.setAntiAlias(true);
        canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity, indexbarPaint);

        if (mSections != null && mSections.size() > 0) {
            // Preview is shown when mCurrentSection is set
            if (mCurrentSection >= 0) {
                Paint previewPaint = new Paint();
                previewPaint.setColor(Color.BLACK);
                previewPaint.setAlpha(96);
                previewPaint.setAntiAlias(true);
                previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

                Paint previewTextPaint = new Paint();
                previewTextPaint.setColor(Color.WHITE);
                previewTextPaint.setAntiAlias(true);
                previewTextPaint.setTextSize(50 * mScaledDensity);



                Rect r = new Rect();
                recyclerView.getWindowVisibleDisplayFrame(r);
                int screenHeight = recyclerView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d("IndexScroller", "keypadHeight = " + keypadHeight);


                int extraBotPaddingWhenKeyboardAppears;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    extraBotPaddingWhenKeyboardAppears = (mListViewHeight - keypadHeight);
                }
                else {
                    // keyboard is closed
                    extraBotPaddingWhenKeyboardAppears = 0;
                }




                float previewTextWidth = previewTextPaint.measureText(mSections.get(mCurrentSection));
                float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
                RectF previewRect = new RectF((mListViewWidth - previewSize) / 2
                        , (mListViewHeight - previewSize - extraBotPaddingWhenKeyboardAppears) / 2
                        , (mListViewWidth - previewSize) / 2 + previewSize
                        , (mListViewHeight - previewSize - extraBotPaddingWhenKeyboardAppears) / 2 + previewSize);

                canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                canvas.drawText(mSections.get(mCurrentSection), previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                        , previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);

                //only show the first letter
                /*canvas.drawText(String.valueOf(mSections.get(mCurrentSection).charAt(0)), previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                        , previewRect.top + mPreviewPadding - previewTextPaint.ascent() + 1, previewTextPaint);*/
            }

            Paint indexPaint = new Paint();
            indexPaint.setColor(Color.WHITE);
            indexPaint.setAlpha((int) (255 * mAlphaRate));
            indexPaint.setAntiAlias(true);
            indexPaint.setTextSize(12 * mScaledDensity);

            float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.size();
            float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
            for (int i = 0; i < mSections.size(); i++) {
                //only show the first letter
                float paddingLeft = (mIndexbarWidth - indexPaint.measureText(String.valueOf(mSections.get(i).charAt(0)))) / 2;
                canvas.drawText(String.valueOf(mSections.get(i).charAt(0)), mIndexbarRect.left + paddingLeft
                        , mIndexbarRect.top + mIndexbarMargin + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
            }
        }
    }


    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // If down event occurs inside index bar region, start indexing
                if (mState != STATE_HIDDEN && contains(ev.getX(), ev.getY())) {
                    setState(STATE_SHOWN);

                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.getY());
                    recyclerView.scrollToPosition(mSectionPosition.get(mCurrentSection));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY())) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        recyclerView.scrollToPosition(mSectionPosition.get(mCurrentSection));
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                }
                if (mState == STATE_SHOWN) {
                    setState(STATE_HIDING);
                }
                break;
        }
        return false;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth
                , mIndexbarMargin
                , w - mIndexbarMargin
                , h - mIndexbarMargin);
    }

    public void show() {
        if (mState == STATE_HIDDEN)
            setState(STATE_SHOWING);
        else if (mState == STATE_HIDING)
            setState(STATE_HIDING);
    }

    public void hide() {
        if (mState == STATE_SHOWN)
            setState(STATE_HIDING);
    }

    private void setState(int state) {

        if (state < STATE_HIDDEN || state > STATE_HIDING)
            return;

        mState = state;
        switch (mState) {
            case STATE_HIDDEN:
                // Cancel any fade effect
                mHandler.removeMessages(0);
                break;
            case STATE_SHOWING:
                // Start to fade in
                mAlphaRate = 0;
                fade(0);
                break;
            case STATE_SHOWN:
                // Cancel any fade effect
                mHandler.removeMessages(0);
                break;
            case STATE_HIDING:
                // Start to fade out after three seconds
                mAlphaRate = 1;
                fade(FADING_DELAY);
                break;
        }
    }

    public boolean contains(float x, float y) {
        if(mIndexbarRect == null ) return false;
        // Determine if the point is in index bar region, which includes the right margin of the bar
        try {
            return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.size() == 0)
            return 0;
        if (y < mIndexbarRect.top + mIndexbarMargin)
            return 0;
        if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
            return mSections.size() - 1;
        return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect.height() - 2 * mIndexbarMargin) / mSections.size()));
    }

    private void fade(long delay) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (mState) {
                case STATE_SHOWING:
                    // Fade in effect
                    mAlphaRate += (1 - mAlphaRate) * 0.2;
                    if (mAlphaRate > 0.9) {
                        mAlphaRate = 1;
                        setState(STATE_SHOWN);
                    }

                    recyclerView.invalidate();
                    fade(10);
                    break;
                case STATE_SHOWN:
                    // If no action, hide automatically
                    setState(STATE_HIDING);
                    break;
                case STATE_HIDING:
                    // Fade out effect
                    mAlphaRate -= mAlphaRate * 0.2;
                    if (mAlphaRate < 0.1) {
                        mAlphaRate = 0;
                        setState(STATE_HIDDEN);
                    }

                    recyclerView.invalidate();
                    fade(10);
                    break;
            }
        }

    };

    public boolean hasDataToShow() {
        return !(mSections.isEmpty() || mSectionPosition.isEmpty());
    }

    public void notifyChanges(List<String> sectionName, List<Integer> sectionPosition) {
    // Pre-calculate and pass your section header and position
        mSections = sectionName;
        mSectionPosition = sectionPosition;

    }
}
