package com.kineticcafe.kcpmall.adapters;


import android.app.Activity;
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
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.InterestedCategoryActivity;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
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
    private ArrayList<KcpPlaces> mKcpPlacesRecommendedList;
    private ArrayList<KcpPlaces> mKcpPlacesOthersList;
    private ArrayList<InterestedCategoryActivity.GridLayoutItem> mGridLayoutItemArrayList;
    private ArrayList<Integer> mFavCatTempList;
    private HashMap<String, KcpContentPage> mFavCatTempMap;
    private ArrayList<String> mFavStoreLikeLinkList;
    private InterestedCategoryActivity.ItemClickListener mItemClickListener;

    private ArrayList<Object> mItems;

    public InterestRecyclerViewAdapter(Context context, ArrayList<KcpCategories> news, ArrayList<InterestedCategoryActivity.GridLayoutItem> gridLayoutItemArrayList, InterestedCategoryActivity.ItemClickListener itemClickListener) {
        mContext = context;
        mInterestType = InterestType.CATEGORY;
        mKcpCategoriesList = new ArrayList<KcpCategories>(news);
        mGridLayoutItemArrayList = gridLayoutItemArrayList;
        mFavCatTempList = FavouriteManager.getInstance(context).getInterestedCategoryList();
        mItemClickListener = itemClickListener;
    }

    public InterestRecyclerViewAdapter(Context context, ArrayList<KcpPlaces> kcpPlaces, InterestedCategoryActivity.ItemClickListener itemClickListener) {
        mInterestType = InterestType.STORE;
        mContext = context;
        mKcpPlacesRecommendedList = new ArrayList<>(kcpPlaces);
        mFavStoreLikeLinkList = FavouriteManager.getInstance(context).getInterestedStoreList();
        mKcpPlacesOthersList = new ArrayList<>(KcpPlacesRoot.getInstance().getPlacesList(KcpPlaces.PLACE_TYPE_STORE));

        mItemClickListener = itemClickListener;

        createItems();
    }

    public ArrayList<Integer> getFavCatTempList() {
        if(mFavCatTempList == null) return new ArrayList<>();
        return mFavCatTempList;
    }

    public ArrayList<String> getFavStoreLikeLinkList() {
        if(mFavStoreLikeLinkList == null) return new ArrayList<String>();
        return mFavStoreLikeLinkList;
    }


    public void createItems(){
        if(mItems == null) mItems = new ArrayList<>();
        else mItems.clear();

        removeDuplicateFromOtherStores();

        int sizeOfRecommendedStores = mKcpPlacesRecommendedList == null ? 0 : mKcpPlacesRecommendedList.size();
        int sizeOfOtherStores = mKcpPlacesOthersList.size();

        boolean recommendedStoresExist =  sizeOfRecommendedStores > 0 ? true : false;
        boolean otherStoresExist = sizeOfOtherStores > 0 ? true : false;

        if(recommendedStoresExist) {
            mItems.add(KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_STORES);
            mItems.addAll(mKcpPlacesRecommendedList);
        }

        if(otherStoresExist){
            mItems.add(KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_STORES);
            mItems.addAll(mKcpPlacesOthersList);
        }
    }

    private void removeDuplicateFromOtherStores(){
        if(mKcpPlacesOthersList.size() != 0 && mKcpPlacesRecommendedList.size() != 0){
            for(KcpPlaces kcpRecommendedPlace : mKcpPlacesRecommendedList){
                if(mKcpPlacesOthersList.contains(kcpRecommendedPlace)) mKcpPlacesOthersList.remove(kcpRecommendedPlace);
            }
        }
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

    public class InterestedStoreSectionHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView  tvIntrstdCatDesc;

        public InterestedStoreSectionHolder(View v) {
            super(v);
            mView = v;
            tvIntrstdCatDesc = (TextView)  v.findViewById(R.id.tvIntrstdCatDesc);
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
            case KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE:
                return new InterestedStoreHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_interested_store,
                                parent,
                                false));
            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_CATEGORY:
                return new InterestedStoreSectionHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_recommended_store_section_header,
                                parent,
                                false));

            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_STORES:
                return new InterestedStoreSectionHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_recommended_store_section_header,
                                parent,
                                false));
            case KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_STORES:
                return new InterestedStoreSectionHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_recommended_store_section_header,
                                parent,
                                false));
        }
        return null;
    }

    public void setSelectedCategory(CardView cardView, TextView textView, boolean selected){
        if(selected){

            /*cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.white));
            textView.setTextColor(mContext.getResources().getColor(R.color.intrstd_txt_on)); *///TODO: new theme but it's not as visible so...

            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.themeColor));
            textView.setTextColor(mContext.getResources().getColor(R.color.white));

//            textView.setTypeface(null, Typeface.BOLD); //1. AFFECTS THE WIDTH and sometimes the texts don't fit, Typeface.BOLD doesn't affect fonts on Samsung devices
            /*((CustomFontTextView) textView).setFont(mContext, mContext.getString(R.string.fontFamily_roboto_bold));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.intrstd_name_selected));*/
        } else {
            cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.intrstd_card_bg));
            textView.setTextColor(mContext.getResources().getColor(R.color.intrstd_txt_off));
