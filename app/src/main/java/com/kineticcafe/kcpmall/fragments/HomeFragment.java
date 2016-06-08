package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.utils.Utility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.adapters.HomeTopViewPagerAdapter;
import com.kineticcafe.kcpmall.instagram.model.InstagramFeed;
import com.kineticcafe.kcpmall.instagram.model.Media;
import com.kineticcafe.kcpmall.instagram.model.Recent;
import com.kineticcafe.kcpmall.kcpData.KcpCategoryManager;
import com.kineticcafe.kcpmall.kcpData.KcpNavigationRootManager;
import com.kineticcafe.kcpmall.kcpData.KcpSocialFeedManager;
import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment {

    private NewsFragment mNewsFragment;
    private DealsFragment mDealsFragment;
    private KcpNavigationRootManager mKcpNavigationRootManager;
    private KcpSocialFeedManager mKcpSocialFeedManager;

    public static ArrayList<TwitterTweet> sTwitterFeedList = new ArrayList<>();
    public static ArrayList<InstagramFeed> sInstaFeedList = new ArrayList<>();


    private static HomeFragment sHomeFragment;
    public static HomeFragment getInstance(){
        if(sHomeFragment == null) sHomeFragment = new HomeFragment();
        return sHomeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeHomeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager vpHome = (ViewPager) view.findViewById(R.id.vpHome);
        setupViewPager(vpHome);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.tlHome);
        tablayout.setupWithViewPager(vpHome);

        updateAdapter(Constants.EXTERNAL_CODE_FEED);
        updateAdapter(Constants.EXTERNAL_CODE_DEAL);
        updateAdapter(Constants.EXTERNAL_CODE_RECOMMENDED);
        if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null ) mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
        if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null) mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateInstaData(sInstaFeedList);

        return view;
    }

    public void initializeHomeData(){
        if(!Utility.isNetworkAvailable(getActivity())){
            mMainActivity.onDataDownloaded(); //TODO: error here when offline
            mMainActivity.showSnackBar(R.string.warning_no_internet_connection, R.string.warning_retry, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initializeHomeData();
                }
            });
            return;
        } else {
            downloadNewsAndDeal();
            downloadSocialFeeds();
            downloadFingerPrintingCategories();
        }
    }

    public void downloadNewsAndDeal(){

        try {
            if(mNewsFragment != null) mNewsFragment.setEmptyState(null);
            if(mDealsFragment != null) mDealsFragment.setEmptyState(null);

            if(mKcpNavigationRootManager == null) {
                mKcpNavigationRootManager = new KcpNavigationRootManager(getActivity(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        switch (inputMessage.arg1) {
                            case KcpNavigationRootManager.DOWNLOAD_STARTED:
                                break;
                            case KcpNavigationRootManager.DOWNLOAD_COMPLETE:
                                if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_completed);
                                String mode = (String) inputMessage.obj;
                                updateAdapter(mode);
                                break;
                            case KcpNavigationRootManager.DATA_ADDED:
                                mNewsFragment.mNewsRecyclerViewAdapter.addData(mKcpNavigationRootManager.getKcpContentPageList());
                                mNewsFragment.mEndlessRecyclerViewScrollListener.onLoadDone();
                                break;
                            case KcpNavigationRootManager.TASK_COMPLETE:
                                break;
                            case KcpNavigationRootManager.DOWNLOAD_FAILED:
                                mMainActivity.onDataDownloaded();
                                if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_failed);
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
            }
            mKcpNavigationRootManager.downloadNewsAndDeal();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void downloadSocialFeeds(){
        if(mKcpSocialFeedManager == null){
            mKcpSocialFeedManager = new KcpSocialFeedManager(getActivity(), new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    switch (inputMessage.arg1) {
                        case KcpSocialFeedManager.DOWNLOAD_FAILED:
                            break;
                        case KcpSocialFeedManager.DOWNLOAD_TWITTER_COMPLETE:
                            sTwitterFeedList.clear();
                            sTwitterFeedList.addAll(mKcpSocialFeedManager.getTwitterTweets());
                            if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                                    mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null ) {
                                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
                            } else {
                            }
                            break;
                        case KcpSocialFeedManager.DOWNLOAD_INSTAGRAM_COMPLETE:

                            Recent recent = mKcpSocialFeedManager.getRecent();
                            int mediaSize = recent.getMediaList().size();

                            sInstaFeedList.clear();
                            for (int i = 0; i < mediaSize; i++) {
                                Media media = recent.getMediaList().get(i);
                                sInstaFeedList.add(new InstagramFeed(media.getImages().getStandardResolution().getUrl()));
                            }
                            if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                                    mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null) {
                                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateInstaData(sInstaFeedList);
                            } else {
                            }

                            break;

                        default:
                            super.handleMessage(inputMessage);
                    }
                }
            });
        }
        mKcpSocialFeedManager.downloadTwitterTweets();
        mKcpSocialFeedManager.downloadInstagram();
    }

    private void downloadFingerPrintingCategories(){
        KcpCategoryManager kcpCategoryManager = new KcpCategoryManager(getActivity(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        break;

                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpCategoryManager.downloadFingerPrintingCategories();
    }




    private void setupViewPager(ViewPager viewPager) {
        HomeTopViewPagerAdapter homeTopViewPagerAdapter = new HomeTopViewPagerAdapter(getChildFragmentManager());
        if(mNewsFragment == null) mNewsFragment = NewsFragment.newInstance();
        if(mDealsFragment == null) mDealsFragment = DealsFragment.newInstance(2);
        homeTopViewPagerAdapter.addFrag(mNewsFragment, getResources().getString(R.string.fragment_news));
        homeTopViewPagerAdapter.addFrag(mDealsFragment, getResources().getString(R.string.fragment_deals));
        viewPager.setAdapter(homeTopViewPagerAdapter);
    }

    public void downloadMoreFeeds(String navigationPageType){
        try {
            KcpNavigationPage kcpNavigationPage = KcpNavigationRoot.getInstance().getNavigationpage(navigationPageType);
            String nextUrl = kcpNavigationPage.getNextContentPageUrl();
            if(!nextUrl.equals("")){
                mNewsFragment.mNewsRecyclerViewAdapter.prepareLoadingImage();
                mKcpNavigationRootManager.downloadContents(nextUrl, navigationPageType);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     *
     * @param mode determines which adapter it should update - if null, it updates all adapters
     */
    private void updateAdapter(String mode){
        try {
            KcpNavigationPage kcpNavigationPage = KcpNavigationRoot.getInstance().getNavigationpage(mode);
            if(kcpNavigationPage != null){
                if(mode.equals(Constants.EXTERNAL_CODE_FEED)) updateNewsAdapter(kcpNavigationPage.getKcpContentPageList(true));
                else if(mode.equals(Constants.EXTERNAL_CODE_DEAL)) updateOtherDealsAdapter(kcpNavigationPage.getKcpContentPageList(true));
                else if(mode.equals(Constants.EXTERNAL_CODE_RECOMMENDED)) updateRecommendedDealsAdapter(kcpNavigationPage.getKcpContentPageList(true));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void updateNewsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        if(mNewsFragment.mNewsRecyclerViewAdapter == null) return;
        mMainActivity.onDataDownloaded();
        if(kcpContentPages.size() == 0) mNewsFragment.setEmptyState(getActivity().getResources().getString(R.string.warning_empty_news));
        mNewsFragment.mNewsRecyclerViewAdapter.updateData(kcpContentPages);
        if (mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null) {
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateInstaData(sInstaFeedList);
        }
    }
    private void updateOtherDealsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        if(mDealsFragment.mDealsRecyclerViewAdapter == null) return;
        if(kcpContentPages.size() == 0) mDealsFragment.setEmptyState(getActivity().getResources().getString(R.string.warning_empty_deals));
        mDealsFragment.mDealsRecyclerViewAdapter.updateOtherDealData(kcpContentPages);
    }

    private void updateRecommendedDealsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        if(mDealsFragment.mDealsRecyclerViewAdapter == null) return;
        mDealsFragment.mDealsRecyclerViewAdapter.updateRecommendedDealData(kcpContentPages);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter(Constants.EXTERNAL_CODE_FEED);
        updateAdapter(Constants.EXTERNAL_CODE_DEAL);
        updateAdapter(Constants.EXTERNAL_CODE_RECOMMENDED);
    }

    /*private RefreshListener mOnRefreshListener;
    public void setOnRefreshListener(RefreshListener refreshListener){
        mOnRefreshListener = refreshListener;
    }
    public interface RefreshListener {
        void onRefresh(int msg);
    }*/

    @Override
    public void onPause(){
        super.onPause();
    }
}
