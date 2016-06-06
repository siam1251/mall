package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.InterestedCategoryActivity;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-05-05.
 */
public class InterestRecyclerViewAdapter extends RecyclerView.Adapter {

    private enum InterestType { CATEGORY, STORE}
    private InterestType mInterestType;
    private Context mContext;
    private ArrayList<KcpCategories> mKcpCategoriesList;
    private ArrayList<KcpPlaces> mKcpPlacesList;
    private ArrayList<InterestedCategoryActivity.GridLayoutItem> mGridLayoutItemArrayList;
    private ArrayList<Integer> mFavCatTempList;
    private ArrayList<String> mFavStoreLikeLinkList;

    public InterestRecyclerViewAdapter(Context context, ArrayList<KcpCategories> news, ArrayList<InterestedCategoryActivity.GridLayoutItem> gridLayoutItemArrayList) {
        mContext = context;
        mInterestType = InterestType.CATEGORY;
        mKcpCategoriesList = news;
        mGridLayoutItemArrayList = gridLayoutItemArrayList;
        mFavCatTempList = Utility.loadGsonArrayList(context, Constants.PREFS_KEY_CATEGORY);
    }

    public InterestRecyclerViewAdapter(Context context, ArrayList<KcpPlaces> kcpPlaces) {
        mInterestType = InterestType.STORE;
        mContext = context;
        mKcpPlacesList = kcpPlaces;
        mFavStoreLikeLinkList = Utility.loadGsonArrayListString(context, Constants.PREFS_KEY_FAV_STORE_LIKE_LINK);
    }

    public void updateData(ArrayList<KcpCategories> kcpContentPages) {
        mKcpCategoriesList.clear();
        mKcpCategoriesList.addAll(kcpContentPages);
        notifyDataSetChanged();
    }

    public class InterestedCategoryHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView  tvIntrstd;
        public CardView cvIntrst;

        public InterestedCategoryHolder(View v) {
            super(v);
            mView = v;
            tvIntrstd = (TextView)  v.findViewById(R.id.tvIntrstd);
            cvIntrst = (CardView)  v.findViewById(R.id.cvIntrst);
        }
    }

    public ArrayList<Integer> getFavCatTempList() {
        if(mFavCatTempList == null) return new ArrayList<>();
        return mFavCatTempList;
    }

    public ArrayList<String> getFavStoreLikeLinkList() {
        if(mFavStoreLikeLinkList == null) return new ArrayList<String>();
        return mFavStoreLikeLinkList;
    }

    public class InterestedStoreHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView  tvIntrstd;
        public ImageView ivFav;
        public ImageView ivIntrstd;
        public CardView cvIntrst;

        public InterestedStoreHolder(View v) {
            super(v);
            mView = v;
            tvIntrstd = (TextView)  v.findViewById(R.id.tvIntrstd);
            ivIntrstd = (ImageView)  v.findViewById(R.id.ivIntrstd);
            ivFav = (ImageView)  v.findViewById(R.id.ivFav);
            cvIntrst = (CardView)  v.findViewById(R.id.cvIntrst);
        }
    }

    public void resetFavCatTempList() {
        mFavCatTempList.clear();
        notifyDataSetChanged();
    }

    public void resetFavStoreLikeLinkList() {
        mFavStoreLikeLinkList.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.PREF_ITEM_TYPE_CAT:
                return new InterestedCategoryHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_interested_category,
                                parent,
                                false));
            case KcpContentTypeFactory.PREF_ITEM_TYPE_STORE:
                return new InterestedStoreHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_interested_store,
                                parent,
                                false));
        }
        return null;
    }

    public void setSelectedCategory(CardView cardView, TextView textView, boolean selected){
        if(selected){
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.themeColor));
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.intrstd_card_bg));
            textView.setTextColor(mContext.getResources().getColor(R.color.intrstd_txt));
        }
    }

    public void setSelectedStore(CardView cardView, ImageView imageView, boolean selected){
        imageView.setSelected(selected);
        if(selected){
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.intrstd_card_bg_with_opacity));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_CAT) {
            final InterestedCategoryHolder interestedCategoryHolder = (InterestedCategoryHolder) holder;
            final KcpCategories kcpCategories = mKcpCategoriesList.get(position);

            setSelectedCategory(interestedCategoryHolder.cvIntrst, interestedCategoryHolder.tvIntrstd, mFavCatTempList.contains(kcpCategories.getCategoryId()));
            interestedCategoryHolder.tvIntrstd.setText(kcpCategories.getCategoryName());
            interestedCategoryHolder.cvIntrst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mFavCatTempList.contains(kcpCategories.getCategoryId())) {
                        mFavCatTempList.remove(Integer.valueOf(kcpCategories.getCategoryId()));
                        setSelectedCategory(interestedCategoryHolder.cvIntrst, interestedCategoryHolder.tvIntrstd, false);
                    } else {
                        mFavCatTempList.add(kcpCategories.getCategoryId());
                        setSelectedCategory(interestedCategoryHolder.cvIntrst, interestedCategoryHolder.tvIntrstd, true);
                    }
                }
            });

            if(mGridLayoutItemArrayList.size() > position ){
                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) interestedCategoryHolder.cvIntrst.getLayoutParams();
                int relativeLayotRule = mGridLayoutItemArrayList.get(position).relativeLayotRule;
                param.addRule(relativeLayotRule);
                interestedCategoryHolder.cvIntrst.setLayoutParams(param);
            }
        } else if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_STORE) {
            final InterestedStoreHolder interestedStoreHolder = (InterestedStoreHolder) holder;
            final KcpPlaces kcpPlaces = mKcpPlacesList.get(position);

            String imageUrl = kcpPlaces.getImageUrl();
            Glide.with(mContext)
                    .load(imageUrl)
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            interestedStoreHolder.tvIntrstd.setVisibility(View.VISIBLE);
                            interestedStoreHolder.tvIntrstd.setText(kcpPlaces.getPlaceName());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .placeholder(R.drawable.placeholder_rectangle)
                    .into(interestedStoreHolder.ivIntrstd);


            setSelectedStore(interestedStoreHolder.cvIntrst, interestedStoreHolder.ivFav, mFavStoreLikeLinkList.contains(kcpPlaces.getLikeLink()));
            interestedStoreHolder.cvIntrst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mFavStoreLikeLinkList.contains(kcpPlaces.getLikeLink())) {
                        mFavStoreLikeLinkList.remove(kcpPlaces.getLikeLink());
                        setSelectedStore(interestedStoreHolder.cvIntrst, interestedStoreHolder.ivFav, false);
                    } else {
                        mFavStoreLikeLinkList.add(kcpPlaces.getLikeLink());
                        setSelectedStore(interestedStoreHolder.cvIntrst, interestedStoreHolder.ivFav, true);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mInterestType.equals(InterestType.CATEGORY)) return mKcpCategoriesList == null ? 0 : mKcpCategoriesList.size();
        else if(mInterestType.equals(InterestType.STORE)) return mKcpPlacesList == null ? 0 : mKcpPlacesList.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(mInterestType.equals(InterestType.CATEGORY)) return KcpContentTypeFactory.PREF_ITEM_TYPE_CAT;
        else if(mInterestType.equals(InterestType.STORE)) return KcpContentTypeFactory.PREF_ITEM_TYPE_STORE;
        else return 0;
    }

}
