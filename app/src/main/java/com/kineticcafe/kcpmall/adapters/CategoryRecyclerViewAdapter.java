package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.fragments.DirectoryFragment;
import com.kineticcafe.kcpmall.views.ProgressBarWhileDownloading;

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
                    String subCategoriesUrl = kcpCategory.getSubCategoriesLink();
                    if(!subCategoriesUrl.equals("")){
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
