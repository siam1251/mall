package com.kineticcafe.kcpmall.adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.fragments.DirectoryFragment;
import com.kineticcafe.kcpmall.utility.Utility;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class ExpandableCategoryRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpCategories> mLevelOneCategoriesList;
    private ArrayList<KcpCategories> mKcpCategoriesList;
    private ArrayList<String> mExpandedExternalCode = new ArrayList<>(); //to track items that are expanded/shrinked

    public ExpandableCategoryRecyclerViewAdapter(Context context, ArrayList<KcpCategories> kcpCategories) {
        mContext = context;
        mKcpCategoriesList = kcpCategories;
        mLevelOneCategoriesList = new ArrayList<>(kcpCategories);
    }

    public void updateData(ArrayList<KcpCategories> kcpContentPages) {
        mKcpCategoriesList.clear();
        mExpandedExternalCode.clear();
        mKcpCategoriesList.addAll(kcpContentPages);
        mLevelOneCategoriesList = new ArrayList<>(kcpContentPages);
        notifyDataSetChanged();
    }

    public void insertItems(ArrayList<KcpCategories> kcpContentPages, String externalCode, int position) {
        position = findTruePosition(externalCode);
        mKcpCategoriesList.addAll(position + 1, kcpContentPages);
        this.notifyItemRangeInserted(position + 1, kcpContentPages.size());
        mExpandedExternalCode.add(externalCode);
    }

    public void removeItems(String externalCode, int position) {
        position = findTruePosition(externalCode);
        ArrayList<KcpCategories> kcpCategories = KcpCategoryRoot.getInstance().getSubcategories(externalCode);
        if(kcpCategories != null && kcpCategories.size() > 0){
            for(int i = position + kcpCategories.size(); i > position ; i--){
                mKcpCategoriesList.remove(i);
            }
            this.notifyItemRangeRemoved(position + 1, kcpCategories.size());
        }
        mExpandedExternalCode.remove(externalCode);
    }

    public int findTruePosition(String externalCode){
        for(int i = 0; i < mKcpCategoriesList.size(); i++){
            if(mKcpCategoriesList.get(i).getExternalCode().equals(externalCode)) return i;
        }
        return 0;
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

    public class SubCategoryHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView  tvCategory;

        public SubCategoryHolder(View v) {
            super(v);
            mView = v;
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
                    if(mExpandedExternalCode.contains(externalCode)){ //expanded - shrink it
                        removeItems(externalCode, position);
                    } else { //shirkned - expand it
                        String subCategoriesUrl = kcpCategory.getSubCategoriesLink();
                        if(!subCategoriesUrl.equals("")){
                            DirectoryFragment.getInstance().tryDownloadSubCategories(mContext, externalCode, categoryName, subCategoriesUrl, position, categoryHolder.tvCategory);
                        }
                    }
                    // NOTE : position is from the original list
//                    Toast.makeText(mContext, "clicked : " + position, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_SUB_CAT) {
            final CategoryHolder categoryHolder = (CategoryHolder) holder;
            final KcpCategories subKcpCategory = mKcpCategoriesList.get(position);

            final String externalCode = subKcpCategory.getExternalCode();
            final String categoryName = subKcpCategory.getCategoryName();
            categoryHolder.tvCategory.setText(categoryName);

            categoryHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String placeUrl = subKcpCategory.getPlacesLink();
                    if(!placeUrl.equals("")){
                        DirectoryFragment.getInstance().tryDownloadPlaces(mContext, categoryName, externalCode, placeUrl, categoryHolder.tvCategory);
                    }
                    // NOTE : position is from the current list
//                    Toast.makeText(mContext, "clicked : " + position, Toast.LENGTH_SHORT).show();
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
        if(mLevelOneCategoriesList.contains(mKcpCategoriesList.get(position))) return KcpContentTypeFactory.PREF_ITEM_TYPE_CAT;
        else return KcpContentTypeFactory.PREF_ITEM_TYPE_SUB_CAT;
    }
}
