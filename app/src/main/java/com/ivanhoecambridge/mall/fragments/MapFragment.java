package com.ivanhoecambridge.mall.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpNavigationRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.kcpandroidsdk.views.ProgressBarWhileDownloading;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.activities.DetailActivity;
import com.ivanhoecambridge.mall.activities.MainActivity;
import com.ivanhoecambridge.mall.activities.ParkingActivity;
import com.ivanhoecambridge.mall.adapters.CategoryStoreRecyclerViewAdapter;
import com.ivanhoecambridge.mall.adapters.adapterHelper.SectionedLinearRecyclerViewAdapter;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.bluedot.BluetoothManager;
import com.ivanhoecambridge.mall.bluedot.FollowMode;
import com.ivanhoecambridge.mall.bluedot.MapViewWithBlueDot;
import com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenter;
import com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenterImpl;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.crashReports.CustomizedExceptionHandler;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.interfaces.MapInterface;
import com.ivanhoecambridge.mall.managers.ThemeManager;
import com.ivanhoecambridge.mall.mappedin.Amenities;
import com.ivanhoecambridge.mall.mappedin.Amenities.OnParkingClickListener;
import com.ivanhoecambridge.mall.mappedin.AmenitiesManager;
import com.ivanhoecambridge.mall.mappedin.Amenity;
import com.ivanhoecambridge.mall.mappedin.Elevator;
import com.ivanhoecambridge.mall.mappedin.EscalatorStairs;
import com.ivanhoecambridge.mall.mappedin.LocationGeneratorFactory;
import com.ivanhoecambridge.mall.mappedin.MapUtility;
import com.ivanhoecambridge.mall.mappedin.ParkingPin;
import com.ivanhoecambridge.mall.mappedin.ParkingPinInterface;
import com.ivanhoecambridge.mall.mappedin.Pin;
import com.ivanhoecambridge.mall.mappedin.Tenant;
import com.ivanhoecambridge.mall.mappedin.VortexPin;
import com.ivanhoecambridge.mall.parking.ChildParking;
import com.ivanhoecambridge.mall.parking.Parking;
import com.ivanhoecambridge.mall.parking.ParkingManager;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.AlertDialogForInterest;
import com.ivanhoecambridge.mall.views.ThemeColorImageView;
import com.mappedin.jpct.Logger;
import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Directions;
import com.mappedin.sdk.Focusable;
import com.mappedin.sdk.Instruction;
import com.mappedin.sdk.Location;
import com.mappedin.sdk.LocationGenerator;
import com.mappedin.sdk.Map;
import com.mappedin.sdk.MapView;
import com.mappedin.sdk.MapViewDelegate;
import com.mappedin.sdk.MappedIn;
import com.mappedin.sdk.MappedinCallback;
import com.mappedin.sdk.Navigatable;
import com.mappedin.sdk.Overlay;
import com.mappedin.sdk.Overlay2DImage;
import com.mappedin.sdk.Path;
import com.mappedin.sdk.Polygon;
import com.mappedin.sdk.Venue;
import com.senionlab.slutilities.type.SLHeadingStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import factory.HeaderFactory;

/**
 * Created by Kay on 2016-06-20.
 */
public class MapFragment extends BaseFragment
        implements MapViewDelegate, Amenities.OnAmenityClickListener, Amenities.OnDealsClickListener, OnParkingClickListener, MapViewWithBlueDot, ParkingPinInterface, LocationGeneratorFactory.LocationGeneratedListener {

    private final String TAG = "MapFragment";
    private static MapFragment sMapFragment;

    public static MapFragment getInstance() {
        if (sMapFragment == null) sMapFragment = new MapFragment();
        return sMapFragment;
    }

    

    private enum SearchMode {STORE, ROUTE_START, ROUTE_DESTINATION}

    public SearchMode mSearchMode = SearchMode.STORE;

    private enum IdType {ID, EXTERNAL_CODE, AMENITY, PARKING, INSTRUCTION}

    private Context context;
    private final String SCREEN_NAME = "MAP - Mall Map";
    private View           view;
    private RelativeLayout rlDirection;
    private TextView       tvStoreName;
    private TextView       tvCategoryName;

    private LinearLayout        llDeals;
    private ImageView           ivDeal;
    private LinearLayout        llDirection;
    private TextView            tvDealName;
    private TextView            tvParkingNote;
    private TextView            tvNumbOfDeals;
    private ImageView           ivCompass;
    private FrameLayout         flCompass;
    private TextView            tvLevel;
    private LinearLayout        llLevel;
    private LinearLayout        llBlueDot;
    private RelativeLayout      rlUpper;
    private RelativeLayout      rlLower;
    private ThemeColorImageView ivUpper;
    private ThemeColorImageView ivLower;
    private ThemeColorImageView ivUpperBg;
    private ThemeColorImageView ivLowerBg;
    private ImageView           ivAmenity;
    private ThemeColorImageView ivFollowMode;
    private FrameLayout         flMap; ///todo: disabled for testing
    private FrameLayout         flCircle; ///todo: disabled for testing
    private RelativeLayout      rlSlidingPanel; ///todo: disabled for testing
    private FrameLayout         flProgressOverlay;
    private ProgressBar         pbMap;
    private TextView tvTilt;
    private TextView tvHeading;

    private RelativeLayout rlRoute;
    private String mSearchString = "";
    public  MenuItem                         mSearchItem;
    private MenuItem                         mFilterItem;
    private MenuItem parkingItem;
    private boolean isParkingItemSelected;
    public  CategoryStoreRecyclerViewAdapter mPlaceRecyclerViewAdapter;
    private Drawable                         mAmeityDrawable;

    private int upperLevel;
    private int lowerLevel;

    //MAPPED IN
    private final int PIN_AMENITY_IMAGE_SIZE_DP = 24; //ends up 64 px
    private final int PIN_VORTEX_IMAGE_SIZE_DP  = 24; //ends up 64 px
    private final int PIN_BLUEDOT               = 20; //ends up 64 px
    private final int PIN_PARKING_IMAGE_SIZE_DP = 30; //ends up 64 px
    private final float PATH_WIDTH = 1.5f;
    private final float PATH_HEIGHT = 1.5f;
    private final float DEFAULT_TILT = (float) Math.PI / 5;
    private final float DEFAULT_HEADING = 0;
    private final static int CURRENT_TILT = -1;
    private final static int CURRENT_HEADING = -1;
    private final static int START_LOCATION = 1;
    private final static int DESTINATION_LOCATION = 2;
    private boolean isDestinationNonPolygon = false;

    /**
     * Flag to check if an error toast is currently scheduled to be shown.
     * For some reason some Polygons although existing on the map, are not actually valid.
     */
    private boolean isShowingErrorToast = false;
    /**
     * Flag to notify that an error toast is actually a false positive and should be ignored.
     * This occurs because didTapPolygon() can be called multiple times when selecting one polygon
     * as a result it can cause the locations size check to return null or 0 when first called
     * and a valid location on subsequent calls leading to a false positive.
     */
    private boolean isFalsePositive = false;

    private final int             CAMERA_ZOOM_LEVEL_NEAREST_PARKING = 90; //
    private final int             CAMERA_ZOOM_LEVEL_DEFAULT         = 5; //BIGGER - farther, SMALLER - closer
    private final int             BLUR_RADIUS                       = 20;
    private       boolean         accessibleDirections              = false;
    private       MapViewDelegate delegate                          = this;

    private MappedIn mappedIn = null;
    private MapView  mapView  = null;
    private Map[] maps;
    ArrayList<String> mMapInPath = new ArrayList<String>();
    private HashMap<Polygon, Integer>                           originalColors       = new HashMap<Polygon, Integer>();
    private ConcurrentHashMap<Overlay, LocationLabelClicker>    overlays             = new ConcurrentHashMap<Overlay, LocationLabelClicker>();
    private ConcurrentHashMap<Coordinate, LocationLabelClicker> mLocationClickersMap = new ConcurrentHashMap<Coordinate, LocationLabelClicker>();
    private ArrayList<Overlay>                                  dealsPinOverlays     = new ArrayList<>();
    private Venue                                               activeVenue          = null;
    private boolean                                             navigationMode       = false;
    private Path path;

    private Navigatable startPolygon         = null;
    private Navigatable destinationPolygon   = null;
    private Polygon     mSavedParkingPolygon = null;
    private int mOriginalColorsForParking; //original colors for parking used to clear its highlights on polygon
    private int mCurrentLevelIndex = -50;
    private Amenity mTemporaryParkingLocation; //used to show the temporary parking spot from detail activity
    public  String  mPendingExternalCode;

    private Pin mSelectedPin;
    private Pin mRemovedPin;
    private Pin mDestinationPin; //such as destination flag when drawing a paht
    private ArrayList<VortexPin> mVortexPins = new ArrayList<VortexPin>(); //such as destination flag when drawing a paht

    private String  mAmenityClicked = ""; //to keep track of previously clicked amenity
    public  boolean mMapLoaded      = false;
    private MapInterface mMapInterface;
    private final float   DEFAULT_PITCH        = 0.7f;
    private       boolean mAnimationInProgress = false;
    private       boolean mBearingEntered      = false; //indicate whether it has entered onCameraBearingChange to decide when to start showing the compass icon
    private       int     mLevelPanelYDistance = 0;
    private       int     mRootViewHeight      = 0;


    //BLUE DOT
    private        SLIndoorLocationPresenter slIndoorLocationPresenter;
    private static Pin                       mBlueDotPin;
    private        Pin                       mBlueDotCompass;
    private FollowMode mFollowMode = FollowMode.NONE;
    private GestureDetector gestureDetector;
    private boolean mShowBlueDotHeader = false; //show whether to 'use Current Location' header in recyclerview when blue dot's available - show in destination editor, don't show in normal search mode
    private static ParkingPin sParkingPin;
    private SLHeadingStatus mSLHeadingStatus = SLHeadingStatus.UNDEFINED;
    private double mBearingFromCamera;
    private boolean mGreyDotDropped = false;
    boolean headingDropped = false;
    float   tempHeading    = 0f;
    private int blueDotSizeInPx = 0;
    private boolean isMapFullyLoaded = false;
    private Polygon deferredPolygon;
    private boolean isDeferred             = false;


    private static HashMap<String, Tenant>   locationHashmapByExternalId = new HashMap<>(); //used to find polygons for stores - that use external iD
    private static HashMap<String, Amenity>  parkingHashMap              = new HashMap<>();
    private static HashMap<String, Location> amenityMap                  = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sParkingPin == null) sParkingPin = new ParkingPin(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        flProgressOverlay = view.findViewById(R.id.ll_map_overlay);
        pbMap = view.findViewById(R.id.pb_map);
        pbMap.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.themeColor), PorterDuff.Mode.MULTIPLY);
        rlDirection = (RelativeLayout) view.findViewById(R.id.rlDirection);
        rlRoute = (RelativeLayout) view.findViewById(R.id.rlRoute);
        tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
        llDirection = (LinearLayout) view.findViewById(R.id.llDirection);
        llDeals = (LinearLayout) view.findViewById(R.id.llDeals);
        ivDeal = (ImageView) view.findViewById(R.id.ivDeal);
        tvDealName = (TextView) view.findViewById(R.id.tvDealName);
        tvParkingNote = (TextView) view.findViewById(R.id.tvParkingNote);
        tvNumbOfDeals = (TextView) view.findViewById(R.id.tvNumbOfDeals);
        ivCompass = (ImageView) view.findViewById(R.id.ivCompass);
        ivCompass.bringToFront();
        flCompass = (FrameLayout) view.findViewById(R.id.flCompass);
        rlSlidingPanel = (RelativeLayout) view.findViewById(R.id.rlSlidingPanel);
        tvTilt =  view.findViewById(R.id.tvTilt);
        tvHeading = view.findViewById(R.id.tvHeading);
