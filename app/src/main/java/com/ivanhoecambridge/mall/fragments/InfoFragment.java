package com.ivanhoecambridge.mall.fragments;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpCategoryManager;
import com.ivanhoecambridge.kcpandroidsdk.managers.KcpPlaceManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.InfoList;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.KcpMallInfoRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.activities.MoviesActivity;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.activities.MallHourActivity;
import com.ivanhoecambridge.mall.activities.MallInfoDetailActivity;
import com.ivanhoecambridge.mall.activities.ParkingActivity;
import com.ivanhoecambridge.mall.adapters.InfoRecyclerViewAdapter;
import factory.HeaderFactory;

import com.ivanhoecambridge.mall.parking.ParkingManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kay on 2016-06-20.
 */
public class InfoFragment extends BaseFragment{

    private View mView;
    private InfoRecyclerViewAdapter mInfoRecyclerViewAdapter;
    private boolean shouldScroll = false;
    private OnListFragmentInteractionListener mListener;
    private TextView tvInfoHoursBold;
    private TextView tvInfoHoursLight;
    private Toolbar toolbar;

    private final String SCREEN_NAME = "MALL INFO - Mall Information";
    private static InfoFragment sInfoFragment;
    public static InfoFragment getInstance(){
        if(sInfoFragment == null) sInfoFragment = new InfoFragment();
        return sInfoFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_info, container, false);
        final RecyclerView rvInfo = (RecyclerView) mView.findViewById(R.id.rvInfo);
        setupRecyclerView(rvInfo);

        final AppBarLayout abInfo = (AppBarLayout) mView.findViewById(R.id.abInfo);
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

        RelativeLayout rlDirection = (RelativeLayout) mView.findViewById(R.id.rlDirection);
        rlDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDirectionsEvent("Get Directions");
                Utility.openGoogleMapWithAddress(getActivity(), HeaderFactory.MALL_NAME);
            }
        });

        RelativeLayout rlSaveMyParkingSpot = (RelativeLayout) mView.findViewById(R.id.rlSaveMyParkingSpot);
        rlSaveMyParkingSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.getInstance(mMainActivity).logEvent("MALLINFO_Savemyparking_Click", "MALL INFO", "Click Save My Parking Spot");
                if(!ParkingManager.isParkingLotSaved(getActivity())){
                    final Intent intent = new Intent (getActivity(), ParkingActivity.class);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
                } else {
                    mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_MAP);
                    MapFragment.getInstance().onParkingClick(true, true);
                }
            }
        });

        ImageView ivCar = (ImageView) mView.findViewById(R.id.ivCar);
        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDirectionsEvent("Driving");
                Utility.openGoogleMapWithAddressWithDrivingMode(getActivity(), HeaderFactory.MALL_NAME);
            }
        });

        ImageView ivSubway = (ImageView) mView.findViewById(R.id.ivTransit);
        ivSubway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDirectionsEvent("Transit");
                Utility.openGoogleMapWithAddressWithTransitMode(getActivity(), HeaderFactory.MALL_NAME);
            }
        });

        ImageView ivWalk = (ImageView) mView.findViewById(R.id.ivWalk);
        ivWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logDirectionsEvent("Walking");
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
                            - getResources().getDimension(R.dimen.info_hours_height)
                            - getResources().getDimension(R.dimen.vpMain_padding_bottom));
                    rvInfo.setLayoutParams(lp);
                }
            });
        }

        toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        tvInfoHoursBold = (TextView) mView.findViewById(R.id.tvInfoHoursBold);
        tvInfoHoursLight = (TextView) mView.findViewById(R.id.tvInfoHoursLight);

        getMallHour();

        return mView;
    }

    public void setParkingSpotCTA(){
        if(mView == null || !this.isAdded()) {
            return;
        }

        ImageView ivPark = (ImageView) mView.findViewById(R.id.ivPark);
        TextView tvPark = (TextView) mView.findViewById(R.id.tvPark);

        if(!ParkingManager.isParkingLotSaved(getActivity())){
            ivPark.setImageResource(R.drawable.icn_parking);
            tvPark.setText(getString(R.string.info_save_my_parking_spot));
        } else {
            ivPark.setImageResource(R.drawable.icn_car);
            String sourceString;
            if(ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION) {
                String parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
                String entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
                sourceString = getString(R.string.info_my_parking_spot) + " " + "<b>" + parkingLotName + ", " + entranceName + "</b> ";
            } else {
                sourceString = "<b>" + getString(R.string.parking_saved_my_parking_spot) + "</b> ";
            }
            tvPark.setText(Html.fromHtml(sourceString));
        }
    }

    @Override
    public void onPageActive() {
        Analytics.getInstance(getContext()).logScreenView(getActivity(), SCREEN_NAME);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void getMallHour() {
        try {
            logger.debug("entered getMallHour");
            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);

            if(kcpPlaces != null){
                setUpMallOpenCloseStatus();
                logger.debug("mall name is : " + kcpPlaces.getPlaceName());
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
 
    private void setupRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mListener =  new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(int position, final InfoList infoList) {
                KcpMallInfoRoot kcpMallInfoRoot = KcpMallInfoRoot.getInstance();
                List<InfoList> infoLists = kcpMallInfoRoot.getKcpMallInfo().getInfoList();
                InfoList infoListItem = infoLists.get(position);
                if(getValueOrDefault(infoListItem.getTitle(), infoListItem.getType(), R.string.mall_info_mall_hours)){
                    getActivity().startActivity(new Intent(getActivity(), MallHourActivity.class));
                } else if (getValueOrDefault(infoListItem.getMenuTitle(), null, R.string.mall_info_cinema)){
                    Intent intent = new Intent(getActivity(), MoviesActivity.class);
                    intent.putExtra(Constants.ARG_TRANSITION_ENABLED, false);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                    ActivityAnimation.startActivityAnimation(getActivity());
                } else {
                    Intent intent = new Intent(getActivity(), MallInfoDetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, infoList);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_LOCATE_GUEST_SERVICE);
                }
                ActivityAnimation.startActivityAnimation(getActivity());
            }
        };

        KcpMallInfoRoot kcpMallInfoRoot = KcpMallInfoRoot.getInstance();
        kcpMallInfoRoot.createOfflineKcpMallInfo(getActivity(), Constants.getStringFromResources(getContext(), getValidMallInfoFile()));

        mInfoRecyclerViewAdapter = new InfoRecyclerViewAdapter(
                getActivity(),
                kcpMallInfoRoot.getKcpMallInfo().getInfoList(),
                mListener);
        recyclerView.setAdapter(mInfoRecyclerViewAdapter);
    }

    private boolean getValueOrDefault(String infoValue, String defValue, int resId) {
        return KcpUtility.resMatchesValueOrDefault(getContext(), infoValue, defValue, resId);
    }

    /**
     * Checks for the <em>mall_info.json</em> file based on the locale. If the locale file does not exist for that mall, it will default to the english file.
     * @return resource ID value that contains the name of the file.
     */
    private int getValidMallInfoFile() {
        int validInfoFileId = R.string.mall_info_json_default;
        if (!KcpUtility.isCurrentLocale(getContext(), "en")) {
            AssetManager assetManager = getContext().getAssets();
            try {
                if (Arrays.asList(assetManager.list("")).contains(getString(R.string.mall_info_json))) {
                    validInfoFileId = R.string.mall_info_json;
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return validInfoFileId;
    }

    private void logDirectionsEvent(String label) {
        Analytics.getInstance(mMainActivity).logEvent("MALLINFO_Malldirection_Click", "MALL INFO", "Click on Get Directions", label);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int position, InfoList infoList);
    }
}
