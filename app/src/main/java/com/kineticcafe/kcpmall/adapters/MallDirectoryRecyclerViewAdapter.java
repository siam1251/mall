package com.kineticcafe.kcpmall.adapters;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.fragments.DirectoryFragment;
import com.kineticcafe.kcpmall.managers.FavouriteManager;
import com.kineticcafe.kcpmall.utility.Utility;
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
    private ArrayList<Integer> mPlacesByKeyword;
    private ArrayList<Integer> mKcpCategories;
    private int mFooterLayout;
    private boolean mFooterExist = false;
    private String mFooterText;
    private ArrayList<Object> mItems;
    private String mKeyword;

    public MallDirectoryRecyclerViewAdapter(Context context,
                                            ArrayList<KcpPlaces> placesByName,
                                            ArrayList<Integer> placesByKeyword,
                                            ArrayList<Integer> kcpCategories,
                                            String keyword) {
        mContext = context;
        mPlacesByName = placesByName == null ? new ArrayList<KcpPlaces>() : placesByName;
        mPlacesByKeyword = placesByKeyword == null ? new ArrayList<Integer>() : placesByKeyword;
        mKcpCategories = kcpCategories == null ? new ArrayList<Integer>() : kcpCategories;
        mKeyword = keyword;

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
            case ITEM_TYPE_PLACE_BY_NAME: //A to Z store list
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_place, parent, false));
            case ITEM_TYPE_PLACE_BY_KEYWORD:
                return new StoreViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_place, parent, false));
            case ITEM_TYPE_CATEGORY:
                return new CategoryHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_category, parent, false));
            case ITEM_TYPE_FOOTER_PLACE:
                return new RecyclerViewFooter.FooterViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_directory_footer, parent, false));
            case ITEM_TYPE_FOOTER_CATEGORY:
                return new RecyclerViewFooter.FooterViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_directory_footer, parent, false));
        }
        return null;
    }


    private KcpPlaces mKcpPlace;
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType() == ITEM_TYPE_PLACE_BY_NAME  || holder.getItemViewType() == ITEM_TYPE_PLACE_BY_KEYWORD) {
            mKcpPlace = null;
            if (holder.getItemViewType() == ITEM_TYPE_PLACE_BY_KEYWORD) {
                int externalCode = (Integer) mItems.get(position);
                mKcpPlace = KcpPlacesRoot.getInstance().getPlaceByExternalCode(String.valueOf(externalCode));
            } else if (holder.getItemViewType() == ITEM_TYPE_PLACE_BY_NAME ) {
                mKcpPlace = (KcpPlaces) mItems.get(position);
            }

            final StoreViewHolder storeViewHolder = (StoreViewHolder) holder;

            String imageUrl = mKcpPlace.getHighestImageUrl();
            storeViewHolder.ivDealLogo.setImageResource(R.drawable.placeholder);

            new GlideFactory().glideWithNoDefaultRatio(
                    mContext,
                    imageUrl,
                    storeViewHolder.ivDealLogo,
                    R.drawable.placeholder_logo);

            final String storename = mKcpPlace.getPlaceName();
            storeViewHolder.tvDealStoreName.setText(storename);

            final String category = mKcpPlace.getCategoryLabelOverride();
            String display = mKcpPlace.getFirstDisplay();
            if(!display.equals("")) storeViewHolder.tvDealTitle.setText(display);
            else storeViewHolder.tvDealTitle.setText(category);

            storeViewHolder.mView.setTag(position);
            storeViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KcpContentPage kcpContentPage = new KcpContentPage();
                    kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, mKcpPlace);

                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);

                    String transitionNameLogo = mContext.getResources().getString(R.string.transition_news_logo);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity)mContext,
                            Pair.create((View)storeViewHolder.ivDealLogo, transitionNameLogo));

                    ActivityCompat.startActivityForResult((Activity) mContext, intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP, options.toBundle());
                    ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }
        else if (holder.getItemViewType() == ITEM_TYPE_FOOTER_PLACE){

            Footer footer = (Footer) mItems.get(position);
            final RecyclerViewFooter.FooterViewHolder footerViewHolder = (RecyclerViewFooter.FooterViewHolder) holder;

            ((RecyclerViewFooter.FooterViewHolder) holder).tvFooter.setText(
                    mContext.getString(R.string.search_store_that_contains) + " " + mKeyword + " " + "(keyword)");

        } else if (holder.getItemViewType() == ITEM_TYPE_FOOTER_CATEGORY){

            Footer footer = (Footer) mItems.get(position);
            final RecyclerViewFooter.FooterViewHolder footerViewHolder = (RecyclerViewFooter.FooterViewHolder) holder;

            ((RecyclerViewFooter.FooterViewHolder) holder).tvFooter.setText(
                    mContext.getString(R.string.search_cat_that_contains) + " " + mKeyword + " " + "(category)");

        } else if (holder.getItemViewType() == ITEM_TYPE_CATEGORY){
            final CategoryHolder categoryHolder = (CategoryHolder) holder;
            int externalCategoryID = (Integer) mItems.get(position);
            final KcpCategories kcpCategory = KcpCategoryRoot.getInstance().getCategory(String.valueOf(externalCategoryID));

            final String categoryName = kcpCategory.getCategoryName();
            categoryHolder.tvCategory.setText(categoryName);
            final String externalCode = kcpCategory.getExternalCode();
            categoryHolder.ivCategory.setImageResource(CategoryIconFactory.getCategoryIcon(externalCode));

            categoryHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subCategoriesUrl = kcpCategory.getSubCategoriesLink();
                    if(!subCategoriesUrl.equals("")){
                        DirectoryFragment.getInstance().tryDownloadSubCategories(mContext, externalCode, categoryName, subCategoriesUrl, position, categoryHolder.tvCategory);
                    }
                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof KcpPlaces){
            return ITEM_TYPE_PLACE_BY_NAME;
        } else if(mItems.get(position) instanceof Footer) {
            Footer footer = (Footer) mItems.get(position);
            if(footer.itemType == ITEM_TYPE_FOOTER_PLACE) {
                return ITEM_TYPE_FOOTER_PLACE;
            } else if(footer.itemType == ITEM_TYPE_FOOTER_CATEGORY) {
                return ITEM_TYPE_FOOTER_CATEGORY;
            }
        } else if(mItems.get(position) instanceof Integer) {
            return ITEM_TYPE_PLACE_BY_KEYWORD;
        }
        return KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE;
    }
}
