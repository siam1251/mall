package com.kineticcafe.kcpmall.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;

import java.util.ArrayList;

public class DealsRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpContentPage> mKcpContentPages;

    public DealsRecyclerViewAdapter(Context context, ArrayList<KcpContentPage> kcpContentPages) {
        mContext = context;
        mKcpContentPages = kcpContentPages;
    }

    public void updateData(ArrayList<KcpContentPage> kcpContentPages) {
        mKcpContentPages.clear();
        mKcpContentPages.addAll(kcpContentPages);
        notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        public MainViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    public class LoadingViewHolder extends MainViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pbNewsAdapter);
        }
    }

    public class SectionHeaderViewHolder extends MainViewHolder {
        public TextView  tvSectionHeader;

        public SectionHeaderViewHolder(View v, String headerText) {
            super(v);
            tvSectionHeader = (TextView)  v.findViewById(R.id.tvSectionHeader);
            tvSectionHeader.setText(headerText);

            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) v.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    public class DealsViewHolder extends MainViewHolder {
        public RelativeLayout rlDeal;
        public ImageView ivDealLogo;
        public TextView tvDealStoreName;
        public TextView tvDealTitle;
        public TextView tvExpiryDate;
        public ImageView  ivFav;

        public DealsViewHolder(View v) {
            super(v);
            rlDeal             = (RelativeLayout)  v.findViewById(R.id.rlDeal);
            ivDealLogo  = (ImageView) v.findViewById(R.id.ivDealLogo);
            tvDealStoreName = (TextView)  v.findViewById(R.id.tvDealStoreName);
            tvDealTitle = (TextView)  v.findViewById(R.id.tvDealTitle);
            tvExpiryDate = (TextView)  v.findViewById(R.id.tvExpiryDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
        }
    }

    public class RecommendedDealsViewHolder extends MainViewHolder {
        public RecommendedDealsViewHolder (View v){
            super(v);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType){
            case KcpContentTypeFactory.ITEM_TYPE_LOADING:
                return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_OTHER_DEAL:
                return new DealsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_deal, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_RECOMMENDED_DEAL:
                return new RecommendedDealsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_interest, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST:
                return new RecommendedDealsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_interest, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_DEALS:
                return new SectionHeaderViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_section_header, parent, false),
                        parent.getContext().getResources().getString(R.string.section_header_recommended_deals));
            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_DEALS:
                return new SectionHeaderViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_section_header, parent, false),
                        parent.getContext().getResources().getString(R.string.section_header_other_deals));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final KcpContentPage kcpContentPage = mKcpContentPages.get(position);
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_OTHER_DEAL){
            final DealsViewHolder dealHolder = (DealsViewHolder) holder;

            String imageUrl = kcpContentPage.getImageUrl();
            new GlideFactory().glideWithDefaultRatio(
                    dealHolder.ivDealLogo.getContext(),
                    imageUrl,
                    dealHolder.ivDealLogo,
                    R.drawable.bg_splash);


            String storename = kcpContentPage.getStoreName();
            dealHolder.tvDealStoreName.setText(storename);

            String title = kcpContentPage.getTitle();
            dealHolder.tvDealTitle.setText(title);


            dealHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: implement fav functionality
                    Toast.makeText(mContext, "fav clicked", Toast.LENGTH_SHORT).show();
                    dealHolder.ivFav.setSelected(!dealHolder.ivFav .isSelected());
                }
            });

            dealHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "ANNOUNCEMENT CLICKED", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);
                    String transitionNameFav = mContext.getResources().getString(R.string.transition_fav);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)dealHolder.ivDealLogo, transitionNameImage),
                            Pair.create((View)dealHolder.ivFav, transitionNameFav));

                    ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
                    ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }
            });

            int daysLeftUntilEffectiveDate = kcpContentPage.getDaysLeftUntilEffectiveEndDate(kcpContentPage.effectiveEndTime);
            String daysLeft = kcpContentPage.getDaysLeftText(daysLeftUntilEffectiveDate, Constants.DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE);

            if(daysLeft.equals("")){
                dealHolder.tvExpiryDate.setVisibility(View.GONE);
            } else {
                dealHolder.tvExpiryDate.setText(daysLeft);
//                dealHolder.tvExpiryDate.setAnimation(new ExpiryDateAnimation().getAnimation());
            }


        }
    }

    @Override
    public int getItemCount() {
        return mKcpContentPages == null ? 0 : mKcpContentPages.size();
    }

    @Override
    public int getItemViewType(int position) {
        KcpContentPage kcpContentPage = mKcpContentPages.get(position);
//        return KcpContentTypeFactory.getContentType(kcpContentPage);
        if(position == 0){
            return KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_DEALS; //testing
//        } else if(position == 1){
//            return KcpContentTypeFactory.ITEM_TYPE_ADJUST_MY_INTEREST; //testing
//        else if(position == 2){
//            return KcpContentTypeFactory.ITEM_TYPE_RECOMMENDED_DEAL; //testing
//        } else if (position == 3){
//            return KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_DEALS; //testing
        } else {
            return KcpContentTypeFactory.ITEM_TYPE_OTHER_DEAL; //testing
        }
    }

}
