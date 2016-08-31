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

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.views.RecyclerViewFooter;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class MallDirectoryRecyclerViewAdapter extends RecyclerView.Adapter {



    private static final int ITEM_TYPE_PLACE_BY_NAME =  0;
    private static final int ITEM_TYPE_FOOTER_PLACE =  1;
    private static final int ITEM_TYPE_PLACE_BY_KEYWORD =  2;
    private static final int ITEM_TYPE_FOOTER_CATEGORY =  3;
    private static final int ITEM_TYPE_CATEGORY =  4;


    private Context mContext;
    private ArrayList<KcpPlaces> mPlacesByName;
    private ArrayList<KcpPlaces> mPlacesByKeyword;
    private ArrayList<KcpCategories> mKcpCategories;
    private int mFooterLayout;
    private boolean mFooterExist = false;
    private String mFooterText;
    private ArrayList<Object> mItems;

    public MallDirectoryRecyclerViewAdapter(Context context,
                                            ArrayList<KcpPlaces> placesByName,
                                            ArrayList<KcpPlaces> placesByKeyword,
                                            ArrayList<KcpCategories> kcpCategories,
                                            String keyword) {
        mContext = context;
        mPlacesByName = placesByName;
        mPlacesByKeyword = placesByKeyword;
        mKcpCategories = kcpCategories;

        createItems(keyword);
    }

    public void updateData(String keyword, ArrayList<KcpPlaces> kcpPlaces) {
        mPlacesByName.clear();
        mPlacesByName.addAll(kcpPlaces);
        notifyDataSetChanged();

        createItems(keyword);
    }

    public void createItems(String keyword){
        if(mItems == null) mItems = new ArrayList<>();
        else mItems.clear();

        int sizeOfPlacesByName = mPlacesByName == null ? 0 : mPlacesByName.size();
        int sizeOfPlacesByKeyword = mPlacesByKeyword == null ? 0 : mPlacesByKeyword.size();
        int sizeOfCategories = mKcpCategories == null ? 0 : mKcpCategories.size();

        boolean placesByNameExist = sizeOfPlacesByName > 0 ? true : false;
        boolean placesByKeywordExist = sizeOfPlacesByKeyword > 0 ? true : false;
        boolean categoryExist = sizeOfCategories > 0 ? true : false;

        if(placesByNameExist){
            mItems.addAll(mPlacesByName);
        }

        if(placesByKeywordExist){
            addFooter(keyword, ITEM_TYPE_FOOTER_PLACE);
            mItems.addAll(mPlacesByKeyword);
        }

        if(categoryExist){
            addFooter(keyword, ITEM_TYPE_FOOTER_CATEGORY);
            mItems.addAll(mKcpCategories);
        }
    }

    public void addFooter(String keyword, int itemType){
        mItems.add(new Footer(keyword, itemType));
    }

    private class Footer {
        public String keyword;
        public int itemType;

        public Footer (String keyword, int itemType){
            this.keyword = keyword;
            this.itemType = itemType;
        }
    }

    private class CategoryHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CardView cvAncmt;
        public ImageView  ivCategory;
        public TextView  tvCategory;

        public CategoryHolder(View v) {
            super(v);
            mView = v;
            cvAncmt = (CardView)  v.findViewById(R.id.cvAncmt);
            ivCategory = (ImageView)  v.findViewById(R.id.ivCategory);
            tvCategory = (TextView)  v.findViewById(R.id.tvCategory);
        }
    }

    private class StoreViewHolder extends RecyclerView.ViewHolder {
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.PREF_ITEM_TYPE_SUB_CAT: //Categorized Store list (gridlayout)
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_sub_category, parent, false));
            case KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE: //A to Z store list
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_place, parent, false));
            case KcpContentTypeFactory.ITEM_TYPE_FOOTER:
                return new RecyclerViewFooter.FooterViewHolder(
                        LayoutInflater.from(mContext).inflate(mFooterLayout, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == ITEM_TYPE_PLACE_BY_NAME) {


        } else if (holder.getItemViewType() == ITEM_TYPE_FOOTER_PLACE){


        } else if (holder.getItemViewType() == ITEM_TYPE_PLACE_BY_KEYWORD){


        } else if (holder.getItemViewType() == ITEM_TYPE_FOOTER_CATEGORY){


        } else if (holder.getItemViewType() == ITEM_TYPE_CATEGORY){


        }


    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE;
    }
}
