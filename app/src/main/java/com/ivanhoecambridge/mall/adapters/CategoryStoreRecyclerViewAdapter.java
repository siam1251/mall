package com.ivanhoecambridge.mall.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.factory.GlideFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.CategoriesFragment;
import com.ivanhoecambridge.mall.fragments.MapFragment;
import com.ivanhoecambridge.mall.interfaces.FavouriteInterface;
import com.ivanhoecambridge.mall.managers.FavouriteManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.KCPSetRatioImageView;
import com.ivanhoecambridge.mall.views.RecyclerViewFooter;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class CategoryStoreRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpPlaces> mKcpPlacesList;
    private int mContentType;
    private FavouriteInterface mFavouriteInterface;
    private MapFragment.OnStoreClickListener mStoreClickListener;
    private int mHeaderLayout;
    private int mFooterLayout;
    private boolean mFooterExist = false;
    private boolean mHeaderExist = false;
    private String mFooterText;
    private View.OnClickListener mOnClickListener;
    private Drawable placeholder;


    public CategoryStoreRecyclerViewAdapter(Context context, ArrayList<KcpPlaces> kcpPlaces, int contentType) {
        mContext = context;
        mKcpPlacesList = kcpPlaces;
        mContentType = contentType;
    }

    public CategoryStoreRecyclerViewAdapter(Context context, ArrayList<KcpPlaces> kcpPlaces, int contentType, MapFragment.OnStoreClickListener onClickListener) {
        mContext = context;
        mKcpPlacesList = kcpPlaces;
        mContentType = contentType;
        mStoreClickListener = onClickListener;
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public RelativeLayout rlDeal;
        public KCPSetRatioImageView ivDealLogo;
        public TextView tvDealStoreName;
        public TextView tvDealTitle;
        public TextView tvExpiryDate;
        public ImageView  ivFav;

        public StoreViewHolder(View v) {
            super(v);
            mView = v;

            rlDeal             = (RelativeLayout)  v.findViewById(R.id.rlDeal);
            ivDealLogo  = (KCPSetRatioImageView) v.findViewById(R.id.ivDealLogo);
            ivDealLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivDealLogo.getLayoutParams();
            int margin = (int) mContext.getResources().getDimension(R.dimen.card_desc_horizontal_padding);
            params.setMargins(margin, margin, margin, margin);
            ivDealLogo.setLayoutParams(params);

            tvDealStoreName = (TextView)  v.findViewById(R.id.tvDealStoreName);
            tvDealTitle = (TextView)  v.findViewById(R.id.tvDealTitle);
            tvExpiryDate = (TextView)  v.findViewById(R.id.tvExpiryDate);
            ivFav         = (ImageView)  v.findViewById(R.id.ivFav);
            v.setTag(this);
        }
    }

    public void setFavouriteListener(FavouriteInterface favouriteInterface){
        mFavouriteInterface = favouriteInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE: //Categorized Store list (gridlayout)
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_deal, parent, false));
            case KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE: //A to Z store list
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_place, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_HEADER:
                return new RecyclerViewFooter.HeaderViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_my_page_header, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_FOOTER:
                return new RecyclerViewFooter.FooterViewHolder(
                        LayoutInflater.from(mContext).inflate(mFooterLayout, parent, false));
        }
        return null;
    }

    public void addHeader(View.OnClickListener onClickListener){
        mHeaderExist = true;
        mOnClickListener = onClickListener;
        KcpPlaces fakeKcpPlaces = new KcpPlaces();
        mKcpPlacesList.add(0, fakeKcpPlaces);
        notifyDataSetChanged();
    }

    public void removeHeader() {
        mHeaderExist = false;
        mOnClickListener = null;
        if(getItemViewType(0) == KcpContentTypeFactory.ITEM_TYPE_HEADER){
            mKcpPlacesList.remove(0);
        }
        notifyDataSetChanged();
    }

    public void addFooter(String footerText, int footerLayout, View.OnClickListener onClickListener){
        mFooterExist = true;
        mFooterText = footerText;
        mFooterLayout = footerLayout;
        mOnClickListener = onClickListener;
        KcpPlaces fakeKcpPlaces = new KcpPlaces();
        mKcpPlacesList.add(fakeKcpPlaces);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_FOOTER){
            RecyclerViewFooter.FooterViewHolder footerViewHolder = (RecyclerViewFooter.FooterViewHolder) holder;
            footerViewHolder.mView.setOnClickListener(mOnClickListener);
            footerViewHolder.tvFooter.setText(mFooterText);
            return;
        } else if (getItemViewType(position) == KcpContentTypeFactory.ITEM_TYPE_HEADER){
            RecyclerViewFooter.HeaderViewHolder headerViewHolder = (RecyclerViewFooter.HeaderViewHolder) holder;
            headerViewHolder.mView.setOnClickListener(mOnClickListener);
            return;
        }

        final KcpPlaces kcpPlace = (KcpPlaces) mKcpPlacesList.get(position);
        final StoreViewHolder storeViewHolder = (StoreViewHolder) holder;

        String imageUrl = kcpPlace.getHighestImageUrl();
        placeholder = ContextCompat.getDrawable(mContext, R.drawable.placeholder);

        Glide.with(mContext)
                .load(imageUrl)
                .crossFade()
                .placeholder(placeholder)
                .error(placeholder)
                .dontAnimate()
                .into(storeViewHolder.ivDealLogo);

        final String storename = kcpPlace.getPlaceName();
        storeViewHolder.tvDealStoreName.setText(storename);


        final String category = kcpPlace.getCategoryWithOverride();
        storeViewHolder.tvDealTitle.setText(category);

        if (getItemViewType(position) == KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE) {

            final String likeLink = kcpPlace.getLikeLink();

            final KcpContentPage kcpContentPage = new KcpContentPage();
            kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);

            storeViewHolder.ivFav.setSelected(FavouriteManager.getInstance(mContext).isLiked(likeLink, kcpContentPage));

            storeViewHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mFooterText==null) {
                        if (storeViewHolder.ivFav.isSelected())
                            Analytics.getInstance(mContext).logEvent("DIRECTORY_Store_Unlike", "DIRECTORY", "Unlike Store", storename, -1);
                        else
                            Analytics.getInstance(mContext).logEvent("DIRECTORY_Store_Like", "DIRECTORY", "Like Store", storename, 1);
                    }

                    Utility.startSqueezeAnimationForFav(new Utility.SqueezeListener() {
                        @Override
                        public void OnSqueezeAnimationDone() {
                            storeViewHolder.ivFav.setSelected(!storeViewHolder.ivFav .isSelected());
                            FavouriteManager.getInstance(mContext).addOrRemoveFavContent(likeLink, kcpContentPage, mFavouriteInterface);
                        }
                    }, (Activity) mContext, storeViewHolder.ivFav);
                }
            });
        } else if (getItemViewType(position) == KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE) {
        }

        storeViewHolder.mView.setTag(position);
        storeViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStoreClickListener != null) mStoreClickListener.onStoreClick(kcpPlace.getPlaceId(), kcpPlace.getExternalCode(), storename, category);
                else {
                    KcpContentPage kcpContentPage = new KcpContentPage();
                    kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameLogo = mContext.getResources().getString(R.string.transition_news_logo);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)storeViewHolder.ivDealLogo, transitionNameLogo));

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mKcpPlacesList == null ? 0 : mKcpPlacesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mFooterExist && position == mKcpPlacesList.size() - 1) return KcpContentTypeFactory.ITEM_TYPE_FOOTER;
        else if(mHeaderExist && position == 0) return KcpContentTypeFactory.ITEM_TYPE_HEADER;
        return mContentType;
    }
}
