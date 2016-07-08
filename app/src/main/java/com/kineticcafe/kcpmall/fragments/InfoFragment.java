package com.kineticcafe.kcpmall.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.managers.KcpCategoryManager;
import com.kineticcafe.kcpandroidsdk.managers.KcpInfoManager;
import com.kineticcafe.kcpandroidsdk.managers.KcpPlaceManager;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.InfoList;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.KcpMallInfoRoot;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.MallHourActivity;
import com.kineticcafe.kcpmall.activities.MallInfoDetailActivity;
import com.kineticcafe.kcpmall.adapters.InfoRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.ActivityAnimation;

import java.util.List;

/**
 * Created by Kay on 2016-06-20.
 */
public class InfoFragment extends BaseFragment {
    private static InfoFragment sInfoFragment;
    public static InfoFragment getInstance(){
        if(sInfoFragment == null) sInfoFragment = new InfoFragment();
        return sInfoFragment;
    }


    private InfoRecyclerViewAdapter mInfoRecyclerViewAdapter;
    private boolean shouldScroll = false;
    private OnListFragmentInteractionListener mListener;
    private TextView tvInfoHoursBold;
    private TextView tvInfoHoursLight;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        final RecyclerView rvInfo = (RecyclerView) view.findViewById(R.id.rvInfo);
        setupRecyclerView(rvInfo);

