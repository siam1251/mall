package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kineticcafe.kcpandroidsdk.instagram.model.InstagramFeed;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.Twitter;
import com.kineticcafe.kcpandroidsdk.twitter.model.TwitterTweet;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.views.ExpandableTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-05-05.
 */
public class SocialFeedDetailRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<InstagramFeed> mInstaFeedList = new ArrayList<>();
    private List<TwitterTweet> mTwitterFeedList = new ArrayList<>();

    public SocialFeedDetailRecyclerViewAdapter(Context context, ArrayList<InstagramFeed> instagramFeeds, ArrayList<TwitterTweet> twitterTweets) {
        mContext = context;
        mInstaFeedList = instagramFeeds;
        mTwitterFeedList = twitterTweets;
    }

    public class InstagraFeedHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView  ivInstaDetail;
        public TextView  tvInstaDetailDate;
        public ExpandableTextView tvInstaDetailDesc;

        public InstagraFeedHolder(View v) {
            super(v);
            mView = v;
            ivInstaDetail = (ImageView)  v.findViewById(R.id.ivInstaDetail);
            tvInstaDetailDate = (TextView)  v.findViewById(R.id.tvInstaDetailDate);
            tvInstaDetailDesc = (ExpandableTextView)  v.findViewById(R.id.tvInstaDetailDesc);
        }
    }

    public class TwitterFeedHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView  ivInstaDetail;
        public TextView  tvInstaDetailDate;
        public TextView  tvInstaDetailDesc;

        public TwitterFeedHolder(View v) {
            super(v);
            mView = v;
            ivInstaDetail = (ImageView)  v.findViewById(R.id.ivInstaDetail);
            tvInstaDetailDate = (TextView)  v.findViewById(R.id.tvInstaDetailDate);
            tvInstaDetailDesc = (TextView)  v.findViewById(R.id.tvInstaDetailDesc);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM:
                return new InstagraFeedHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_insta_detail, parent, false));
            /*case KcpContentTypeFactory.ITEM_TYPE_TWITTER:
                return new InstagraFeedHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_tw_detail, parent, false));*/
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM) {
            final InstagraFeedHolder placeHolder = (InstagraFeedHolder) holder;

            Glide.with(mContext)
                    .load(mInstaFeedList.get(position).instaPicUrl)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(placeHolder.ivInstaDetail);

            placeHolder.tvInstaDetailDate.setText(DateUtils.getRelativeTimeSpanString(mInstaFeedList.get(position).createdTime * 1000));
            placeHolder.tvInstaDetailDesc.setText(mInstaFeedList.get(position).text);

        } else if (holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_TWITTER){
            final TwitterFeedHolder placeHolder = (TwitterFeedHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        if(mTwitterFeedList == null) return mInstaFeedList.size();
        else return mTwitterFeedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mTwitterFeedList == null) return KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM;
        else return KcpContentTypeFactory.ITEM_TYPE_TWITTER;
    }
}
