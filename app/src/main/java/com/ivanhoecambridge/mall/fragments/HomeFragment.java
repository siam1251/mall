package com.ivanhoecambridge.mall.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivanhoecambridge.kcpandroidsdk.instagram.model.InstagramFeed;
import com.ivanhoecambridge.kcpandroidsdk.instagram.model.Media;
import com.ivanhoecambridge.kcpandroidsdk.instagram.model.Recent;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpNavigationRootManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpSocialFeedManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.twitter.model.TwitterTweet;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.adapters.HomeTopViewPagerAdapter;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.fragments.presenters.HomePresenter;
import com.ivanhoecambridge.mall.fragments.uiviews.HomeView;
import com.ivanhoecambridge.mall.interfaces.ViewPagerListener;
import com.ivanhoecambridge.mall.mappedin.Amenities;

import java.util.ArrayList;

import constants.MallConstants;

public class HomeFragment extends BaseFragment implements ViewPagerListener, HomeView{


    public interface UserProfileLikesListener {
        void onProfileDataUpdated();
    }

    private HomePresenter            homePresenter;
    private UserProfileLikesListener userProfileLikesListener;


    public final static int VIEWPAGER_PAGE_NEWS = 0;
    public final static int VIEWPAGER_PAGE_DEALS = 1;

    private final static String SCREEN_NAME = "HOME - ";
    private KcpNavigationRootManager mKcpNavigationRootManager;
    private NewsFragment mNewsFragment;
    private DealsFragment mDealsFragment;
    private KcpSocialFeedManager mKcpSocialFeedManager;
    private ViewPager mViewPager;
    private int mViewPageToLoad = -1;
    private int mCurrentTab = VIEWPAGER_PAGE_NEWS;

    public static ArrayList<TwitterTweet> sTwitterFeedList = new ArrayList<>();
    public static ArrayList<InstagramFeed> sInstaFeedList = new ArrayList<>();

