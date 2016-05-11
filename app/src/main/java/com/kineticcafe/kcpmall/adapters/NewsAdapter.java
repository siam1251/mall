package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.model.InstagramFeed;
import com.kineticcafe.kcpmall.model.TwitterFeed;
import com.kineticcafe.kcpmall.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-05-05.
 */
public class NewsAdapter extends RecyclerView.Adapter {

    private static final int TYPE_ANNOUNCEMENT = 0;
    private static final int TYPE_SET_MY_INTEREST = 1;
    private static final int TYPE_TWITTER = 2;
    private static final int TYPE_INSTAGRAM = 3;

    private Context mContext;
    private List<String> mDataset;
    private List<TwitterFeed> mTwitterFeedList;
    private List<InstagramFeed> mInstaFeedList;

    /** circle indicator */

    public NewsAdapter(Context context, List<String> myDataset, List<TwitterFeed> twitterFeedList, List<InstagramFeed> instaFeedList) {
        mContext = context;
        mDataset = myDataset;
        mTwitterFeedList = twitterFeedList;
        mInstaFeedList = instaFeedList;
    }


    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        //for social feed specific
        public ViewPager vpTw;
        public ImageView ivSocialFeedLogo;
        public TextView tvSocialFeedUser;
        public LinearLayout llViewPagerCountDots;

