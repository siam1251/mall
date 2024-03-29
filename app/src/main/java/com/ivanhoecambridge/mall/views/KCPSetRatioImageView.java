package com.ivanhoecambridge.mall.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.ZoomableImage;

/**
 * Created by Kay on 2016-05-05.
 */
public class KCPSetRatioImageView extends ImageView implements ImageView.OnClickListener{
    private float mImageRatio;
    private boolean mZoomable;
    private Context mContext;
    public KCPSetRatioImageView(Context context) {
        super(context);
    }

    public KCPSetRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        if(!this.isInEditMode()) {
            if (attrs != null) {
                float imageRatio = KcpUtility.getFloat(context, R.dimen.ancmt_image_ratio);
                TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.KCPSetRatioImageView);
                mImageRatio = a.getFloat(R.styleable.KCPSetRatioImageView_imageRatio, imageRatio);
                mZoomable = a.getBoolean(R.styleable.KCPSetRatioImageView_zoomable, false);
                if(mZoomable) setOnClickListener(this);
                a.recycle();
            }
        }
    }

    public KCPSetRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageRatio(float ratio){
        mImageRatio = ratio;
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                setMeasuredDimension(0, 0);
            } else {
                float imageSideRatio = mImageRatio; //Width = 344, Height = 215
                float viewSideRatio = (float) MeasureSpec.getSize(widthMeasureSpec) / (float) MeasureSpec.getSize(heightMeasureSpec); //1.8

                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) (width / imageSideRatio);
                setMeasuredDimension(width, height);
            }
        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            GlideBitmapDrawable drawable = (GlideBitmapDrawable) getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            if(bitmap != null){
                Intent intent = new Intent(mContext, ZoomableImage.class);
                intent.putExtra(Constants.ARG_IMAGE_BITMAP, bitmap);
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