//            textView.setTypeface(null, Typeface.NORMAL);
            /*((CustomFontTextView) textView).setFont(mContext, mContext.getString(R.string.fontFamily_roboto_regular));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.intrstd_name));*/
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
                    Utility.startSqueezeAnimationForInterestedCat(new Utility.SqueezeListener() {
                        @Override
                        public void OnSqueezeAnimationDone() {
                        }
                    }, (Activity) mContext, interestedCategoryHolder.cvIntrst);


                    if(mFavCatTempList.contains(kcpCategories.getCategoryId())) {
                        mFavCatTempList.remove(Integer.valueOf(kcpCategories.getCategoryId()));
                        setSelectedCategory(interestedCategoryHolder.cvIntrst, interestedCategoryHolder.tvIntrstd, false);
                    } else {
                        mFavCatTempList.add(kcpCategories.getCategoryId());
                        setSelectedCategory(interestedCategoryHolder.cvIntrst, interestedCategoryHolder.tvIntrstd, true);
                    }

                    if(mItemClickListener != null) {
                        if(mFavCatTempList.size() > 0) mItemClickListener.onItemClick(false);
                        else mItemClickListener.onItemClick(true);
                    }
                }
            });
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) interestedCategoryHolder.cvIntrst.getLayoutParams();
            int relativeLayotRule = mGridLayoutItemArrayList.get(position).relativeLayoutRule;



            if(relativeLayotRule == RelativeLayout.CENTER_HORIZONTAL) {
//                ((RelativeLayout) interestedCategoryHolder.mView).setGravity(Gravity.CENTER);
//                int margin = KcpUtility.dpToPx((Activity )mContext, 3);
//                param.setMargins(margin, margin, margin, margin);
            }
            param.addRule(relativeLayotRule);
            interestedCategoryHolder.cvIntrst.setLayoutParams(param);
        } else if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE) {
            final InterestedStoreHolder interestedStoreHolder = (InterestedStoreHolder) holder;

            final KcpPlaces kcpPlaces = (KcpPlaces) mItems.get(position);
            String imageUrl = kcpPlaces.getHighestImageUrl();
            Glide.with(mContext)
                    .load(imageUrl)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            interestedStoreHolder.tvIntrstd.setVisibility(View.VISIBLE);
                            interestedStoreHolder.tvIntrstd.setText(kcpPlaces.getPlaceName());
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            interestedStoreHolder.tvIntrstd.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .placeholder(R.drawable.placeholder_rectangle)
                    .into(interestedStoreHolder.ivIntrstd);

            setSelectedStore(interestedStoreHolder.cvIntrst, interestedStoreHolder.ivFav, mFavStoreLikeLinkList.contains(kcpPlaces.getLikeLink()));
            interestedStoreHolder.cvIntrst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.startSqueezeAnimationForInterestedCat(new Utility.SqueezeListener() {
                        @Override
                        public void OnSqueezeAnimationDone() {
                        }
                    }, (Activity) mContext, interestedStoreHolder.cvIntrst);
                    if(mFavStoreLikeLinkList.contains(kcpPlaces.getLikeLink())) {
                        mFavStoreLikeLinkList.remove(kcpPlaces.getLikeLink());
                        setSelectedStore(interestedStoreHolder.cvIntrst, interestedStoreHolder.ivFav, false);
                    } else {
                        mFavStoreLikeLinkList.add(kcpPlaces.getLikeLink());
                        setSelectedStore(interestedStoreHolder.cvIntrst, interestedStoreHolder.ivFav, true);
                    }

                    if(mItemClickListener != null) {
                        if(mFavStoreLikeLinkList.size() > 0) mItemClickListener.onItemClick(false);
                        else mItemClickListener.onItemClick(true);
                    }
                }
            });
        } else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_RECOMMENDED_STORES){
            final InterestedStoreSectionHolder interestedStoreSectionHolder = (InterestedStoreSectionHolder) holder;
            interestedStoreSectionHolder.tvIntrstdCatDesc.setText(mContext.getResources().getString(R.string.intrstd_cat_recommended_section_header));
        }  else if(holder.getItemViewType() == KcpContentTypeFactory.ITEM_TYPE_SECTION_HEADER_OTHER_STORES){
            final InterestedStoreSectionHolder interestedStoreSectionHolder = (InterestedStoreSectionHolder) holder;
            interestedStoreSectionHolder.tvIntrstdCatDesc.setText(mContext.getResources().getString(R.string.intrstd_cat_other_section_header));
        }
    }

    @Override
    public int getItemCount() {
        if(mInterestType.equals(InterestType.CATEGORY)) return mKcpCategoriesList == null ? 0 : mKcpCategoriesList.size();
        else if(mInterestType.equals(InterestType.STORE)) return mItems == null ? 0 : mItems.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(mInterestType.equals(InterestType.CATEGORY)) {
            return KcpContentTypeFactory.PREF_ITEM_TYPE_CAT;
        }
        else if(mInterestType.equals(InterestType.STORE)) {
            Object item =  mItems.get(position);
            if (item instanceof Integer) {
                return (Integer) item;
            } else {
                return KcpContentTypeFactory.PREF_ITEM_TYPE_PLACE;
            }
        }
        else return 0;
    }

}
