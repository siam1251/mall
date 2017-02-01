package com.ivanhoecambridge.mall.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpPlaceManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategories;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpCategoryRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.SubCategoryActivity;
import com.ivanhoecambridge.mall.activities.SubStoreActivity;
import com.ivanhoecambridge.mall.adapters.HomeTopViewPagerAdapter;
import com.ivanhoecambridge.mall.adapters.MallDirectoryRecyclerViewAdapter;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.CategoryIconFactory;
import factory.HeaderFactory;

import com.ivanhoecambridge.mall.managers.NetworkManager;
import com.ivanhoecambridge.mall.managers.ThemeManager;
import com.ivanhoecambridge.mall.searchIndex.IndexManager;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.NewsRecyclerItemDecoration;

import java.util.ArrayList;
import java.util.Map;

public class DirectoryFragment extends BaseFragment {
    private CategoriesFragment mCategoriesFragment;
    private PlacesFragment mPlacesFragment;
    private ViewPager mViewPager;
    public MenuItem mSearchItem;
    private SearchView mSearchView;
    private String mSearchString = "";
    private MallDirectoryRecyclerViewAdapter mMallDirectoryRecyclerViewAdapter;

    private ArrayList<KcpPlaces> mKcpPlacesFiltered;
    private ArrayList<Integer> mPlaceByBrand;
    private ArrayList<Integer> mPlaceByTag;
    private ArrayList<KcpCategories> mKcpCategories;

    private Thread mBrandThread;
    private Thread mTagThread;
    private Thread mCategoryThread;


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

