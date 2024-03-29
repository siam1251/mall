package com.ivanhoecambridge.mall.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.bluedot.CoordinateListener;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.HomeFragment;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.MovieRecyclerItemDecoration;
import com.ivanhoecambridge.mall.views.RecyclerViewFooter;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPagesNews;
    private FavouriteInterface mFavouriteInterface;
    private boolean mIsCardFullWidth;
    private boolean mFooterExist;
    private int mFooterLayout;
    private String mFooterText;
    private String mPageTitle;
    private View.OnClickListener mFooterOnClickListener;

    public EventRecyclerViewAdapter(Context context, ArrayList<KcpContentPage> events, boolean isCardFullWidth, FavouriteInterface favouriteInterface) {
        mContext = context;
        mKcpContentPagesNews = events == null ? new ArrayList<KcpContentPage>() : events;
        mIsCardFullWidth = isCardFullWidth;
        mFavouriteInterface = favouriteInterface;
    }

    public void setFavouriteListener(FavouriteInterface favouriteInterface) {
        mFavouriteInterface = favouriteInterface;
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public class EventViewHolder extends MainViewHolder {
        public RelativeLayout rlAncmt;
        public ImageView ivAnnouncementLogo;
        public TextView  tvAnnouncementTitle;
        public TextView  tvAnnouncementDate;
        public ImageView  ivFav;
        public ImageView  ivSymbol;
        public CoordinatorLayout  clEvent;

        public EventViewHolder(View v) {
            super(v);
            rlAncmt             = (RelativeLayout)  v.findViewById(R.id.rlAncmt);
            ivAnnouncementLogo  = (ImageView) v.findViewById(R.id.ivAncmtLogo);
            tvAnnouncementTitle = (TextView)  v.findViewById(R.id.tvAncmtTitle);
            tvAnnouncementDate  = (TextView)  v.findViewById(R.id.tvAncmtDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
            ivSymbol         = (ImageView)  v.findViewById(R.id.ivSymbol);
            clEvent         = (CoordinatorLayout)  v.findViewById(R.id.clEvent);


            if(mIsCardFullWidth) {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) clEvent.getLayoutParams();
                lp.width = RecyclerView.LayoutParams.MATCH_PARENT;
                clEvent.setLayoutParams(lp);

            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_EVENT:
                return new EventViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_event, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_FOOTER:
                return new RecyclerViewFooter.FooterViewHolder(LayoutInflater.from(mContext).inflate(mFooterLayout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        if (holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_FOOTER) {
            RecyclerViewFooter.FooterViewHolder footerViewHolder = (RecyclerViewFooter.FooterViewHolder) holder;
            footerViewHolder.mView.setOnClickListener(mFooterOnClickListener);
            footerViewHolder.tvFooter.setText(mFooterText);
        } else if(getItemViewType(position) == KcpContentTypeFactory.ITEM_TYPE_EVENT){
            final EventViewHolder eventHolder = (EventViewHolder) holder;
            String imageUrl = kcpContentPage.getHighestResImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    eventHolder.ivAnnouncementLogo,
                    R.drawable.placeholder);

            final String title = kcpContentPage.getTitle();
            eventHolder.tvAnnouncementTitle.setText(title);

            //if event is for one day, also show its begin/end hours
            String time = "";
            String startingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.getStringFromResources(mContext, R.string.date_format_effective_event));
            String endingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.getStringFromResources(mContext, R.string.date_format_effective_event));

            if(!startingTime.equals(endingTime)) {
                time = startingTime + " - " + endingTime;
            } else {
                String eventStartHour = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.getStringFromResources(mContext, R.string.date_format_event_hour));
                String eventEndingHour = kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.getStringFromResources(mContext, R.string.date_format_event_hour));
                time = startingTime + " @ " + eventStartHour + " to " + eventEndingHour;
            }

            eventHolder.tvAnnouncementDate.setText(time);

            final String likeLink = kcpContentPage.getLikeLink();
            eventHolder.ivFav.setSelected(FavouriteManager.getInstance(mContext).isLiked(likeLink, kcpContentPage));
            eventHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.startSqueezeAnimationForFav(new Utility.SqueezeListener() {
                        @Override
                        public void OnSqueezeAnimationDone() {
                            if (getPageTitle().equals(mContext.getString(R.string.my_page_deals_for_today)) || getPageTitle().equals(mContext.getString(R.string.my_page_deals))) {
                                if (eventHolder.ivFav.isSelected()) {
                                    Analytics.getInstance(mContext).logEvent("PROFILE_Event_Unlike", "PROFILE", "Unlike deal", title, -1);
                                } else {
                                    Analytics.getInstance(mContext).logEvent("PROFILE_Event_Like", "PROFILE", "Like deal", title, 1);
                                }
                            }
                            eventHolder.ivFav.setSelected(!eventHolder.ivFav.isSelected());
                            FavouriteManager.getInstance(mContext).addOrRemoveFavContent(likeLink, kcpContentPage, mFavouriteInterface);

                        }
                    }, (Activity) mContext, eventHolder.ivFav);
                }
            });

            eventHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(HomeFragment.getInstance().isResumed()) {
                        Analytics.getInstance(mContext).logEvent("HOME_Event_Click", "HOME", "Click on Event", title);
                    } else if (getPageTitle().equals(mContext.getString(R.string.my_page_events))) {
                        Analytics.getInstance(mContext).logEvent("PROFILE_Event_Click", "PROFILE", "Click on Event", title);
                    }
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)eventHolder.ivAnnouncementLogo, transitionNameImage));

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                    ActivityAnimation.startActivityAnimation(mContext);
                }
            });
        }
    }

    public void addFooter(String footerText, int footerLayout, View.OnClickListener onClickListener) {
        mFooterExist = true;
        mFooterText = footerText;
        mFooterLayout = footerLayout;
        mFooterOnClickListener = onClickListener;
        mKcpContentPagesNews.add(new KcpContentPage());
    }

    public void updatePageTitle(String pageTitle) {
        mPageTitle = pageTitle;
    }

    private String getPageTitle() {
        return mPageTitle == null ? "" : mPageTitle;
    }

    @Override
    public int getItemCount() {
        return mKcpContentPagesNews == null ? 0 : mKcpContentPagesNews.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterExist && position == mKcpContentPagesNews.size() - 1) return KcpContentTypeFactory.ITEM_TYPE_FOOTER;
        KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        return KcpContentTypeFactory.getContentType(kcpContentPage);
    }
}
