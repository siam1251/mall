package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpandroidsdk.utils.Utility;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.instagram.InstagramService;
import com.kineticcafe.kcpmall.instagram.model.Media;
import com.kineticcafe.kcpmall.instagram.model.Recent;
import com.kineticcafe.kcpmall.kcpData.KcpService;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.adapters.HomeTopViewPagerAdapter;
import com.kineticcafe.kcpmall.instagram.model.InstagramFeed;
import com.kineticcafe.kcpmall.twitter.TwitterAsyncTask;
import com.kineticcafe.kcpmall.twitter.model.TwitterTweet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {

    private NewsFragment mNewsFragment;
    private DealsFragment mDealsFragment;
    private KcpService mKcpService;
    /*public static ArrayList<TwitterTweet> mTwitterTweets = new ArrayList<>();
    public static ArrayList<InstagramFeed> mInstagramFeeds = new ArrayList<>();*/

    public static ArrayList<TwitterTweet> sTwitterFeedList = new ArrayList<>();
    public static ArrayList<InstagramFeed> sInstaFeedList = new ArrayList<>();

    /*private ArrayList<TwitterTweet> mTwitterTweets = new ArrayList<>();
    private ArrayList<InstagramFeed> mInstagramFeeds = new ArrayList<>();*/

    private static HomeFragment sHomeFragment;
    public static HomeFragment getInstance(){
        if(sHomeFragment == null) sHomeFragment = new HomeFragment();
        return sHomeFragment;
    }

    private View.OnClickListener mDownloadDealOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            downloadNewsAndDeal();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utility.isNetworkAvailable(getActivity())){
            downloadNewsAndDeal();
        } else {
            mMainActivity.onDataDownloaded(); //TODO: error here when offline
            mMainActivity.showSnackBar(R.string.warning_no_internet_connection, R.string.warning_retry,  mDownloadDealOnClickListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ViewPager vpHome = (ViewPager) view.findViewById(R.id.vpHome);
        setupViewPager(vpHome);

        TabLayout tablayout = (TabLayout) view.findViewById(R.id.tlHome);
        tablayout.setupWithViewPager(vpHome);
        return view;
    }


    private void setupViewPager(ViewPager viewPager) {
        HomeTopViewPagerAdapter homeTopViewPagerAdapter = new HomeTopViewPagerAdapter(getChildFragmentManager());
        if(mNewsFragment == null) mNewsFragment = NewsFragment.newInstance();
        if(mDealsFragment == null) mDealsFragment = DealsFragment.newInstance(2);
        homeTopViewPagerAdapter.addFrag(mNewsFragment, "NEWS");
        homeTopViewPagerAdapter.addFrag(mDealsFragment, "DEALS");
        viewPager.setAdapter(homeTopViewPagerAdapter);
    }


    public void downloadNewsAndDeal(){
        downloadTwitterTweets();
        downloadInstagram();

        mKcpService = ServiceFactory.createRetrofitService(getActivity(), new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        Call<KcpNavigationRoot> call = mKcpService.getNavigationRoot(Constants.URL_NAVIGATION_ROOT);
        call.enqueue(new Callback<KcpNavigationRoot>() {
            @Override
            public void onResponse(Call<KcpNavigationRoot> call, Response<KcpNavigationRoot> response) {
                if(response.isSuccessful()){
                    KcpNavigationRoot kcpNavigationRoot = KcpNavigationRoot.getInstance();
                    kcpNavigationRoot = response.body();
                    for(int i = 0; i < kcpNavigationRoot.getNavigationPageListSize(); i++){
                        KcpNavigationPage kcpNavigationPage = kcpNavigationRoot.getNavigationPage(i);
                        String fullHref = kcpNavigationPage.getFullLink();
                        String mode = kcpNavigationPage.getMode();
                        downloadNavigationPage(fullHref, mode);
                    }
                }
            }

            @Override
            public void onFailure(Call<KcpNavigationRoot> call, Throwable t) {
                mMainActivity.onDataDownloaded();
                mMainActivity.showSnackBar(R.string.warning_no_internet_connection, R.string.warning_retry,  mDownloadDealOnClickListener);
            }
        });
    }

    private void downloadNavigationPage(String url, final String mode){

        Call<KcpNavigationPage> call = mKcpService.getNavigationPage(url);
        call.enqueue(new Callback<KcpNavigationPage>() {
            @Override
            public void onResponse(Call<KcpNavigationPage> call, Response<KcpNavigationPage> response) {
                if(response.isSuccessful()){
                    KcpNavigationPage kcpNavigationPageResponse = response.body();
                    KcpNavigationRoot.getInstance().setNavigationpage(mode, kcpNavigationPageResponse);
                    String fullHref = kcpNavigationPageResponse.getSelfLink() + Constants.URL_VIEW_ALL_CONTENT;
                    downloadContents(fullHref, mode);
                }
            }

            @Override
            public void onFailure(Call<KcpNavigationPage> call, Throwable t) {

            }
        });
    }

    private void downloadContents(String url, final String mode){
        Call<KcpContentPage> call = mKcpService.getContentPage(url);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if (response.isSuccessful()) {
                    try {
                        final KcpContentPage kcpContentPageResponse = response.body();
                        KcpNavigationPage kcpNavigationPage = KcpNavigationRoot.getInstance().getNavigationpage(mode);
                        boolean isDataAdded = kcpNavigationPage.setKcpContentPage(kcpContentPageResponse);
                        if(isDataAdded) {
                            mNewsFragment.mNewsRecyclerViewAdapter.addData(kcpContentPageResponse.getContentPageList());
                            mNewsFragment.mEndlessRecyclerViewScrollListener.onLoadDone();
                        } else {
                            updateAdapter(mode);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                } else {}
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                logger.error(t);
            }
        });
    }

    public void downloadMoreFeeds(String navigationPageType){
        try {
            KcpNavigationPage kcpNavigationPage = KcpNavigationRoot.getInstance().getNavigationpage(navigationPageType);
            String nextUrl = kcpNavigationPage.getNextContentPageUrl();
            if(!nextUrl.equals("")){
                mNewsFragment.mNewsRecyclerViewAdapter.prepareLoadingImage();
                downloadContents(nextUrl, navigationPageType);
            } else {
                //TODO: should stop loading image
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
        if(mOnRefreshListener != null) mOnRefreshListener.onRefresh();
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
        mMainActivity.onDataDownloaded();
        mNewsFragment.mNewsRecyclerViewAdapter.updateData(kcpContentPages);
        if (mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null) {
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateInstaData(sInstaFeedList);
        }
    }
    private void updateOtherDealsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        mDealsFragment.mDealsRecyclerViewAdapter.updateOtherDealData(kcpContentPages);
    }

    private void updateRecommendedDealsAdapter(ArrayList<KcpContentPage> kcpContentPages){
        mDealsFragment.mDealsRecyclerViewAdapter.updateRecommendedDealData(kcpContentPages);
    }

    private void downloadTwitterTweets() {
        try {
            if (getActivity() != null && Utility.isNetworkAvailable(getActivity())) {
                new TwitterAsyncTask(
                        Constants.NUMB_OF_TWEETS,
                        Constants.TWITTER_API_KEY,
                        Constants.TWITTER_API_SECRET,
                        new TwitterAsyncTask.OnTwitterFeedDownloadCompleteListener() {
                            @Override
                            public void onTwitterFeedDownloadComplete(ArrayList<TwitterTweet> twitterTweets) {
                                if(twitterTweets == null) {
                                } else {
                                    sTwitterFeedList.clear();
                                    sTwitterFeedList.addAll(twitterTweets);
                                    if (mNewsFragment.mNewsRecyclerViewAdapter != null &&
                                            mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter() != null ) {
                                        mNewsFragment.mNewsRecyclerViewAdapter.getSocialFeedViewPagerAdapter().updateTwitterData(sTwitterFeedList);
                                    } else {
                                    }
                                }
                            }
                        }).execute(Constants.TWITTER_SCREEN_NAME, 5, this);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void downloadInstagram() {
        try {
            if (getActivity() != null && Utility.isNetworkAvailable(getActivity())) {
                InstagramService instagramService = ServiceFactory.createRetrofitService(getActivity(), InstagramService.class, Constants.INSTAGRAM_BASE_URL);;
                Call<Recent> recent = instagramService.getRecentWithClientId(Constants.INSTAGRAM_USER_ID, Constants.INSTAGRAM_CLIENT_ID, Constants.NUMB_OF_INSTA, null, null, null, null);
                recent.enqueue(new Callback<Recent>() {
                    @Override
                    public void onResponse(Call<Recent> call, Response<Recent> response) {
                        if (response.isSuccessful()) {
                            Recent recent = response.body();
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
                        } else {
                            logger.debug("instagram download unsuccessful");
                        }
                    }

                    @Override
                    public void onFailure(Call<Recent> call, Throwable t) {
                        logger.error(t);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter(Constants.EXTERNAL_CODE_FEED);
        updateAdapter(Constants.EXTERNAL_CODE_DEAL);
        updateAdapter(Constants.EXTERNAL_CODE_RECOMMENDED);
    }

    private RefreshListener mOnRefreshListener;
    public void setOnRefreshListener(RefreshListener refreshListener){
        mOnRefreshListener = refreshListener;
    }

    public interface RefreshListener {
        void onRefresh();
    }

    @Override
    public void onPause(){
        super.onPause();
        mOnRefreshListener = null;
    }
}