        mViewPager = (ViewPager) view.findViewById(R.id.vpHome);
        setupViewPager(mViewPager);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.tlHome);
        tablayout.setupWithViewPager(mViewPager);

        updateCategoryAdapter();
        setHasOptionsMenu(true);

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

    public void expandLevelOnCategory(Context context, String externalCode, String categoryName, int position, View view) {
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

        getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
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
                            if(NetworkManager.isConnected(context)) return;
                            break;
                        case KcpCategoryManager.DOWNLOAD_COMPLETE:
                            expandLevelOnCategory(context, externalCode, categoryName, position, view);
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
                expandLevelOnCategory(context, externalCode, categoryName, position, view);
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
                            if(NetworkManager.isConnected(context)) return;
                            ProgressBarWhileDownloading.showProgressDialog(getActivity(), R.layout.layout_loading_item, false);
                            break;
                        case KcpCategoryManager.DOWNLOAD_COMPLETE:
                            showStores(context, externalCode, categoryName, view);
                            ProgressBarWhileDownloading.showProgressDialog(getActivity(), R.layout.layout_loading_item, false);
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
        Intent intent = new Intent(context, SubStoreActivity.class);
        intent.putExtra(Constants.ARG_CATEGORY_ACTIVITY_TYPE, Constants.CategoryActivityType.STORE.toString());
        intent.putExtra(Constants.ARG_EXTERNAL_CODE, externalCode);
        intent.putExtra(Constants.ARG_CAT_NAME, categoryName);

        String transitionCatName = context.getResources().getString(R.string.transition_category_name);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (Activity)context,
                Pair.create(view, ""));

        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
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
                        InfoFragment.getInstance().getMallHour();
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpPlaceManager.downloadPlaces();
    }

    public void selectPage(int pageIndex){
        mViewPager.setCurrentItem(pageIndex);
    }

    public class QueryTextListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            onTextChange(s);
            return false;
        }
    }

    public void onTextChange(String s) {
        mSearchString = s.toString();
        setupRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_store, menu);

        mSearchItem = menu.findItem(R.id.action_store_search);
        mSearchItem.setIcon(ThemeManager.getThemedMenuDrawable(getActivity(), R.drawable.icn_search));
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(new QueryTextListener());
        mSearchView.setQueryHint(getString(R.string.hint_search_directory));
        mSearchItem.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setOnActionExpandListener(mSearchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mMainActivity.setActiveMallDot(true);
                        mMainActivity.rvMallDirectory.setVisibility(View.INVISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mMainActivity.setActiveMallDot(false);
                        mMainActivity.rvMallDirectory.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

        NewsRecyclerItemDecoration itemDecoration = new NewsRecyclerItemDecoration(getActivity(), R.dimen.card_horizontal_margin);
        mMainActivity.rvMallDirectory.addItemDecoration(itemDecoration);
    }

    private void setupRecyclerView(){
        //initialize
        if(mBrandThread != null) mBrandThread.interrupt();
        if(mTagThread != null) mTagThread.interrupt();
        if(mCategoryThread != null) mCategoryThread.interrupt();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mMainActivity.rvMallDirectory.setLayoutManager(linearLayoutManager);

        //PLACE
        ArrayList<KcpPlaces> kcpPlaces = KcpPlacesRoot.getInstance().getPlacesList(KcpPlaces.PLACE_TYPE_STORE);
        if(mSearchString.equals("")) mKcpPlacesFiltered = new ArrayList<>();
        else {
            mKcpPlacesFiltered = new ArrayList<>();
            for(int i = 0; i < kcpPlaces.size(); i++){
                if(kcpPlaces.get(i).getPlaceName().toLowerCase().contains(mSearchString.toLowerCase())) {
                    mKcpPlacesFiltered.add(kcpPlaces.get(i));
                }
            }
        }

        mBrandThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //KEYWORD
                mPlaceByBrand = new ArrayList<>();
                if(!mSearchString.equals("")) {
                    for (Map.Entry<String, ArrayList<Integer>> entry : IndexManager.sBrandsMap.entrySet()) {
                        String keyword = entry.getKey();
                        ArrayList<Integer> value = entry.getValue();
                        if(keyword.toLowerCase().contains(mSearchString.toLowerCase())) {
                            if(mPlaceByBrand == null) mPlaceByBrand = new ArrayList<>();
                            mPlaceByBrand.removeAll(value);
                            mPlaceByBrand.addAll(value);
                        }
                    }
                }
                sortKcpPlaces(mPlaceByBrand);
                createOrUpdateAdapter();
            }
        });
        mBrandThread.start();


        mTagThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //KEYWORD
                mPlaceByTag = new ArrayList<>();
                if(!mSearchString.equals("")) {
                    for (Map.Entry<String, ArrayList<Integer>> entry : IndexManager.sTagsMap.entrySet()) {
                        String keyword = entry.getKey();
                        ArrayList<Integer> value = entry.getValue();
                        if(keyword.toLowerCase().contains(mSearchString.toLowerCase())) {
                            if(mPlaceByTag == null) mPlaceByTag = new ArrayList<>();
                            mPlaceByTag.removeAll(value);
                            mPlaceByTag.addAll(value);
                        }
                    }
                }
                sortKcpPlaces(mPlaceByTag);
                createOrUpdateAdapter();
            }
        });
        mTagThread.start();

        mCategoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //CATEGORY
                ArrayList<Integer> categoryByName = new ArrayList<>();
                if(mSearchString.equals("")) categoryByName = new ArrayList<>();
                else {
                    for (Map.Entry<String, ArrayList<Integer>> entry : IndexManager.sCategoriesMap.entrySet()) {
                        String keyword = entry.getKey();
                        ArrayList<Integer> value = entry.getValue();
                        if(keyword.toLowerCase().contains(mSearchString.toLowerCase())) {
                            categoryByName.removeAll(value);
                            categoryByName.addAll(value);
                        }
                    }
                }

                //Making sure the categories exist within the kcp data
                mKcpCategories = new ArrayList<KcpCategories>();
                for(int categoryId : categoryByName) {
                    KcpCategories categoryFound = KcpCategoryRoot.getInstance().getCategoryWithId(categoryId);
                    if(categoryFound != null) {
                        if(mKcpCategories == null) mKcpCategories = new ArrayList<KcpCategories>();
                        mKcpCategories.add(categoryFound);
                    }
                }
                sortKcpCategories(mKcpCategories);
                createOrUpdateAdapter();
            }
        });
        mCategoryThread.start();
        createOrUpdateAdapter();
    }

    private void createOrUpdateAdapter(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    mMallDirectoryRecyclerViewAdapter = new MallDirectoryRecyclerViewAdapter(getActivity(), mKcpPlacesFiltered, mPlaceByBrand, mPlaceByTag, mKcpCategories, mSearchString);
                    mMainActivity.rvMallDirectory.setAdapter(mMallDirectoryRecyclerViewAdapter);
            }
        });
    }

    private void sortKcpPlaces(ArrayList<Integer> listToSort){
        KcpUtility.sortPlaceListById(listToSort);
    }

    private void sortKcpCategories(ArrayList<KcpCategories> listToSort){
        KcpUtility.sortCategoryListById(listToSort);
    }
}