        final AppBarLayout abInfo = (AppBarLayout) view.findViewById(R.id.abInfo);
        abInfo.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout abInfo, int abInfoverticalOffset) {
                shouldScroll = abInfo.getTotalScrollRange() == Math.abs(abInfoverticalOffset);
            }
        });

        rvInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //to prevent the recyclerview from scrolling before the imageview's completely collapsed
                if (!shouldScroll) {
                    rvInfo.scrollToPosition(0);
                } else super.onScrolled(recyclerView, dx, dy);
            }
        });

        RelativeLayout rlDirection = (RelativeLayout) view.findViewById(R.id.rlDirection);
        rlDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openGoogleMapWithAddress(getActivity(), HeaderFactory.MALL_NAME);
            }
        });

        RelativeLayout rlSaveMyParkingSpot = (RelativeLayout) view.findViewById(R.id.rlSaveMyParkingSpot);
        rlSaveMyParkingSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });


        ImageView ivCar = (ImageView) view.findViewById(R.id.ivCar);
        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openGoogleMapWithAddressWithDrivingMode(getActivity(), HeaderFactory.MALL_NAME);
            }
        });

        ImageView ivSubway = (ImageView) view.findViewById(R.id.ivTransit);
        ivSubway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openGoogleMapWithAddressWithTransitMode(getActivity(), HeaderFactory.MALL_NAME);
            }
        });

        ImageView ivWalk = (ImageView) view.findViewById(R.id.ivWalk);
        ivWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openGoogleMapWithAddressWithWalkingMode(getActivity(), HeaderFactory.MALL_NAME);
            }
        });


        //when recyclerview's placed under collapsed, it sometimes fails to set its height properly - last items cannot be scrolled to so manually setting the height
        final ViewTreeObserver viewTreeObserver = rvInfo.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    rvInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) rvInfo.getLayoutParams();
                    lp.height = (int) (KcpUtility.getScreenHeight(getActivity())
                            - KcpUtility.getStatusBarHeight(getActivity())
                            - getActivity().getResources().getDimension(R.dimen.abc_action_bar_default_height_material)
//                            - getResources().getDimension(R.dimen.info_collapsed_height) //decided to leave only hours section at top when scrolled down
                            - getResources().getDimension(R.dimen.info_hours_height)
                            - getResources().getDimension(R.dimen.vpMain_padding_bottom));
                    rvInfo.setLayoutParams(lp);
                }
            });
        }

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tvInfoHoursBold = (TextView) view.findViewById(R.id.tvInfoHoursBold);
        tvInfoHoursLight = (TextView) view.findViewById(R.id.tvInfoHoursLight);
        getMallHour();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getMallHour() {
        try {
            logger.debug("entered getMallHour");
            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);

            logger.debug("mall name is : " + kcpPlaces.getPlaceName());

            if(kcpPlaces != null){
                setUpMallOpenCloseStatus();
            } else {
                if(toolbar != null) toolbar.setVisibility(View.GONE);
                KcpPlaceManager kcpPlaceManager = new KcpPlaceManager(getActivity(), 0, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message inputMessage) {
                        switch (inputMessage.arg1) {
                            case KcpCategoryManager.DOWNLOAD_FAILED:
                                break;
                            case KcpCategoryManager.DOWNLOAD_COMPLETE:
                                setUpMallOpenCloseStatus();
                                break;
                            default:
                                super.handleMessage(inputMessage);
                        }
                    }
                });
                kcpPlaceManager.downloadPlaces();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void setUpMallOpenCloseStatus(){
        try {
            if(toolbar == null) return;
            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);
            toolbar.setVisibility(View.VISIBLE);
            String[] timeArray = new String[2];
            String time = kcpPlaces.getStoreHourForToday(timeArray, kcpPlacesRoot.getMallContinuousOverrides());
            if(time.equals("")) {
                toolbar.setVisibility(View.GONE);
            }

            tvInfoHoursBold.setText(timeArray[0]);
            tvInfoHoursLight.setText(timeArray[1]);

            if(time.startsWith("Open")){
                logger.debug("mall is OPEN");
                toolbar.setBackgroundColor(getResources().getColor(R.color.info_hours_bg_open));
            } else if (time.startsWith("Closed")){
                logger.debug("mall is CLOSED");
                toolbar.setBackgroundColor(getResources().getColor(R.color.info_hours_bg_closed));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }



    public void initializeMallInfoData(){
        if(getActivity() == null){
            setOnFragmentInteractionListener(new OnFragmentInteractionListener() {
                @Override
                public void onFragmentInteraction() {
                    downloadMallInfo();
                }
            });
        } else {
            downloadMallInfo();
        }
    }

    public void downloadMallInfo(){
        KcpInfoManager kcpInfoManager = new KcpInfoManager(getActivity(), R.layout.layout_loading_item, new HeaderFactory().getHeaders(), new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_failed);
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        if(mMainActivity.mOnRefreshListener != null) mMainActivity.mOnRefreshListener.onRefresh(R.string.warning_download_completed);
                        if(mInfoRecyclerViewAdapter != null) mInfoRecyclerViewAdapter.updateData(KcpMallInfoRoot.getInstance().getKcpMallInfo().getInfoList());
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        logger.debug("URL is " + HeaderFactory.MALL_INFO_URL);
        kcpInfoManager.downloadMallInfo(HeaderFactory.MALL_INFO_URL_BASE, HeaderFactory.MALL_INFO_URL);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mListener =  new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(int position, final InfoList infoList) {
                KcpMallInfoRoot kcpMallInfoRoot = KcpMallInfoRoot.getInstance();
                List<InfoList> infoLists = kcpMallInfoRoot.getKcpMallInfo().getInfoList();
                if(infoLists.get(position).getTitle().contains(getResources().getString(R.string.mall_info_mall_hours))){
                    getActivity().startActivity(new Intent(getActivity(), MallHourActivity.class));
                } else {
                    Intent intent = new Intent(getActivity(), MallInfoDetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, infoList);
                    getActivity().startActivity(intent);
                }
                ActivityAnimation.startActivityAnimation(getActivity());
            }
        };


        KcpMallInfoRoot kcpMallInfoRoot = KcpMallInfoRoot.getInstance();
        kcpMallInfoRoot.createOfflineKcpMallInfo(getActivity(), HeaderFactory.MALL_INFO_OFFLINE_TEXT);

        mInfoRecyclerViewAdapter = new InfoRecyclerViewAdapter(
                getActivity(),
                kcpMallInfoRoot.getKcpMallInfo().getInfoList(),
                mListener);
        recyclerView.setAdapter(mInfoRecyclerViewAdapter);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position, InfoList infoList);
    }
}
