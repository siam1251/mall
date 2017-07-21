package com.ivanhoecambridge.mall.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.factory.CategoryIconFactory;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.fragments.DirectoryFragment;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpCategories> mKcpCategoriesList;
    private int mCategoryType;


    public CategoryRecyclerViewAdapter(Context context, ArrayList<KcpCategories> kcpCategories, int categoryType) {
        mContext = context;
        mKcpCategoriesList = kcpCategories;
        mCategoryType = categoryType;
    }

    public void updateData(ArrayList<KcpCategories> kcpContentPages) {
        mKcpCategoriesList.clear();
        mKcpCategoriesList.addAll(kcpContentPages);
        notifyDataSetChanged();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        public View mView;
        public ImageView  ivCategory;
        public TextView  tvCategory;

        public CategoryHolder(View v) {
            super(v);
            mView = v;
            ivCategory = (ImageView)  v.findViewById(R.id.ivCategory);
            tvCategory = (TextView)  v.findViewById(R.id.tvCategory);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.PREF_ITEM_TYPE_CAT:
                return new CategoryHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_category, parent, false));
            case KcpContentTypeFactory.PREF_ITEM_TYPE_SUB_CAT:
                return new CategoryHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_sub_category, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_CAT) {
            final CategoryHolder categoryHolder = (CategoryHolder) holder;
            final KcpCategories kcpCategory = mKcpCategoriesList.get(position);

            final String categoryName = kcpCategory.getCategoryName();
            categoryHolder.tvCategory.setText(categoryName);
            final String externalCode = kcpCategory.getExternalCode();
            categoryHolder.ivCategory.setImageResource(CategoryIconFactory.getCategoryIcon(externalCode));

            categoryHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Analytics.getInstance(mContext).logEvent("DIRECTORY_Category_Click", "DIRECTORY", "Click on Category", categoryName);
                    String subCategoriesUrl = kcpCategory.getSubCategoriesLink();
                    boolean hideChildCategories = kcpCategory.getCustom().getHideChildCategories();
                    if(hideChildCategories || subCategoriesUrl.equals("")) {
                        DirectoryFragment.getInstance().tryDownloadPlacesForThisCategory(mContext, categoryName, externalCode, kcpCategory.getPlacesLink(), categoryHolder.tvCategory);
                    } else {
                        DirectoryFragment.getInstance().tryDownloadSubCategories(mContext, externalCode, categoryName, subCategoriesUrl, position, categoryHolder.tvCategory);
                    }
                }
            });
        } else if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_SUB_CAT) {
            final CategoryHolder categoryHolder = (CategoryHolder) holder;
            final KcpCategories subKcpCategory = mKcpCategoriesList.get(position);

            final String externalCode = subKcpCategory.getExternalCode();
            final String categoryName = subKcpCategory.getCategoryName();

            if(position == 0) categoryHolder.tvCategory.setText(mContext.getResources().getString(R.string.see_all_stores));
            else categoryHolder.tvCategory.setText(categoryName);


            categoryHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Analytics.getInstance(mContext).logEvent("DIRECTORY_Subcategory_Click", "DIRECTORY", "Click on Subcategory", categoryName);
                    String placeUrl = subKcpCategory.getPlacesLink();
                    if(!placeUrl.equals("")){
                        DirectoryFragment.getInstance().tryDownloadPlacesForThisCategory(mContext, categoryName, externalCode, placeUrl, categoryHolder.tvCategory);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mKcpCategoriesList == null ? 0 : mKcpCategoriesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mCategoryType;
    }
}
