package com.kineticcafe.kcpmall.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.instagram.model.InstagramFeed;
import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kay on 2016-05-06.
 */
public class SocialFeedViewPagerAdapter extends PagerAdapter {

    private enum SocialFeedType { TWITTER, INSTA };
    private Context mContext;
    private List<TwitterTweet> mTwitterFeedList = new ArrayList<>();
    private List<InstagramFeed> mInstaFeedList = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private SocialFeedType mSocialFeedType;
    private int mListSize;
    private OnSocialFeedClickListener mSocialFeedClickListener;
    public interface OnSocialFeedClickListener {
        void onSocialFeedClicked();
    }

    public SocialFeedViewPagerAdapter(){}

    public SocialFeedViewPagerAdapter getTwitterViewPagerAdapter(Context context, List<TwitterTweet> twitterFeedList, OnSocialFeedClickListener onSocialFeedClickListener) {
        init(context);
        mTwitterFeedList.addAll(twitterFeedList);
        mSocialFeedType = SocialFeedType.TWITTER;
        mListSize = mTwitterFeedList.size();
        mSocialFeedClickListener = onSocialFeedClickListener;

        return this;
    }

    public SocialFeedViewPagerAdapter getInstaViewPagerAdapter(Context context, List<InstagramFeed> instaFeedList, OnSocialFeedClickListener onSocialFeedClickListener) {
        init(context);
        mInstaFeedList.addAll(instaFeedList);
        mSocialFeedType = SocialFeedType.INSTA;
        mListSize = mInstaFeedList.size();
        mSocialFeedClickListener = onSocialFeedClickListener;


        return this;
    }

    private void init(Context context){
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //TODO: see if you need to update social adapter through news adapter or if notifyDatasetchanged needs to be called from news adapter afterwards
    public void updateTwitterData(ArrayList<TwitterTweet> twitterTweets) {
        mTwitterFeedList.clear();
        mTwitterFeedList.addAll(twitterTweets);
        notifyDataSetChanged();
    }

    public void updateInstaData(ArrayList<InstagramFeed> instagramFeeds) {
        mInstaFeedList.clear();
        mInstaFeedList.addAll(instagramFeeds);
        notifyDataSetChanged();
    }

    public Object instantiateItem(ViewGroup collection, int position) {
        if(mSocialFeedType.equals(SocialFeedType.TWITTER)){
            View itemView = mLayoutInflater.inflate(R.layout.list_item_tw, collection, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSocialFeedClickListener != null) mSocialFeedClickListener.onSocialFeedClicked();
                }
            });

            TextView tvTwDesc = (TextView) itemView.findViewById(R.id.tvTwDesc);
            TextView tvTwDate = (TextView) itemView.findViewById(R.id.tvTwDate);

            tvTwDesc.setText(mTwitterFeedList.get(position).getText());
            tvTwDate.setText(mTwitterFeedList.get(position).getCreatedAt());

            collection.addView(itemView);
            return itemView;
        } else if(mSocialFeedType.equals(SocialFeedType.INSTA)){
            View itemView = mLayoutInflater.inflate(R.layout.list_item_insta, collection, false);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSocialFeedClickListener != null) mSocialFeedClickListener.onSocialFeedClicked();
                }
            });

            ImageView ivInsta = (ImageView) itemView.findViewById(R.id.ivInsta);
            /*new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    mInstaFeedList.get(position).instaPicUrl,
                    ivInsta,
                    R.drawable.test
                    );*/
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    mInstaFeedList.get(position).instaPicUrl,
                    ivInsta,
                    R.drawable.view_shadow

            );

            collection.addView(itemView);
            return itemView;
        }
        return null;

    }

    @Override
    public int getCount() {
        return mListSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