//        rlSlidingPanel.setVisibility(View.GONE); //DELETED FOR TESTING


        ivCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAnimationInProgress = true;
                flCompass.setAnimation(ProgressBarWhileDownloading.getEndAnimation(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mAnimationInProgress = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }));
                flCompass.setVisibility(View.INVISIBLE);

               // mapView.getCamera().setPositionTo(0, 0);
               // mapView.getCamera().setRotationTo(DEFAULT_PITCH, 0);
            }
        });

        tvLevel = (TextView) view.findViewById(R.id.tvLevel);
        llLevel = (LinearLayout) view.findViewById(R.id.llLevel);
        llBlueDot = (LinearLayout) view.findViewById(R.id.llBlueDot);
        rlUpper = (RelativeLayout) view.findViewById(R.id.rlUpper);
        rlLower = (RelativeLayout) view.findViewById(R.id.rlLower);
        ivUpper = (ThemeColorImageView) view.findViewById(R.id.ivUpper);
        ivUpperBg = (ThemeColorImageView) view.findViewById(R.id.ivUpperBg);
        ivLower = (ThemeColorImageView) view.findViewById(R.id.ivLower);
        ivLowerBg = (ThemeColorImageView) view.findViewById(R.id.ivLowerBg);
        ivAmenity = (ImageView) view.findViewById(R.id.ivAmenity);
        ivFollowMode = (ThemeColorImageView) view.findViewById(R.id.ivFollowMode);
        flCircle = (FrameLayout) view.findViewById(R.id.flCircle);
        flCircle.setOnClickListener(onFollowButtonListener);

        flMap = (FrameLayout) view.findViewById(R.id.flMap); //todo: disabled for testing

        View viewRoute = (View) view.findViewById(R.id.viewRoute);
        viewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.getInstance(getContext()).logEvent("MAP_Directions_Click", "MAP", "Click on Directions", tvStoreName.getText().toString());
                showDirectionEditor("", tvStoreName.getText().toString());
            }
        });

        mappedIn = new MappedIn(getActivity().getApplication());
        mapView = new MapView(); //TODO: disabled for testing

        mMainActivity.setOnAmenityClickListener(MapFragment.this);
        mMainActivity.setOnDealsClickListener(MapFragment.this);
        mMainActivity.setOnParkingClickListener(MapFragment.this);
        setupRecyclerView();
        setHasOptionsMenu(true);
        setViewListener();
        initializeMap();



        return view;
    }

    private void initializeBlueDot() {
        if (BuildConfig.BLUEDOT) {
            slIndoorLocationPresenter = new SLIndoorLocationPresenterImpl(getContext(), MapFragment.this);
            if (mMainActivity.mGeofenceManager != null)
                mMainActivity.mGeofenceManager.setSLIndoorLocationPresenterImpl((SLIndoorLocationPresenterImpl) slIndoorLocationPresenter);
        }
    }

    private void setViewListener() {
        mMainActivity.rlDestinationEditor.setTag(mMainActivity.rlDestinationEditor.getVisibility());
        mMainActivity.rlDestinationEditor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newVis = mMainActivity.rlDestinationEditor.getVisibility();
                if ((int) mMainActivity.rlDestinationEditor.getTag() != newVis) {
                    mMainActivity.rlDestinationEditor.setTag(mMainActivity.rlDestinationEditor.getVisibility());
                    if (flCompass.getVisibility() == View.VISIBLE) {
                        animateCompass();
                    }
                }
            }
        });
    }

    private void animateCompass() {
        if (mMainActivity.rlDestinationEditor.getVisibility() == View.VISIBLE) { //Visibility changed!
            flCompass.animate().setStartDelay(100).y((int) getResources().getDimension(R.dimen.map_compass_top_width_expanded));
        } else {
            flCompass.animate().setStartDelay(100).y((int) getResources().getDimension(R.dimen.map_compass_top_width));
        }
    }

    public void initializeMap() {
        try {
            if (mMainActivity == null) return;
            if (mMainActivity.mSplashScreenGone) {
                setProgressIndicator(true);
                mappedIn.getVenues(new GetVenuesCallback());
            } else {
                mMainActivity.setOnReadyToLoadMapListener(new MainActivity.ReadyToLoadMapListener() {
                    @Override
                    public void onReady() {
                        initializeMap();
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public interface OnStoreClickListener {
        public void onStoreClick(int storeId, String externalId, String storeName, String categoryName);
    }

    public void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mMainActivity.rvMap.setLayoutManager(linearLayoutManager);

        ArrayList<KcpPlaces> kcpPlaces = KcpPlacesRoot.getInstance().getPlacesList(KcpPlaces.PLACE_TYPE_STORE);
        ArrayList<KcpPlaces> kcpPlacesFiltered;

        if (mSearchString.equals("")) kcpPlacesFiltered = new ArrayList<>(kcpPlaces);
        else {
            kcpPlacesFiltered = KcpPlacesRoot.getInstance().getPlaceByName(mSearchString.toLowerCase());
        }

        mPlaceRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                getActivity(),
                kcpPlacesFiltered, KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE, onStoreClickedListener);

        List<SectionedLinearRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedLinearRecyclerViewAdapter.Section>();
        List<String> sectionName = new ArrayList<String>();
        List<Integer> sectionPosition = new ArrayList<Integer>();
        String startLetter = null;

        int startIndex = isBlueDotShown() && mShowBlueDotHeader ? 1 : 0;
        if (startIndex == 1) mPlaceRecyclerViewAdapter.addHeader(useMyLocationListener);
        for (int i = startIndex; i < kcpPlacesFiltered.size(); i++) {
            String storeName = kcpPlacesFiltered.get(i).getPlaceName().toUpperCase();
            String currentStoreNameStartLetter = "";
            if (storeName.length() > 0)
                currentStoreNameStartLetter = String.valueOf(storeName.charAt(0));
            if (startLetter == null || !startLetter.equals(currentStoreNameStartLetter)) {
                startLetter = currentStoreNameStartLetter;
                sections.add(new SectionedLinearRecyclerViewAdapter.Section(i, startLetter));
                sectionName.add(startLetter);
                sectionPosition.add(i + sections.size());
            }
        }

        mMainActivity.rvMap.setFastScrollEnabled(true);
        mMainActivity.rvMap.setIndexAdapter(sectionName, sectionPosition);

        SectionedLinearRecyclerViewAdapter.Section[] dummy = new SectionedLinearRecyclerViewAdapter.Section[sections.size()];
        SectionedLinearRecyclerViewAdapter mSectionedAdapter = new SectionedLinearRecyclerViewAdapter(
                getActivity(),
                R.layout.list_section_place,
                R.id.section_text,
                mMainActivity.rvMap,
                mPlaceRecyclerViewAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        mMainActivity.rvMap.setAdapter(mSectionedAdapter);

        if (kcpPlacesFiltered.isEmpty() && !mSearchString.equals("")) {
            mPlaceRecyclerViewAdapter.addFooter(getString(R.string.search_empty_results_place_holder), R.layout.list_item_directory_footer_empty);
        }
    }

    public static Amenity getAmenityWithLocationId(String locationId) {
        try {
            for (Location location : amenityMap.values()) {
                if (location instanceof Amenity) {
                    Amenity amenity = (Amenity) location;

                    if (amenity.id.equals(locationId)) {
                        return amenity;
                    }
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Polygon> getParkingPolygonsFromLocationWithId(String id) {

        if (parkingHashMap.containsKey(id)) {

            ArrayList<Polygon> polygons = new ArrayList<>();

            if (parkingHashMap.get(id).getPolygons() != null) {
                polygons = new ArrayList<>(Arrays.asList(parkingHashMap.get(id).getPolygons()));
            }

            return polygons;
        }

        return null;
    }

    public static Polygon getNearestParkingPolygonFromStorePolygon(Polygon polygon) {
        try {
            Coordinate storeCoordinate = polygon.getLocations()[0].getNavigatableCoordinates()[0];

            double nearestDistance = Double.MAX_VALUE;
            Polygon nearestParkingPolygon = null;

            for (Amenity parkingLocation : parkingHashMap.values()) {
                Coordinate[] navigatableCoordinates = parkingLocation.getNavigatableCoordinates();

                for (Coordinate parkingLotCoord : navigatableCoordinates) {
                    double distance = parkingLotCoord.metersFrom(storeCoordinate);

                    if (distance < nearestDistance) {
                        nearestParkingPolygon = parkingLocation.getPolygons()[0];
                        nearestDistance = distance;
                    }
                }
            }

            return nearestParkingPolygon;
        } catch (Exception e) {
            return null;
        }
    }

    private Polygon getPolygonWithPlaceExternalId(String placeExternalCode) {
        ArrayList<Polygon> polygons = getPolygonsFromLocationWithExternalCode(placeExternalCode);
        if (polygons != null && polygons.size() > 0) {
            return polygons.get(0);
        }
        return null;
    }

    public static ArrayList<Polygon> getPolygonsFromLocationWithExternalCode(String externalCode) {

        if (locationHashmapByExternalId.containsKey(externalCode)) {
            ArrayList<Polygon> polygons = new ArrayList<>();

            if (locationHashmapByExternalId.get(externalCode).getPolygons() != null) {
                polygons = new ArrayList<Polygon>(Arrays.asList(locationHashmapByExternalId.get(externalCode).getPolygons()));
            }

            return polygons;
        }

        return null;
    }

    public static Location getLocationWithExternalCode(String externalCode) {
        return locationHashmapByExternalId.get(externalCode);
    }

    // Get the basic info for all Venues we have access to
    private class GetVenuesCallback implements MappedinCallback<List<Venue>> {


        @Override
        public void onCompleted(List<Venue> venues) {
            //todo: disabled for testing
            for (Venue venue : venues) {
                if (venue.getName().equals(HeaderFactory.MAP_VENUE_NAME)) {
                    activeVenue = venue;
                }
            }
            if (activeVenue == null || getActivity() == null) return;
            //TODO: choose fragment method
            android.app.FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            if (!mapView.isAdded()) {
                Log.d("test", "MAP WAS NOT ADDED!");
                transaction.add(R.id.flMap, mapView);
            } else {
                Log.e("test", "MAP ALREADY ADDED!");
                transaction.remove(mapView);
                mapView = new MapView();
                transaction.add(R.id.flMap, mapView);
            }
            transaction.commitAllowingStateLoss();

            mapView.setDelegate(delegate);

            
            LocationGenerator amenity = LocationGeneratorFactory.prepareLocation(LocationGeneratorFactory.AMENITY, getLocationGeneratedListener());
            LocationGenerator tenant = LocationGeneratorFactory.prepareLocation(LocationGeneratorFactory.TENANT, getLocationGeneratedListener());
            LocationGenerator elevators = LocationGeneratorFactory.prepareLocation(LocationGeneratorFactory.ELEVATOR, getLocationGeneratedListener());
            LocationGenerator escalatorStairs = LocationGeneratorFactory.prepareLocation(LocationGeneratorFactory.ESCALATOR_STAIRS, getLocationGeneratedListener());

            LocationGenerator[] locationGenerators;
            if (getString(R.string.app_name).equals("Kinetic Cafe Office")) {
               locationGenerators = new LocationGenerator[]{tenant};
            } else {
                locationGenerators = new LocationGenerator[]{tenant, amenity, elevators, escalatorStairs};
            }
            mappedIn.getVenue(activeVenue, locationGenerators, new GetVenueCallback());

        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading any venues. Did you set your credentials? Exception: " + e);
        }
    }

    // Get the full details on a single Venue
    private class GetVenueCallback implements MappedinCallback<Venue> {
        @Override
        public void onCompleted(final Venue venue) {
            initializeBlueDot();
            maps = venue.getMaps();
            if (maps.length == 0) {
                Logger.log("No maps! Make sure your venue is set up correctly!");
                return;
            }

            if (maps.length > 0) {
                //set the map elevation if it exists
                Arrays.sort(maps, new Comparator<Map>() {
                    @Override
                    public int compare(Map a, Map b) {
                        return (int) (a.getAltitude() - b.getAltitude());
                    }
                });
            }

            int groundMapIndex = MapUtility.getGroundMapIndex(maps);
            if (groundMapIndex == -50) groundMapIndex = 0;
            setMapLevel(groundMapIndex, null, null);
            loadAmenitiesAndParkingLot();

            setMapLevelArrowVisibility();
            setBlueDotIconVisibility();

            mMapLoaded = true;
            if (mMapInterface != null) mMapInterface.mapLoaded();
            //MappedIn has their own progress bar that's not accessible, if our progress bar isn't delayed then we would
            //briefly see the ugly MappedIn one.no
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setProgressIndicator(false);
                    setIsMapFullyLoaded(maps[mCurrentLevelIndex].mapReady());
                }
            }, 1800);


        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading Venue: " + e);
        }
    }

    @Override
    public void onTenantGenerated(Tenant tenant) {
        if (tenant.externalId != null) {
            locationHashmapByExternalId.put(tenant.externalId, tenant);
        }
    }

    @Override
    public void onAmenityGenerated(Amenity amenity) {
        addLocationToAmenityMap(amenity);
        if (amenity.amenityType != null && amenity.amenityType.equals("parking")) {
            if (amenity.id != null) {
                parkingHashMap.put(amenity.id, amenity);
            }
        }
    }

    @Override
    public void onEscalatorStairsGenerated(EscalatorStairs escalatorStairs) {
        addLocationToAmenityMap(escalatorStairs);
    }

    @Override
    public void onElevatorGenerated(Elevator elevator) {
        addLocationToAmenityMap(elevator);
    }

    public LocationGeneratorFactory.LocationGeneratedListener getLocationGeneratedListener() {
        return this;
    }

    private void addLocationToAmenityMap(Location locationData) {
        if (locationData instanceof Amenity) {
            Amenity amenity = (Amenity) locationData;
            if (amenity.amenityType == null || amenity.amenityType.isEmpty()) {
                amenityMap.put(amenity.getName(), amenity);
            } else {
                amenityMap.put(amenity.amenityType, amenity);
            }
        } else {
            amenityMap.put(locationData.getName(), locationData);
        }
    }

    private void setMapLevelArrowVisibility() {
        if (maps != null && maps.length > 1) {
            rlSlidingPanel.setVisibility(View.VISIBLE);
            llLevel.setVisibility(View.VISIBLE);
        }
    }

    private void setBlueDotIconVisibility() {
        if (BuildConfig.BLUEDOT) {
            rlSlidingPanel.setVisibility(View.VISIBLE);
            llBlueDot.setVisibility(View.VISIBLE);
        }
    }

    public void setMapInterface(MapInterface mapInterface) {
        mMapInterface = mapInterface;
    }

    private void loadAmenitiesAndParkingLot() {
        //LOAD AMENITIES, DEALS
        onDealsClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_DEAL, false));
        for (int i = 0; i < AmenitiesManager.sAmenities.getAmenityList().size(); i++) {
            Amenities.Amenity amenity = AmenitiesManager.sAmenities.getAmenityList().get(i);
            boolean isAmenityEnabled = amenity.isEnabled();
            for (String externalID : amenity.getExternalIds()) {
                if (amenity.getExternalIds().length > 1) {
                    isAmenityEnabled = Amenities.isToggled(getContext(), Amenities.GSON_KEY_AMENITY + amenity.getExternalIds()[0], amenity.isEnabled());
                }
                onAmenityClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_AMENITY + externalID, isAmenityEnabled), externalID, false, null);
            }
        }

        if (ParkingManager.isParkingLotSaved(getActivity())) {
            onParkingClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_PARKING, false), false);
        }
    }

    /**
     * set map level using one of the three params
     *
     * @param mapIndex  index of the map to load in maps array
     * @param shortName shortname of the map to load
     * @param mapName   mapname of the map to load
     */
    public void setMapLevel(int mapIndex, @Nullable String shortName, @Nullable String mapName) {
        //todo: when setting map level from normal arrow button, follow the normal level order
        // if this is called by selecting the highlighted arrow, load the right level
        try {
            if (shortName != null) {
                for (int i = 0; i < maps.length; i++) {
                    Map map = maps[i];
                    if (map.getShortName().equals(shortName)) {
                        if (i == mCurrentLevelIndex) {
                            setLevelImageView(maps);
                        }
                        mCurrentLevelIndex = i;
                        break;
                    }
                }
            } else if (mapName != null) {
                for (int i = 0; i < maps.length; i++) {
                    Map map = maps[i];
                    if (map.getName().equals(mapName)) {
                        if (i == mCurrentLevelIndex) {
                            setLevelImageView(maps);
                        }
                        mCurrentLevelIndex = i;
                        break;
                    }
                }
            } else {
                if (mapIndex == mCurrentLevelIndex) {
                    setLevelImageView(maps);
                }
                mCurrentLevelIndex = mapIndex;
            }
            setIsMapFullyLoaded(maps[mCurrentLevelIndex].mapReady());
            mapView.setMap(maps[mCurrentLevelIndex]);
            tvLevel.setText(maps[mCurrentLevelIndex].getShortName());
            setLevelImageView(maps);
            showInstruction(maps[mCurrentLevelIndex].getAltitude());
            //highlight the store that has been pending
            if (mPendingExternalCode != null) {
                ArrayList<Polygon> polygons = getPolygonsFromLocationWithExternalCode(mPendingExternalCode);
                mPendingExternalCode = null;
                if (polygons != null && polygons.size() > 0) {
                    showStoreOnTheMapFromDetailActivity(polygons.get(0));
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void setLevelImageView(final Map[] maps) {
        try {
            if (ivUpper == null || getActivity() == null) {
                CustomizedExceptionHandler.writeToFile(getActivity(), "setLevelImageView - ivUpper is null - fragment is not attached to activity");
                Log.e("MapFragment", "FRAGMENT NOT ATTACHED TO ACTIVITY");
                return;
            }
            upperLevel = mCurrentLevelIndex + 1;
            lowerLevel = mCurrentLevelIndex - 1;

            ivUpperBg.setSelected(false);
            ivLowerBg.setSelected(false);

            ivUpper.setColor(getResources().getColor(R.color.map_level_disabled), getResources().getColor(R.color.map_level_enabled));
            ivLower.setColor(getResources().getColor(R.color.map_level_disabled), getResources().getColor(R.color.map_level_enabled));

            if (mMapInPath.size() > 0) {
                String currentLevelName = maps[mCurrentLevelIndex].getShortName();
                if (mMapInPath.contains(currentLevelName)) {
                    int currentLevelIndexInList = mMapInPath.indexOf(currentLevelName);
                    if (mMapInPath.size() > currentLevelIndexInList + 1) {
                        String nextLevelInPath = mMapInPath.get(currentLevelIndexInList + 1);
                        int nextLevelIndex = MapUtility.getMapIndexWithShortName(maps, nextLevelInPath);
                        if (mCurrentLevelIndex > nextLevelIndex) { //path continued to lower levels
                            lowerLevel = nextLevelIndex;
                            ivUpperBg.setSelected(false);
                            ivLowerBg.setSelected(true);
                            ivLower.setColor(getResources().getColor(R.color.transparent), getResources().getColor(R.color.white));
                        } else { //path continued to upper levels
                            upperLevel = nextLevelIndex;
                            ivUpperBg.setSelected(true);
                            ivLowerBg.setSelected(false);
                            ivUpper.setColor(getResources().getColor(R.color.transparent), getResources().getColor(R.color.white));
                        }
                    }
                }
            }

            //checking whether upperLevel exists && ivUpper has been colored white which means upper level has a path
            if (upperLevel >= maps.length && ivUpper.getSelectedFilterColor() != getResources().getColor(R.color.white)) {
                ivUpper.setSelected(false);
                rlUpper.setOnClickListener(null);
            } else {
                ivUpper.setSelected(true);
                rlUpper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFollowMode(FollowMode.NONE);
                        setMapLevel(upperLevel, null, null);
                    }
                });
            }

            if (lowerLevel < 0 && ivLower.getSelectedFilterColor() != getResources().getColor(R.color.white)) {
                ivLower.setSelected(false);
                rlLower.setOnClickListener(null);
            } else {
                ivLower.setSelected(true);
                rlLower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFollowMode(FollowMode.NONE);
                        setMapLevel(lowerLevel, null, null);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void showStoreOnTheMapFromDetailActivity(Polygon polygon) {
        float tilt = (float) Math.PI / 5;
        try {
            focusOn(polygon, 0, tilt);

            destinationPolygon = null;
            showDirectionCard(false, null, 0, null, null, null);
            clearHighlightedColours();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MapFragment.getInstance().didTapPolygon(polygon);
        tiltCamera(polygon, tilt);
    }

    private void setIsMapFullyLoaded(boolean isMapFullyLoaded) {
        this.isMapFullyLoaded = isMapFullyLoaded;
        if (isMapFullyLoaded) {
            highlightDeferredFromStore();
        }
    }

    private void focusOn(Focusable focusItem, float heading, float tilt) {
        if (tilt == CURRENT_TILT) {
            tilt = mapView.getCameraTilting();
        }
        if (heading == CURRENT_HEADING) {
            heading = mapView.getCameraHeading();
        }
        mapView.frame(focusItem, heading, DEFAULT_TILT, 2.5f);
    }

    private void focusOn(Focusable focusItem, float heading, float tilt, Map mapAttrs) {
        setMapLevel(mapAttrs.getFloor(), mapAttrs.getShortName(), mapAttrs.getName());
        focusOn(focusItem, heading, tilt);
    }

    private void drawPath() {
        if (path != null) {
            didTapNothing();
            return;
        }

        //because only destinationPolygon can either be polygon or location
        Directions directions = null;
        Location arriveAtLocation = null;

        if (destinationPolygon instanceof Polygon) {

            arriveAtLocation = ((Polygon) destinationPolygon).getLocations()[0];
        } else if (destinationPolygon instanceof Tenant) {

            arriveAtLocation = ((Tenant) destinationPolygon);
        } else if (destinationPolygon instanceof Amenity) {

            arriveAtLocation = ((Amenity) destinationPolygon);
        }

        String storeName = getString(R.string.bluedot_my_location);
        if (mMainActivity.getDestStoreName().equals(storeName) && mBlueDotPin != null) {

            directions = mBlueDotPin.getCoordinate().directionsTo(activeVenue, startPolygon, ((Polygon) startPolygon).getLocations()[0], null); //FROM BLUEDOT
        } else if (mMainActivity.getStartStoreName().equals(storeName) && mBlueDotPin != null) {

            directions = destinationPolygon.directionsFrom(activeVenue, mBlueDotPin.getCoordinate(), null, arriveAtLocation);
        }

        if (directions != null) {
            path = new Path(directions.getPath(), 1.5f, 1.5f, ContextCompat.getColor(getContext(), R.color.map_wayfinding_path), 0);
            mapView.addElement(path);
            setMapLevelsInPath(directions.getPath());
        }

        Coordinate destinationPolygonCoordinate = null;
        if (destinationPolygon instanceof Polygon) {

            highlightPolygon((Polygon) destinationPolygon, R.color.themeColor);
            destinationPolygonCoordinate = ((Polygon) destinationPolygon).getAnchor();
        } else if (destinationPolygon instanceof Coordinate) {

            clearHighlightedColours();
            LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(destinationPolygon);
            if (locationLabelClicker != null) {
                destinationPolygonCoordinate = (Coordinate) destinationPolygon;
                locationLabelClicker.highlightThisLabel();
            }
        } else if (destinationPolygon instanceof Amenity) {
            destinationPolygonCoordinate = ((Amenity) destinationPolygon).getNavigatableCoordinates()[0];
        } else if (destinationPolygon instanceof Tenant) {
            destinationPolygonCoordinate = ((Tenant) destinationPolygon).getNavigatableCoordinates()[0];
        }

        showDirectionCard(false, null, 0, null, null, null);
        if (mBlueDotPin.getCoordinate().getMap() != null) {
            setMapLevel(-50, mBlueDotPin.getCoordinate().getMap().getShortName(), null);
        }

        dropDestinationPin(destinationPolygonCoordinate, getResources().getDrawable(R.drawable.icn_wayfinding_destination));
        dropVortexOnThePath(directions.getInstructions());
        showInstruction(maps[mCurrentLevelIndex].getAltitude());
    }

    private void filterSearchMode(Polygon polygon, SearchMode searchMode) {
        if (searchMode.equals(SearchMode.STORE) || searchMode.equals(SearchMode.ROUTE_DESTINATION)) {
            updateDestinationPolygon(polygon);
        } else if (searchMode.equals(SearchMode.ROUTE_START)) {
            updateStartPolygon(polygon);
        }
    }

    private void updateStartPolygon(Polygon startPolygon) {
        this.startPolygon = startPolygon;
    }

    private void updateDestinationPolygon(Polygon destinationPolygon) {
        if (getLocationSizeForPolygon(destinationPolygon) > 0) {
            this.destinationPolygon = destinationPolygon;
        }
    }

    private void clearDestinationPolygon() {
        this.destinationPolygon = null;
    }


    private boolean setupForNavigation(Polygon polygon) {
        if (path != null) {
            didTapNothing();
            return false;
        }

        Directions directions = getDirections(getLocation(START_LOCATION), getLocation(DESTINATION_LOCATION),
                getStartNavigatable(), getDestinationNavigatable());
        if (directions == null) {
            return false;
        }
        path = createPath(directions.getPath());
        mapView.addElement(path);
        if (isDestinationNonPolygon()) {
            frameMultipleFocusabes(path, new Focusable[] {getStartPolygon(), (Coordinate) getDestinationNavigatable()});
        } else {
            frameBetweenPolygons(path, getStartPolygon(), getDestinationPolygon());
        }


        Coordinate destinationPolygonCoords = null;
        if (getDestinationNavigatable() instanceof Polygon) {
            highlightPolygon(getDestinationPolygon(), R.color.colorAccent);
            destinationPolygonCoords = getDestinationPolygon().getAnchor();
        } else if (getDestinationNavigatable() instanceof Coordinate) {
            clearHighlightedColours();
            LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(getDestinationNavigatable());
            if (locationLabelClicker != null) {
                destinationPolygonCoords = (Coordinate) getDestinationNavigatable();
                locationLabelClicker.highlightThisLabel();
            }
        } else {
            destinationPolygonCoords = getDestinationNavigatable().getNavigatableCoordinates()[0];
        }

        if (getStartPolygon().getMap().getShortName() != null) {
            setMapLevel(-50, getStartPolygon().getMap().getShortName(), null);
        }
        highlightPolygon(getStartPolygon(), R.color.colorAccent);
        showDirectionCardNull();
        dropDestinationPin(destinationPolygonCoords, ContextCompat.getDrawable(getContext(), R.drawable.icn_wayfinding_destination));
        dropVortexOnThePath(directions.getInstructions());
        showInstruction(maps[mCurrentLevelIndex].getAltitude());

        return true;
    }

    private Directions getDirections(Location start, Location destination, Navigatable fromNavigatable, Navigatable toNavigatable) {
        return toNavigatable.directionsFrom(activeVenue, fromNavigatable, start, destination);
    }

    private Location getLocation(int whichLocation) {
        Location location = null;
        switch (whichLocation) {
            case START_LOCATION:
                if (getLocationSizeForPolygon(getStartPolygon()) > 0) {
                    location = getStartPolygon().getLocations()[0];
                }
                break;
            case DESTINATION_LOCATION:
                    if (getDestinationNavigatable() instanceof Polygon) {
                        if (getLocationSizeForPolygon(getDestinationPolygon()) > 0) {
                            location = getDestinationPolygon().getLocations()[0];
                        }
                        setDestinationNonPolygon(false);
                    } else if (getDestinationNavigatable() instanceof Coordinate) {
                        location = null;
                        setDestinationNonPolygon(true);
                    }
                    break;
        }
        return location;
    }


    private int getLocationSizeForPolygon(Polygon polygon) {
        return polygon.getLocations() == null ? 0 : polygon.getLocations().length;
    }

    private void setDestinationNonPolygon(boolean isDestinationNonPolygon) {
        this.isDestinationNonPolygon = isDestinationNonPolygon;
    }

    private boolean isDestinationNonPolygon() {
        return isDestinationNonPolygon;
    }

    void setFalsePositive(boolean isFalsePositive) {
        this.isFalsePositive = isFalsePositive;
    }

    void setShowingErrorToast(boolean isShowingErrorToast) {
        this.isShowingErrorToast = isShowingErrorToast;
    }

    @Override
    public boolean didTapPolygon(Polygon polygon) {
        setFalsePositive(false);
        deferredPolygon = null;
        if (!isMapFullyLoaded) {
            isMapFullyLoaded = true;
        }
        boolean stopGoingToNextPolygon = true;

        try {
            replaceSelectedPinWithRemovedPin();

            if (isDeselection(polygon, mSearchMode)) {
                setFalsePositive(true);
                setShowingErrorToast(false);
                return true;
            }

            filterSearchMode(polygon, mSearchMode);
            int polygonLocations = (polygon.getLocations() == null) ? 0 : polygon.getLocations().length;
            if (polygonLocations == 0) {
                if (!isShowingErrorToast) {
                    isShowingErrorToast = true;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isFalsePositive) {
                                showErrorAsToast(getString(R.string.mappedin_invalid_selection));
                                setShowingErrorToast(false);
                            }
                        }
                    }, 100);
                }

                stopGoingToNextPolygon = false;
                return false;
            }

            setFalsePositive(true);
            setShowingErrorToast(false);

            setFollowMode(FollowMode.NONE);

            if (navigationMode) {
                stopGoingToNextPolygon = setupForNavigation(polygon);
            } else {
                clearHighlightedColours();
                highlightPolygon(polygon, R.color.colorAccent);
                focusOn(polygon, DEFAULT_HEADING, DEFAULT_TILT);
                if (mSavedParkingPolygon != null && destinationPolygon == mSavedParkingPolygon) {
                    tiltCamera(polygon, 0);
                    showSavedParkingDetail();
                } else {
                    showNavigatableDetails(polygon.getLocations()[0]);
                }
            }
        } catch (Exception e) {
            Log.e("TapPolygon", e.getMessage());
        }
        return stopGoingToNextPolygon;
    }

    private void showErrorAsToast(String error) {
        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks if the given Polygon is the last selected destination Polygon. (This will be the case if the user tapped on the Polygon)
     * If the polygons match, then the polygon will be deselected and the return will be true.
     * @param polygon Polygon to compare against.
     * @param searchMode SearchMode, will only validate if the SearchMode is SearchMode.STORE
     * @return true if the given Polygon matches the destination polygon, false otherwise.
     */
    private boolean isDeselection(Polygon polygon, SearchMode searchMode) {
        if (searchMode.equals(SearchMode.STORE) && isPolygonLastSelectedDestination(polygon)
                && mSavedParkingPolygon != polygon) {
            clearDestinationPolygon();
            showDirectionCardNull();
            deselectPolygon(polygon);
            return true;
        }
        return false;
    }

    private boolean isPolygonLastSelectedDestination(Polygon polygon) {
        if (getDestinationPolygon() == null || getLocationSizeForPolygon(getDestinationPolygon()) == 0) return false;
        return getLocationSizeForPolygon(polygon) != 0 &&
                getDestinationPolygon().getLocations()[0].getName().equals(polygon.getLocations()[0].getName());

    }

    private void deselectPolygon(Polygon polygon) {
        if (polygon != null) {
            mapView.resetPolygon(polygon);
        }
    }

    public boolean didTapPolygonOld(Polygon polygon) {
        deferredPolygon = null;
        boolean stopGoingToNextPolygon = true;
        try {
            if (path != null || polygon == null)
                return true; //map shouldn't be clicakble when the paths drawn
            if (polygon.getLocations().length == 0) { //TODO: clearHighlightedColours() used to be above this line - polygon.getLocationWithExternalCode().size() was sometimes 0 resulting in skipping highlightPolygon (it returned)
                stopGoingToNextPolygon = false;
                return stopGoingToNextPolygon;
            }
            replaceSelectedPinWithRemovedPin();
            //tapping the same polygon should dismiss the detail and remove the highlights
            if (mSearchMode.equals(SearchMode.STORE) && destinationPolygon != null && destinationPolygon == polygon && mSavedParkingPolygon != polygon) {
                destinationPolygon = null;
                showDirectionCard(false, null, 0, null, null, null);
                clearHighlightedColours();
                stopGoingToNextPolygon = true;
            }

            if (mSearchMode.equals(SearchMode.STORE)) {
                destinationPolygon = polygon;
            } else if (mSearchMode.equals(SearchMode.ROUTE_START)) {
                startPolygon = polygon;

            } else if (mSearchMode.equals(SearchMode.ROUTE_DESTINATION)) {
                destinationPolygon = polygon;
            }

            setFollowMode(FollowMode.NONE);

            if (navigationMode) {
                if (path != null) {
                    didTapNothing();
                    stopGoingToNextPolygon = true;
                }

                if (polygon.getLocations().length == 0) {
                    stopGoingToNextPolygon = true;
                }

                //because only destinationPolygon can either be polygon or location
                Directions directions;
                Location arriveAtLocation = null;

                if (destinationPolygon instanceof Polygon) {
                    arriveAtLocation = ((Polygon) destinationPolygon).getLocations()[0];
                } else if (destinationPolygon instanceof Location) {
                    arriveAtLocation = ((Location) destinationPolygon);
                } else if (destinationPolygon instanceof Coordinate) {

                }

                directions = destinationPolygon.directionsFrom(activeVenue, startPolygon, ((Polygon) startPolygon).getLocations()[0], arriveAtLocation); //real use

                if (directions != null) {
                    path = createPath(directions.getPath());
                    mapView.addElement(path);
                    frameBetweenPolygons(path, getStartPolygon(), getDestinationPolygon());
                    setMapLevelsInPath(directions.getPath());
                }

                Coordinate destinationPolygonCoordinate = null;
                if (destinationPolygon instanceof Polygon) {
                    highlightPolygon((Polygon) destinationPolygon, R.color.themeColor);
                    destinationPolygonCoordinate = ((Polygon) destinationPolygon).getAnchor();
                } else if (destinationPolygon instanceof Amenity && !((Amenity) destinationPolygon).amenityType.equals(KcpPlaces.PLACE_TYPE_PARKING)) {
                } else if (destinationPolygon instanceof Coordinate) {
                    Coordinate startPolygonCoord = ((Polygon) startPolygon).getLocations()[0].getNavigatableCoordinates()[0];
                    clearHighlightedColours();
                    LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(destinationPolygon);
                    if (locationLabelClicker != null) {
                        destinationPolygonCoordinate = (Coordinate) destinationPolygon;
                        locationLabelClicker.highlightThisLabel();
                    }
                } else {
                    destinationPolygonCoordinate = destinationPolygon.getNavigatableCoordinates()[0];
                }

                if (((Polygon) startPolygon).getMap().getShortName() != null) {
                    setMapLevel(-50, ((Polygon) startPolygon).getMap().getShortName(), null);
                }
                highlightPolygon((Polygon) startPolygon, R.color.map_destination_store);
                showDirectionCard(false, null, 0, null, null, null);
                dropDestinationPin(destinationPolygonCoordinate, getResources().getDrawable(R.drawable.icn_wayfinding_destination));
                dropVortexOnThePath(directions.getInstructions());
                showInstruction(maps[mCurrentLevelIndex].getAltitude());

                stopGoingToNextPolygon = true;
            } else {
                clearHighlightedColours();
                if (destinationPolygon instanceof Polygon) {
                    Polygon destination = (Polygon) destinationPolygon;
                    highlightPolygon(destination, R.color.themeColor);
                    try {
                        if (mSavedParkingPolygon != null && destinationPolygon == mSavedParkingPolygon) {
                            focusOn(polygon, 0 , 0);
                            tiltCamera(polygon, 0);
                            showSavedParkingDetail();
                        } else {
                            showNavigatableDetails(destination.getLocations()[0]);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                        e.printStackTrace();
                    }
                }
                if (polygon.getMap().getShortName() != null)
                    setMapLevel(-50, polygon.getMap().getShortName(), null);
            }
        } catch (Resources.NotFoundException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (Exception e) {
            Polygon storePolygon = (Polygon) startPolygon;
            if (storePolygon != null) {
                if (((Polygon) startPolygon).getMap().getShortName() != null) {
                    setMapLevel(-50, ((Polygon) startPolygon).getMap().getShortName(), null);
                }
            }
            logger.error(e);
            e.printStackTrace();
        }
        return stopGoingToNextPolygon;
    }

    private Path createPath(Coordinate[] pathCoordinates) {
        return new Path(pathCoordinates, PATH_WIDTH, PATH_HEIGHT, ContextCompat.getColor(getContext(), R.color.map_wayfinding_path), 0);
    }

    private void frameBetweenPolygons(Path path, Polygon from, Polygon to) {
        if (path != null && from != null && to != null) {
            mapView.frame(new Focusable[] {path, to, from}, 0, DEFAULT_TILT, 0);
        }

    }

    private void frameMultipleFocusabes(Path path, Focusable[] focusables) {
        if (path != null && focusables != null) {
            mapView.frame(focusables, 0, DEFAULT_TILT, 2.5f);
        }
    }

    private Polygon getStartPolygon() {
        return (Polygon) startPolygon;
    }

    private Polygon getDestinationPolygon() {
        return (Polygon) destinationPolygon;
    }

    private Navigatable getStartNavigatable() {
        return startPolygon;
    }

    private Navigatable getDestinationNavigatable() {
        return destinationPolygon;
    }

    private void dropVortexOnThePath(List<Instruction> instructions) {
        for (Instruction instruction : instructions) {
            if (VortexPin.isVortex(instruction)) {
                VortexPin vortexPin = new VortexPin(getActivity(), instruction);
                mVortexPins.add(vortexPin);
                dropVortexPin(vortexPin);
            }
        }
    }

    private void dropVortexPin(VortexPin vortexPin) {
        Overlay2DImage label = new Overlay2DImage(getVortexAndDestinationPinSize(), getVortexAndDestinationPinSize(), vortexPin.getVortexPinDrawable(), getVortexAndDestinationPinSize() / 2, getVortexAndDestinationPinSize() / 2);
        label.setPosition(vortexPin.getVortexCoordinate());
        vortexPin.setVortexPin(new Pin(vortexPin.getVortexCoordinate(), label));
        mapView.addElement(label);
    }

    private void showInstruction(double elevation) {
        if (mVortexPins.size() > 0) {
            int position = VortexPin.getPostionOfVortext(mVortexPins, elevation);
            if (position == -1) {
                showDirectionCard(false, null, 0, null, null, null);
                return;
            }
            VortexPin vortexPin = mVortexPins.get(position);
            showDirectionCard(true, IdType.INSTRUCTION, 0, vortexPin.getTextInstruction(), "", vortexPin.getVortexInstructionDrawable());
        }
    }

    /**
     * creates arraylist of map shortnames of floors where the path's drawn onto
     *
     * @param directionCoordinates
     */
    private void setMapLevelsInPath(Coordinate[] directionCoordinates) {
        mMapInPath.clear();
        for (Coordinate coordinate : directionCoordinates) {
            String mapShortName = coordinate.getMap().getShortName();
            if (!mMapInPath.contains(mapShortName)) mMapInPath.add(mapShortName);
        }
    }



    private void tiltCamera(Focusable focusItem, float tiltRadians) {
        mapView.orbit(focusItem, 0, tiltRadians, 0);
    }

    private void showSavedParkingDetail() {
        String parkingLotName;
        String entranceName;
        String parkingNote = ParkingManager.getParkingNotes(getActivity());

        if (ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION) {
            parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
            entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
            showParkingDetail(sParkingPin.getParkingLocationPin(), false, parkingLotName, entranceName, parkingNote, -1, -1);
        } else {
            showParkingDetail(false);
        }
    }

    @Override
    public boolean didTapOverlay(Overlay overlay) {
        LocationLabelClicker clicker = overlays.get(overlay);
        if (clicker != null) {
            clearHighlightedColours();
            clicker.onClick();
        }
        return true;
    }

    public void didTapBack() {
        clearHighlightedColours();
        clearLocationDetails();
        stopNavigation();

        startPolygon = null;
        destinationPolygon = null;
        mSearchMode = SearchMode.STORE;
        mMapInPath.clear();
        setLevelImageView(maps);
        if (mDestinationPin != null) {
            mapView.removeElement(mDestinationPin.getOverlay2DImage());
            mDestinationPin = null;
        }

        if (mVortexPins.size() > 0) {
            for (VortexPin vortexPin : mVortexPins) {
                if (vortexPin != null && vortexPin.getVortexPin() != null) {
                    mapView.removeElement(vortexPin.getVortexPin().getOverlay2DImage());
                }
            }
            mVortexPins.clear();
        }

        replaceSelectedPinWithRemovedPin();
    }

    @Override
    public void didTapNothing() {

        if (path == null) {

            clearHighlightedColours();
            clearLocationDetails();
            stopNavigation();

            startPolygon = null;
            destinationPolygon = null;
            mSearchMode = SearchMode.STORE;
            mMapInPath.clear();
            setLevelImageView(maps);
            if (mDestinationPin != null) {
                mapView.removeElement(mDestinationPin.getOverlay2DImage());
                mDestinationPin = null;
            }

            if (mVortexPins.size() > 0) {
                for (VortexPin vortexPin : mVortexPins) {
                    if (vortexPin != null && vortexPin.getVortexPin() != null) {
                        mapView.removeElement(vortexPin.getVortexPin().getOverlay2DImage());
                    }
                }
                mVortexPins.clear();
            }

            replaceSelectedPinWithRemovedPin();
        }
    }

    @Override
    public void onCameraBearingChange(final double bearing) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flCompass.getVisibility() != View.VISIBLE && !mAnimationInProgress && mBearingEntered) {
                    flCompass.setAnimation(ProgressBarWhileDownloading.getStartAnimation(null));
                    flCompass.setVisibility(View.VISIBLE);
                    animateCompass();
                }
                mBearingFromCamera = bearing;
                ivCompass.setRotation((float) (bearing / Math.PI * 180));
                mBearingEntered = true;
            }
        });
    }

    @Override
    public void manipulatedCamera() {
       tvHeading.setText(getString(R.string.mappedin_debug_overlay_heading, mapView.getCameraHeading(), Math.toDegrees(mapView.getCameraHeading())));
       tvTilt.setText(getString(R.string.mappedin_debug_overlay_tilt, mapView.getCameraTilting(), Math.toDegrees(mapView.getCameraTilting())));
    }


    private void setProgressIndicator(boolean toggleVisible) {
        flProgressOverlay.setVisibility(toggleVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * Highlights a store/parking when requesting to locate a location before the map has loaded.
     * The location will be stored into a deferredPolygon object and then re-highlighted when the map is ready.
     */
    private void highlightDeferredFromStore() {
        if (deferredPolygon != null) {
           isDeferred = true;
           highlightPolygon(deferredPolygon, R.color.themeColor);
        }
    }

    private void highlightPolygon(final Polygon polygon, final int color) {
        if (mSavedParkingPolygon != null && mSavedParkingPolygon == polygon && mOriginalColorsForParking == 0) {
            //mOriginalColorsForParking != 0 so that when tapping already highlighted parking polygon, it doesn't save that highlight color to mOriginalColorsForParking
            mOriginalColorsForParking = polygon.color();
        } else {
            if (!originalColors.containsKey(polygon)) {
                try {
                    originalColors.put(polygon, polygon.color());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //Not a long term solution, MappedIn SDKs have changed so once we update it'll be a more efficient solution.
        if (isMapFullyLoaded) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mapView != null) {
                        try {
                            setColorOnPolygon(polygon, color);
                        } catch (NullPointerException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }, 95);
        } else {
            deferredPolygon = polygon;
        }

    }

    private void setColorOnPolygon(Polygon polygon, int color) {
        if (color <= 0) {
            mapView.resetPolygon(polygon);
        } else {
            mapView.setColor(polygon, ContextCompat.getColor(getContext(), color), 0);
        }

    }

    private void clearHighlightedColours() {
        Set<java.util.Map.Entry<Polygon, Integer>> colours = originalColors.entrySet();
        for (java.util.Map.Entry<Polygon, Integer> pair : colours) {
            setColorOnPolygon(pair.getKey(), pair.getValue());
        }

        originalColors.clear();
        if (mTemporaryParkingLocation != null) removePin(mTemporaryParkingLocation);
        if (sParkingPin.getTempParkingCoordinatePin() != null) {
            removePin(sParkingPin.getTempParkingCoordinatePin().getOverlay2DImage(), sParkingPin.getTempParkingCoordinatePin().getCoordinate());
        }
    }

    private void showParkingDetail(boolean isThisTempParkingSpot) {
        String llDealsTitle = "";
        if (isThisTempParkingSpot)
            llDealsTitle = getResources().getString(R.string.parking_polygon_blue_dot_temporary);
        else llDealsTitle = getResources().getString(R.string.parking_polygon_store_name);

        showDirectionCard(true, IdType.AMENITY, -100, llDealsTitle, getString(R.string.parking_pinned_at_current_location), null);
        llDeals.setVisibility(View.VISIBLE);
        slidePanel(true);

        if (isThisTempParkingSpot) {
            tvParkingNote.setVisibility(View.GONE);
            tvDealName.setText(getResources().getString(R.string.parking_save_as_my_parking_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ParkingManager.isParkingLotSaved(getActivity())) {
                        AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                        alertDialogForInterest.getAlertDialog(
                                getActivity(),
                                R.string.title_change_parking_spot,
                                R.string.warning_change_my_parking_spot,
                                R.string.action_ok,
                                R.string.action_cancel,
                                new AlertDialogForInterest.DialogAnsweredListener() {
                                    @Override
                                    public void okClicked() {
                                        setAsParkingSpot(sParkingPin.getTempParkingCoordinatePin().getLatitude(),
                                                sParkingPin.getTempParkingCoordinatePin().getLongitude(),
                                                sParkingPin.getTempParkingCoordinatePin().getCoordinate().getMap().getAltitude());
                                    }
                                });
                    } else {
                        setAsParkingSpot(sParkingPin.getTempParkingCoordinatePin().getLatitude(),
                                sParkingPin.getTempParkingCoordinatePin().getLongitude(),
                                sParkingPin.getTempParkingCoordinatePin().getCoordinate().getMap().getAltitude());
                    }
                }
            });
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_parking_car_outline));
        } else {
            tvDealName.setText(getResources().getString(R.string.parking_remove_my_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                    alertDialogForInterest.getAlertDialog(
                            getActivity(),
                            R.string.title_remove_my_parking,
                            R.string.warning_remove_my_parking,
                            R.string.action_ok,
                            R.string.action_cancel,
                            new AlertDialogForInterest.DialogAnsweredListener() {
                                @Override
                                public void okClicked() {
                                    didTapNothing();
                                    removePin(sParkingPin.getParkingCoordinatePin().getOverlay2DImage(), sParkingPin.getParkingCoordinatePin().getCoordinate());

                                    ParkingManager.removeParkingLot(getActivity());
                                    Amenities.saveToggle(getActivity(), Amenities.GSON_KEY_PARKING, false); //make sure to set this as false otherwise everytime map fragment's tapped, it will start parkingActivity
                                    toggleParkingCTA(false);
                                    mMainActivity.setUpRightSidePanel();
                                    Toast.makeText(getActivity(), "Removed Parking Spot", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
        ivAmenity.setVisibility(View.GONE); //parking doesn't have icon so manually hide it
        tvNumbOfDeals.setText("");
        llDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), ParkingActivity.class), Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
            }
        });
    }

    /**
     * @param location              parking spot location to drop a pin
     * @param isThisTempParkingSpot to determine whether this is for the saved parking pin or temporary parking pin near the store of interest
     * @param parkingLotName        parking name
     * @param entranceName          entrance name
     * @param parkingNote           parking note
     * @param parkingPosition       position of the parking in the parking arraylist
     */
    private void showParkingDetail(final Amenity location, boolean isThisTempParkingSpot, String parkingLotName, String entranceName, String parkingNote, final int parkingPosition, final int entrancePosition) {
        String llDealsTitle = "";
        if (isThisTempParkingSpot)
            llDealsTitle = getResources().getString(R.string.parking_polygon_store_name_temporary);
        else llDealsTitle = getResources().getString(R.string.parking_polygon_store_name);

        int externalId = Integer.MAX_VALUE;

        if (location.externalId != null && !location.externalId.equals("")) {
            externalId = Integer.valueOf(location.externalId);
        }

        showDirectionCard(true, IdType.AMENITY, externalId, llDealsTitle, parkingLotName + ", " + entranceName, null);
        llDeals.setVisibility(View.VISIBLE);
        slidePanel(true);

        if (isThisTempParkingSpot) {
            tvParkingNote.setVisibility(View.GONE);
            tvDealName.setText(getResources().getString(R.string.parking_save_as_my_parking_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ParkingManager.isParkingLotSaved(getActivity())) {
                        AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                        alertDialogForInterest.getAlertDialog(
                                getActivity(),
                                R.string.title_change_parking_spot,
                                R.string.warning_change_my_parking_spot,
                                R.string.action_ok,
                                R.string.action_cancel,
                                new AlertDialogForInterest.DialogAnsweredListener() {
                                    @Override
                                    public void okClicked() {
                                        setAsParkingSpot(parkingPosition, entrancePosition);
                                    }
                                });
                    } else {
                        setAsParkingSpot(parkingPosition, entrancePosition);
                    }
                }
            });
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_parking_car_outline));
        } else {
            if (!parkingNote.equals("")) {
                tvParkingNote.setVisibility(View.VISIBLE);
                tvParkingNote.setText(parkingNote);
            }
            tvDealName.setText(getResources().getString(R.string.parking_remove_my_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                    alertDialogForInterest.getAlertDialog(
                            getActivity(),
                            R.string.title_remove_my_parking,
                            R.string.warning_remove_my_parking,
                            R.string.action_ok,
                            R.string.action_cancel,
                            new AlertDialogForInterest.DialogAnsweredListener() {
                                @Override
                                public void okClicked() {
                                    if (BuildConfig.PARKING_POLYGON) {
                                        String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                                        showMySavedParkingPolygon(false, parkingId, true);
                                    } /*else {*/
                                    didTapNothing();
                                    removePin(location);
                                    ParkingManager.removeParkingLot(getActivity());
                                    Amenities.saveToggle(getActivity(), Amenities.GSON_KEY_PARKING, false); //make sure to set this as false otherwise everytime map fragment's tapped, it will start parkingActivity
                                    toggleParkingCTA(false);
                                    mMainActivity.setUpRightSidePanel();
                                    Toast.makeText(getActivity(), "Removed Parking Spot", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
        ivAmenity.setVisibility(View.GONE); //parking doesn't have icon so manually hide it
        tvNumbOfDeals.setText("");
        llDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityForResult(new Intent(getActivity(), ParkingActivity.class), Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
            }
        });

        ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_remove_parking));
        clearHighlightedColours();
    }

    private void setAsParkingSpot(final int parkingPosition, final int entrancePosition) {
        if (parkingPosition == -1) return;
        clearHighlightedColours();
        mTemporaryParkingLocation = null;
        ParkingManager.saveParkingSpotAndEntrance(getActivity(), "", parkingPosition, entrancePosition);
        mMainActivity.setUpRightSidePanel();
        onParkingClick(true, true);
        InfoFragment.getInstance().setParkingSpotCTA();
    }

    private void setAsParkingSpot(final Amenity location) {
        String parkingId;
        if (ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION) {
            parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
            showMySavedParkingPolygon(false, parkingId, true);
        }

        parkingId = location.id;
        ParkingManager.saveParkingSpotAndEntrance(getActivity(), parkingId);
        mMainActivity.setUpRightSidePanel();
        onParkingClick(true, true);
        InfoFragment.getInstance().setParkingSpotCTA();
    }

    private void setAsParkingSpot(double x, double y, double elevation) {
        if (x == 0 || y == 0) {
            Log.e(TAG, "parking location is wrong");
            return;
        }

        didTapNothing();
        ParkingManager.saveParkingSpotAndEntrance(getActivity(), x, y, elevation);
        mMainActivity.setUpRightSidePanel();
        onParkingClick(true, true);
        InfoFragment.getInstance().setParkingSpotCTA();
    }

    private void showAmenityDetail(final Location location, final Drawable locationDrawable) {

        String categoryName = "";

        if (location.getCategories() != null && location.getCategories().length > 0) {
            categoryName = location.getCategories()[0].getName();
        }

        mAmenityClicked = getAmenityType(location);

        int externalId = Integer.MAX_VALUE;
        String amenityExternalId = getAmenityExternalId(location);

        if (!amenityExternalId.equals("")) {
            externalId = Integer.parseInt(amenityExternalId);
        }

        showDirectionCard(true, IdType.AMENITY, externalId, location.getName(), categoryName, locationDrawable);
    }

    private String getAmenityType(final Location location) {
        if (location instanceof Amenity) {
            return ((Amenity) location).amenityType;
        } else {
            return location.getName();
        }
    }

    private String getAmenityExternalId(final Location location) {

        String externalId = "";

        if (location instanceof Amenity) {
            externalId = ((Amenity) location).externalId;
        } else if (location instanceof EscalatorStairs) {
            externalId = ((EscalatorStairs) location).externalId;
        } else if (location instanceof Elevator) {
            externalId = ((Elevator) location).externalId;
        }

        return (externalId != null) ? externalId : "";
    }

    private void showLocationDetails(final Tenant location) {

        String categoryName = "";
        try {
            String externalId = location.externalId;
            KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceByExternalCode(externalId);
            categoryName = (kcpPlace != null) ? kcpPlace.getCategoryWithOverride() : location.getCategories()[0].getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IdType idType = IdType.EXTERNAL_CODE;
        String storeName = location.getName();
        showDirectionCard(true, idType, Integer.valueOf(location.externalId), storeName, categoryName, null);
    }

    private void showNavigatableDetails(final Location location) {
        if (location instanceof Tenant) {
            //Showing the direction/details card has to be delayed when highlighting is deferred because the map
            //and any depending layouts still need to be laid out. The switch floors toggle and blue dot compass won't
            //be shown properly if we don't delay the details. This only happens when the map is requested before it is loaded.
            long delay = isDeferred ? 500 : 0;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLocationDetails((Tenant) location);
                    isDeferred = false;
                }
            }, delay);

        }
    }

    private void clearLocationDetails() {
        mMainActivity.toggleDestinationEditor(true, null, null, null);
        showDirectionCard(false, null, 0, null, null, null);
    }

    private void clearMarkers() {
        mapView.removeAllElements();
    }

    private void startNavigation() {
        stopNavigation();
        navigationMode = true;
        mMainActivity.rvMap.setVisibility(View.INVISIBLE);
        Utility.closeKeybaord(getActivity());
    }

    private void stopNavigation() {
        if (path != null) {
            mapView.removeElement(path);
        }
        navigationMode = false;
        path = null;
    }

    //TODO: Clean up amenityMap processing

    /**
     * @param enabled
     * @param amenityName
     * @param clickOverlay if true, highlight pin as it was clicked to give it a focus
     * @param mapName      when mapName Exists, it's looking for a specific amenity on a specific floor
     */
    @Override
    public void onAmenityClick(boolean enabled, final String amenityName, final boolean clickOverlay, final @Nullable String mapName) {
        try {
            if (amenityMap.size() > 0) {
                final Location locationItem = amenityMap.get(amenityName);
                if (locationItem != null) {
                    if (enabled) {
                        String imageUrl = null;
                        if (locationItem instanceof EscalatorStairs) {
                            EscalatorStairs escalatorStairs = (EscalatorStairs) locationItem;
                            imageUrl = escalatorStairs.logo.getImage(getImagePinSize()).toString();
                        } else if (locationItem instanceof Elevator) {
                            Elevator elevator = (Elevator) locationItem;
                            imageUrl = elevator.logo.getImage(getImagePinSize()).toString();
                        } else if (locationItem instanceof Amenity) {
                            Amenity amenity = (Amenity) locationItem;
                            imageUrl = amenity.logo.getImage(getImagePinSize()).toString();
                        }
                        for (final Coordinate coords : Arrays.asList(locationItem.getNavigatableCoordinates())) {
                            Glide.with(getContext())
                                    .load(imageUrl)
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            mAmeityDrawable = new BitmapDrawable(getResources(), resource);
                                            mAmeityDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                                            dropPin(coords, locationItem, mAmeityDrawable);
                                            if (clickOverlay) {
                                                clickOverlayWithNameAndPosition(amenityName, mapName);
                                            }
                                        }
                                    });

                        }
                    } else {
                        if (mAmenityClicked.equals(amenityName)) {
                            showDirectionCard(false, null, 0, null, null, null);
                        }
                        removePin(locationItem);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private boolean isElevator(String amenityName) {
        return amenityName.equalsIgnoreCase("Elevator");
    }


    public void dropPinWithColor(final Coordinate coordinate, final Drawable pinDrawable) {
        //TODO: only add pins to the current floor
        Drawable clone = pinDrawable.getConstantState().newDrawable();
        clone.mutate(); //prevent all instance of drawables from being affected
        clone.setColorFilter(getResources().getColor(R.color.themeColor), PorterDuff.Mode.MULTIPLY); //change white colors
//        clone.setColorFilter(getResources().getColor(R.color.themeColor), PorterDuff.Mode.SRC_ATOP); //change the entire colors
        Overlay2DImage label = new Overlay2DImage(getImagePinSize(), getImagePinSize(), clone, getImagePinSize() / 2, getImagePinSize() / 2);
        label.setPosition(coordinate);
//        LocationLabelClicker clicker = new LocationLabelClicker(location, pinDrawable, label, coordinate);
        //if I remove the lable at the same spot and add another label with new LocationLabelClicker,
        //removed label's LocationLabelClicker gets called again so shouldn't add another labelClicker
        mSelectedPin = new Pin(coordinate, label);
        overlays.put(label, mLocationClickersMap.get(coordinate)); //should add to overlays so when removing pins, selected (highlighted) ones get removed too
        mapView.addElement(label);
    }

    private int getImagePinSize() {
        return PIN_AMENITY_IMAGE_SIZE_DP;
    }


    private int getBlueDotSize() {
        if (blueDotSizeInPx == 0) {
            blueDotSizeInPx = KcpUtility.dpToPx(context, PIN_BLUEDOT);
        }
        return blueDotSizeInPx;
    }

    private int getVortexAndDestinationPinSize() {
        return PIN_VORTEX_IMAGE_SIZE_DP;
    }

    @Override
    public void removeParkingPinAtLocation(Amenity parkingLocationPin) {
        removePin(parkingLocationPin);
    }

    @Override
    public void removeParkingPinAtCoordinate(Pin parkingCoordinatePin) {
        if (parkingCoordinatePin != null) {
            removePin(parkingCoordinatePin.getOverlay2DImage(), parkingCoordinatePin.getCoordinate());
        }
    }

    @Override
    public void removeTempParkingPinAtCoordinate(Pin parkingCoordinatePin) {
        mapView.removeElement(parkingCoordinatePin.getOverlay2DImage());
    }

    @Override
    public void dropBlueDot(double x, double y, int floor) {
        if (maps == null) return;
        if (maps.length <= floor) floor = 0;
        if (mFollowMode == FollowMode.CENTER) {
            focusOnBlueDot(floor, null);
        }
        android.location.Location targetLocation = MapUtility.getLocation(x, y);
        Overlay2DImage label;
        Coordinate coordinate = new Coordinate(targetLocation, maps[floor]);
        if (mBlueDotPin == null) { //first time dropping blue dot
            label = new Overlay2DImage(getBlueDotSize(), getBlueDotSize(), getResources().getDrawable(R.drawable.icn_bluebutton), getBlueDotSize() / 2, getBlueDotSize() / 2);
            mBlueDotPin = new Pin(coordinate, label, x, y);
            startPulseAnimation();
            setFollowMode(FollowMode.CENTER); //very first time it's found
        } else {
            mapView.removeElement(mBlueDotPin.getOverlay2DImage());
            if (mGreyDotDropped) {
                label = new Overlay2DImage(getBlueDotSize(), getBlueDotSize(), getResources().getDrawable(R.drawable.icn_bluebutton), getBlueDotSize() / 2, getBlueDotSize() / 2);
                mBlueDotPin = new Pin(coordinate, label, x, y);
            } else {
                label = mBlueDotPin.getOverlay2DImage();
            }
            mBlueDotPin.setLocation(coordinate, x, y);
        }

        label.setPosition(coordinate);
        mapView.addElement(label);

        mGreyDotDropped = false;
    }

    @Override
    public void dropGreyBlueDot() {
        if (mBlueDotPin != null) {
            android.location.Location targetLocation = MapUtility.getLocation(mBlueDotPin.getLatitude(), mBlueDotPin.getLongitude());
            int mapIndex = MapUtility.getIndexWithMapElevation(maps, mBlueDotPin.getCoordinate().getMap().getAltitude());
            Overlay2DImage label;
            Coordinate coordinate = new Coordinate(targetLocation, maps[mapIndex]);
            mapView.removeElement(mBlueDotPin.getOverlay2DImage());
            label = new Overlay2DImage(getBlueDotSize(), getBlueDotSize(), getResources().getDrawable(R.drawable.icn_greybutton), getBlueDotSize() / 2, getBlueDotSize() / 2);
            mBlueDotPin.setOverlay2DImage(label);
            label.setPosition(coordinate);
            mapView.addElement(label);
            mGreyDotDropped = true;

            if (mBlueDotCompass != null) {
                mapView.removeElement(mBlueDotCompass.getOverlay2DImage());
            }
        }
    }

    @Override
    public void dropHeading(double x, double y, final float heading, SLHeadingStatus headingStatus) {
        try {
            if (maps == null || mBlueDotPin == null) return;

            if (mBlueDotPin.getCoordinate().getMap().getAltitude() != maps[mCurrentLevelIndex].getAltitude()) {
                return;
            }
            int mapIndex = MapUtility.getIndexWithMapElevation(maps, mBlueDotPin.getCoordinate().getMap().getAltitude());

            android.location.Location targetLocation = MapUtility.getLocation(mBlueDotPin.getLatitude(), mBlueDotPin.getLongitude());
            final Overlay2DImage label;

            if (mBlueDotCompass == null) {
                label = new Overlay2DImage(getBlueDotSize(), getBlueDotSize(), getResources().getDrawable(R.drawable.icn_bluebutton), getBlueDotSize() / 2, getBlueDotSize() / 2);
            } else {
                label = mBlueDotCompass.getOverlay2DImage();
            }
            Coordinate coordinate = new Coordinate(targetLocation, maps[mapIndex]);
            if (mBlueDotCompass != null) {
                mapView.removeElement(mBlueDotCompass.getOverlay2DImage());
            }
            mBlueDotCompass = new Pin(coordinate, label);
            mBlueDotCompass.setCoordinate(coordinate);
            label.setRotation((float) Math.toRadians(heading) + (float) mBearingFromCamera);
            label.setPosition(coordinate);
            mapView.addElement(label);
            tempHeading = heading;
            headingDropped = true;
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener onTestGeofenceButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "SetGeofence(true)", Toast.LENGTH_SHORT).show();
            mMainActivity.mGeofenceManager.setGeofence(false);
            mMainActivity.mGeofenceManager.setGeofence(true);

        }
    };

    private OnStoreClickListener onStoreClickedListener = new OnStoreClickListener() {
        @Override
        public void onStoreClick(final int storeId, final String externalCode, final String storeName, final String categoryName) {
            try {
                Analytics.getInstance(mMainActivity).logEvent("MAP_Searchrequest_Term", "MAP", "Click on Search Result", mSearchString);
                Analytics.getInstance(mMainActivity).logEvent("MAP_Searchrequest_Click", "MAP", "Click on Search Result", storeName);

                if (!mMapLoaded) {
                    setMapInterface(new MapInterface() {
                        @Override
                        public void mapLoaded() {
                            onStoreClick(storeId, externalCode, storeName, categoryName);
                        }
                    });
                    return;
                }

                if (mSearchMode.equals(SearchMode.STORE)) {
                    stopNavigation();
                } else if (mSearchMode.equals(SearchMode.ROUTE_START)) {
                    if (mMainActivity.getDestStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_store), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(storeName, null);
                    if (!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();
                } else if (mSearchMode.equals(SearchMode.ROUTE_DESTINATION)) {
                    if (mMainActivity.getStartStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_store), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(null, storeName);
                    if (!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();
                }

                Polygon placePolygon = getPolygonWithPlaceExternalId(externalCode);
                if (placePolygon != null) {
                    focusOn(placePolygon, 0, (float) Math.PI/5);
                    didTapPolygon(placePolygon);
                } else {
                    showDirectionCard(true, IdType.ID, storeId, storeName, categoryName, null);
                }
                if (!mSearchMode.equals(SearchMode.STORE)) mMainActivity.moveFocusToNextEditText();
                mSearchString = "";
                setupRecyclerView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener onFollowButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (mFollowMode) {
                case NONE:
                    setFollowMode(FollowMode.CENTER);
                    break;
                case CENTER:
                    setFollowMode(FollowMode.NONE);
                    break;

            }
        }
    };

    private void setFollowMode(FollowMode followMode) {
        try {
            if (followMode == mFollowMode) return;
            if (isBlueDotShown()) {
                mFollowMode = followMode;
                updateFollowMode();
            } else {
                slIndoorLocationPresenter.onResume();
                AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                alertDialogForInterest.getAlertDialog(
                        getActivity(),
                        getActivity().getString(R.string.title_current_location_not_available),
                        getActivity().getString(R.string.warning_current_location_not_available),
                        getActivity().getString(R.string.action_got_it),
                        null,
                        new AlertDialogForInterest.DialogAnsweredListener() {
                            @Override
                            public void okClicked() {
                                return;
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPulseAnimation() {
        final int DURATION_RIPPLE = 900;
        final int RIPPLE_START_OFFSET = 200;
        if (Build.VERSION.SDK_INT >= 21) {
            ImageView ivCircle = (ImageView) view.findViewById(R.id.ivCircle);
            ivCircle.setBackground(getActivity().getResources().getDrawable(R.drawable.round_ripple));
            final RippleDrawable rippleDrawable = (RippleDrawable) ivCircle.getBackground();
            rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});

            for (int i = 0; i < 6; i++) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
                        rippleDrawable.setState(new int[]{});
                    }
                }, i * DURATION_RIPPLE + RIPPLE_START_OFFSET);
            }
        } else {
            Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse);
            ivFollowMode.startAnimation(pulse);

        }
    }

    private void updateFollowMode() {
        try {
            switch (mFollowMode) {
                case NONE:

                    if (mBlueDotCompass != null) {
                        mapView.removeElement(mBlueDotCompass.getOverlay2DImage());
                        mBlueDotCompass = null;
                    }


                    ivFollowMode.setImageResource(R.drawable.icn_current_location);
                    ivFollowMode.setSelected(false);
                    break;
                case CENTER:

                    if (mBlueDotCompass != null) {
                        mapView.removeElement(mBlueDotCompass.getOverlay2DImage());
                        mBlueDotCompass = null;
                    }

                    ivFollowMode.setImageResource(R.drawable.icn_current_location);
                    ivFollowMode.setSelected(true);
                    if (mBlueDotPin != null)
                        focusOnBlueDot(-100, mBlueDotPin.getCoordinate().getMap().getName());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusOnBlueDot(int floor, @Nullable String mapName) {
        try {
            if (mBlueDotPin != null) {
                if (mCurrentLevelIndex != floor) {
                    setMapLevel(floor, null, mapName);
                }
                focusOn(mBlueDotPin.getCoordinate(), 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isBlueDotShown() {
        return BluetoothManager.isBluetoothEnabled() && mBlueDotPin != null;
    }


    @Override
    public int getCurrentFloor() {
        return mCurrentLevelIndex;
    }

    @Override
    public void removeBlueDot() {
        if (mBlueDotPin != null) {
            mapView.removeElement(mBlueDotPin.getOverlay2DImage());
            mBlueDotPin = null;
            mSLHeadingStatus = SLHeadingStatus.UNDEFINED;
        }
    }

    public Overlay2DImage dropPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable, int pinSize) {
        Overlay2DImage label = new Overlay2DImage(pinSize, pinSize, pinDrawable, pinSize / 2, pinSize / 2);
        if (mLocationClickersMap != null && mLocationClickersMap.containsKey(coordinate)) {
            return null;
        }
        label.setPosition(coordinate);
        LocationLabelClicker clicker = new LocationLabelClicker(location, pinDrawable, label, coordinate);
        overlays.put(label, clicker);
        mLocationClickersMap.put(coordinate, clicker);
        mapView.addElement(label);
        return label;
    }

    public Overlay2DImage dropPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable) {
        return dropPin(coordinate, location, pinDrawable, getImagePinSize());
    }

    public void dropDealsPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable, String externalId) {
        //TODO: only add pins to the current floor
        Overlay2DImage label = new Overlay2DImage(getImagePinSize(), getImagePinSize(), pinDrawable, getImagePinSize() / 2, getImagePinSize() / 2);
        if (mLocationClickersMap != null && overlays.containsKey(coordinate)) {
            return;
        }
        label.setPosition(coordinate);

        LocationLabelClicker clicker = new LocationLabelClicker(location, pinDrawable, label, coordinate, externalId);
        overlays.put(label, clicker);
        dealsPinOverlays.add(label);
        mLocationClickersMap.put(coordinate, clicker);

        mapView.addElement(label);
    }

    private void dropDestinationPin(final Coordinate coordinate, final Drawable pinDrawable) {
        Overlay2DImage label = new Overlay2DImage(getVortexAndDestinationPinSize(), getVortexAndDestinationPinSize(), pinDrawable, getVortexAndDestinationPinSize() / 2, getVortexAndDestinationPinSize() / 2);
        label.setPosition(coordinate);
        mDestinationPin = new Pin(coordinate, label);
        mapView.addElement(label);
    }

    public void clickOverlayWithNameAndPosition(String amenityName, String mapName) {

        Location amenityItem = amenityMap.get(amenityName);
        if (amenityItem != null) {
            List<Coordinate> coords = Arrays.asList(amenityItem.getNavigatableCoordinates());
            for (Coordinate coordinate : coords) {
                if (mapName == null || (mapName != null && coordinate.getMap().getName().equals(mapName))) {
                    //if the map's already has the pin selected then return
                    if (mSelectedPin != null && mSelectedPin.getCoordinate() == coordinate) {
                        return;
                    }
                    LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(coordinate);
                    locationLabelClicker.onClick();
                    return;
                }
            }
        }
    }

    public void removePin(Overlay overlay, Coordinate coordinate) {
        try {
            mapView.removeElement(overlay);
            if (mLocationClickersMap.containsKey(coordinate))
                mLocationClickersMap.remove(coordinate);
            if (overlays.containsKey(overlay)) overlays.remove(overlay);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void removePin(final Location location) {
        try {
            for (HashMap.Entry<Overlay, LocationLabelClicker> entry : overlays.entrySet()) {

                Overlay overlay = entry.getKey();
                LocationLabelClicker locationLabelClicker = entry.getValue();

                if (locationLabelClicker.amenity == location || locationLabelClicker.escalatorStairs == location
                        || locationLabelClicker.elevator == location) {

                    mapView.removeElement(overlay);

                    mLocationClickersMap.remove(overlay.getPosition());
                    overlays.remove(overlay);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void removePin(Overlay overlay) {
        try {
            mLocationClickersMap.remove(overlay.getPosition());
            overlays.remove(overlay);
            mapView.removeElement(overlay);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onDealsClick(boolean enabled) {
        //TODO: when deals list's refreshed, this mRecommendedDealsExternalCodeList should be set to null to refresh this list too
        ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true); //RECOMMENDED DEALS
        // clear all the previous deals pin
        if (dealsPinOverlays != null) {
                for (Overlay overlay : dealsPinOverlays) {
                    removePin(overlay);
                }
                dealsPinOverlays.clear();
        }

        // put the deals pin if the toogle button is enabled
        if (enabled && dealContentPages != null) {
            for (KcpContentPage kcpContentPage : dealContentPages) {
                Location location = getLocationWithExternalCode(kcpContentPage.getExternalCode());
                if (location != null) {
                    List<Coordinate> coords = Arrays.asList(location.getNavigatableCoordinates());
                    for (Coordinate coordinate : coords) {
                        Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_deals, 0);
                        KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                        dropDealsPin(coordinate, location, amenityDrawable, kcpPlace.getExternalCode());

                    }
                }
            }
        }
    }

    public Drawable getDrawableFromView(int drawable, int backgroundResource) {

        ImageView amientyView = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.layout_amenity, null); //image is an overlay and not stuck anywhere
        if (backgroundResource != 0) amientyView.setBackgroundResource(backgroundResource);
        amientyView.setImageResource(drawable);
        amientyView.setDrawingCacheEnabled(true);

        amientyView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        amientyView.layout(0, 0, KcpUtility.dpToPx(getActivity(), PIN_PARKING_IMAGE_SIZE_DP), KcpUtility.dpToPx(getActivity(), PIN_PARKING_IMAGE_SIZE_DP));

        amientyView.buildDrawingCache(true);
        Bitmap bitmap = amientyView.getDrawingCache();

        Drawable d = new BitmapDrawable(getResources(), bitmap);
        return d;
    }

    public void showParkingSpotAtBlueDot() {
        if (mBlueDotPin == null) return;
        Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, R.drawable.circle_imageview_background_black);
        Overlay2DImage label = dropPin(mBlueDotPin.getCoordinate(), null, amenityDrawable);
        sParkingPin.setTempParkingCoordinatePin(new Pin(mBlueDotPin.getCoordinate(), label, mBlueDotPin.getLatitude(), mBlueDotPin.getLongitude()));
        showParkingDetail(true);
        focusOn(mBlueDotPin.getCoordinate(), 0, 0);
        //MAP-REPLACE mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL_NEAREST_PARKING); -- investigate for new SDK 0.14.0
        destinationPolygon = sParkingPin.getTempParkingCoordinatePin().getCoordinate();
    }

    /**
     * @param parkingLotPosition
     * @param polygon            find the closest parking spot(entrance) from the store within the lot
     */
    public void showParkingSpotFromDetailActivity(int parkingLotPosition, Polygon polygon) {
        try {
            if (polygon == null) return;
            if (BuildConfig.PARKING_POLYGON) {
                focusOn(polygon, 0, DEFAULT_TILT);
                didTapPolygon(polygon);
            }

            if (parkingLotPosition != -1) {
                HashMap<String, Amenity> parkingHashMap = this.parkingHashMap;
                Parking storeParking = ParkingManager.sParkings.getParkings().get(parkingLotPosition);
                List<ChildParking> childParkings = storeParking.getChildParkings();

                Amenity currentNearestParkingLocation = null;
                double currentDistanceFromParkingToStore = 0.0;
                ChildParking currentNearestChildParking = null;

                int entrancePosition = 0;
                for (int childParkingPosition = 0; childParkingPosition < childParkings.size(); childParkingPosition++) {
                    ChildParking childParking = childParkings.get(childParkingPosition);
                    String parkingId = childParking.getParkingId();
                    if (parkingHashMap.containsKey(parkingId)) {
                        Amenity parkingLocation = parkingHashMap.get(parkingId);
                        List<Coordinate> coords = Arrays.asList(parkingLocation.getNavigatableCoordinates());
                        if (coords.size() > 0) {
                            Coordinate parkingLotCoordinate = coords.get(0);
                            Coordinate storeCoordinate = polygon.getLocations()[0].getNavigatableCoordinates()[0];
                            double distance = storeCoordinate.metersFrom(parkingLotCoordinate);
                            if (currentDistanceFromParkingToStore == 0.0 || distance < currentDistanceFromParkingToStore) {
                                currentNearestParkingLocation = parkingLocation;
                                currentDistanceFromParkingToStore = distance;
                                currentNearestChildParking = childParking;
                                entrancePosition = childParkingPosition;
                            }
                        }
                    }
                }

                if (mTemporaryParkingLocation != null) removePin(mTemporaryParkingLocation);
                mTemporaryParkingLocation = currentNearestParkingLocation;

                String parkingLotName = storeParking.getName();
                String entranceName = currentNearestChildParking.getName();
                showParkingDetail(mTemporaryParkingLocation, true, parkingLotName, entranceName, "", parkingLotPosition, entrancePosition);

                Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, R.drawable.circle_imageview_background_black);
                dropPin(mTemporaryParkingLocation.getNavigatableCoordinates()[0], mTemporaryParkingLocation, amenityDrawable);
                focusOn(polygon, 0, DEFAULT_TILT);
                //MAP-REPLACE mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL_NEAREST_PARKING); -- investigate for new SDK 0.14.0

                destinationPolygon = mTemporaryParkingLocation;
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void showMySavedParkingPolygon(boolean showParkingSpot, String parkingId, boolean focus) {
        ArrayList<Polygon> polygons = getParkingPolygonsFromLocationWithId(parkingId);
        if (polygons != null && polygons.size() > 0) {
            if (showParkingSpot) {
                if (mSavedParkingPolygon != null && mOriginalColorsForParking != 0) {
                    //mSavedParkingPolygon.setColor(mOriginalColorsForParking);
                    mOriginalColorsForParking = 0;
                }
                sParkingPin.setParkingLocationPin(this.parkingHashMap.get(parkingId));
                mSavedParkingPolygon = polygons.get(0);
                didTapPolygon(mSavedParkingPolygon);
                if (focus) {
                    focusOn(mSavedParkingPolygon, 0, DEFAULT_TILT);

                }
            } else {
                if (mSavedParkingPolygon != null) { //mSavedParkingPolygon.setColor(mOriginalColorsForParking);
                    didTapNothing();
                    originalColors.put(mSavedParkingPolygon, mSavedParkingPolygon.color());
                }
                mSavedParkingPolygon = null;
                mOriginalColorsForParking = 0;
            }
        }
    }

    @Override
    public void onParkingClick(boolean enabled, boolean focus) {
        try {
            if (!ParkingManager.isParkingLotSaved(getActivity())) {
                final Intent intent = new Intent(getActivity(), ParkingActivity.class);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
            } else {
                toggleParkingCTA(enabled);

                setFollowMode(FollowMode.NONE);
                if (BuildConfig.PARKING_POLYGON) {
                    if (ParkingManager.getMyEntrance(getActivity()) != null) {
                        String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                        showMySavedParkingPolygon(enabled, parkingId, focus);
                    }
                }


                HashMap<String, Amenity> parkingHashMap = this.parkingHashMap;
                if (ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION) {
                    String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                    if (parkingHashMap.containsKey(parkingId)) {
                        sParkingPin.setParkingLocationPin(parkingHashMap.get(parkingId));
                        if (enabled) {
                            List<Coordinate> coords = Arrays.asList(sParkingPin.getParkingLocationPin().getNavigatableCoordinates());
                            for (final Coordinate coordinate : coords) {
                                Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, 0);
                                dropPin(coordinate, sParkingPin.getParkingLocationPin(), amenityDrawable);
                                if (mLocationClickersMap.containsKey(coordinate)) {
                                    mLocationClickersMap.remove(coordinate);
                                }
                                destinationPolygon = sParkingPin.getParkingLocationPin();
                                if (focus) {
                                   // focusOn(coordinate, 0, DEFAULT_TILT, coordinate.getMap());
                                    focusOn(coordinate, 0, DEFAULT_TILT);
                                    showSavedParkingDetail();
                                }
                                break; //prevent it from dropping two pin images in case there is more than one polygon
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            slidePanel(true);
                                        }
                                    });
                                }
                            }, 100);
                        } else {
                            removePin(sParkingPin.getParkingLocationPin());
                            didTapNothing();
                        }
                    }
                } else {
                    ParkingManager.ParkingSpot parkingSpot = ParkingManager.getSavedParkingSpot(getActivity());
                    if (parkingSpot != null) {
                        Coordinate savedParkingPinCoordinate = parkingSpot.getCoordinate(maps);
                        if (enabled && savedParkingPinCoordinate != null) {

                            Drawable parkingDrawable = getDrawableFromView(R.drawable.icn_car, 0);
                            Overlay2DImage label = dropPin(savedParkingPinCoordinate, null, parkingDrawable);
                            Pin parkingSpotPin = new Pin(savedParkingPinCoordinate, label);
                            sParkingPin.setParkingCoordinatePin(parkingSpotPin);
                            destinationPolygon = sParkingPin.getParkingCoordinatePin().getCoordinate();
                            if (focus) {
                                focusOn(sParkingPin.getParkingCoordinatePin().getCoordinate(), 0, DEFAULT_TILT);
                                showSavedParkingDetail();
                            }

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            slidePanel(true);
                                        }
                                    });
                                }
                            }, 100);
                        } else {
                            removePin(sParkingPin.getParkingCoordinatePin().getOverlay2DImage(), sParkingPin.getParkingCoordinatePin().getCoordinate());
                            didTapNothing();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private class LocationLabelClicker {

        public LocationLabelClicker(Location location, Drawable pinDrawable, Overlay2DImage label, Coordinate coordinate) {

            if (location instanceof Tenant) {
                this.Tenant = (Tenant) location;
            } else if (location instanceof EscalatorStairs) {
                this.escalatorStairs = (EscalatorStairs) location;
            } else if (location instanceof Elevator) {
                this.elevator = (Elevator) location;
            } else {
                this.amenity = (Amenity) location;
            }

            this.drawable = pinDrawable;
            this.label = label;
            this.coordinate = coordinate;
        }

        //if there is store associated with this location label
        public LocationLabelClicker(Location location, Drawable pinDrawable, Overlay2DImage label, Coordinate coordinate, String placeExternalId) {
            if (location.getType().equals("Tenant")) {
                this.Tenant = (Tenant) location;
            } else {
                this.amenity = (Amenity) location;
            }

            this.drawable = pinDrawable;
            this.label = label;
            this.coordinate = coordinate;
            this.placeExternalId = placeExternalId;
        }

        public EscalatorStairs escalatorStairs = null;
        public Elevator        elevator        = null;
        public Tenant          Tenant          = null;
        public Amenity         amenity         = null;
        public Drawable        drawable        = null;
        public Overlay2DImage  label           = null;
        public Coordinate      coordinate      = null;
        public String          placeExternalId = null;


        //TODO: if there are two pins, only one that overlaps the other one receives touch (it should pass it to other ones from SDK level)
        public void onClick() {
            try {
                if (path != null || coordinate.getMap().getAltitude() != maps[mCurrentLevelIndex].getAltitude()) {
                    return; // map shouldn't be clickable when the paths drawn or this indicates the pin clicked is not on the floor you are looking at
                }

                if (amenity != null) {

                    //TODO Refactor parking
                    if (amenity == sParkingPin.getParkingLocationPin()) {
                        String parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
                        String entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
                        String parkingNote = ParkingManager.getParkingNotes(getActivity());
                        showParkingDetail(amenity, false, parkingLotName, entranceName, parkingNote, -1, -1);
                    } else if (placeExternalId != null) { //if deal's clicked, tab the polygon instead
                        didTapPolygon(getPolygonWithPlaceExternalId(placeExternalId));
                        return;
                    }
                    focusAmenity(amenity);
                } else if (escalatorStairs != null) {
                    focusAmenity(escalatorStairs);
                } else if (elevator != null) {
                    focusAmenity(elevator);
                } else {
                    if (label == sParkingPin.getParkingCoordinatePin().getOverlay2DImage()) {
                        showParkingDetail(false);
                        focusOn(coordinate, DEFAULT_HEADING, DEFAULT_TILT);
                        destinationPolygon = coordinate;
                    } else if (label == mBlueDotPin.getOverlay2DImage()) {
                        //user's clicking on blue dot that's above the parking lot pin
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "error in overlay image onClick");
            }
        }

        private void focusAmenity(Location location) {

            destinationPolygon = coordinate;
            boolean shouldFocus = false;

            if (mSelectedPin == null) {
                highlightThisLabel();
                showAmenityDetail(location, drawable);
                shouldFocus = true;
            } else {
                Coordinate removableMarkerCoordinate = mSelectedPin.getCoordinate();
                replaceSelectedPinWithRemovedPin();

                if (removableMarkerCoordinate != coordinate) {
                    highlightThisLabel();
                    showAmenityDetail(location, drawable);
                    shouldFocus = true;
                }
            }
            if (shouldFocus) {
                focusOn(coordinate, CURRENT_HEADING, CURRENT_TILT);
            }
        }

        void highlightThisLabel() {
            dropPinWithColor(coordinate, drawable);

            if (amenity != null || escalatorStairs != null || elevator != null) {
                mRemovedPin = new Pin(coordinate, label);
            }

            mapView.removeElement(label);
        }
    }

    private void replaceSelectedPinWithRemovedPin() {
        if (mSelectedPin != null) {
            mapView.removeElement(mSelectedPin.getOverlay2DImage());
            overlays.remove(mSelectedPin.getOverlay2DImage());
            mSelectedPin = null;
        }
        if (mRemovedPin != null) {
            mapView.addElement(mRemovedPin.getOverlay2DImage());
            mRemovedPin = null;
            showDirectionCard(false, null, 0, null, null, null);
        }
    }

    @Override
    public void onPageActive() {
        Analytics.getInstance(getContext()).logScreenView(getActivity(), SCREEN_NAME);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (Constants.IS_APP_IN_PRODUCTION) {

        } else {
            menu.findItem(R.id.action_backend_vm).setVisible(false);
            menu.findItem(R.id.action_backend_mp).setVisible(false);
            menu.findItem(R.id.action_test).setVisible(false);
            menu.findItem(R.id.action_geofence_test).setVisible(false);
            menu.findItem(R.id.action_geofence_disconnect).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_map, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mFilterItem = menu.findItem(R.id.action_filter);
        parkingItem = menu.findItem(R.id.action_parking);

        mFilterItem.setIcon(ThemeManager.getThemedMenuDrawable(getContext(), R.drawable.icn_filter));
        mSearchItem.setIcon(ThemeManager.getThemedMenuDrawable(getContext(), R.drawable.icn_search));

        parkingItem.setIcon(ThemeManager.getThemedMenuDrawable(getContext(), R.drawable.icn_car));

        isParkingItemSelected = Amenities.isToggled(getContext(), Amenities.GSON_KEY_PARKING, false);
        toggleParkingCTA(isParkingItemSelected);

        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(new QueryTextListener());
        mSearchView.setQueryHint(getString(R.string.hint_search_store));
        mSearchItem.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setOnActionExpandListener(mSearchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mMainActivity.setActiveMallDot(true);
                        //BUG : onMenuItemActionCollapse is called when editStartStore or editDestStore's collapsed - is this because of requestFocus?
                        if (mSearchMode.equals(SearchMode.STORE) ||
                                (!mSearchMode.equals(SearchMode.STORE) && !mMainActivity.isEditTextsEmpty()))
                            mMainActivity.rvMap.setVisibility(View.INVISIBLE);


                        mFilterItem.setVisible(true);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mMainActivity.setActiveMallDot(false);
                        mSearchMode = SearchMode.STORE;
                        showDirectionCard(false, null, 0, null, null, null);
                        mFilterItem.setVisible(false);
                        mMainActivity.rvMap.setVisibility(View.VISIBLE);

                        mShowBlueDotHeader = false;
                        setupRecyclerView();

                        return true;
                    }
                });
    }

    public class FocusListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                int topMargin = (int) (KcpUtility.dpToPx(getActivity(), 95) - (KcpUtility.dpToPx(getActivity(), 55))); //directionEditor height - actionbar height
                mMainActivity.rvMap.setPadding(0, topMargin, 0, 0);
                mMainActivity.rvMap.setVisibility(View.VISIBLE);
                if (v.getId() == R.id.etDestStore) {
                    mSearchMode = SearchMode.ROUTE_DESTINATION;
                    mShowBlueDotHeader = false;
                    setupRecyclerView();
                } else if (v.getId() == R.id.etStartStore) {
                    mSearchMode = SearchMode.ROUTE_START;
                    if (isBlueDotShown()) {
                        mShowBlueDotHeader = true;
                        setupRecyclerView();
                    } else {
                        mShowBlueDotHeader = false;
                        setupRecyclerView();
                    }
                }
            } else {
                mMainActivity.rvMap.setPadding(0, 0, 0, 0);
                mMainActivity.rvMap.setVisibility(View.INVISIBLE);
            }
        }
    }

    private View.OnClickListener useMyLocationListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            try {
                String storeName = getString(R.string.bluedot_my_location);
                if (mSearchMode.equals(SearchMode.ROUTE_START)) {
                    if (mMainActivity.getDestStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_location), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(storeName, null);
                    if (!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();
                    drawPath();
                } else if (mSearchMode.equals(SearchMode.ROUTE_DESTINATION)) {
                    if (mMainActivity.getStartStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_location), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(null, storeName);
                    if (!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();
                    didTapPolygon((Polygon) startPolygon);
                }

                mMainActivity.moveFocusToNextEditText();
                mSearchString = "";
                setupRecyclerView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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

    public boolean isDirectionCardVisible() {
        if (rlDirection == null) {
            return false;
        }
        return rlDirection.getVisibility() == View.VISIBLE;
    }

    private void showDirectionCardNull() {
        showDirectionCard(false, null, 0, null, null, null);
    }

    /**
     * @param showCard
     * @param idType       if idType.AMENITY, show amenity icon, if idType.ID or idType.EXTERNAL_CODE, check for deals and show it at the bottom of the card
     * @param id           id of the pin
     * @param storeName
     * @param categoryName
     */
    public void showDirectionCard(final boolean showCard, final IdType idType, final int id, final String storeName, final String categoryName, final Drawable amenityDrawable) {

        Log.d("Card to show", "Show card : " + showCard + " type : " + idType + " id : " + id + " storename : " + storeName + " categoryname : " + categoryName + " drawable " + amenityDrawable);
        if (!showCard) {
            if (isDirectionCardVisible()) { //only do animation if direction card is visible
                slidePanel(false);
            }
            mAmenityClicked = "";
            return;
        }

        //if idType is NOT parking && (NOT amenity and id == 0 or -1) return
        if (!idType.equals(IdType.PARKING) && !idType.equals(IdType.AMENITY) && !idType.equals(IdType.INSTRUCTION)
                && (id == 0 || id == -1)) {
            return;
        }

        tvStoreName.setMaxLines(1);
        tvParkingNote.setVisibility(View.GONE);
        rlRoute.setVisibility(View.VISIBLE);

        if (amenityDrawable != null && idType.equals(IdType.AMENITY) || idType.equals(IdType.INSTRUCTION)) {
            tvCategoryName.setVisibility(View.GONE);
            ivAmenity.setVisibility(View.VISIBLE);
            ivAmenity.setImageDrawable(amenityDrawable);
            if (idType.equals(IdType.INSTRUCTION)) {
                tvStoreName.setMaxLines(2);
                rlRoute.setVisibility(View.GONE);
            }
        } else {
            tvCategoryName.setVisibility(View.VISIBLE);
            ivAmenity.setVisibility(View.GONE);
        }

        ArrayList<KcpContentPage> dealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true);
        ArrayList<KcpContentPage> recommendedDealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true);

        if (recommendedDealsList != null) {
            dealsList.removeAll(recommendedDealsList);
            dealsList.addAll(recommendedDealsList);
        }

        final ArrayList<KcpContentPage> dealsForThisStore = new ArrayList<KcpContentPage>();
        if (dealsList != null) {
            for (int i = 0; i < dealsList.size(); i++) {
                if ((idType.equals(IdType.ID) && dealsList.get(i).getStore().getPlaceId() == id) ||
                        (idType.equals(IdType.EXTERNAL_CODE) && (dealsList.get(i).getStore() != null && id == Integer.parseInt(dealsList.get(i).getStore().getExternalCode())))) { //dealsList.get(i).getStore() error occured because the deal didn't have any embedded store feb. 9th
                    dealsForThisStore.add(dealsList.get(i));
                }
            }
        }

        if (dealsForThisStore.size() > 0 || idType.equals(IdType.PARKING)) {
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_deals));
            llDeals.setVisibility(View.VISIBLE);
            if (dealsForThisStore.size() > 0)
                tvDealName.setText(dealsForThisStore.get(0).getTitle());

            if (dealsForThisStore.size() > 1) {
                int moreDeals = dealsForThisStore.size() - 1;
                String textEnd = moreDeals == 1 ? " More Deal" : " More Deals";
                tvNumbOfDeals.setText("+" + moreDeals + textEnd);
                tvNumbOfDeals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_HOME);
                        HomeFragment.getInstance().selectPage(HomeFragment.VIEWPAGER_PAGE_DEALS);
                    }
                });
            } else tvNumbOfDeals.setText("");

            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, dealsForThisStore.get(0));
                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        } else {
            llDeals.setVisibility(View.GONE);
        }
        slidePanel(true);

        mMainActivity.expandTopNav();
        if (mSearchItem != null) mSearchItem.collapseActionView();

        tvStoreName.setText(storeName);
        tvCategoryName.setText(categoryName);

        rlDirection.setVisibility(View.VISIBLE);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_slide_up_from_out_of_screen);
        slideUpAnimation.reset();
        rlDirection.startAnimation(slideUpAnimation);

        llDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Analytics.getInstance(mMainActivity).logEvent("MAP_Storeinformation_Click", "MAP", "Click on Store Information", storeName);
                KcpPlaces kcpPlace = null;
                if (idType.equals(IdType.ID))
                    kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(id);
                else if (idType.equals(IdType.EXTERNAL_CODE))
                    kcpPlace = KcpPlacesRoot.getInstance().getPlaceByExternalCode(String.valueOf(id));
                if (kcpPlace == null) return;

                if (kcpPlace != null) {
                    KcpContentPage kcpContentPage = new KcpContentPage();
                    kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);
                    getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_VIEW_STORE_ON_MAP);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    //internal method to test highlighting
    public void focusAll() {
        int delayMore = 0;
        for (final Tenant Tenant : locationHashmapByExternalId.values()) {
            focusHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Tenant.getPolygons().length > 0) {
                        Polygon polygon = Tenant.getPolygons()[0];
                        clearHighlightedColours();
                        highlightPolygon(polygon, R.color.themeColor);
                    }
                }
            }, 400 * delayMore++);

        }
    }

    private Handler focusHandler = new Handler();

    private void slidePanel(final boolean up) {
        if (getActivity() == null) {
            return;
        }
        final RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) rlDirection.getLayoutParams();
        View rootView = getActivity().findViewById(android.R.id.content);
        if (mRootViewHeight == 0) mRootViewHeight = rootView.getHeight();
        if (up) {
            if (llDeals.getVisibility() == View.VISIBLE) {
                param.height = (int) getActivity().getResources().getDimension(R.dimen.bottom_sheet_height_extended);
            } else {
                param.height = (int) getActivity().getResources().getDimension(R.dimen.bottom_sheet_height_normal);
            }
            rlDirection.setLayoutParams(param);
            if (rlRoute.getVisibility() != View.VISIBLE)
                param.height = (int) getActivity().getResources().getDimension(R.dimen.bottom_sheet_height_reduced);
            logger.debug("sliding up - rootView.getHeight() : " + mRootViewHeight + " rlSlidingPanel.getHeight() : " + rlSlidingPanel.getHeight() + " param.height : " + param.height);
            rlSlidingPanel.animate().setStartDelay(100).y(mRootViewHeight - rlSlidingPanel.getHeight() - (int) getResources().getDimension(R.dimen.map_level_panel_space_btn_direction_panel) - param.height);
        } else {
            rlDirection.setVisibility(View.GONE);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.anim_slide_down_out_of_screen);
            slideUpAnimation.reset();
            rlDirection.startAnimation(slideUpAnimation);
            rlSlidingPanel.animate().setStartDelay(100).y(mRootViewHeight - rlSlidingPanel.getHeight() - (int) getResources().getDimension(R.dimen.map_level_panel_bot_margin));
        }
    }

    public void showDirectionEditor(String start, String dest) {
        mMainActivity.toggleDestinationEditor(false, start, dest, new FocusListener());
    }
    private void toggleParkingCTA(boolean selected) {
        isParkingItemSelected = selected;
        Amenities.saveToggle(getContext(), Amenities.GSON_KEY_PARKING, isParkingItemSelected);
        if (parkingItem != null) {
            Drawable parkingItemIcon = parkingItem.getIcon();
            if (parkingItemIcon != null){
                parkingItemIcon.setColorFilter(ContextCompat.getColor(getContext(), selected ? R.color.themeColor : R.color.filter_off_color), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            mMainActivity.openRightDrawerLayout();
            return true;
        }
        if (id == R.id.action_parking) {
            isParkingItemSelected = !isParkingItemSelected;
            onParkingClick(isParkingItemSelected, isParkingItemSelected);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (slIndoorLocationPresenter != null) slIndoorLocationPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (slIndoorLocationPresenter != null) slIndoorLocationPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (slIndoorLocationPresenter != null) slIndoorLocationPresenter.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
