package com.kineticcafe.kcpmall.adapters;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.fragments.HomeFragment;
import com.kineticcafe.kcpmall.instagram.model.InstagramFeed;
import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;
import com.kineticcafe.kcpmall.utility.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-05-05.
 */
public class NewsAdapter extends RecyclerView.Adapter {

    /*private static final int TYPE_LOADING = 0;
    private static final int TYPE_ANNOUNCEMENT = 1;
    private static final int TYPE_EVENT = 2;
    private static final int TYPE_SET_MY_INTEREST = 3;
    private static final int TYPE_TWITTER = 4;
    private static final int TYPE_INSTAGRAM = 5;*/

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPages;
    private SocialFeedViewPagerAdapter mSocialFeedViewPagerAdapter;
    private List<TwitterTweet> mTwitterFeedList;
    private List<InstagramFeed> mInstaFeedList;

    public NewsAdapter(Context context, ArrayList<KcpContentPage> kcpContentPages, List<TwitterTweet> twitterFeedList, List<InstagramFeed> instaFeedList) {
        mContext = context;
        mKcpContentPages = kcpContentPages;
        mTwitterFeedList = twitterFeedList;
        mInstaFeedList = instaFeedList;
    }

    public void updateData(ArrayList<KcpContentPage> kcpContentPages) {
        mKcpContentPages.clear();
        mKcpContentPages.addAll(kcpContentPages);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<KcpContentPage> kcpContentPages){
        removeLoadingImage();
        //testing

        /*
        for(int i = 0; i < 5; i++){
            mKcpContentPages.add(new KcpContentPage());
        }
        int curSize = getItemCount();
        notifyItemRangeInserted(curSize, 5 - 1);*/

        mKcpContentPages.addAll(kcpContentPages);
        int curSize = getItemCount();
        notifyItemRangeInserted(curSize, kcpContentPages.size() - 1);
    }

    public void prepareLoadingImage(){
        mKcpContentPages.add(null);
        notifyItemInserted(mKcpContentPages.size() - 1);
    }

    public void removeLoadingImage(){
        mKcpContentPages.remove(mKcpContentPages.size() - 1);
        notifyItemRemoved(mKcpContentPages.size());
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

    public class LoadingViewHolder extends MainViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbNewsAdapter);
        }
    }



    public class AnnouncementHolder extends MainViewHolder {
        public CardView  cvAnnouncement;
        public RelativeLayout rlAncmt;
        public ImageView ivAnnouncementLogo;
        public TextView  tvAnnouncementTitle;
        public TextView  tvAnnouncementDate;
        public ImageView  ivFav;

        public AnnouncementHolder(View v) {
            super(v);
            cvAnnouncement      = (CardView)  v.findViewById(R.id.cvAncmt);
            rlAncmt             = (RelativeLayout)  v.findViewById(R.id.rlAncmt);
            ivAnnouncementLogo  = (ImageView) v.findViewById(R.id.ivAncmtLogo);
            tvAnnouncementTitle = (TextView)  v.findViewById(R.id.tvAncmtTitle);
            tvAnnouncementDate  = (TextView)  v.findViewById(R.id.tvAncmtDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
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
            case Constants.TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false));
            case Constants.TYPE_ANNOUNCEMENT:
                return new AnnouncementHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_announcement, parent, false));
            case Constants.TYPE_EVENT:
                return new AnnouncementHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_announcement, parent, false));
            case Constants.TYPE_SET_MY_INTEREST:
                return new SetMyInterestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_interest, parent, false));
            case Constants.TYPE_TWITTER:
                return new TwitterFeedViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_social_feed_pager, parent, false),
                        R.drawable.icn_twitter,
                        "@" + Constants.TWITTER_SCREEN_NAME);
            case Constants.TYPE_INSTAGRAM:
                return new InstagramFeedViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_social_feed_pager, parent, false),
                        R.drawable.icn_instagram,
                        "@" + Constants.INSTAGRAM_USER_NAME);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final KcpContentPage kcpContentPage = mKcpContentPages.get(position);
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
        //testing
        /*else {
            String title = position + "";

            AnnouncementHolder ancmtHolder = (AnnouncementHolder) holder;
            new GlideFactory().glideWithDefaultRatio(
                    ancmtHolder.ivAnnouncementLogo.getContext(),
                    R.drawable.bg_splash,
                    ancmtHolder.ivAnnouncementLogo
                    );

            ancmtHolder.tvAnnouncementTitle.setText(title);
        }*/



        else if (holder.getItemViewType() == Constants.TYPE_ANNOUNCEMENT || holder.getItemViewType() == Constants.TYPE_EVENT) {
            String imageUrl = kcpContentPage.getImageUrl();
            String title = kcpContentPage.getTitle();
//            String title = position + ""; //testing

            final AnnouncementHolder ancmtHolder = (AnnouncementHolder) holder;
            new GlideFactory().glideWithDefaultRatio(
                    ancmtHolder.ivAnnouncementLogo.getContext(),
                    imageUrl,
                    ancmtHolder.ivAnnouncementLogo,
                    R.drawable.bg_splash);

            ancmtHolder.tvAnnouncementTitle.setText(title);
            if(holder.getItemViewType() == Constants.TYPE_EVENT){
                String time =
                        kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EFFECTIVE) +
                                " - " +
                                kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EFFECTIVE);
                ancmtHolder.tvAnnouncementDate.setText(time);
            } else if(holder.getItemViewType() == Constants.TYPE_ANNOUNCEMENT){
                ancmtHolder.tvAnnouncementDate.setVisibility(View.GONE);
                ViewGroup.LayoutParams rlAncmtParam = (ViewGroup.LayoutParams) ancmtHolder.rlAncmt.getLayoutParams();
                rlAncmtParam.height =  (int) mContext.getResources().getDimension(R.dimen.ancmt_desc_height_without_date);
                ancmtHolder.rlAncmt.setLayoutParams(rlAncmtParam);
            }

            ancmtHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: implement fav functionality
                    Toast.makeText(mContext, "fav clicked", Toast.LENGTH_SHORT).show();
                    ancmtHolder.ivFav.setSelected(!ancmtHolder.ivFav .isSelected());
                }
            });

            ancmtHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "ANNOUNCEMENT CLICKED", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);
                    String transitionName = mContext.getResources().getString(R.string.transition_news_image);


                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    (Activity) mContext,
                                    ancmtHolder.ivAnnouncementLogo,   // The view which starts the transition
                                    transitionName    // The transitionName of the view we’re transitioning to
                            );
                    ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
