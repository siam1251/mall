package com.ivanhoecambridge.mall.adapters.adapterHelper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class ActiveMallRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPages;
    private int mContentType;


    public ActiveMallRecyclerViewAdapter(Context context, ArrayList<KcpContentPage> kcpContentPages, int contentType) {
        mContext = context;
        mKcpContentPages = kcpContentPages == null ? new ArrayList<KcpContentPage>() : new ArrayList<KcpContentPage>(kcpContentPages);
        mContentType = contentType;
    }

    public class KcpContentViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView ivActiveMall;
        public TextView tvActiveMallTitle;
        public TextView tvActiveMallDesc;

        public KcpContentViewHolder(View v) {
            super(v);
            mView = v;

            ivActiveMall  = (ImageView) v.findViewById(R.id.ivActiveMall);
            tvActiveMallTitle = (TextView)  v.findViewById(R.id.tvActiveMallTitle);
            tvActiveMallDesc = (TextView)  v.findViewById(R.id.tvActiveMallDesc);
            v.setTag(this);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_EVENT:
                return new KcpContentViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_active_mall, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_DEAL:
                return new KcpContentViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_active_mall, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final KcpContentPage kcpContentPage = mKcpContentPages.get(position);

        final KcpContentViewHolder kcpContentViewHolder = (KcpContentViewHolder) holder;
        if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_EVENT) {

            String imageUrl = kcpContentPage.getHighestResImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    kcpContentViewHolder.ivActiveMall,
                    R.drawable.placeholder);

            String title = kcpContentPage.getTitle();
            kcpContentViewHolder.tvActiveMallTitle.setText(title);

            String time = mContext.getString(R.string.mall_hours_format,
                    kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.getStringFromResources(mContext, R.string.date_format_effective)),
                    kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.getStringFromResources(mContext, R.string.date_format_effective))
            );
            kcpContentViewHolder.tvActiveMallDesc.setText(time);
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_DEAL) {

            String imageUrl = kcpContentPage.getHighestResImageUrl();
            if(imageUrl.equals("")) imageUrl = kcpContentPage.getHighestResFallbackImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    kcpContentViewHolder.ivActiveMall,
                    R.drawable.placeholder);

            String storename = kcpContentPage.getStoreName();
            kcpContentViewHolder.tvActiveMallTitle.setText(storename);

            String title = kcpContentPage.getTitle();
            kcpContentViewHolder.tvActiveMallDesc.setText(title);
        }

        kcpContentViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (Activity)mContext,
                        Pair.create((View)kcpContentViewHolder.ivActiveMall, transitionNameImage));

                ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                ActivityAnimation.startActivityAnimation(mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKcpContentPages == null ? 0 : mKcpContentPages.size() > 2 ? 3 : mKcpContentPages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mContentType;
    }
}