    private static HomeFragment sHomeFragment;
    public static HomeFragment getInstance(){
        if(sHomeFragment == null) sHomeFragment = new HomeFragment();
        return sHomeFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userProfileLikesListener = (UserProfileLikesListener) context;
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

        if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null ) mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
        if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null) mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateInstaData(sInstaFeedList);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onPageActive();
    }

    public void initializeHomeData(){
        if (homePresenter == null) {
            homePresenter = new HomePresenter(this);
            setOnFragmentInteractionListener(new OnFragmentInteractionListener() {
                @Override
                public void onFragmentInteraction() {
                    homePresenter.downloadNewData();
                }
            });
        }

        homePresenter.downloadNewData();
    }


    public void downloadNewsAndDeal(){

        try {
            if(mNewsFragment != null) mNewsFragment.setEmptyState(null);
            if(mDealsFragment != null) mDealsFragment.setEmptyState(null);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void downloadSocialFeeds(){
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
                        if(recent == null) break;
                        int mediaSize = recent.getMediaList().size();
                        sInstaFeedList.clear();
                        for (int i = 0; i < mediaSize; i++) {
                            Media media = recent.getMediaList().get(i);
                            sInstaFeedList.add(new InstagramFeed(media.getImages().getStandardResolution().getUrl(), media.getCaption().getCreatedTime(), media.getCaption().getText()));
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

        mKcpSocialFeedManager.downloadTwitterTweets(MallConstants.TWITTER_SCREEN_NAME, MallConstants.NUMB_OF_TWEETS, MallConstants.TWITTER_API_KEY, MallConstants.TWITTER_API_SECRET);
//        mKcpSocialFeedManager.downloadInstagram(Constants.INSTAGRAM_USER_NAME, Constants.INSTAGRAM_USER_ID, Constants.INSTAGRAM_ACCESS_TOKEN, Constants.INSTAGRAM_BASE_URL, Constants.NUMB_OF_INSTA); //DISABLED : IA-170
    }


    private void setupViewPager(ViewPager viewPager) {
        HomeTopViewPagerAdapter homeTopViewPagerAdapter = new HomeTopViewPagerAdapter(getChildFragmentManager());
        if(mNewsFragment == null) mNewsFragment = NewsFragment.newInstance();
        if(mDealsFragment == null) mDealsFragment = DealsFragment.newInstance(2);
        homeTopViewPagerAdapter.addFrag(mNewsFragment, getResources().getString(R.string.fragment_news));
        homeTopViewPagerAdapter.addFrag(mDealsFragment, getResources().getString(R.string.fragment_deals));
        viewPager.setAdapter(homeTopViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mCurrentTab = position;
                onPageActive();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        onViewPagerCreated();
    }

    public void downloadMoreFeeds(String navigationPageType){
        //todo check ths method out more
        if (mKcpNavigationRootManager == null) return;
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

    private void updateAdapter(String mode, ArrayList<KcpContentPage> kcpContentPages) {
        try {
            switch (mode) {
                case Constants.EXTERNAL_CODE_RECOMMENDED:
                    updateRecommendedDealsAdapter(kcpContentPages);
                    MapFragment.getInstance().onDealsClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_DEAL, false));
                    break;
                case Constants.EXTERNAL_CODE_FEED:
                    updateNewsAdapter(kcpContentPages);
                    break;
                case Constants.EXTERNAL_CODE_DEAL:
                    updateOtherDealsAdapter(kcpContentPages);
                    break;
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }



    private void updateNewsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        if(mNewsFragment.mNewsRecyclerViewAdapter == null) return;
        if(kcpContentPages.size() == 0) mNewsFragment.setEmptyState(getActivity().getResources().getString(R.string.warning_empty_news));
        mNewsFragment.mNewsRecyclerViewAdapter.updateData(kcpContentPages);
        mMainActivity.onDataDownloaded();

        if (mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null) {
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateInstaData(sInstaFeedList);
        }
    }

    private void updateOtherDealsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        if(mDealsFragment.mDealsRecyclerViewAdapter == null) return;
        if(kcpContentPages.size() == 0) {
            mDealsFragment.setEmptyState(getActivity().getResources().getString(R.string.warning_empty_deals));
        }
        KcpUtility.sortKcpContentpageByExpiryDate(kcpContentPages);
        mDealsFragment.mDealsRecyclerViewAdapter.updateOtherDealData(kcpContentPages);
    }

    private void updateRecommendedDealsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        if(mDealsFragment.mDealsRecyclerViewAdapter == null) return;
        mDealsFragment.mDealsRecyclerViewAdapter.updateRecommendedDealData(kcpContentPages);
    }

    public void selectPage(int pageIndex){
        if(mViewPager == null)  mViewPageToLoad = pageIndex;
        else mViewPager.setCurrentItem(pageIndex);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (homePresenter != null) {
            homePresenter.refreshAdapterData();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onViewPagerCreated() {
        if(mViewPageToLoad != -1) mViewPager.setCurrentItem(mViewPageToLoad);
        mViewPageToLoad = -1;
    }

    @Override
    public void onPageActive() {
        String screenTab;
        switch (mCurrentTab) {
            case VIEWPAGER_PAGE_NEWS:
            default:
                screenTab = "News Tab";
                break;
            case VIEWPAGER_PAGE_DEALS:
                screenTab = "Deals Tab";
                break;
        }
        Analytics.getInstance(getContext()).logScreenView(getActivity(), SCREEN_NAME + screenTab);
    }

    @Override
    public void setProgressIndicator(boolean isShowing) {

    }


    @Override
    public void onAdapterDataRefresh(String mode, ArrayList<KcpContentPage> kcpContentPages) {
        updateAdapter(mode, kcpContentPages);
    }

    @Override
    public void onNewsAndDealsUpdated(boolean isAddedData, String mode, ArrayList<KcpContentPage> kcpContentPages) {
        if (!isAddedData) {
            updateAdapter(mode, kcpContentPages);
        } else {
            mNewsFragment.mNewsRecyclerViewAdapter.addData(kcpContentPages);
            mNewsFragment.mEndlessRecyclerViewScrollListener.onLoadDone();
        }
    }

    @Override
    public void onSocialFeedUpdated(KcpSocialFeedManager kcpSocialFeedManager, int socialFeedType) {
        switch (socialFeedType) {
            case HomePresenter.SOCIAL_FEED_TWITTER:
                updateTwitterFeedAdapter(kcpSocialFeedManager.getTwitterTweets());
                break;
            case HomePresenter.SOCIAL_FEED_IG:
                break;
        }

    }

    private void updateTwitterFeedAdapter(ArrayList<TwitterTweet> tweets) {
        sTwitterFeedList.addAll(tweets);
        if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null ) {
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
        }
    }

    @Override
    public void updateProfileData() {
        if (userProfileLikesListener != null) {
            userProfileLikesListener.onProfileDataUpdated();
        }
    }

    @Override
    public void onAllDataDownloadSuccess() {
        mMainActivity.mIsDataLoaded = true;
        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_completed);
    }

    @Override
    public void onDataDownloadFailure(int failedOn) {
        if (failedOn == HomePresenter.NEWS_DEALS) {
            mMainActivity.mIsDataLoaded = false;
            mMainActivity.onDataDownloaded();
            if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_failed);
        }
    }
}
