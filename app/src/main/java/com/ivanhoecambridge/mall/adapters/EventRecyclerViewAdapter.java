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
import com.ivanhoecambridge.mall.bluedot.CoordinateListener;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPagesNews;
    private FavouriteInterface mFavouriteInterface;
    private boolean mIsCardFullWidth;

    public EventRecyclerViewAdapter(Context context, ArrayList<KcpContentPage> events, boolean isCardFullWidth) {
        mContext = context;
        mKcpContentPagesNews = events == null ? new ArrayList<KcpContentPage>() : events;
        mIsCardFullWidth = isCardFullWidth;
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
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        if(getItemViewType(position) == KcpContentTypeFactory.ITEM_TYPE_EVENT){
            final EventViewHolder eventHolder = (EventViewHolder) holder;
            String imageUrl = kcpContentPage.getHighestResImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    eventHolder.ivAnnouncementLogo,
                    R.drawable.placeholder);

            String title = kcpContentPage.getTitle();
            eventHolder.tvAnnouncementTitle.setText(title);

            //if event is for one day, also show its begin/end hours
            String time = "";
            String startingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EFFECTIVE_EVENT);
            String endingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EFFECTIVE_EVENT);

            if(!startingTime.equals(endingTime)) {
                time = startingTime + " - " + endingTime;
            } else {
                String eventStartHour = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EVENT_HOUR);
                String eventEndingHour = kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EVENT_HOUR);
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
                            eventHolder.ivFav.setSelected(!eventHolder.ivFav.isSelected());
                            FavouriteManager.getInstance(mContext).addOrRemoveFavContent(likeLink, kcpContentPage, mFavouriteInterface);
                        }
                    }, (Activity) mContext, eventHolder.ivFav);
                }
            });

            eventHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

    @Override
    public int getItemCount() {
        return mKcpContentPagesNews == null ? 0 : mKcpContentPagesNews.size();
    }

    @Override
    public int getItemViewType(int position) {
        KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        return KcpContentTypeFactory.getContentType(kcpContentPage);
    }
}
