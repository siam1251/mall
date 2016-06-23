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
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.managers.KcpPlaceManager;
import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.SubCategoryActivity;
import com.kineticcafe.kcpmall.adapters.HomeTopViewPagerAdapter;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.views.ActivityAnimation;

import java.util.ArrayList;

public class DirectoryFragment extends BaseFragment {

    private CategoriesFragment mCategoriesFragment;
    private PlacesFragment mPlacesFragment;

    private static DirectoryFragment sDirectoryFragment;
    public static DirectoryFragment getInstance(){
        if(sDirectoryFragment == null) sDirectoryFragment = new DirectoryFragment();
        return sDirectoryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager vpHome = (ViewPager) view.findViewById(R.id.vpHome);
        setupViewPager(vpHome);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.tlHome);
        tablayout.setupWithViewPager(vpHome);

        updateCategoryAdapter();

        return view;
    }

    public void initializeDirectoryData(){
        if(getActivity() == null){
            setOnFragmentInteractionListener(new OnFragmentInteractionListener() {
                @Override
                public void onFragmentInteraction() {
                    downloadCategories();
                    downloadPlaces();
                }
            });
        } else {
            downloadCategories();
            downloadPlaces();
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        HomeTopViewPagerAdapter homeTopViewPagerAdapter = new HomeTopViewPagerAdapter(getChildFragmentManager());
        if(mCategoriesFragment == null) mCategoriesFragment = CategoriesFragment.newInstance();
        if(mPlacesFragment == null) mPlacesFragment = mPlacesFragment.newInstance();
        homeTopViewPagerAdapter.addFrag(mCategoriesFragment, getResources().getString(R.string.fragment_categories));
        homeTopViewPagerAdapter.addFrag(mPlacesFragment, getResources().getString(R.string.fragment_stores));
        viewPager.setAdapter(homeTopViewPagerAdapter);
    }

    private void updateCategoryAdapter(){
        try {
            if(mCategoriesFragment != null && mCategoriesFragment.mCategoryRecyclerViewAdapter != null) mCategoriesFragment.mCategoryRecyclerViewAdapter.updateData(CategoryIconFactory.getFilteredKcpCategoryList(KcpCategoryRoot.getInstance().getCategoriesList()));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void updatePlacesAdapter(){
        try {
//            if(mPlacesFragment != null && mPlacesFragment.mPlaceRecyclerViewAdapter != null) mPlacesFragment.mPlaceRecyclerViewAdapter.updateData(KcpPlacesRoot.getInstance().getPlacesList());
            if(mPlacesFragment != null && mPlacesFragment.mPlaceRecyclerViewAdapter != null) mPlacesFragment.setupRecyclerView();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void downloadCategories(){
        KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(getActivity(), R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_failed);
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_completed);
                        updateCategoryAdapter();
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
                Pair.create(view, ""));

//        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
        getActivity().startActivity(intent);
        ActivityAnimation.startActivityAnimation(getActivity());
    }

    public void tryDownloadSubCategories(final Context context, final String externalCode, final String categoryName, String url, final int position, final View view) {
        //sub categories never downloaded - not likely happen as it should be done previously when categories were downloaded
        if(KcpCategoryRoot.getInstance().getSubcategories(externalCode) == null){
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(getActivity(), R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
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
            //sub categories are downloaded but its size is 0 - open the store list
            if(KcpCategoryRoot.getInstance().getSubcategories(externalCode).size() == 0) {
                ArrayList<KcpCategories> kcpCategories = KcpCategoryRoot.getInstance().getCategoriesList();
                for(KcpCategories kcpCategory : kcpCategories) {
                    if(kcpCategory.getExternalCode().equals(externalCode)){
                        String placeUrl = kcpCategory.getPlacesLink();
                        if(!placeUrl.equals("")){
                            DirectoryFragment.getInstance().tryDownloadPlacesForThisCategory(context, categoryName, externalCode, placeUrl, view);
                        }
                    }
                }
            } else {
                expandLevelOnCategory(externalCode, categoryName, position, view);
            }
        }
    }

    public void tryDownloadPlacesForThisCategory(final Context context, final String categoryName, final String externalCode, String url, final View view){
        if(KcpCategoryRoot.getInstance().getPlaces(externalCode) == null){
            KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(context, R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
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
            kcpCategoryManager.downloadPlacesForThisCategoryExternalId(externalCode, url);
        } else {
            showStores(context, externalCode, categoryName, view);
        }
    }

    public void showStores(Context context, String externalCode, String categoryName, View view) {
        //SEPARATE ACTIVITY
        Intent intent = new Intent(context, SubCategoryActivity.class);
        intent.putExtra(Constants.ARG_CATEGORY_ACTIVITY_TYPE, Constants.CategoryActivityType.STORE.toString());
        intent.putExtra(Constants.ARG_EXTERNAL_CODE, externalCode);
        intent.putExtra(Constants.ARG_CAT_NAME, categoryName);

        String transitionCatName = context.getResources().getString(R.string.transition_category_name);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity)context,
                Pair.create(view, ""));

        context.startActivity(intent);
        ActivityAnimation.startActivityAnimation(context);
    }


    public void downloadPlaces(){
        KcpPlaceManager kcpPlaceManager = new KcpPlaceManager(getActivity(), R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_failed);
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_completed);
                        updatePlacesAdapter();
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpPlaceManager.downloadPlaces();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
