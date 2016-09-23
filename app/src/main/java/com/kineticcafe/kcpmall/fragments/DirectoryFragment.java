package com.kineticcafe.kcpmall.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.managers.KcpPlaceManager;
import com.kineticcafe.kcpandroidsdk.models.KcpCategories;
import com.kineticcafe.kcpandroidsdk.models.KcpCategoryRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.SubCategoryActivity;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.HomeTopViewPagerAdapter;
import com.kineticcafe.kcpmall.adapters.MallDirectoryRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.adapterHelper.SectionedLinearRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.CategoryIconFactory;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.managers.ThemeManager;
import com.kineticcafe.kcpmall.mappedin.CustomLocation;
import com.kineticcafe.kcpmall.searchIndex.IndexManager;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.mappedin.sdk.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryFragment extends BaseFragment {

    private CategoriesFragment mCategoriesFragment;
    private PlacesFragment mPlacesFragment;
    private ViewPager mViewPager;
    public MenuItem mSearchItem;
    private SearchView mSearchView;
    private RecyclerView rvMallDirectory;
    private String mSearchString = "";
    private MallDirectoryRecyclerViewAdapter mMallDirectoryRecyclerViewAdapter;

    private ArrayList<KcpPlaces> mKcpPlacesFiltered;
    private ArrayList<Integer> mPlaceByKeyword;
    private ArrayList<KcpCategories> mKcpCategories;

    private Thread mKeywordThread;
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
        rvMallDirectory = (RecyclerView) view.findViewById(R.id.rvMallDirectory);
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
        Intent intent = new Intent(context, SubCategoryActivity.class);
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
                        rvMallDirectory.setVisibility(View.INVISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mMainActivity.setActiveMallDot(false);
                        rvMallDirectory.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
    }

    private void setupRecyclerView(){

        //initialize
        if(mKeywordThread != null) mKeywordThread.interrupt();
        if(mCategoryThread != null) mCategoryThread.interrupt();
//        mPlaceByKeyword = null;
//        mKcpCategories = null;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvMallDirectory.setLayoutManager(linearLayoutManager);

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

        mKeywordThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //KEYWORD
                mPlaceByKeyword = new ArrayList<>();
                if(!mSearchString.equals("")) {
                    for (Map.Entry<String, ArrayList<Integer>> entry : IndexManager.sTagsMap.entrySet()) {
                        String keyword = entry.getKey();
                        ArrayList<Integer> value = entry.getValue();
                        if(keyword.toLowerCase().contains(mSearchString.toLowerCase())) {
                            if(mPlaceByKeyword == null) mPlaceByKeyword = new ArrayList<>();
                            mPlaceByKeyword.addAll(value);
                        }
                    }
                }
                createOrUpdateAdapter();
            }
        });
        mKeywordThread.start();

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
                            categoryByName.addAll(value);
                        }
                    }
                }

                mKcpCategories = new ArrayList<KcpCategories>();
                for(int categoryId : categoryByName) {
                    KcpCategories categoryFound = KcpCategoryRoot.getInstance().getCategoryWithId(categoryId);
                    if(categoryFound != null) {
                        if(mKcpCategories == null) mKcpCategories = new ArrayList<KcpCategories>();
                        mKcpCategories.add(categoryFound);
                    }
                }
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
//                if(mMallDirectoryRecyclerViewAdapter == null) {
                    mMallDirectoryRecyclerViewAdapter = new MallDirectoryRecyclerViewAdapter(getActivity(), mKcpPlacesFiltered, mPlaceByKeyword, mKcpCategories, mSearchString);
//                    mMallDirectoryRecyclerViewAdapter = new MallDirectoryRecyclerViewAdapter(getActivity(), new ArrayList<KcpPlaces>(), mPlaceByKeyword, mKcpCategories, mSearchString); //testing
                    rvMallDirectory.setAdapter(mMallDirectoryRecyclerViewAdapter);
//                } else {
//                    mMallDirectoryRecyclerViewAdapter.updateData(mKcpPlacesFiltered, mPlaceByKeyword, mKcpCategories, mSearchString);
//                    mMallDirectoryRecyclerViewAdapter.updateData(new ArrayList<KcpPlaces>(), mPlaceByKeyword, mKcpCategories, mSearchString);
//                }
            }
        });
    }

}
