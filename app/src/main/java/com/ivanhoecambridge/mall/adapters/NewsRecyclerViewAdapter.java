package com.ivanhoecambridge.mall.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MoviesActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.activities.InterestedCategoryActivity;
import com.ivanhoecambridge.mall.activities.SocialDetailActivity;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.HomeFragment;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.RecyclerViewFooter;

import java.util.ArrayList;

import constants.MallConstants;

/**
 * Created by Kay on 2016-05-05.
 */
public class NewsRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPagesNews;
    private SocialFeedViewPagerAdapter mSocialFeedViewPagerAdapter;
    private FavouriteInterface mFavouriteInterface;
    private boolean showInstagramFeed = false;

    public NewsRecyclerViewAdapter(Context context, ArrayList<KcpContentPage> news) {
        mContext = context;
        mKcpContentPagesNews = news == null ? new ArrayList<KcpContentPage>() : news;
        removeInstagram();
        removeInterestIfNeeded();
    }

    public void updateData(ArrayList<KcpContentPage> kcpContentPages) {
        mKcpContentPagesNews.clear();
        mKcpContentPagesNews.addAll(kcpContentPages);
        removeInterestIfNeeded();
        removeInstagram();
        notifyDataSetChanged();
    }

    public void setFavouriteListener(FavouriteInterface favouriteInterface){
        mFavouriteInterface = favouriteInterface;
    }

    public void addData(ArrayList<KcpContentPage> kcpContentPages){
        removeLoadingImage();
        mKcpContentPagesNews.addAll(kcpContentPages);
        removeInterestIfNeeded();
        int curSize = getItemCount();
        notifyItemRangeInserted(curSize, kcpContentPages.size() - 1);
    }

    private void removeInterestIfNeeded(){
        //if you have set interest previously, do not display interest card
        if(FavouriteManager.getInstance(mContext).getInterestFavSize() > 0){
            for(int i = 0; i < mKcpContentPagesNews.size(); i++){
                KcpContentPage kcpContentPage = mKcpContentPagesNews.get(i);
                if(KcpContentTypeFactory.getContentType(kcpContentPage) == KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST) {
                    mKcpContentPagesNews.remove(i);
                    break;
                }
            }
        }
    }

    private void removeInstagram(){
        if(!showInstagramFeed){
            for(KcpContentPage kcpContentPage : mKcpContentPagesNews){
                int contentType = KcpContentTypeFactory.getContentType(kcpContentPage);
                if(contentType == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM){
                    mKcpContentPagesNews.remove(kcpContentPage);
                    return;
                }
            }
        }
    }


    private int mFooterLayout;
    private boolean mFooterExist = false;
    private String mFooterText;
    private View.OnClickListener mOnClickListener;

    public void addFooter(String footerText, int footerLayout, View.OnClickListener onClickListener){
        mFooterExist = true;
        mFooterText = footerText;
        mFooterLayout = footerLayout;
        mOnClickListener = onClickListener;
        KcpContentPage fakeKcpContentPageForFooter = new KcpContentPage();
        fakeKcpContentPageForFooter.setContentType("footer");
        mKcpContentPagesNews.add(fakeKcpContentPageForFooter);
        notifyDataSetChanged();
    }



    public void prepareLoadingImage(){
        mKcpContentPagesNews.add(null);
        notifyItemInserted(mKcpContentPagesNews.size() - 1);
    }

    public void removeLoadingImage(){
        mKcpContentPagesNews.remove(mKcpContentPagesNews.size() - 1);
        notifyItemRemoved(mKcpContentPagesNews.size());
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

    public class AnnouncementViewHolder extends MainViewHolder {
        public RelativeLayout rlAncmt;
        public ImageView ivAnnouncementLogo;
        public TextView  tvAnnouncementTitle;
        public TextView  tvAnnouncementDate;
        public ImageView  ivFav;
        public ImageView  ivSymbol;

        public AnnouncementViewHolder(View v) {
            super(v);
            rlAncmt             = (RelativeLayout)  v.findViewById(R.id.rlAncmt);
            ivAnnouncementLogo  = (ImageView) v.findViewById(R.id.ivAncmtLogo);
            tvAnnouncementTitle = (TextView)  v.findViewById(R.id.tvAncmtTitle);
            tvAnnouncementDate  = (TextView)  v.findViewById(R.id.tvAncmtDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
            ivSymbol         = (ImageView)  v.findViewById(R.id.ivSymbol);
        }
    }

    public class SetMyInterestViewHolder extends MainViewHolder {
        public SetMyInterestViewHolder (View v){
            super(v);
        }
    }

    public class TwitterFeedViewHolder extends MainViewHolder {
        public TwitterFeedViewHolder (View v, int socialFeedIcon, String socialFeedType) {
            super(v);
            vpTw                 = (ViewPager) v.findViewById(R.id.vpSocialFeed);
            ivSocialFeedLogo     = (ImageView) v.findViewById(R.id.ivSocialFeedLogo);
            tvSocialFeedUser     = (TextView) v.findViewById(R.id.tvSocialFeedUser);
            llViewPagerCountDots = (LinearLayout) v.findViewById(R.id.llViewPagerCircle);

            onSocialFeedCreated(socialFeedIcon, socialFeedType);
        }
    }

    public class InstagramFeedViewHolder extends MainViewHolder {
        public InstagramFeedViewHolder (View v, int socialFeedIcon, String socialFeedType){
            super(v);

            vpTw                 = (ViewPager) v.findViewById(R.id.vpSocialFeed);
            ivSocialFeedLogo     = (ImageView) v.findViewById(R.id.ivSocialFeedLogo);
            tvSocialFeedUser     = (TextView) v.findViewById(R.id.tvSocialFeedUser);
            llViewPagerCountDots = (LinearLayout) v.findViewById(R.id.llViewPagerCircle);

            onSocialFeedCreated(socialFeedIcon, socialFeedType);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_loading_item, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT:
                return new AnnouncementViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_announcement, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_EVENT:
                return new AnnouncementViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_announcement, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_MOVIE:
                return new AnnouncementViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_announcement, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST:
                return new SetMyInterestViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_interest, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_TWITTER:
                return new TwitterFeedViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_social_feed_pager, parent, false),
                        R.drawable.icn_twitter,
                        "@" + MallConstants.TWITTER_SCREEN_NAME);
            case KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM:
                return new InstagramFeedViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_social_feed_pager, parent, false),
                        R.drawable.icn_instagram,
                        "@" + MallConstants.INSTAGRAM_USER_NAME);
            case KcpContentTypeFactory.ITEM_TYPE_FOOTER:
                return new RecyclerViewFooter.FooterViewHolder(
                        LayoutInflater.from(mContext).inflate(mFooterLayout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if (holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT || holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_EVENT) {
            final AnnouncementViewHolder ancmtHolder = (AnnouncementViewHolder) holder;
            String imageUrl = kcpContentPage.getHighestResImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    ancmtHolder.ivAnnouncementLogo,
                    R.drawable.placeholder);

            String title = kcpContentPage.getTitle();
            ancmtHolder.tvAnnouncementTitle.setText(title);
            if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_EVENT){
                //if event is for one day, also show its begin/end hours
                String time = "";
                String startingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EFFECTIVE);
                String endingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EFFECTIVE);

                if(!startingTime.equals(endingTime)) {
                    time = startingTime + " - " + endingTime;
                } else {
                    String eventStartHour = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EVENT_HOUR);
                    String eventEndingHour = kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EVENT_HOUR);
                    time = startingTime + " @ " + eventStartHour + " to " + eventEndingHour;
                }

                ancmtHolder.tvAnnouncementDate.setText(time);
            } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
                ancmtHolder.tvAnnouncementDate.setVisibility(View.GONE);
                ViewGroup.LayoutParams rlAncmtParam = (ViewGroup.LayoutParams) ancmtHolder.rlAncmt.getLayoutParams();
                rlAncmtParam.height =  (int) mContext.getResources().getDimension(R.dimen.ancmt_desc_height_without_date);
                ancmtHolder.rlAncmt.setLayoutParams(rlAncmtParam);
                ancmtHolder.ivFav.setVisibility(View.GONE); //Announcement should not be favourited
            }

            final String likeLink = kcpContentPage.getLikeLink();
            ancmtHolder.ivFav.setSelected(FavouriteManager.getInstance(mContext).isLiked(likeLink, kcpContentPage));
            ancmtHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.startSqueezeAnimationForFav(new Utility.SqueezeListener() {
                        @Override
                        public void OnSqueezeAnimationDone() {
                            ancmtHolder.ivFav.setSelected(!ancmtHolder.ivFav.isSelected());
                            FavouriteManager.getInstance(mContext).addOrRemoveFavContent(likeLink, kcpContentPage, mFavouriteInterface);
                        }
                    }, (Activity) mContext, ancmtHolder.ivFav);
                }
            });

            ancmtHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)ancmtHolder.ivAnnouncementLogo, transitionNameImage));

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                    ActivityAnimation.startActivityAnimation(mContext);
                }
            });

        } else if (holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_MOVIE) {

            final AnnouncementViewHolder ancmtHolder = (AnnouncementViewHolder) holder;
            Glide.with(mContext)
                    .load(R.drawable.img_movies)
                    .crossFade()
                    .error(R.drawable.placeholder)
                    .override(KcpUtility.getScreenWidth(mContext), (int) (KcpUtility.getScreenWidth(mContext) / KcpUtility.getFloat(mContext, R.dimen.ancmt_image_ratio)))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .placeholder(R.drawable.placeholder)
                    .into(ancmtHolder.ivAnnouncementLogo);


            ancmtHolder.ivSymbol.setVisibility(View.VISIBLE);
            ancmtHolder.tvAnnouncementTitle.setText(mContext.getString(R.string.movie_title));
            ancmtHolder.tvAnnouncementDate.setText(mContext.getString(R.string.movie_desc));
            ancmtHolder.ivFav.setVisibility(View.GONE);
            ancmtHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MoviesActivity.class);
                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)ancmtHolder.ivAnnouncementLogo, transitionNameImage));

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                    ActivityAnimation.startActivityAnimation(mContext);
                }
            });


        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SET_MY_INTEREST){
            SetMyInterestViewHolder intrstHolder = (SetMyInterestViewHolder) holder;
            intrstHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)mContext).startActivityForResult(new Intent(mContext, InterestedCategoryActivity.class), Constants.REQUEST_CODE_CHANGE_INTEREST);
                    ActivityAnimation.startActivityAnimation(mContext);

                }
            });

        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_TWITTER || holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM){
            mSocialFeedViewPagerAdapter = new SocialFeedViewPagerAdapter();
            MainViewHolder viewHolder = null;
            if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_TWITTER) {
                viewHolder = (TwitterFeedViewHolder) holder;
                mSocialFeedViewPagerAdapter.getTwitterViewPagerAdapter(mContext, HomeFragment.sTwitterFeedList, new SocialFeedViewPagerAdapter.OnSocialFeedClickListener() {
                    @Override
                    public void onSocialFeedClicked() {
                        Intent intent = new Intent(mContext, SocialDetailActivity.class);
                        intent.putExtra(Constants.ARG_ACTIVITY_TYPE, KcpContentTypeFactory.ITEM_TYPE_TWITTER);
                        mContext.startActivity(intent);
                        ActivityAnimation.startActivityAnimation(mContext);
                    }
                });
            } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM){
                viewHolder = (InstagramFeedViewHolder) holder;
                mSocialFeedViewPagerAdapter.getInstaViewPagerAdapter(mContext, HomeFragment.sInstaFeedList, new SocialFeedViewPagerAdapter.OnSocialFeedClickListener() {
                    @Override
                    public void onSocialFeedClicked() {
                        Intent intent = new Intent(mContext, SocialDetailActivity.class);
                        intent.putExtra(Constants.ARG_ACTIVITY_TYPE, KcpContentTypeFactory.ITEM_TYPE_INSTAGRAM);
                        mContext.startActivity(intent);
                        ActivityAnimation.startActivityAnimation(mContext);
                    }
                });
            }

            ViewGroup.LayoutParams vpTwParam = (ViewGroup.LayoutParams) viewHolder.vpTw.getLayoutParams();
            vpTwParam.height =  (int) (KcpUtility.getScreenWidth(mContext) / KcpUtility.getFloat(mContext, R.dimen.ancmt_image_ratio));
            viewHolder.vpTw.setLayoutParams(vpTwParam);
            initializeSocialFeedViews(viewHolder, mSocialFeedViewPagerAdapter);
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_FOOTER){
            RecyclerViewFooter.FooterViewHolder footerViewHolder = (RecyclerViewFooter.FooterViewHolder) holder;
            footerViewHolder.mView.setOnClickListener(mOnClickListener);
            footerViewHolder.tvFooter.setText(mFooterText);
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
                holder.dots[position % holder.dots.length].setImageDrawable(mContext.getResources().getDrawable(R.drawable.viewpager_circle_page_incdicator_dot_selected));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        Log.d("NewsRecyclerViewAdapter", "setUiPageViewController");
        setUiPageViewController(holder);
    }

    @Override
    public int getItemCount() {
        return mKcpContentPagesNews == null ? 0 : mKcpContentPagesNews.size();
    }

    @Override
    public int getItemViewType(int position) {
        KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        return KcpContentTypeFactory.getContentType(kcpContentPage);
    }

    public SocialFeedViewPagerAdapter getSocialFeedViewPagerAdapter(){
        return mSocialFeedViewPagerAdapter;
    }

    /** circle page indicator*/
    private void setUiPageViewController(MainViewHolder holder) {
        holder.dotsCount = 5; //viewpagerAdapter.getCount(); used for actual counting
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
