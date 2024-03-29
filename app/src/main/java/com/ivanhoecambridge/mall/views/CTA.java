package com.ivanhoecambridge.mall.views;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;

/**
 * Created by Kay on 2016-06-28.
 */
public class CTA {

    private View mView;
    private ProgressBar pb;
    private View.OnClickListener mOnClickListener;
    public String title;
    private TextView tvDetailBtnTitle;
    private ImageView ivDetailBtnImage;

    /**
     *
     * @param activity
     * @param parentView
     * @param layout
     * @param drawable
     * @param title
     * @param onClickListener
     * @param hideIfEmpty for the ones that are available to download - set false to show progressbar
     */
    public CTA(Activity activity, ViewGroup parentView, int layout, int drawable, String title, View.OnClickListener onClickListener, boolean hideIfEmpty) {
        mView = activity.getLayoutInflater().inflate(
                layout,
                parentView,
                false);
        this.title = title;
        pb = (ProgressBar) mView.findViewById(R.id.pb);
        tvDetailBtnTitle= (TextView) mView.findViewById(R.id.tvDetailBtnTitle);
        ivDetailBtnImage= (ImageView) mView.findViewById(R.id.ivDetailBtnImage);

        if(hideIfEmpty && (title == null || title.equals(""))){
            mView.setVisibility(View.GONE);
        } else {
            if(title == null || title.equals("")) {
                tvDetailBtnTitle.setVisibility(View.GONE);
                ivDetailBtnImage.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
            } else {
                tvDetailBtnTitle.setVisibility(View.VISIBLE);
                ivDetailBtnImage.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        }

        ivDetailBtnImage.setImageResource(drawable);
        tvDetailBtnTitle.setText(title);
        mOnClickListener = onClickListener;
        mView.setOnClickListener(mOnClickListener);
    }

    public void setTitle(String title){
        if(!title.equals("")){
            tvDetailBtnTitle.setText(title);
            tvDetailBtnTitle.setVisibility(View.VISIBLE);
            ivDetailBtnImage.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
        }
    }
    public View getView(){
        return mView;
    }
}