        //for viewpager indicator
        public int dotsCount;
        public ImageView[] dots;


        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }

        public void onSocialFeedCreated(int socialFeedIcon, String socialFeedType) {
            ivSocialFeedLogo.setImageResource(socialFeedIcon);
            tvSocialFeedUser.setText(socialFeedType);
        }
    }

    public class AnnouncementHolder extends MainViewHolder {
        public CardView  cvAnnouncement;
        public ImageView ivAnnouncementLogo;
        public TextView  tvAnnouncementTitle;
        public TextView  tvAnnouncementDate;


        public AnnouncementHolder(View v) {
            super(v);
            cvAnnouncement      = (CardView)  v.findViewById(R.id.cvAncmt);
            ivAnnouncementLogo  = (ImageView) v.findViewById(R.id.ivAncmtLogo);
            tvAnnouncementTitle = (TextView)  v.findViewById(R.id.tvAncmtTitle);
            tvAnnouncementDate  = (TextView)  v.findViewById(R.id.tvAncmtDate);
        }
    }

    public class SetMyInterestViewHolder extends MainViewHolder {
        public SetMyInterestViewHolder (View v){
            super(v);
        }
    }

    public class TwitterFeedViewHolder extends MainViewHolder /*, ViewPager.OnPageChangeListener*/{
        public TwitterFeedViewHolder (View v, int socialFeedIcon, String socialFeedType){
            super(v);
            vpTw                 = (ViewPager) v.findViewById(R.id.vpSocialFeed);
            ivSocialFeedLogo     = (ImageView) v.findViewById(R.id.ivSocialFeedLogo);
            tvSocialFeedUser     = (TextView) v.findViewById(R.id.tvSocialFeedUser);
            llViewPagerCountDots = (LinearLayout) v.findViewById(R.id.llViewPagerCircle);

            onSocialFeedCreated(socialFeedIcon, socialFeedType);
        }
    }

    public class InstagramFeedViewHolder extends MainViewHolder /*, ViewPager.OnPageChangeListener*/{
        public InstagramFeedViewHolder (View v, int socialFeedIcon, String socialFeedType){
            super(v);
            vpTw                 = (ViewPager) v.findViewById(R.id.vpSocialFeed);
            ivSocialFeedLogo     = (ImageView) v.findViewById(R.id.ivSocialFeedLogo);
            tvSocialFeedUser     = (TextView) v.findViewById(R.id.tvSocialFeedUser);
            llViewPagerCountDots = (LinearLayout) v.findViewById(R.id.llViewPagerCircle);

            onSocialFeedCreated(socialFeedIcon, socialFeedType);
        }

    }
    public interface SocialFeedTypeDefine {
        void onSocialFeedCreated(int socialFeedIcon, String socialFeedType);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_ANNOUNCEMENT:
                return new AnnouncementHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_announcement, parent, false));
            case TYPE_SET_MY_INTEREST:
                return new SetMyInterestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_interest, parent, false));
            case TYPE_TWITTER:
                return new TwitterFeedViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_social_feed_pager, parent, false),
                        R.drawable.icn_twitter,
                        mContext.getResources().getString(R.string.app_name));
            case TYPE_INSTAGRAM:
                return new InstagramFeedViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_social_feed_pager, parent, false),
                        R.drawable.icn_instagram,
                        mContext.getResources().getString(R.string.app_name));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ANNOUNCEMENT) {

            AnnouncementHolder ancmtHolder = (AnnouncementHolder) holder;
            new GlideFactory().glideWithDefaultRatio(
                    ancmtHolder.ivAnnouncementLogo.getContext(),
                    R.drawable.test,
                    ancmtHolder.ivAnnouncementLogo);

            ancmtHolder.tvAnnouncementTitle.setText("Special Shopping Event");
            ancmtHolder.tvAnnouncementDate.setText("Monday, May 23 - Friday, May 27");
            ancmtHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "ANNOUNCEMENT CLICKED", Toast.LENGTH_SHORT).show();
                }
            });


        } else if(holder.getItemViewType() == TYPE_SET_MY_INTEREST){
            SetMyInterestViewHolder intrstHolder = (SetMyInterestViewHolder) holder;
            intrstHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "SET MY INTEREST CLICKED", Toast.LENGTH_SHORT).show();
                }
            });

        } else if(holder.getItemViewType() == TYPE_TWITTER || holder.getItemViewType() == TYPE_INSTAGRAM){
            SocialFeedViewPagerAdapter socialFeedViewPagerAdapter = new SocialFeedViewPagerAdapter();
            MainViewHolder viewHolder = null;
            if(holder.getItemViewType() == TYPE_TWITTER) {
                viewHolder = (TwitterFeedViewHolder) holder;
                List<TwitterFeed> twitterFeedList = new ArrayList<>();
                //TODO: temporarily using local feed list
                for(int i = 0; i < 5; i++){
                    twitterFeedList.add(new TwitterFeed(
                            mContext.getResources().getString(R.string.app_name),
                            i + 1 + " : " + mContext.getResources().getString(R.string.lorem),
                            "Apr 25, 2016 at 11:02 AM"));
                }

                socialFeedViewPagerAdapter.getTwitterViewPagerAdapter(mContext, twitterFeedList, new SocialFeedViewPagerAdapter.OnSocialFeedClickListener() {
                    @Override
                    public void onSocialFeedClicked() {
                        Toast.makeText(mContext, "TWITTER CLICKED", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(holder.getItemViewType() == TYPE_INSTAGRAM){
                viewHolder = (InstagramFeedViewHolder) holder;
                //TODO: temporarily using local feed list
                List<InstagramFeed> instagramFeedList = new ArrayList<>();
                for(int i = 0; i < 5; i++){
                    instagramFeedList.add(new InstagramFeed(
                            "https://api.metropolisatmetrotown.com/media/thumbs/event/MDay2016_Facebook.PNG.375x218_q85_crop-scale_upscale.png")
                    );
                }
                socialFeedViewPagerAdapter.getInstaViewPagerAdapter(mContext, instagramFeedList, new SocialFeedViewPagerAdapter.OnSocialFeedClickListener() {
                    @Override
                    public void onSocialFeedClicked() {
                        Toast.makeText(mContext, "INSTAGRAM CLICKED", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            ViewGroup.LayoutParams vpTwParam = (ViewGroup.LayoutParams) viewHolder.vpTw.getLayoutParams();
            vpTwParam.height =  (int) (Utility.getScreenWidth(mContext) / Utility.getFloat(mContext, R.dimen.ancmt_image_ratio));
            viewHolder.vpTw.setLayoutParams(vpTwParam);
            initializeSocialFeedViews(viewHolder, socialFeedViewPagerAdapter);

        }
    }


    private void initializeSocialFeedViews(final MainViewHolder holder, PagerAdapter pagerAdapter) {
        holder.vpTw.setAdapter(pagerAdapter);
        holder.vpTw.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < holder.dotsCount; i++) {
                    holder.dots[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_unselected));
                }
                holder.dots[position].setImageDrawable(mContext.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_selected));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Log.d("NewsAdapter", "setUiPageViewController");
        setUiPageViewController(pagerAdapter, holder);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 1) return TYPE_SET_MY_INTEREST;
        else if(position == 2) return TYPE_TWITTER;
        else if(position == 3) return TYPE_INSTAGRAM;
        else return TYPE_ANNOUNCEMENT;
    }


    /** circle page indicator*/
    private void setUiPageViewController(android.support.v4.view.PagerAdapter viewpagerAdapter, MainViewHolder holder) {
        holder.dotsCount = viewpagerAdapter.getCount();
        holder.dots = new ImageView[holder.dotsCount];

        holder.llViewPagerCountDots.removeAllViews(); //prevent from creating second indicator
        for (int i = 0; i < holder.dotsCount; i++) {
            holder.dots[i] = new ImageView(mContext);
            holder.dots[i].setImageDrawable(mContext.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_unselected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            int viewPagerCircleMargin = (int) mContext.getResources().getDimension(R.dimen.viewpager_circle_dot_margin);
            params.setMargins(viewPagerCircleMargin, 0, viewPagerCircleMargin, 0);
            holder.llViewPagerCountDots.addView(holder.dots[i], params);
        }

        if(holder.dots.length > 0) holder.dots[0].setImageDrawable(mContext.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_selected));
    }


}