//                    ((Activity)mContext).startActivity(intent, options.toBundle());
                    ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
            });

        } else if(holder.getItemViewType() == Constants.TYPE_SET_MY_INTEREST){
            SetMyInterestViewHolder intrstHolder = (SetMyInterestViewHolder) holder;
            intrstHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "SET MY INTEREST CLICKED", Toast.LENGTH_SHORT).show();
                }
            });

        } else if(holder.getItemViewType() == Constants.TYPE_TWITTER || holder.getItemViewType() == Constants.TYPE_INSTAGRAM){
            mSocialFeedViewPagerAdapter = new SocialFeedViewPagerAdapter();
            MainViewHolder viewHolder = null;
            if(holder.getItemViewType() == Constants.TYPE_TWITTER) {
                viewHolder = (TwitterFeedViewHolder) holder;
                mSocialFeedViewPagerAdapter.getTwitterViewPagerAdapter(mContext, HomeFragment.sTwitterTweets, new SocialFeedViewPagerAdapter.OnSocialFeedClickListener() {
                    @Override
                    public void onSocialFeedClicked() {
                        Toast.makeText(mContext, "TWITTER CLICKED", Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(holder.getItemViewType() == Constants.TYPE_INSTAGRAM){
                viewHolder = (InstagramFeedViewHolder) holder;
                mSocialFeedViewPagerAdapter.getInstaViewPagerAdapter(mContext, HomeFragment.sInstagramFeeds, new SocialFeedViewPagerAdapter.OnSocialFeedClickListener() {
                    @Override
                    public void onSocialFeedClicked() {
                        Toast.makeText(mContext, "INSTAGRAM CLICKED", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            ViewGroup.LayoutParams vpTwParam = (ViewGroup.LayoutParams) viewHolder.vpTw.getLayoutParams();
            vpTwParam.height =  (int) (Utility.getScreenWidth(mContext) / Utility.getFloat(mContext, R.dimen.ancmt_image_ratio));
            viewHolder.vpTw.setLayoutParams(vpTwParam);
            initializeSocialFeedViews(viewHolder, mSocialFeedViewPagerAdapter);

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
//        int twitterCount = mTwitterFeedList.size() == 0 ? 0 : 1;
//        int instaCount = mInstaFeedList.size() == 0 ? 0 : 1;
//        return mKcpContentPages.size() + twitterCount + instaCount;
//        return mKcpContentPages.size();
        return mKcpContentPages == null ? 0 : mKcpContentPages.size();
    }

    @Override
    public int getItemViewType(int position) {
        KcpContentPage kcpContentPage = mKcpContentPages.get(position);
        if(kcpContentPage == null){
            return Constants.TYPE_LOADING;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_ANNOUNCEMENT)){
            return Constants.TYPE_ANNOUNCEMENT;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_EVENT)){
            return Constants.TYPE_EVENT;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_INTEREST)){
            return Constants.TYPE_SET_MY_INTEREST;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_TWITTER)){
            return Constants.TYPE_TWITTER;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_INSTAGRAM)){
            return Constants.TYPE_INSTAGRAM;
        } else {
            return Constants.TYPE_ANNOUNCEMENT;
        }
    }

    public SocialFeedViewPagerAdapter getSocialFeedViewPagerAdapter(){
        return mSocialFeedViewPagerAdapter;
    }

    /** circle page indicator*/
    private void setUiPageViewController(PagerAdapter viewpagerAdapter, MainViewHolder holder) {
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
