package com.kineticcafe.kcpmall.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class CategoryStoreRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpPlaces> mKcpPlacesList;

    public CategoryStoreRecyclerViewAdapter(Context context, ArrayList<KcpPlaces> kcpPlaces) {
        mContext = context;
        mKcpPlacesList = kcpPlaces;
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public RelativeLayout rlDeal;
        public ImageView ivDealLogo;
        public TextView tvDealStoreName;
        public TextView tvDealTitle;
        public TextView tvExpiryDate;
        public ImageView  ivFav;

        public StoreViewHolder(View v) {
            super(v);
            mView = v;
            rlDeal             = (RelativeLayout)  v.findViewById(R.id.rlDeal);
            ivDealLogo  = (ImageView) v.findViewById(R.id.ivDealLogo);
            tvDealStoreName = (TextView)  v.findViewById(R.id.tvDealStoreName);
            tvDealTitle = (TextView)  v.findViewById(R.id.tvDealTitle);
            tvExpiryDate = (TextView)  v.findViewById(R.id.tvExpiryDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.PREF_ITEM_TYPE_STORE:
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_deal, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_STORE) {

            final KcpPlaces kcpPlace = (KcpPlaces) mKcpPlacesList.get(position);
            final StoreViewHolder storeViewHolder = (StoreViewHolder) holder;

            String imageUrl = kcpPlace.getImageUrl();
            storeViewHolder.ivDealLogo.setImageResource(R.drawable.placeholder);

            new GlideFactory().glideWithDefaultRatio(
                    mContext,
                    imageUrl,
                    storeViewHolder.ivDealLogo,
                    R.drawable.placeholder);

            String storename = kcpPlace.getPlaceName();
            storeViewHolder.tvDealStoreName.setText(storename);

            String display = kcpPlace.getFirstDisplay();
            storeViewHolder.tvDealTitle.setText(display);

            storeViewHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "fav clicked", Toast.LENGTH_SHORT).show();
                    storeViewHolder.ivFav.setSelected(!storeViewHolder.ivFav .isSelected());
                }
            });


            storeViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KcpContentPage kcpContentPage = new KcpContentPage();
                    kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameImage = mContext.getResources().getString(R.string.transition_news_image);
                    String transitionNameFav = mContext.getResources().getString(R.string.transition_fav);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (Activity)mContext,
                                Pair.create((View)storeViewHolder.ivDealLogo, transitionNameImage),
                                Pair.create((View)storeViewHolder.ivFav, transitionNameFav));

                    ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
                    ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mKcpPlacesList == null ? 0 : mKcpPlacesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return KcpContentTypeFactory.PREF_ITEM_TYPE_STORE;
    }
}
