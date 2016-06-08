package com.kineticcafe.kcpmall.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.SubCategoryActivity;
import com.kineticcafe.kcpmall.adapters.HomeTopViewPagerAdapter;
import com.kineticcafe.kcpmall.kcpData.KcpCategoryManager;

import java.util.ArrayList;

public class DirectoryFragment extends BaseFragment {

    private CategoriesFragment mCategoriesFragment;
    private StoresFragment mStoresFragment;

    private static DirectoryFragment sDirectoryFragment;
    public static DirectoryFragment getInstance(){
        if(sDirectoryFragment == null) sDirectoryFragment = new DirectoryFragment();
        return sDirectoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDirectoryData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager vpHome = (ViewPager) view.findViewById(R.id.vpHome);
        setupViewPager(vpHome);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.tlHome);
        tablayout.setupWithViewPager(vpHome);

        updateAdapter();
        return view;
    }

    public void initializeDirectoryData(){
        downloadCategories();
    }

    private void downloadCategories(){
        KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(getActivity(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_failed);
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_completed);
                        updateAdapter();
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpCategoryManager.downloadCategories();
    }

    public void expandLevelOnCategory(String externalCode, String categoryName, int position, View view) {
        //EXPANDING
//        ArrayList<KcpCategories> kcpCategories = KcpCategoryRoot.getInstance().getSubcategories(externalCode);
//        if(kcpCategories != null) mCategoriesFragment.mExpandableCategoryRecyclerViewAdapter.insertItems(kcpCategories, externalCode, position);

        //CREATING SEPARATE ACTIVITY
        Intent intent = new Intent(getActivity(), SubCategoryActivity.class);
        intent.putExtra(Constants.ARG_CATEGORY_ACTIVITY_TYPE, Constants.CategoryActivityType.SUBCATEGORY.toString());
        intent.putExtra(Constants.ARG_EXTERNAL_CODE, externalCode);
        intent.putExtra(Constants.ARG_CAT_NAME, categoryName);

        String transitionCatName = getActivity().getResources().getString(R.string.transition_category_name);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                Pair.create(view, transitionCatName));

        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    public void tryDownloadSubCategories(final Context context, final String externalCode, final String categoryName, String url, final int position, final View view){
        if(KcpCategoryRoot.getInstance().getSubcategories(externalCode) == null){
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(getActivity(), new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    switch (inputMessage.arg1) {
                        case KcpCategoryManager.DOWNLOAD_FAILED:
                            break;
                        case KcpCategoryManager.DOWNLOAD_COMPLETE:
                            expandLevelOnCategory(externalCode, categoryName, position, view);
                            break;
                        default:
                            super.handleMessage(inputMessage);
                    }
                }
            });
            kcpCategoryManager.downloadSubCategories(externalCode, url, true);
        } else {
            if(KcpCategoryRoot.getInstance().getSubcategories(externalCode).size() == 0) {
                ArrayList<KcpCategories> kcpCategories = KcpCategoryRoot.getInstance().getCategoriesList();
                for(KcpCategories kcpCategory : kcpCategories) {
                    if(kcpCategory.getExternalCode().equals(externalCode)){
                        String placeUrl = kcpCategory.getPlacesLink();
                        if(!placeUrl.equals("")){
                            DirectoryFragment.getInstance().tryDownloadPlaces(context, categoryName, externalCode, placeUrl, view);
                        }
                    }
                }
            } else {
                expandLevelOnCategory(externalCode, categoryName, position, view);
            }
        }
    }

    public void tryDownloadPlaces(final Context context, final String categoryName, final String externalCode, String url, final View view){
        if(KcpCategoryRoot.getInstance().getPlaces(externalCode) == null){
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(getActivity(), new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    switch (inputMessage.arg1) {
                        case KcpCategoryManager.DOWNLOAD_FAILED:
                            break;
                        case KcpCategoryManager.DOWNLOAD_COMPLETE:
                            showStores(context, externalCode, categoryName, view);
                            break;
                        default:
                            super.handleMessage(inputMessage);
                    }
                }
            });
            kcpCategoryManager.downloadPlaces(externalCode, url);
        } else {
            showStores(context, externalCode, categoryName, view);
        }
    }

    public void showStores(Context context, String externalCode, String categoryName, View view) {
        //FRAGMENT WAY
        /*FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        CategoryStoreFragment articleFrag = (CategoryStoreFragment)
                fragmentManager.findFragmentByTag(getResources().getString(R.string.fragment_category_stores));

        if (articleFrag == null) {
            articleFrag = CategoryStoreFragment.newInstance(externalCode, categoryName);
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.flContents, articleFrag, getResources().getString(R.string.fragment_category_stores));
        fragmentTransaction.addToBackStack(getResources().getString(R.string.fragment_category_stores));
        fragmentTransaction.commit();*/


        //SEPARATE ACTIVITY
        Intent intent = new Intent(context, SubCategoryActivity.class);
        intent.putExtra(Constants.ARG_CATEGORY_ACTIVITY_TYPE, Constants.CategoryActivityType.STORE.toString());
        intent.putExtra(Constants.ARG_EXTERNAL_CODE, externalCode);
        intent.putExtra(Constants.ARG_CAT_NAME, categoryName);

        String transitionCatName = context.getResources().getString(R.string.transition_category_name);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity)context,
                Pair.create(view, transitionCatName));

        ActivityCompat.startActivity(((Activity)context), intent, options.toBundle());
        ((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupViewPager(ViewPager viewPager) {
        HomeTopViewPagerAdapter homeTopViewPagerAdapter = new HomeTopViewPagerAdapter(getChildFragmentManager());
        if(mCategoriesFragment == null) mCategoriesFragment = CategoriesFragment.newInstance();
        if(mStoresFragment == null) mStoresFragment = StoresFragment.newInstance();
        homeTopViewPagerAdapter.addFrag(mCategoriesFragment, getResources().getString(R.string.fragment_categories));
        homeTopViewPagerAdapter.addFrag(mStoresFragment, getResources().getString(R.string.fragment_stores));
        viewPager.setAdapter(homeTopViewPagerAdapter);
    }

    private void updateAdapter(){
        try {
//            if(mCategoriesFragment != null && mCategoriesFragment.mExpandableCategoryRecyclerViewAdapter != null) mCategoriesFragment.mExpandableCategoryRecyclerViewAdapter.updateData(KcpCategoryRoot.getInstance().getCategoriesList());
            if(mCategoriesFragment != null && mCategoriesFragment.mCategoryRecyclerViewAdapter != null) mCategoriesFragment.mCategoryRecyclerViewAdapter.updateData(KcpCategoryRoot.getInstance().getCategoriesList());
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /*OnCategoryClickListener mCallback;
    public interface OnCategoryClickListener {
        public void onCategorySelected(String externalCode, String categoryName);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCategoryClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnHeadlineSelectedListener");
        }
    }*/

    @Override
    public void onResume() {
        super.onResume();
    }
}
