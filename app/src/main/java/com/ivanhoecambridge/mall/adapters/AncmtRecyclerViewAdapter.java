package com.ivanhoecambridge.mall.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class AncmtRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPagesNews;
    private final int NUM_OF_ANCMT = 3;
    private boolean mCardView = false;

    public AncmtRecyclerViewAdapter(Context context, ArrayList<KcpContentPage> news, boolean limitAncmt) {
        mContext = context;
        mCardView = !limitAncmt;

        mKcpContentPagesNews = news == null ? new ArrayList<KcpContentPage>() : new ArrayList<KcpContentPage>(news);
        if(limitAncmt) {
            //todo: test
            if(mKcpContentPagesNews.size() > NUM_OF_ANCMT) mKcpContentPagesNews.subList(NUM_OF_ANCMT , mKcpContentPagesNews.size()).clear();
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public class AncmtViewHolder extends MainViewHolder {
        public CardView cvAncmt;
        public ImageView ivAncmtLogo;
        public TextView tvAncmtTitle;
        public TextView tvAncmtDate;

        public AncmtViewHolder(View v) {
            super(v);
            cvAncmt =     (CardView) v.findViewById(R.id.cvAncmt);
            ivAncmtLogo = (ImageView) v.findViewById(R.id.ivAncmtLogo);
            tvAncmtTitle = (TextView) v.findViewById(R.id.tvAncmtTitle);
            tvAncmtDate = (TextView) v.findViewById(R.id.tvAncmtDate);

            if(mCardView) {
                cvAncmt.setCardElevation(KcpUtility.dpToPx((Activity) mContext, 1));
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) cvAncmt.getLayoutParams();
                int margin = KcpUtility.dpToPx((Activity) mContext, 0.5f);
                lp.setMargins(0, margin, 0, margin);
                lp.height = KcpUtility.dpToPx((Activity) mContext, 110);
                cvAncmt.setLayoutParams(lp);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT:
                return new AncmtViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_ancmt, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final KcpContentPage kcpContentPage = mKcpContentPagesNews.get(position);
        if(getItemViewType(position) == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
            final AncmtViewHolder ancmtViewHolder = (AncmtViewHolder) holder;

            String imageUrl = kcpContentPage.getHighestResImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    ancmtViewHolder.ivAncmtLogo,
                    R.drawable.placeholder_square);

            String title = kcpContentPage.getTitle();
            ancmtViewHolder.tvAncmtTitle.setText(title);

            String startingTime = kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_ANNOUNCEMENT_GROUPED);
            ancmtViewHolder.tvAncmtDate.setText(startingTime);

            ancmtViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)ancmtViewHolder.ivAncmtLogo, transitionNameImage));

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
