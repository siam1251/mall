package com.ivanhoecambridge.mall.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.instagram.model.InstagramFeed;
import com.ivanhoecambridge.kcpandroidsdk.twitter.model.TwitterTweet;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.factory.GlideFactory;

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
    private int mActualTitleListSize;
    private OnSocialFeedClickListener mSocialFeedClickListener;
    public interface OnSocialFeedClickListener {
        void onSocialFeedClicked();
    }

    public SocialFeedViewPagerAdapter(){}

    public SocialFeedViewPagerAdapter getTwitterViewPagerAdapter(Context context, List<TwitterTweet> twitterFeedList, OnSocialFeedClickListener onSocialFeedClickListener) {
        init(context);
        if(twitterFeedList.size() > 4) mTwitterFeedList.addAll(twitterFeedList.subList(0, 5));
        else mTwitterFeedList.addAll(twitterFeedList);
        mSocialFeedType = SocialFeedType.TWITTER;
        mActualTitleListSize = mTwitterFeedList.size();
        mSocialFeedClickListener = onSocialFeedClickListener;

        return this;
    }

    public SocialFeedViewPagerAdapter getInstaViewPagerAdapter(Context context, List<InstagramFeed> instaFeedList, OnSocialFeedClickListener onSocialFeedClickListener) {
        init(context);
        if(instaFeedList.size() > 4) mInstaFeedList.addAll(instaFeedList.subList(0, 5));
        else mInstaFeedList.addAll(instaFeedList);
        mSocialFeedType = SocialFeedType.INSTA;
        mActualTitleListSize = mInstaFeedList.size();
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
        if(twitterTweets.size() > 4) mTwitterFeedList.addAll(twitterTweets.subList(0, 5));
        else mTwitterFeedList.addAll(twitterTweets);
        mActualTitleListSize = mTwitterFeedList.size();
        notifyDataSetChanged();
    }

    public void updateInstaData(ArrayList<InstagramFeed> instagramFeeds) {
        mInstaFeedList.clear();
        if(instagramFeeds.size() > 4) mInstaFeedList.addAll(instagramFeeds.subList(0, 5));
        else mInstaFeedList.addAll(instagramFeeds);
        mActualTitleListSize = mInstaFeedList.size();
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        int virtualPosition = position % mActualTitleListSize;
        position = virtualPosition;

        if(mSocialFeedType.equals(SocialFeedType.TWITTER)){
            View itemView = mLayoutInflater.inflate(R.layout.list_item_tw, collection, false);
            if(mTwitterFeedList.size() == 0) return itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSocialFeedClickListener != null) mSocialFeedClickListener.onSocialFeedClicked();
                }
            });

            TextView tvTwDesc = (TextView) itemView.findViewById(R.id.tvTwDesc);
            TextView tvTwDate = (TextView) itemView.findViewById(R.id.tvTwDate);

//            tvTwDesc.setText(mTwitterFeedList.get(position).getText());
            tvTwDesc.setText(Html.fromHtml(mTwitterFeedList.get(position).getText()));
            tvTwDate.setText(mTwitterFeedList.get(position).getCreatedAt());

            collection.addView(itemView);
            return itemView;
        } else if(mSocialFeedType.equals(SocialFeedType.INSTA)){
            View itemView = mLayoutInflater.inflate(R.layout.list_item_insta, collection, false);
            if(mInstaFeedList.size() == 0) return itemView;
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
                    ivInsta,
                    R.drawable.placeholder
            );

            collection.addView(itemView);
            return itemView;
        }
        return null;

    }

    @Override
    public int getCount() {
        return mActualTitleListSize == 0 ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

//        int virtualPosition = position % mActualTitleListSize;
//        super.destroyItem(container, virtualPosition, object);

    }


}
