package com.kineticcafe.kcpmall.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.model.InstagramFeed;
import com.kineticcafe.kcpmall.model.TwitterFeed;

import java.util.List;


/**
 * Created by Kay on 2016-05-06.
 */
public class SocialFeedViewPagerAdapter extends PagerAdapter {

    private enum SocialFeedType { TWITTER, INSTA };

    /*private static Context mContext;
    private static List<TwitterFeed> mTwitterFeedList;
    private static List<InstagramFeed> mInstaFeedList;
    private static LayoutInflater mLayoutInflater;
    private static SocialFeedType mSocialFeedType;*/

    private Context mContext;
    private List<TwitterFeed> mTwitterFeedList;
    private List<InstagramFeed> mInstaFeedList;
    private LayoutInflater mLayoutInflater;
    private SocialFeedType mSocialFeedType;
    private int mListSize;
    private OnSocialFeedClickListener mSocialFeedClickListener;
    public interface OnSocialFeedClickListener {
        void onSocialFeedClicked();
    }

    public SocialFeedViewPagerAdapter(){}

    public SocialFeedViewPagerAdapter getTwitterViewPagerAdapter(Context context, List<TwitterFeed> twitterFeedList, OnSocialFeedClickListener onSocialFeedClickListener) {
        init(context);
//        SocialFeedViewPagerAdapter socialFeedViewPagerAdapter = new SocialFeedViewPagerAdapter();
        mTwitterFeedList = twitterFeedList;
        mSocialFeedType = SocialFeedType.TWITTER;
        mListSize = mTwitterFeedList.size();
        mSocialFeedClickListener = onSocialFeedClickListener;

        return this;
    }

    public SocialFeedViewPagerAdapter getInstaViewPagerAdapter(Context context, List<InstagramFeed> instaFeedList, OnSocialFeedClickListener onSocialFeedClickListener) {
        init(context);
//        SocialFeedViewPagerAdapter instaViewPagerAdapter = new SocialFeedViewPagerAdapter();
        mInstaFeedList = instaFeedList;
        mSocialFeedType = SocialFeedType.INSTA;
        mListSize = mInstaFeedList.size();
        mSocialFeedClickListener = onSocialFeedClickListener;


        return this;
    }

    private void init(Context context){
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*public SocialFeedViewPagerAdapter(Context context, List<Object> twitterFeedList){
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(twitterFeedList.get(0) instanceof  TwitterFeed){
            mTwitterFeedList = twitterFeedList;

        } else if(){

        }
    }*/

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

            tvTwDesc.setText(mTwitterFeedList.get(position).twDesc);
            tvTwDate.setText(mTwitterFeedList.get(position).twDate);

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
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    mInstaFeedList.get(position).instaPicUrl,
                    ivInsta
                    );

            collection.addView(itemView);
            return itemView;
        }
        return null;

    }

    @Override
    public int getCount() {
//        return mTwitterFeedList.size();
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
