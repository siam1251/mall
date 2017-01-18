package com.ivanhoecambridge.mall.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
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
import com.ivanhoecambridge.mall.bluedot.BluetoothManager;
import com.ivanhoecambridge.mall.bluedot.FollowMode;
import com.ivanhoecambridge.mall.bluedot.MapViewWithBlueDot;
import com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenter;
import com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenterImpl;
import com.ivanhoecambridge.mall.constants.Constants;
import com.ivanhoecambridge.mall.factory.KcpContentTypeFactory;
import com.ivanhoecambridge.mall.interfaces.MapInterface;
import com.ivanhoecambridge.mall.managers.ThemeManager;
import com.ivanhoecambridge.mall.mappedin.Amenities;
import com.ivanhoecambridge.mall.mappedin.Amenities.OnParkingClickListener;
import com.ivanhoecambridge.mall.mappedin.AmenitiesManager;
import com.ivanhoecambridge.mall.mappedin.CustomLocation;
import com.ivanhoecambridge.mall.mappedin.MapUtility;
import com.ivanhoecambridge.mall.mappedin.ParkingPin;
import com.ivanhoecambridge.mall.mappedin.ParkingPinInterface;
import com.ivanhoecambridge.mall.mappedin.Pin;
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
import com.mappedin.sdk.RawData;
import com.mappedin.sdk.Venue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import factory.HeaderFactory;

import static com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenterImpl.sLocationAvailability;

/**
 * Created by Kay on 2016-06-20.
 */
//public class MapFragment extends SLIndoorLocationServiceFragment implements MapViewDelegate, Amenities.OnAmenityClickListener, Amenities.OnDealsClickListener, OnParkingClickListener, MapViewWithBlueDot {
public class MapFragment extends BaseFragment
        implements MapViewDelegate, Amenities.OnAmenityClickListener, Amenities.OnDealsClickListener, OnParkingClickListener, MapViewWithBlueDot, ParkingPinInterface {

    private final String TAG = "MapFragment";

    private static MapFragment sMapFragment;
    public static MapFragment getInstance(){
        if(sMapFragment == null) sMapFragment = new MapFragment();
        return sMapFragment;
    }

    @Override
    public void removeParkingPinAtLocation(CustomLocation parkingLocationPin) {
        removePin(parkingLocationPin);
    }

    @Override
    public void removeParkingPinAtCoordinate(Pin parkingCoordinatePin) {
        if(parkingCoordinatePin != null) {
            removePin(parkingCoordinatePin.getOverlay2DImage(), parkingCoordinatePin.getCoordinate());
        }
    }

    @Override
    public void removeTempParkingPinAtCoordinate(Pin parkingCoordinatePin) {
        mapView.removeMarker(parkingCoordinatePin.getOverlay2DImage());
    }


    enum SearchMode { STORE, ROUTE_START, ROUTE_DESTINATION }
    public SearchMode mSearchMode = SearchMode.STORE;

    enum IdType { ID, EXTERNAL_CODE, AMENITY, PARKING, INSTRUCTION };

    private ProgressBar pb;
    private View view;
    private SearchView mSearchView;
    //    private IndexableRecylerView rvMap;
    private RelativeLayout rlDirection;
    private TextView tvStoreName;
    private TextView tvCategoryName;
    private Button btnShowMap;

    private LinearLayout llDeals;
    private ImageView ivDeal;
    private LinearLayout llDirection;
    private TextView tvDealName;
    private TextView tvParkingNote;
    private TextView tvNumbOfDeals;
    private ImageView ivCompass;
    private FrameLayout flCompass;
    private TextView tvLevel;
    private LinearLayout llLevel;
    private LinearLayout llBlueDot;
    private RelativeLayout rlUpper;
    private RelativeLayout rlLower;
    private ThemeColorImageView ivUpper;
    private ThemeColorImageView ivLower;
    //    private ImageView ivUpper;
//    private ImageView ivLower;
    private ThemeColorImageView ivUpperBg;
    private ThemeColorImageView ivLowerBg;
    private ImageView ivAmenity;
    private ImageView ivShadow;
    private ThemeColorImageView ivFollowMode;
    private ImageView ivTest;
    private FrameLayout flMap; ///todo: disabled for testing
    private FrameLayout flCircle; ///todo: disabled for testing
    private RelativeLayout rlSlidingPanel; ///todo: disabled for testing

    private View viewRoute;
    private RelativeLayout rlRoute;
    private String mSearchString = "";
    public MenuItem mSearchItem;
    private MenuItem mFilterItem;
    public CategoryStoreRecyclerViewAdapter mPlaceRecyclerViewAdapter;
    //    private ArrayList<String> mRecommendedDealsExternalCodeList;
    private ArrayList<KcpContentPage> mRecommendedDealsContentPageList;
    private Drawable mAmeityDrawable;

    //MAPPED IN
    //when using getOverlayImageWithPadding
    private final int PIN_AMENITY_IMAGE_SIZE_DP = 24; //ends up 64 px
    private final int PIN_VORTEX_IMAGE_SIZE_DP = 24; //ends up 64 px
    private final int PIN_BLUEDOT = 36; //ends up 64 px
    private final int PIN_PARKING_IMAGE_SIZE_DP = 30; //ends up 64 px

    /*private final int PIN_AMENITY_IMAGE_SIZE_DP = 60;
    private final int PIN_PARKING_IMAGE_SIZE_DP = 60;*/

    /*private final int PIN_AMENITY_IMAGE_SIZE_DP = 97;
    private final int PIN_PARKING_IMAGE_SIZE_DP = 97;*/ //use with Overlay2DImage but the pin size varies a bit depending on the device resolution as of 0.4.5


    private final int CAMERA_ZOOM_LEVEL_NEAREST_PARKING = 90; //
    //    private final int CAMERA_ZOOM_LEVEL_DEFAULT = 40; //BIGGER - farther, SMALLER - closer
    private final int CAMERA_ZOOM_LEVEL_DEFAULT = 25; //BIGGER - farther, SMALLER - closer
    private final int CAMERA_ZOOM_LEVEL_ZOOMED_IN = 2; //BIGGER - farther, SMALLER - closer
    private final int BLUR_RADIUS = 20;
    private boolean accessibleDirections = false;
    private MapViewDelegate delegate = this;

    private MappedIn mappedIn = null;
    private MapView mapView = null;
    private Map[] maps;
    ArrayList<String> mMapInPath = new ArrayList<String>();
    private HashMap<Polygon, Integer> originalColors = new HashMap<Polygon, Integer>();
    private ConcurrentHashMap<Overlay, LocationLabelClicker> overlays = new ConcurrentHashMap<Overlay, LocationLabelClicker>();
    private ConcurrentHashMap<Coordinate, LocationLabelClicker> mLocationClickersMap = new ConcurrentHashMap<Coordinate, LocationLabelClicker>();
    private Venue activeVenue = null;
    private boolean navigationMode = false;
    private Path path;

    private Navigatable startPolygon = null;
    private Navigatable destinationPolygon = null;
    private Polygon mSavedParkingPolygon = null;
    private int mOriginalColorsForParking; //original colors for parking used to clear its highlights on polygon
    private int mCurrentLevelIndex = -50;
    //    private CustomLocation mSavedParkingLocation;
    private CustomLocation mTemporaryParkingLocation; //used to show the temporary parking spot from detail activity

    public String mPendingExternalCode;
    /*private Overlay2DImage mSelectedPin = null;//to keep track of highlited amenity drawable to set back to the original state in clearHighlighted
    private Overlay2DImage mRemovedPin = null; //overlayImage that was replaced by mSelectedOverlayImage*/

    private Pin mSelectedPin;
    private Pin mRemovedPin;
    private Pin mDestinationPin; //such as destination flag when drawing a paht
    private ArrayList<VortexPin> mVortexPins = new ArrayList<VortexPin>(); //such as destination flag when drawing a paht

    private String mAmenityClicked = ""; //to keep track of previously clicked amenity
    public boolean mMapLoaded = false;
    private MapInterface mMapInterface;
    private final float DEFAULT_PITCH = 0.7f;
    private boolean mAnimationInProgress = false;
    private boolean mBearingEntered = false; //indicate whether it has entered onCameraBearingChange to decide when to start showing the compass icon
    private int mLevelPanelYDistance = 0;
    private int mRootViewHeight = 0;


    //BLUE DOT
    private SLIndoorLocationPresenter slIndoorLocationPresenter;
    private static Pin mBlueDotPin;
    private Pin mBlueDotCompass;
    private FollowMode mFollowMode = FollowMode.NONE;
    private GestureDetector gestureDetector;
    private boolean mShowBlueDotHeader = false; //show whether to 'use Current Location' header in recyclerview when blue dot's available - show in destination editor, don't show in normal search mode
    private static ParkingPin sParkingPin;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(sParkingPin == null) sParkingPin = new ParkingPin(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        pb = (ProgressBar) view.findViewById(R.id.pb);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.themeColor),
                android.graphics.PorterDuff.Mode.MULTIPLY);
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
        btnShowMap = (Button) view.findViewById(R.id.btnShowMap);
        ivCompass = (ImageView) view.findViewById(R.id.ivCompass);
        ivCompass.bringToFront();
        flCompass = (FrameLayout) view.findViewById(R.id.flCompass);
        rlSlidingPanel = (RelativeLayout) view.findViewById(R.id.rlSlidingPanel);
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
                mapView.getCamera().setPositionTo(0, 0);
                mapView.getCamera().setRotationTo(DEFAULT_PITCH, 0);
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
        ivShadow = (ImageView) view.findViewById(R.id.ivShadow);
        ivFollowMode = (ThemeColorImageView) view.findViewById(R.id.ivFollowMode);
        flCircle = (FrameLayout) view.findViewById(R.id.flCircle);
        flCircle.setOnClickListener(onFollowButtonListener);
        ivTest = (ImageView) view.findViewById(R.id.ivTest);
        ivTest.setOnClickListener(onTestButtonListener);
        flMap = (FrameLayout) view.findViewById(R.id.flMap); //todo: disabled for testing

        viewRoute = (View) view.findViewById(R.id.viewRoute);
        viewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirectionEditor("", tvStoreName.getText().toString());
            }
        });

        mappedIn = new MappedIn(getActivity());
        mapView = new MapView(); //TODO: disabled for testing

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeMap();
            }
        });

        //todo: disabled for testing
        mMainActivity.setOnAmenityClickListener(MapFragment.this);
        mMainActivity.setOnDealsClickListener(MapFragment.this);
        mMainActivity.setOnParkingClickListener(MapFragment.this);
        setupRecyclerView();
        setHasOptionsMenu(true);
        setViewListener();
        initializeMap();

        if(BuildConfig.BLUEDOT) {
            slIndoorLocationPresenter = new SLIndoorLocationPresenterImpl(getActivity(), this);
        }

        return view;
    }

    private void setViewListener(){
        mMainActivity.rlDestinationEditor.setTag(mMainActivity.rlDestinationEditor.getVisibility());
        mMainActivity.rlDestinationEditor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newVis = mMainActivity.rlDestinationEditor.getVisibility();
                if((int)mMainActivity.rlDestinationEditor.getTag() != newVis) {
                    mMainActivity.rlDestinationEditor.setTag(mMainActivity.rlDestinationEditor.getVisibility());
                    if(flCompass.getVisibility() == View.VISIBLE) {
                        animateCompass();
                    }
                }
            }
        });
    }

    private void animateCompass(){
        if(mMainActivity.rlDestinationEditor.getVisibility() == View.VISIBLE){ //Visibility changed!
            flCompass.animate().setStartDelay(100).y((int) getResources().getDimension(R.dimen.map_compass_top_width_expanded));
        } else {
            flCompass.animate().setStartDelay(100).y((int) getResources().getDimension(R.dimen.map_compass_top_width));
        }
    }

    public void initializeMap() {
        if(mMainActivity.mSplashScreenGone){
            btnShowMap.setVisibility(View.GONE);
            btnShowMap = null;
            pb.setVisibility(View.VISIBLE);
            mappedIn.getVenues(new GetVenuesCallback());
        } else {
            mMainActivity.setOnReadyToLoadMapListener(new MainActivity.ReadyToLoadMapListener() {
                @Override
                public void onReady() {
                    initializeMap();
                }
            });
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

        if(mSearchString.equals("")) kcpPlacesFiltered = new ArrayList<>(kcpPlaces);
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
        if(startIndex == 1) mPlaceRecyclerViewAdapter.addHeader(useMyLocationListener);
        for(int i = startIndex; i < kcpPlacesFiltered.size(); i++){
            String storeName = kcpPlacesFiltered.get(i).getPlaceName().toUpperCase();
            String currentStoreNameStartLetter = "";
            if(storeName.length() > 0) currentStoreNameStartLetter = String.valueOf(storeName.charAt(0));
            if(startLetter == null || !startLetter.equals(currentStoreNameStartLetter)) {
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

    }

    private Polygon getPolygonWithPlaceExternalId(String placeExternalCode){
        ArrayList<Polygon> polygons = CustomLocation.getPolygonsFromLocationWithExternalCode(placeExternalCode);
        if(polygons != null && polygons.size() > 0) {
            return polygons.get(0);
        }
        return null;
    }

    // Get the basic info for all Venues we have access to
    private class GetVenuesCallback implements MappedinCallback<Venue[]> {
        @Override
        public void onCompleted(final Venue[] venues) {
            //todo: disabled for testing
            for(Venue venue : venues){
                if(venue.getName().equals(HeaderFactory.MAP_VENUE_NAME)){
                    activeVenue = venue;
                }
            }
            if(activeVenue == null || getActivity() == null) return;
            //TODO: choose fragment method
            android.app.FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            if(!mapView.isAdded()){
                Log.d("test", "MAP WAS NOT ADDED!");
                transaction.add(R.id.flMap, mapView);
            } else {
                Log.e("test", "MAP ALREADY ADDED!");
                transaction.remove(mapView);
                mapView = new MapView();
                transaction.add(R.id.flMap, mapView);
            }
            transaction.commit();
            mapView.setDelegate(delegate);
            mappedIn.getVenue(activeVenue, accessibleDirections, new CustomLocationGenerator(), new GetVenueCallback());
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
            maps = venue.getMaps();
            if (maps.length == 0) {
                Logger.log("No maps! Make sure your venue is set up correctly!");
                return;
            }

            if(maps.length > 0) {
                //set the map elevation if it exists
                Arrays.sort(maps, new Comparator<Map>() {
                    @Override
                    public int compare(Map a, Map b) {
                        return (int) (a.getElevation() - b.getElevation());
                    }
                });
            }

            int groundMapIndex = MapUtility.getGroundMapIndex(maps);
            if(groundMapIndex == -50) groundMapIndex = 0;
            setMapLevel(groundMapIndex, null, null);
            loadAmenitiesAndParkingLot();

            if(pb != null) pb.setVisibility(View.GONE);
            setMapLevelArrowVisibility();
            setBlueDotIconVisibility();

            mMapLoaded = true;
            if(mMapInterface != null) mMapInterface.mapLoaded();
        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading Venue: " + e);
        }
    }

    private void setMapLevelArrowVisibility(){
        if(maps != null && maps.length > 1) {
            rlSlidingPanel.setVisibility(View.VISIBLE);
            llLevel.setVisibility(View.VISIBLE);
        }
    }

    private void setBlueDotIconVisibility(){
        if(BuildConfig.BLUEDOT) {
            rlSlidingPanel.setVisibility(View.VISIBLE);
            llBlueDot.setVisibility(View.VISIBLE);
        }
    }

    public void setMapInterface(MapInterface mapInterface){
        mMapInterface = mapInterface;
    }

    private void loadAmenitiesAndParkingLot(){
        //LOAD AMENITIES, DEALS
        onDealsClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_DEAL, false));
        for(int i = 0; i < AmenitiesManager.sAmenities.getAmenityList().size(); i++){
            Amenities.Amenity amenity = AmenitiesManager.sAmenities.getAmenityList().get(i);
            for(String externalID : amenity.getExternalIds()) {
                onAmenityClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_AMENITY + externalID, amenity.isEnabled()), externalID, false, null);
            }
        }

        if(ParkingManager.isParkingLotSaved(getActivity())) {
            onParkingClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_PARKING, false), false);
        }
    }

    /**
     * set map level using one of the three params
     * @param mapIndex index of the map to load in maps array
     * @param shortName shortname of the map to load
     * @param mapName mapname of the map to load
     *
     */
    public void setMapLevel (int mapIndex, @Nullable String shortName, @Nullable String mapName){
        //todo: when setting map level from normal arrow button, follow the normal level order
        // if this is called by selecting the highlighted arrow, load the right level
        try {
            if(shortName != null){
                for(int i = 0; i < maps.length; i++){
                    Map map = maps[i];
                    if(map.getShortName().equals(shortName)){
                        if(i == mCurrentLevelIndex) {
                            setLevelImageView(maps);
//                            return;
                        }
                        mCurrentLevelIndex = i;
                        break;
                    }
                }
            } else if(mapName != null) {
                for(int i = 0; i < maps.length; i++){
                    Map map = maps[i];
                    if(map.getName().equals(mapName)){
                        if(i == mCurrentLevelIndex) {
                            setLevelImageView(maps);
//                            return;
                        }
                        mCurrentLevelIndex = i;
                        break;
                    }
                }
            } else {
                if(mapIndex == mCurrentLevelIndex) {
                    setLevelImageView(maps);
//                    return;
                }
                mCurrentLevelIndex = mapIndex;
            }

//            logger.debug("setting map level: " + mCurrentLevelIndex);
            mapView.setMap(maps[mCurrentLevelIndex]);
            tvLevel.setText(maps[mCurrentLevelIndex].getShortName());
            setLevelImageView(maps);
            showInstruction(maps[mCurrentLevelIndex].getElevation());

            //highlight the store that has been pending
            if(mPendingExternalCode != null) {
                ArrayList<Polygon> polygons = CustomLocation.getPolygonsFromLocationWithExternalCode(mPendingExternalCode);
                mPendingExternalCode = null;
                if(polygons != null && polygons.size() > 0) {
                    showStoreOnTheMapFromDetailActivity(polygons.get(0));
                }
            }

            /*if(destinationPolygon != null) {
                Coordinate coordinate = null;
                if(destinationPolygon instanceof Polygon){
                    coordinate = ((Polygon) destinationPolygon).getLocations().get(0).getNavigatableCoordinates().get(0);
                } else if(destinationPolygon instanceof CustomLocation){
                    List<Coordinate> coords = ((CustomLocation) destinationPolygon).getNavigatableCoordinates();
                    coordinate = coords.get(0);
                }
                mapView.getCamera().focusOn(coordinate);
                zoomInOut();
            }*/

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private int upperLevel;
    private int lowerLevel;

    private void setLevelImageView(final Map[] maps) {
        try {

            upperLevel = mCurrentLevelIndex + 1;
            lowerLevel = mCurrentLevelIndex - 1;

            ivUpperBg.setSelected(false);
            ivLowerBg.setSelected(false);

            ivUpper.setColor(getResources().getColor(R.color.map_level_disabled), getResources().getColor(R.color.map_level_enabled));
            ivLower.setColor(getResources().getColor(R.color.map_level_disabled), getResources().getColor(R.color.map_level_enabled));

            if(mMapInPath.size() > 0) {
                String currentLevelName = maps[mCurrentLevelIndex].getShortName();
                if(mMapInPath.contains(currentLevelName)){
                    int currentLevelIndexInList = mMapInPath.indexOf(currentLevelName);
                    if(mMapInPath.size() > currentLevelIndexInList + 1){
                        String nextLevelInPath = mMapInPath.get(currentLevelIndexInList + 1);
                        int nextLevelIndex = MapUtility.getMapIndexWithShortName(maps, nextLevelInPath);
                        if(mCurrentLevelIndex > nextLevelIndex) { //path continued to lower levels
                            lowerLevel = nextLevelIndex;
                            ivUpperBg.setSelected(false);
                            ivLowerBg.setSelected(true);
                            ivLower.setColor(getResources().getColor(R.color.transparent), getResources().getColor(R.color.white));
                        }  else { //path continued to upper levels
                            upperLevel = nextLevelIndex;
                            ivUpperBg.setSelected(true);
                            ivLowerBg.setSelected(false);
                            ivUpper.setColor(getResources().getColor(R.color.transparent), getResources().getColor(R.color.white));
                        }
                    }
                }
            }

            //checking whether upperLevel exists && ivUpper has been colored white which means upper level has a path
            if(upperLevel >= maps.length && ivUpper.getSelectedFilterColor() != getResources().getColor(R.color.white)) {
                ivUpper.setSelected(false);
                rlUpper.setOnClickListener(null);
//                ivUpper.setOnClickListener(null);
            } else {
                ivUpper.setSelected(true);
                rlUpper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFollowMode(FollowMode.NONE);
                        setMapLevel(upperLevel, null, null);
                    }
                });
//                ivUpper.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        setMapLevel(upperLevel, null, null);
//                    }
//                });
            }

            if(lowerLevel < 0 && ivLower.getSelectedFilterColor() != getResources().getColor(R.color.white)) {
                ivLower.setSelected(false);
//                ivLower.setOnClickListener(null);
                rlLower.setOnClickListener(null);
            } else {
                ivLower.setSelected(true);
                /*ivLower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setMapLevel(lowerLevel, null, null);
                    }
                });*/
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

    private class CustomLocationGenerator implements LocationGenerator {
        @Override
        public Location locationGenerator(RawData rawData) throws Exception {
            return new CustomLocation(rawData);
        }
    }

    public void showStoreOnTheMapFromDetailActivity(Polygon polygon){
        try {
            mapView.getCamera().focusOn(polygon);

            destinationPolygon = null;
            showDirectionCard(false, null, 0, null, null, null);
            clearHighlightedColours();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MapFragment.getInstance().didTapPolygon(polygon);
        zoomInOut();
    }


    private void drawPath(){
        if (path != null) {
            didTapNothing();
            return;
        }

        //because only destinationPolygon can either be polygon or location
        Directions directions = null;
        Location arriveAtLocation = null;

        if(destinationPolygon instanceof Polygon){
            arriveAtLocation = ((Polygon) destinationPolygon).getLocations().get(0);
        } else if(destinationPolygon instanceof CustomLocation){
            arriveAtLocation = ((CustomLocation) destinationPolygon);
        } else if (destinationPolygon instanceof  Coordinate) {

        }

        String storeName = getString(R.string.bluedot_my_location);
        if(mMainActivity.getDestStoreName().equals(storeName) && mBlueDotPin != null) {
            directions = mBlueDotPin.getCoordinate().directionsTo(activeVenue, startPolygon, ((Polygon) startPolygon).getLocations().get(0), null);
//                    directions = startPolygon.directionsTo(activeVenue, mBlueDotPin.getCoordinate(), null, null);
        } else if (mMainActivity.getStartStoreName().equals(storeName) && mBlueDotPin != null){
            directions = destinationPolygon.directionsFrom(activeVenue, mBlueDotPin.getCoordinate(), null, arriveAtLocation); //FROM BLUEDOT
        }

        if (directions != null) {
            path = new Path(directions.getPath(), 1.5f, 1.5f, getResources().getColor(R.color.map_destination_store));
            mapView.addPath(path);
            setMapLevelsInPath(directions.getPath());
        }

        Coordinate destinationPolygonCoordinate = null;
        if(destinationPolygon instanceof Polygon) {
            highlightPolygon((Polygon) destinationPolygon, getResources().getColor(R.color.themeColor));
            destinationPolygonCoordinate = ((Polygon) destinationPolygon).getAnchor();
        } else if(destinationPolygon instanceof CustomLocation && !((CustomLocation) destinationPolygon).getAmenityType().equals(CustomLocation.TYPE_AMENITY_PARKING)) {
        } else if (destinationPolygon instanceof Coordinate){
            clearHighlightedColours();
            LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(destinationPolygon);
            if(locationLabelClicker != null) {
                destinationPolygonCoordinate = (Coordinate) destinationPolygon;
                locationLabelClicker.highlightThisLabel();
            }
        } else {
            destinationPolygonCoordinate = ((CustomLocation) destinationPolygon).getNavigatableCoordinates().get(0);
        }

        showDirectionCard(false, null, 0, null, null, null);
        if(mBlueDotPin.getCoordinate().getMap() != null) {
            setMapLevel(-50, mBlueDotPin.getCoordinate().getMap().getShortName(), null);
        }

        dropDestinationPin(destinationPolygonCoordinate, getResources().getDrawable(R.drawable.icn_wayfinding_destination));
        dropVortexOnThePath(directions.getInstructions());
        showInstruction(maps[mCurrentLevelIndex].getElevation());
    }

    public boolean didTapPolygon(Polygon polygon) {
        try {
            if(path != null || polygon == null) return true; //map shouldn't be clicakble when the paths drawn
            if (polygon.getLocations().size() == 0) { //TODO: clearHighlightedColours() used to be above this line - polygon.getLocationWithExternalCode().size() was sometimes 0 resulting in skipping highlightPolygon (it returned)
                return true;
            }
            /*if(polygon.getMap().getShortName() != null) setMapLevel(-50, polygon.getMap().getShortName());*/
            replaceSelectedPinWithRemovedPin();
            //tapping the same polygon should dismiss the detail and remove the highlights
            if(mSearchMode.equals(SearchMode.STORE) && destinationPolygon != null && destinationPolygon == polygon && mSavedParkingPolygon != polygon) {
                destinationPolygon = null;
                showDirectionCard(false, null, 0, null, null, null);
                clearHighlightedColours();
                return true;
            }

            if(mSearchMode.equals(SearchMode.STORE)) {
                destinationPolygon = polygon;
            } else if(mSearchMode.equals(SearchMode.ROUTE_START)){
                startPolygon = polygon;
            } else if(mSearchMode.equals(SearchMode.ROUTE_DESTINATION)){
                destinationPolygon = polygon;
            }

            if (navigationMode) {
                if (path != null) {
                    didTapNothing();
                    return true;
                }

                if (polygon.getLocations().size() == 0) {
                    return true;
                }

                //because only destinationPolygon can either be polygon or location
                Directions directions = null;
                Location arriveAtLocation = null;

                if(destinationPolygon instanceof Polygon){
                    arriveAtLocation = ((Polygon) destinationPolygon).getLocations().get(0);
                } else if(destinationPolygon instanceof CustomLocation){
                    arriveAtLocation = ((CustomLocation) destinationPolygon);
                } else if (destinationPolygon instanceof  Coordinate) {

                }

                directions = destinationPolygon.directionsFrom(activeVenue, startPolygon, ((Polygon) startPolygon).getLocations().get(0), arriveAtLocation); //real use

                if (directions != null) {
                    path = new Path(directions.getPath(), 1.5f, 1.5f, getResources().getColor(R.color.map_destination_store));
                    mapView.addPath(path);
                    setMapLevelsInPath(directions.getPath());
                }

                Coordinate destinationPolygonCoordinate = null;
                if(destinationPolygon instanceof Polygon) {
                    highlightPolygon((Polygon) destinationPolygon, getResources().getColor(R.color.themeColor));
                    destinationPolygonCoordinate = ((Polygon) destinationPolygon).getAnchor();
                } else if(destinationPolygon instanceof CustomLocation && !((CustomLocation) destinationPolygon).getAmenityType().equals(CustomLocation.TYPE_AMENITY_PARKING)) {
                    //when path's drawn to amenity from store (ex. a&w to washroom), route's drawn to the nearest amenity. so that nearest amenity pin needs to be highlighted
                    /*ArrayList<Coordinate> navigatableCoords = ((CustomLocation) destinationPolygon).getNavigatableCoordinates() ;
                    Coordinate startPolygonCoord = ((Polygon) startPolygon).getLocations().get(0).getNavigatableCoordinates().get(0);
                    if(navigatableCoords != null && startPolygonCoord != null) {
                        int nearestCoordinatePosition = MapUtility.getNearestCoordinatePositionFromStore(navigatableCoords, startPolygonCoord);
                        clearHighlightedColours();
                        LocationLabelClicker locationLabelClicker = getLabelClicker(((CustomLocation) destinationPolygon).getAmenityType(), nearestCoordinatePosition);
                        if(locationLabelClicker != null) {
                            destinationPolygonCoordinate = navigatableCoords.get(nearestCoordinatePosition);
                            locationLabelClicker.highlightThisLabel();
                        }
                    }*/
                } else if (destinationPolygon instanceof Coordinate){
                    Coordinate startPolygonCoord = ((Polygon) startPolygon).getLocations().get(0).getNavigatableCoordinates().get(0);
                    clearHighlightedColours();
                    LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(destinationPolygon);
                    if(locationLabelClicker != null) {
                        destinationPolygonCoordinate = (Coordinate) destinationPolygon;
                        locationLabelClicker.highlightThisLabel();
                    }
                } else {
                    destinationPolygonCoordinate = ((CustomLocation) destinationPolygon).getNavigatableCoordinates().get(0);
                }

                if(((Polygon) startPolygon).getMap().getShortName() != null) {
                    setMapLevel(-50, ((Polygon) startPolygon).getMap().getShortName(), null);
                }
                highlightPolygon((Polygon) startPolygon, getResources().getColor(R.color.map_destination_store));
                showDirectionCard(false, null, 0, null, null, null);
                dropDestinationPin(destinationPolygonCoordinate, getResources().getDrawable(R.drawable.icn_wayfinding_destination));
                dropVortexOnThePath(directions.getInstructions());
                showInstruction(maps[mCurrentLevelIndex].getElevation());
                setFollowMode(FollowMode.NONE);

                return true;
            } else {
                clearHighlightedColours();
                if(destinationPolygon instanceof Polygon){
                    highlightPolygon((Polygon) destinationPolygon, getResources().getColor(R.color.themeColor));
//                    mapView.getCamera().focusOn(polygon);
//                    zoomInOut();
                    try {
                        if(mSavedParkingPolygon != null && destinationPolygon == mSavedParkingPolygon) {
                            mapView.getCamera().focusOn(polygon);
                            zoomInOut();
                            showSavedParkingDetail();
                        } else {
                            showLocationDetails((CustomLocation) ((Polygon) destinationPolygon).getLocations().get(0));
//                            if(polygon.getMap().getShortName() != null) setMapLevel(-50, polygon.getMap().getShortName(), null);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                        e.printStackTrace();
                    }
                }
                if(polygon.getMap().getShortName() != null) setMapLevel(-50, polygon.getMap().getShortName(), null);
            }
        } catch (Resources.NotFoundException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (Exception e) {
            if(((Polygon) startPolygon).getMap().getShortName() != null) {
                setMapLevel(-50, ((Polygon) startPolygon).getMap().getShortName(), null);
            }
            logger.error(e);
            e.printStackTrace();
        }
        return true;
    }

    private void dropVortexOnThePath(List<Instruction> instructions){
        for(Instruction instruction : instructions) {
            logger.debug("type : " + instruction.atLocation.getType() + " : " + instruction.instruction);
            if(VortexPin.isVortex(instruction)) {
                VortexPin vortexPin = new VortexPin(getActivity(), instruction);
                mVortexPins.add(vortexPin);
                dropVortexPin(vortexPin);
            }
        }
    }

    private void dropVortexPin(VortexPin vortexPin){
        Overlay2DImage label = new Overlay2DImage(getVortexAndDestinationPinSize(), getVortexAndDestinationPinSize(), vortexPin.getVortexPinDrawable());
        label.setPosition(vortexPin.getVortexCoordinate());
        vortexPin.setVortexPin(new Pin(vortexPin.getVortexCoordinate(), label));
        mapView.addMarker(label, false);
    }

    private void showInstruction(double elevation){
        if(mVortexPins.size() > 0) {
            int position = VortexPin.getPostionOfVortext(mVortexPins, elevation);
            if(position == -1) {
                showDirectionCard(false, null, 0, null, null, null);
                return;
            }
            VortexPin vortexPin = mVortexPins.get(position);
            showDirectionCard(true, IdType.INSTRUCTION, 0, vortexPin.getTextInstruction(), "", vortexPin.getVortexInstructionDrawable());
        }
    }

    /**
     * creates arraylist of map shortnames of floors where the path's drawn onto
     * @param directionCoordinates
     */
    private void setMapLevelsInPath(Coordinate[] directionCoordinates){
        mMapInPath.clear();
        for(Coordinate coordinate : directionCoordinates){
            String mapShortName = coordinate.getMap().getShortName();
            if(!mMapInPath.contains(mapShortName)) mMapInPath.add(mapShortName);
        }
    }

    private void highlightMapArrow(){

    }


    private void zoomInOut(){
        /*float zoomLevel = CAMERA_ZOOM_LEVEL_DEFAULT; //camera's currently too zoomed out
        //allow the camera to zoom in if it's too far away or keep its current zoom if it's close enough
        if(mapView.getCamera().getZoom() <= CAMERA_ZOOM_LEVEL_DEFAULT) { //already zoomed in - keep the same zoom
            zoomLevel = mapView.getCamera().getZoom();
        }
        mapView.getCamera().setZoomTo(zoomLevel);*/
        mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL_DEFAULT);
    }

    private void showSavedParkingDetail(){
        String parkingLotName;
        String entranceName;
        String parkingNote = ParkingManager.getParkingNotes(getActivity());

        if(ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION) {
            parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
            entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
            showParkingDetail(sParkingPin.getParkingLocationPin(), false, parkingLotName, entranceName, parkingNote, -1, -1);
        } else { //sParkingPin.getParkingLocationPin() == null
            showParkingDetail(false);
        }
    }

    public boolean didTapOverlay(Overlay overlay) {
        LocationLabelClicker clicker = overlays.get(overlay);
        if (clicker != null) {
            clicker.onClick();
        } else {
            Logger.log("No onClick");
        }
        return true;
    }

    public void didTapNothing() {
        clearHighlightedColours();
        clearLocationDetails();
        stopNavigation();

        startPolygon = null;
        destinationPolygon = null;
        mSearchMode = SearchMode.STORE;
        mMapInPath.clear();
        setLevelImageView(maps);
        if(mDestinationPin != null) {
            mapView.removeMarker(mDestinationPin.getOverlay2DImage());
            mDestinationPin = null;
        }

        if(mVortexPins.size() > 0) {
            for(VortexPin vortexPin : mVortexPins){
                mapView.removeMarker(vortexPin.getVortexPin().getOverlay2DImage());
            }
            mVortexPins.clear();
        }

        replaceSelectedPinWithRemovedPin();
    }

    @Override
    public void onCameraBearingChange(final double bearing) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(flCompass.getVisibility() != View.VISIBLE && !mAnimationInProgress && mBearingEntered){
                    flCompass.setAnimation(ProgressBarWhileDownloading.getStartAnimation(null));
                    flCompass.setVisibility(View.VISIBLE);
                    animateCompass();
                }
//                logger.debug("bearing :" + (float)(bearing/Math.PI*180));
                ivCompass.setRotation((float)(bearing/Math.PI*180));
                mBearingEntered = true;
            }
        });
    }

    public void slideDownCompass(boolean slideDown){
        final int yBy = KcpUtility.dpToPx(getActivity(), 40);
        final int yInitialPosition = KcpUtility.dpToPx(getActivity(), 55);
        final int yFinalPosition = (int) getResources().getDimension(R.dimen.map_compass_margin);
        if(slideDown) {
            Animation anim = new TranslateAnimation(0, 0, 0, yBy);
            anim.setDuration(100);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flCompass.getLayoutParams();
                    lp.topMargin = yInitialPosition; // use topmargin for the y-property, left margin for the x-property of your view
                    flCompass.setLayoutParams(lp);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            flCompass.startAnimation(anim);


        } else {
            Animation anim = new TranslateAnimation(0, 0, 0, -yBy);
            anim.setDuration(100);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) flCompass.getLayoutParams();
                    lp.topMargin = yFinalPosition; // use topmargin for the y-property, left margin for the x-property of your view
                    flCompass.setLayoutParams(lp);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            flCompass.startAnimation(anim);
        }
    }

    private void highlightPolygon(Polygon polygon, int color) {
        if (mSavedParkingPolygon != null && mSavedParkingPolygon == polygon && mOriginalColorsForParking == 0) {
            //mOriginalColorsForParking != 0 so that when tapping already highlighted parking polygon, it doesn't save that highlight color to mOriginalColorsForParking
            mOriginalColorsForParking = polygon.getColor();
        } else {
            if (!originalColors.containsKey(polygon)) {
                try {
                    originalColors.put(polygon, polygon.getColor());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        polygon.setColor(color);
    }

    private void clearHighlightedColours() {
        Set<java.util.Map.Entry<Polygon, Integer>> colours = originalColors.entrySet();
        for  (java.util.Map.Entry<Polygon, Integer> pair : colours) {
            pair.getKey().setColor(pair.getValue());
        }

        originalColors.clear();

        /*if(mRemovedPin != null) {
            mapView.addMarker(mRemovedPin, false);
            mRemovedPin = null;
        }

        if(mSelectedPin != null) {
            mapView.removeMarker(mSelectedPin);
            mSelectedPin = null;
        }*/

        if(mTemporaryParkingLocation != null) removePin(mTemporaryParkingLocation);
        if(sParkingPin.getTempParkingCoordinatePin() != null) {
            removePin(sParkingPin.getTempParkingCoordinatePin().getOverlay2DImage(), sParkingPin.getTempParkingCoordinatePin().getCoordinate());
        }
    }

    private void showParkingDetail(boolean isThisTempParkingSpot){
        String llDealsTitle = "";
        if(isThisTempParkingSpot) llDealsTitle = getResources().getString(R.string.parking_polygon_blue_dot_temporary);
        else llDealsTitle = getResources().getString(R.string.parking_polygon_store_name);

        showDirectionCard(true, IdType.AMENITY, -100, llDealsTitle, getString(R.string.parking_pinned_at_current_location), null);
        llDeals.setVisibility(View.VISIBLE);
        slidePanel(true);

        if(isThisTempParkingSpot) {
            tvParkingNote.setVisibility(View.GONE);
            tvDealName.setText(getResources().getString(R.string.parking_save_as_my_parking_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ParkingManager.isParkingLotSaved(getActivity())) {
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
                                                sParkingPin.getTempParkingCoordinatePin().getCoordinate().getMap().getElevation());
                                    }
                                }).show();
                    } else {
                        setAsParkingSpot(sParkingPin.getTempParkingCoordinatePin().getLatitude(),
                                sParkingPin.getTempParkingCoordinatePin().getLongitude(),
                                sParkingPin.getTempParkingCoordinatePin().getCoordinate().getMap().getElevation());
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
                                    mMainActivity.setUpRightSidePanel();
                                    Toast.makeText(getActivity(), "Removed Parking Spot", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
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

//        ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_remove_parking));
//        clearHighlightedColours();
    }

    /**
     *
     * @param location parking spot location to drop a pin
     * @param isThisTempParkingSpot to determine whether this is for the saved parking pin or temporary parking pin near the store of interest
     * @param parkingLotName parking name
     * @param entranceName entrance name
     * @param parkingNote parking note
     * @param parkingPosition position of the parking in the parking arraylist
     */
    private void showParkingDetail(final CustomLocation location, boolean isThisTempParkingSpot, String parkingLotName, String entranceName, String parkingNote, final int parkingPosition, final int entrancePosition){
        String llDealsTitle = "";
        if(isThisTempParkingSpot) llDealsTitle = getResources().getString(R.string.parking_polygon_store_name_temporary);
        else llDealsTitle = getResources().getString(R.string.parking_polygon_store_name);

        showDirectionCard(true, IdType.AMENITY, Integer.valueOf(location.getExternalID()), llDealsTitle, parkingLotName + ", " + entranceName, null);
        llDeals.setVisibility(View.VISIBLE);
        slidePanel(true);

        if(isThisTempParkingSpot) {
            tvParkingNote.setVisibility(View.GONE);
            tvDealName.setText(getResources().getString(R.string.parking_save_as_my_parking_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ParkingManager.isParkingLotSaved(getActivity())) {
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
                                }).show();
                    } else {
                        setAsParkingSpot(parkingPosition, entrancePosition);
                    }
                }
            });
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_parking_car_outline));
        } else {
            if(!parkingNote.equals("")) {
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
                                    if(BuildConfig.PARKING_POLYGON) {
                                        String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                                        showMySavedParkingPolygon(false, parkingId, true);
                                    } /*else {*/
                                    didTapNothing();
                                    removePin(location);

                                    ParkingManager.removeParkingLot(getActivity());
                                    Amenities.saveToggle(getActivity(), Amenities.GSON_KEY_PARKING, false); //make sure to set this as false otherwise everytime map fragment's tapped, it will start parkingActivity
                                    mMainActivity.setUpRightSidePanel();
                                    Toast.makeText(getActivity(), "Removed Parking Spot", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
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

    private void setAsParkingSpot(final int parkingPosition, final int entrancePosition){
        if(parkingPosition == -1) return;
        clearHighlightedColours();
        mTemporaryParkingLocation = null;
        ParkingManager.saveParkingSpotAndEntrance(getActivity(), "", parkingPosition, entrancePosition);
        mMainActivity.setUpRightSidePanel();
        onParkingClick(true, true);
        InfoFragment.getInstance().setParkingSpotCTA();
    }

    private void setAsParkingSpot(final CustomLocation location){
        String parkingId;
//        if(ParkingManager.isParkingLotSaved(getActivity())){
        if(ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION){
            parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
            showMySavedParkingPolygon(false, parkingId, true);
        }

        parkingId = location.getId();
        ParkingManager.saveParkingSpotAndEntrance(getActivity(), parkingId);
        mMainActivity.setUpRightSidePanel();
        onParkingClick(true, true);
        InfoFragment.getInstance().setParkingSpotCTA();
    }

    private void setAsParkingSpot(double x, double y, double elevation){
        if(x == 0 || y == 0) {
            Log.e(TAG, "parking location is wrong");
            return;
        }

        didTapNothing();
        ParkingManager.saveParkingSpotAndEntrance(getActivity(), x, y, elevation);
        mMainActivity.setUpRightSidePanel();
        onParkingClick(true, true);
        InfoFragment.getInstance().setParkingSpotCTA();
    }

    private void showAmenityDetail(final CustomLocation location, final Drawable amenityDrawable){
        logger.debug("Location: name = " + location.getName() + " externalID = " + location.getExternalID());
        String categoryName = "";
        try {
            if(location.getCategories() != null && location.getCategories().size() > 0) categoryName = location.getCategories().get(0).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAmenityClicked = location.getAmenityType();
        showDirectionCard(true, IdType.AMENITY, Integer.valueOf(location.getExternalID()), location.getName(), categoryName, amenityDrawable);
        clearHighlightedColours();
    }

    private void showLocationDetails(final CustomLocation location) {
        logger.debug("Location: name = " + location.getName() + " externalID = " + location.getExternalID());

        String categoryName = "";
        try {
            String externalId = location.getExternalID();
            KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceByExternalCode(externalId);
            if(kcpPlace != null) categoryName = kcpPlace.getCategoryWithOverride();
            else categoryName = location.getCategories().get(0).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IdType idType = IdType.EXTERNAL_CODE;
        String storeName = location.getName();
        //Below should only be entered if parking polygon's enabled (if(BuildConfig.ParkingPolygonEnabled))
        if(location.getAmenityType() != null && location.getAmenityType().equals(CustomLocation.TYPE_AMENITY_PARKING)) {
            idType = IdType.PARKING;
            storeName = ParkingManager.sParkings.getChildParkingNameById(location.getId());

            String llDealsTitle = getResources().getString(R.string.parking_polygon_parking_lot);
            showDirectionCard(true, idType, Integer.valueOf(location.getExternalID()), llDealsTitle, storeName, null);
            ivDeal.setVisibility(View.VISIBLE);
            slidePanel(true);
            tvParkingNote.setVisibility(View.GONE);
            tvDealName.setText(getResources().getString(R.string.parking_save_as_my_parking_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ParkingManager.isParkingLotSaved(getActivity())) {
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
                                        if(location == null) {
                                            Log.e(TAG, "location clicked is null");
                                            return;
                                        }
                                        setAsParkingSpot(location);

                                    }
                                }).show();
                    } else {
                        if(location == null) {
                            Log.e(TAG, "location clicked is null");
                            return;
                        }
                        setAsParkingSpot(location);
                    }
                }
            });
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_parking_car_outline));
        }  else {
            showDirectionCard(true, idType, Integer.valueOf(location.getExternalID()), storeName, categoryName, null);
        }
    }

    private void clearLocationDetails() {
        mMainActivity.toggleDestinationEditor(true, null, null, null);
        showDirectionCard(false, null, 0, null, null, null);
    }

    private void clearMarkers() {
        mapView.removeAllMarkers();
    }

    private void startNavigation() {
        stopNavigation();
        navigationMode = true;
        mMainActivity.rvMap.setVisibility(View.INVISIBLE);
        Utility.closeKeybaord(getActivity());
    }

    private void stopNavigation() {
        mapView.removeAllPaths();
        navigationMode = false;
        path = null;
    }

    /**
     *
     * @param enabled
     * @param amenityName
     * @param clickOverlay if true, highlight pin as it was clicked to give it a focus
     * @param mapName when mapName Exists, it's looking for a specific amenity on a specific floor
     */
    @Override
    public void onAmenityClick(boolean enabled, final String amenityName, final boolean clickOverlay, final @Nullable String mapName) {
        try {
            ArrayList<CustomLocation> amenityList = CustomLocation.getAmenityHashMap().get(amenityName);
            if(amenityList != null){
                for(final CustomLocation location : amenityList) {
                    if(enabled){
                        List<Coordinate> coords = location.getNavigatableCoordinates();
                        for(final Coordinate coordinate : coords) {
                            Glide
                                    .with(getActivity())
                                    .load(location.logo.get(getImagePinSize()))
                                    .asBitmap()
//                                    .into(new SimpleTarget<Bitmap>(getImagePinSize(), getImagePinSize()) {
                                    .into(new SimpleTarget<Bitmap>(MapUtility.getDp(getActivity(), PIN_AMENITY_IMAGE_SIZE_DP), MapUtility.getDp(getActivity(), PIN_AMENITY_IMAGE_SIZE_DP)) {
                                        @Override
                                        public void onResourceReady(final Bitmap resource, GlideAnimation glideAnimation) {
                                            mAmeityDrawable = new BitmapDrawable(getResources(), resource);
                                            mAmeityDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                                            dropPin(coordinate, location, mAmeityDrawable);
                                            if(clickOverlay) {
                                                clickOverlayWithNameAndPosition(amenityName, mapName);
                                            }
                                        }
                                    });
                        }
                    } else {
                        if(mAmenityClicked.equals(amenityName)){
                            showDirectionCard(false, null, 0, null, null, null);
                        }
//                        mRemovedPin = null;
                        removePin(location);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void dropPinWithColor(final Coordinate coordinate, final Drawable pinDrawable){
        //TODO: only add pins to the current floor
        Drawable clone = pinDrawable.getConstantState().newDrawable();
        clone.mutate(); //prevent all instance of drawables from being affected
        clone.setColorFilter(getResources().getColor(R.color.themeColor), PorterDuff.Mode.MULTIPLY); //change white colors
//        clone.setColorFilter(getResources().getColor(R.color.themeColor), PorterDuff.Mode.SRC_ATOP); //change the entire colors
        Overlay2DImage label = new Overlay2DImage(getImagePinSize(), getImagePinSize(), clone);
        label.setPosition(coordinate);
//        LocationLabelClicker clicker = new LocationLabelClicker(location, pinDrawable, label, coordinate);
        //if I remove the lable at the same spot and add another label with new LocationLabelClicker,
        //removed label's LocationLabelClicker gets called again so shouldn't add another labelClicker
        mSelectedPin = new Pin(coordinate, label);
        overlays.put(label, mLocationClickersMap.get(coordinate)); //should add to overlays so when removing pins, selected (highlighted) ones get removed too
        mapView.addMarker(label, false);
    }


    private int getImagePinSize(){
        return PIN_AMENITY_IMAGE_SIZE_DP;
    }

    private int getVortexAndDestinationPinSize(){
        return PIN_VORTEX_IMAGE_SIZE_DP;
    }


    @Override
    public void dropBlueDot(double x, double y, int floor) {
        if(maps == null) return;
        if(maps.length <= floor) floor = 0;
        if(mFollowMode == FollowMode.CENTER) {
            focusOnBlueDot(floor, null);
        }

        android.location.Location targetLocation = MapUtility.getLocation(x, y);
        Overlay2DImage label;
        Coordinate coordinate  = new Coordinate(targetLocation, maps[floor]);

        if(mBlueDotPin == null) {
            label = new Overlay2DImage(PIN_BLUEDOT, PIN_BLUEDOT, getResources().getDrawable(R.drawable.icn_bluebutton));
            mBlueDotPin = new Pin(coordinate, label, x, y);
        } else {
            mapView.removeMarker(mBlueDotPin.getOverlay2DImage());
            label = mBlueDotPin.getOverlay2DImage();
            mBlueDotPin.setLocation(coordinate, x, y);
        }
        label.setPosition(coordinate);
        mapView.addMarker(label, false);


    }

    @Override
    public void drawHeading(double x, double y, float heading) {
        if(maps == null) return;


        /*android.location.Location targetLocation = MapUtility.getLocation(x, y);
        Overlay2DImage label;
        Drawable rotatedHeading = MapUtility.getRotatedDrawable(getActivity(), R.drawable.icn_bluedot_orientation_pointer, heading);
//        Drawable rotatedHeading = MapUtility.rotate(getActivity(), R.drawable.icn_bluedot_orientation_pointer, heading);
        label = new Overlay2DImage(PIN_BLUEDOT, PIN_BLUEDOT, rotatedHeading);
        Coordinate coordinate = new Coordinate(targetLocation, maps[mCurrentLevelIndex]);;
        if(mBlueDotCompass != null) mapView.removeMarker(mBlueDotCompass.getOverlay2DImage());
        mBlueDotCompass = new Pin(coordinate, label);
        mBlueDotCompass.setCoordinate(coordinate);
        label.setPosition(coordinate);
        mapView.addMarker(label, false);*/
    }

    private View.OnClickListener onTestButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            /*if(tempFloor == 0) tempFloor = 1;
            else tempFloor = 0;*/

//            dropBlueDot(43.64249432344601, -79.37441128178561, 1);

            if(mBlueDotPin == null) {
                Random r = new Random();
                int randomFloor = r.nextInt(5);
//                dropBlueDot(49.2268235, -123.0004849, randomFloor); //MP
                dropBlueDot(51.2030627,-113.9956265, randomFloor); //CROSSIRON
                Toast.makeText(getActivity(), "Floor : " + randomFloor, Toast.LENGTH_SHORT).show();
            } else {
                removeBlueDot();
                Toast.makeText(getActivity(), "bluedot removed", Toast.LENGTH_SHORT).show();
            }




        }
    };

    private OnStoreClickListener onStoreClickedListener = new OnStoreClickListener() {
        @Override
        public void onStoreClick(final int storeId, final String externalCode, final String storeName, final String categoryName) {
            try {

                if(!mMapLoaded) {
                    setMapInterface(new MapInterface() {
                        @Override
                        public void mapLoaded() {
                            onStoreClick(storeId, externalCode, storeName, categoryName);
                        }
                    });
                    return;
                }

                if(mSearchMode.equals(SearchMode.STORE)){
                    stopNavigation();
                } else if(mSearchMode.equals(SearchMode.ROUTE_START)){
                    if(mMainActivity.getDestStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_store), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(storeName, null);
                    if(!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();
                } else if(mSearchMode.equals(SearchMode.ROUTE_DESTINATION)){
                    if(mMainActivity.getStartStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_store), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(null, storeName);
                    if(!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();
                }


                Polygon placePolygon = getPolygonWithPlaceExternalId(externalCode);
                if(placePolygon != null) {
                    mapView.getCamera().focusOn(placePolygon);
                    didTapPolygon(placePolygon);
                    zoomInOut();
                } else {
                    showDirectionCard(true, IdType.ID, storeId, storeName, categoryName, null);
                }
                if(!mSearchMode.equals(SearchMode.STORE)) mMainActivity.moveFocusToNextEditText();
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

    private void setFollowMode(FollowMode followMode){
        try {
            if(followMode == mFollowMode) return;
            if(isBlueDotShown()) {
                mFollowMode = followMode;
                updateFollowMode();
            } else {
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
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFollowMode(){
        try {
            switch (mFollowMode) {
                case NONE:
                    ivFollowMode.setSelected(false);
                    break;
                case CENTER:
                    ivFollowMode.setSelected(true);
                    if(mBlueDotPin != null) focusOnBlueDot(-100, mBlueDotPin.getCoordinate().getMap().getName());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void focusOnBlueDot(int floor, @Nullable String mapName){
        try {
            if(mBlueDotPin != null) {
                if(mCurrentLevelIndex != floor) {
                    setMapLevel(floor, null, mapName);
                }
                mapView.getCamera().focusOn(mBlueDotPin.getCoordinate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isBlueDotShown(){
        return BluetoothManager.isBluetoothEnabled() && mBlueDotPin != null && sLocationAvailability.isAvailable();
    }


    @Override
    public int getCurrentFloor() {
        return mCurrentLevelIndex;
    }

    @Override
    public void removeBlueDot() {
        if(mBlueDotPin != null) {
            mapView.removeMarker(mBlueDotPin.getOverlay2DImage());
            mBlueDotPin = null;
        }
    }

//    public void dropPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable){
    public Overlay2DImage dropPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable){
        //TODO: only add pins to the current floor
        Overlay2DImage label = new Overlay2DImage(getImagePinSize(), getImagePinSize(), pinDrawable);
        if(mLocationClickersMap != null && mLocationClickersMap.containsKey(coordinate)) return null;
        label.setPosition(coordinate);
        LocationLabelClicker clicker = new LocationLabelClicker(location, pinDrawable, label, coordinate);
        overlays.put(label, clicker);
        mLocationClickersMap.put(coordinate, clicker);
        mapView.addMarker(label, false);
        return label;
    }

    public void dropDealsPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable, String externalId) {
        //TODO: only add pins to the current floor
        Overlay2DImage label = new Overlay2DImage(getImagePinSize(), getImagePinSize(), pinDrawable);
        label.setPosition(coordinate);

        LocationLabelClicker clicker = new LocationLabelClicker(location, pinDrawable, label, coordinate, externalId);
        overlays.put(label, clicker);
        mLocationClickersMap.put(coordinate, clicker);

        mapView.addMarker(label, false);
    }



    private void dropDestinationPin(final Coordinate coordinate, final Drawable pinDrawable){
        Overlay2DImage label = new Overlay2DImage(getVortexAndDestinationPinSize(), getVortexAndDestinationPinSize(), pinDrawable);
        label.setPosition(coordinate);
        mDestinationPin = new Pin(coordinate, label);
        mapView.addMarker(label, false);
    }


    //    public void clickOverlayWithNameAndPosition(String amenityName, Coordinate amenityCoordinate){
    public void clickOverlayWithNameAndPosition(String amenityName, String mapName){
        ArrayList<CustomLocation> amenityList = CustomLocation.getAmenityHashMap().get(amenityName);
        if(amenityList != null){
            for(final CustomLocation location : amenityList) {
                List<Coordinate> coords = location.getNavigatableCoordinates();
                for(Coordinate coordinate : coords) {
                    if(mapName == null || (mapName != null && coordinate.getMap().getName().equals(mapName))) {
                        //if the map's already has the pin selected then return
                        if(mSelectedPin != null && mSelectedPin.getCoordinate() == coordinate) {


                            return;
                        }
                        LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(coordinate);
                        locationLabelClicker.onClick();
                        return;
                    }
                }
            }
        }
    }

    public LocationLabelClicker getLabelClicker(String amenityName, int position) {
        Coordinate coordinate;
        ArrayList<CustomLocation> amenityList = CustomLocation.getAmenityHashMap().get(amenityName);
        if(amenityList != null){
            for(final CustomLocation location : amenityList) {
                List<Coordinate> coords = location.getNavigatableCoordinates();
                coordinate = coords.get(position);
                return mLocationClickersMap.get(coordinate);
            }
        }
        return null;
    }

    public void clickOverlayWithCoordinate(Coordinate coordinate, int position){
        if(coordinate != null){
            LocationLabelClicker locationLabelClicker = mLocationClickersMap.get(coordinate);
            locationLabelClicker.onClick();
        }
    }

    public Overlay getOverlayFromMap(Location location){

        try {
            if(location == null) return null;
            for (HashMap.Entry<Overlay, LocationLabelClicker> entry : overlays.entrySet()) {
                Overlay overlay = entry.getKey();
                LocationLabelClicker locationLabelClicker = entry.getValue();
                if(locationLabelClicker.location == location){
                    return overlay;
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    public void removePin(Overlay overlay, Coordinate coordinate) {
        mapView.removeMarker(overlay);
        if(mLocationClickersMap.containsKey(coordinate)) mLocationClickersMap.remove(coordinate);
    }

    public void removePin(final Location location) {
        try {
            for (HashMap.Entry<Overlay, LocationLabelClicker> entry : overlays.entrySet()) {
                Overlay overlay = entry.getKey();
                LocationLabelClicker locationLabelClicker = entry.getValue();
                if(locationLabelClicker.location == location){

                    mapView.removeMarker(overlay);
                    mLocationClickersMap.remove(overlay.getPosition());
                    overlays.remove(overlay);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onDealsClick(boolean enabled) {
        //TODO: when deals list's refreshed, this mRecommendedDealsExternalCodeList should be set to null to refresh this list too

//          ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true); //ALL DEALS
        /*ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true); //RECOMMENDED DEALS
        if(mRecommendedDealsExternalCodeList == null) {
            if(dealContentPages == null) return;
            mRecommendedDealsExternalCodeList = new ArrayList<String>();
            for(KcpContentPage kcpContentPage : dealContentPages) {
                mRecommendedDealsExternalCodeList.add(kcpContentPage.getExternalCode());
            }
        }

        for( String externalCode: mRecommendedDealsExternalCodeList) {
            Location location = CustomLocation.getLocationWithExternalCode(externalCode);
            if(location != null) removePin(location);
        }

        if(enabled && dealContentPages != null) {
            mRecommendedDealsExternalCodeList = new ArrayList<String>();
            for(KcpContentPage kcpContentPage : dealContentPages) {
                Location location = CustomLocation.getLocationWithExternalCode(kcpContentPage.getExternalCode());
                if(location != null){
                    List<Coordinate> coords = location.getNavigatableCoordinates();
                    for(Coordinate coordinate : coords) {
                        Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_deals, 0);
//                        dropPin(coordinate, location, amenityDrawable);
                        dropDealsPin(coordinate, location, amenityDrawable);
                    }
                }
                mRecommendedDealsExternalCodeList.add(kcpContentPage.getExternalCode());
            }
        }*/

        //          ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true); //ALL DEALS
        ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true); //RECOMMENDED DEALS
        if(mRecommendedDealsContentPageList == null) {
            if(dealContentPages == null) return;
            mRecommendedDealsContentPageList = new ArrayList<KcpContentPage>();
            for(KcpContentPage kcpContentPage : dealContentPages) {
                mRecommendedDealsContentPageList.add(kcpContentPage);
            }
        }

        for( KcpContentPage kcpContentPage: mRecommendedDealsContentPageList) {
            Location location = CustomLocation.getLocationWithExternalCode(kcpContentPage.getExternalCode());
            if(location != null) removePin(location);
        }

        if(enabled && dealContentPages != null) {
            mRecommendedDealsContentPageList = new ArrayList<KcpContentPage>();
            for(KcpContentPage kcpContentPage : dealContentPages) {
                Location location = CustomLocation.getLocationWithExternalCode(kcpContentPage.getExternalCode());
                if(location != null){
                    List<Coordinate> coords = location.getNavigatableCoordinates();
                    for(Coordinate coordinate : coords) {
                        Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_deals, 0);
//                        dropPin(coordinate, location, amenityDrawable);
                        KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                        dropDealsPin(coordinate, location, amenityDrawable, kcpPlace.getExternalCode());
                    }
                }
                mRecommendedDealsContentPageList.add(kcpContentPage);
            }
        }
    }

    public Drawable getDrawableFromView(int drawable, int backgroundResource){
//        View amientyView = View.inflate(getActivity(), R.layout.layout_amenity, (ViewGroup) view); //image gets stuck to parentview
//        View amientyView = getActivity().getLayoutInflater().inflate(R.layout.layout_amenity, null); //image is an overlay and not stuck anywhere
        ImageView amientyView = (ImageView) getActivity().getLayoutInflater().inflate(R.layout.layout_amenity, null); //image is an overlay and not stuck anywhere
        if(backgroundResource != 0) amientyView.setBackgroundResource(backgroundResource);
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

    public void showParkingSpotAtBlueDot(){
        if(mBlueDotPin == null) return;
//        if(sParkingPin.getTempParkingCoordinatePin() != null) removeTempParkingPinAtCoordinate(sParkingPin.getTempParkingCoordinatePin());

        Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, R.drawable.circle_imageview_background_black);
        Overlay2DImage label = dropPin(mBlueDotPin.getCoordinate(), null, amenityDrawable);

//        Overlay2DImage label = new Overlay2DImage(getImagePinSize(), getImagePinSize(), amenityDrawable);
//        label.setPosition(mBlueDotPin.getCoordinate());
//        mapView.addMarker(label, false);
        sParkingPin.setTempParkingCoordinatePin(new Pin(mBlueDotPin.getCoordinate(), label, mBlueDotPin.getLatitude(), mBlueDotPin.getLongitude()));
        showParkingDetail(true);

        mapView.getCamera().focusOn(mBlueDotPin.getCoordinate());
        mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL_NEAREST_PARKING);

        destinationPolygon = sParkingPin.getTempParkingCoordinatePin().getCoordinate();
    }


    /**
     *
     * @param parkingLotPosition
     * @param polygon
     * find the closest parking spot(entrance) from the store within the lot
     */
    public void showParkingSpotFromDetailActivity(int parkingLotPosition, Polygon polygon){
        try {
            if(polygon == null) return;
            if(BuildConfig.PARKING_POLYGON) {
                mapView.getCamera().focusOn(polygon);
                didTapPolygon(polygon);
            }

            if(parkingLotPosition != -1){
                HashMap<String, CustomLocation> parkingHashMap = CustomLocation.getParkingHashMap();
                Parking storeParking = ParkingManager.sParkings.getParkings().get(parkingLotPosition);
                List<ChildParking> childParkings = storeParking.getChildParkings();

                CustomLocation currentNearestParkingLocation = null;
                double currentDistanceFromParkingToStore = 0.0;
                ChildParking currentNearestChildParking = null;

                int entrancePosition = 0;
                for(int childParkingPosition = 0; childParkingPosition < childParkings.size(); childParkingPosition++) {
                    ChildParking childParking = childParkings.get(childParkingPosition);
                    String parkingId = childParking.getParkingId();
                    if(parkingHashMap.containsKey(parkingId)){
                        CustomLocation parkingLocation = parkingHashMap.get(parkingId);
                        List<Coordinate> coords = parkingLocation.getNavigatableCoordinates();
                        if(coords.size() > 0) {
                            Coordinate parkingLotCoordinate = coords.get(0);
                            Coordinate storeCoordinate = polygon.getLocations().get(0).getNavigatableCoordinates().get(0);
                            double distance = storeCoordinate.metersFrom(parkingLotCoordinate);
                            if(currentDistanceFromParkingToStore == 0.0 || distance < currentDistanceFromParkingToStore) {
                                currentNearestParkingLocation = parkingLocation;
                                currentDistanceFromParkingToStore = distance;
                                currentNearestChildParking = childParking;
                                entrancePosition = childParkingPosition;
                            }
                        }
                    }
                }

                if(mTemporaryParkingLocation != null) removePin(mTemporaryParkingLocation);
                mTemporaryParkingLocation = currentNearestParkingLocation;

                String parkingLotName = storeParking.getName();
                String entranceName = currentNearestChildParking.getName();
                showParkingDetail(mTemporaryParkingLocation, true, parkingLotName, entranceName, "", parkingLotPosition, entrancePosition);

                Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, R.drawable.circle_imageview_background_black);
                dropPin(mTemporaryParkingLocation.getNavigatableCoordinates().get(0), mTemporaryParkingLocation, amenityDrawable);
                mapView.getCamera().focusOn(polygon);
                mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL_NEAREST_PARKING);

                destinationPolygon = mTemporaryParkingLocation;
//
            }





        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void showMySavedParkingPolygon(boolean showParkingSpot, String parkingId, boolean focus){
        ArrayList<Polygon> polygons = CustomLocation.getParkingPolygonsFromLocationWithId(parkingId);
        if(polygons != null && polygons.size() > 0) {
            if(showParkingSpot) {
                /*if(mSavedParkingLocation != null) {
                    removePin(mSavedParkingLocation);
                }*/
                if(mSavedParkingPolygon != null && mOriginalColorsForParking != 0) {
                    mSavedParkingPolygon.setColor(mOriginalColorsForParking);
                    mOriginalColorsForParking = 0;
                }
                sParkingPin.setParkingLocationPin(CustomLocation.getParkingHashMap().get(parkingId));
                mSavedParkingPolygon = polygons.get(0);
                didTapPolygon(mSavedParkingPolygon);
                if(focus) {
                    mapView.getCamera().focusOn(mSavedParkingPolygon);
                    zoomInOut();
                }
            } else {
                if(mSavedParkingPolygon != null) mSavedParkingPolygon.setColor(mOriginalColorsForParking);
                didTapNothing();
                mSavedParkingPolygon = null;
                mOriginalColorsForParking = 0;
            }
        }
    }

    @Override
    public void onParkingClick(boolean enabled, boolean focus) {
        try {
            if(!ParkingManager.isParkingLotSaved(getActivity())){
                final Intent intent = new Intent (getActivity(), ParkingActivity.class);
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
            } else {
                if(BuildConfig.PARKING_POLYGON) {
                    if(ParkingManager.getMyEntrance(getActivity()) != null) {
                        String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                        showMySavedParkingPolygon(enabled, parkingId, focus);
                    }
                }

                HashMap<String, CustomLocation> parkingHashMap = CustomLocation.getParkingHashMap();
                if(ParkingManager.getParkingMode(getActivity()) == ParkingManager.ParkingMode.LOCATION) {
                    String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                    if(parkingHashMap.containsKey(parkingId)) {
                        sParkingPin.setParkingLocationPin(parkingHashMap.get(parkingId));
                        if(enabled) {
                            List<Coordinate> coords = sParkingPin.getParkingLocationPin().getNavigatableCoordinates();
                            for(final Coordinate coordinate : coords) {
                                Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, 0);
                                dropPin(coordinate, sParkingPin.getParkingLocationPin(), amenityDrawable);
                                destinationPolygon = sParkingPin.getParkingLocationPin();
                                if(focus) {
                                    mapView.getCamera().focusOn(coordinate);
                                    zoomInOut();
                                    showSavedParkingDetail();
                                }
                                break; //prevent it from dropping two pin images in case there is more than one polygon
                            }
                        } else {
                            removePin(sParkingPin.getParkingLocationPin());
                            didTapNothing();
                        }
                    }
                } else {
                    ParkingManager.ParkingSpot parkingSpot = ParkingManager.getSavedParkingSpot(getActivity());
                    if(parkingSpot != null) {
                        Coordinate savedParkingPinCoordinate = parkingSpot.getCoordinate(maps);
                        if(enabled && savedParkingPinCoordinate != null) {

                            Drawable parkingDrawable = getDrawableFromView(R.drawable.icn_car, 0);
                            Overlay2DImage label = dropPin(savedParkingPinCoordinate, sParkingPin.getParkingLocationPin(), parkingDrawable);
//                            label.setPosition(savedParkingPinCoordinate);
                            Pin parkingSpotPin = new Pin(savedParkingPinCoordinate, label);

                            sParkingPin.setParkingCoordinatePin(parkingSpotPin);



                            destinationPolygon = sParkingPin.getParkingCoordinatePin().getCoordinate();
                            if(focus) {
                                mapView.getCamera().focusOn(sParkingPin.getParkingCoordinatePin().getCoordinate());
                                zoomInOut();
                                showSavedParkingDetail();
                            }
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

        public LocationLabelClicker(Location location, Drawable pinDrawable, Overlay2DImage label, Coordinate coordinate){
            this.location = (CustomLocation) location;
            this.drawable = pinDrawable;
            this.label = label;
            this.coordinate = coordinate;
        }

        //if there is store associated with this location label
        public LocationLabelClicker(Location location, Drawable pinDrawable, Overlay2DImage label, Coordinate coordinate, String placeExternalId){
            this.location = (CustomLocation) location;
            this.drawable = pinDrawable;
            this.label = label;
            this.coordinate = coordinate;
            this.placeExternalId = placeExternalId;
        }

        public CustomLocation location = null;
        public Drawable drawable = null;
        public Overlay2DImage label = null;
        public Coordinate coordinate = null;
        public String placeExternalId = null;

        public void onClick() {
            if(path != null || coordinate.getMap().getElevation() != maps[mCurrentLevelIndex].getElevation()) return; //map shouldn't be clicakble when the paths drawn or this indicates the pin clicked is not on the floor you are looking at
            if(location != null) {
                if(location == sParkingPin.getParkingLocationPin()) {
                    String parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
                    String entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
                    String parkingNote = ParkingManager.getParkingNotes(getActivity());
                    showParkingDetail(location, false, parkingLotName, entranceName, parkingNote, -1, -1);
                } else if(placeExternalId != null) { //if deal's clicked, tab the polygon instead
                    didTapPolygon(getPolygonWithPlaceExternalId(placeExternalId));
                    return;
                }

                mapView.getCamera().focusOn(coordinate);
                zoomInOut();

//                destinationPolygon = location; //if location's set as destinationPolygon, it will automatically choose the closest amenity, in which case, the closest amenity has to be manually highlighted in didTapPolygon
                destinationPolygon = coordinate;
                if(location == sParkingPin.getParkingLocationPin()) return;

                if(mSelectedPin == null) {
                    highlightThisLabel();
                    showAmenityDetail((CustomLocation) location, drawable);
                } else {
                    Coordinate removeableMarkerCoordinate = mSelectedPin.getCoordinate();
                    replaceSelectedPinWithRemovedPin();
                    if(removeableMarkerCoordinate != coordinate) {
                        highlightThisLabel();
                        showAmenityDetail((CustomLocation) location, drawable);
                    }


                }
            } else {

                String a = "ewfsdf";



            }
        }

        public void highlightThisLabel(){
//            destinationPolygon = location;
            dropPinWithColor(coordinate, drawable);
            if(!location.getAmenityType().equals(CustomLocation.TYPE_AMENITY_PARKING)) mRemovedPin = new Pin(coordinate, label); //parking pin is temporary - shouldn'be be readded
            mapView.removeMarker(label);
        }
    }

    private void replaceSelectedPinWithRemovedPin() {
        if(mSelectedPin != null) {
            mapView.removeMarker(mSelectedPin.getOverlay2DImage());
            overlays.remove(mSelectedPin.getOverlay2DImage());
            mSelectedPin = null;
        }
        if(mRemovedPin != null) {
            mapView.addMarker(mRemovedPin.getOverlay2DImage(), false);
            mRemovedPin = null;
            showDirectionCard(false, null, 0, null, null, null);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(Constants.IS_APP_IN_PRODUCTION) {

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
        mFilterItem.setIcon(ThemeManager.getThemedMenuDrawable(getActivity(), R.drawable.icn_filter));
        mSearchItem.setIcon(ThemeManager.getThemedMenuDrawable(getActivity(), R.drawable.icn_search));
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
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
                        if(mSearchMode.equals(SearchMode.STORE) ||
                                (!mSearchMode.equals(SearchMode.STORE) && !mMainActivity.isEditTextsEmpty()) ) mMainActivity.rvMap.setVisibility(View.INVISIBLE);

                        if(btnShowMap != null) btnShowMap.setVisibility(View.VISIBLE);
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
                        if(btnShowMap != null) btnShowMap.setVisibility(View.GONE);

                        mShowBlueDotHeader = false;
                        setupRecyclerView();

                        return true;
                    }
                });
    }

    public class FocusListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                int topMargin = (int) (KcpUtility.dpToPx(getActivity(), 95) - (KcpUtility.dpToPx(getActivity(), 55))); //directionEditor height - actionbar height
                mMainActivity.rvMap.setPadding(0, topMargin, 0, 0);
                mMainActivity.rvMap.setVisibility(View.VISIBLE);
                if(btnShowMap != null) btnShowMap.setVisibility(View.GONE);
                if(v.getId() == R.id.etDestStore) {
                    mSearchMode = SearchMode.ROUTE_DESTINATION;
                    mShowBlueDotHeader = false;
                    setupRecyclerView();
                } else if(v.getId() == R.id.etStartStore) {
                    mSearchMode = SearchMode.ROUTE_START;
                    if(isBlueDotShown()) {
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
                if(btnShowMap != null) btnShowMap.setVisibility(View.VISIBLE);
            }
        }
    }

    private View.OnClickListener useMyLocationListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            try {
                String storeName = getString(R.string.bluedot_my_location);
                if(mSearchMode.equals(SearchMode.ROUTE_START)){
                    if(mMainActivity.getDestStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_location), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(storeName, null);
                    if(!mMainActivity.isEditTextsEmpty()) {
                        startNavigation();
                    } else stopNavigation();

//                    destinationPolygon = null;
//                    mSearchMode = SearchMode.ROUTE_DESTINATION;
//                    didTapPolygon(mBlueDotPin.getCoordinate());
                    drawPath();

                } else if(mSearchMode.equals(SearchMode.ROUTE_DESTINATION)){
                    if(mMainActivity.getStartStoreName().equals(storeName)) {
                        Toast.makeText(getActivity(), getString(R.string.warning_selected_same_location), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMainActivity.setDestionationNames(null, storeName);
                    if(!mMainActivity.isEditTextsEmpty()) {
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
        return rlDirection.getVisibility() == View.VISIBLE;
    }
    /**
     *
     * @param showCard
     * @param idType if idType.AMENITY, show amenity icon, if idType.ID or idType.EXTERNAL_CODE, check for deals and show it at the bottom of the card
     * @param id id of the pin
     * @param storeName
     * @param categoryName
     */
    public void showDirectionCard(final boolean showCard, final IdType idType, final int id, final String storeName, final String categoryName, final Drawable amenityDrawable){
        if(!showCard){
            if(isDirectionCardVisible()){ //only do animation if direction card is visible
                slidePanel(false);
            }
            mAmenityClicked = "";
            return;
        }

        //if idType is NOT parking && (NOT amenity and id == 0 or -1) return
        if( !idType.equals(IdType.PARKING) && !idType.equals(IdType.AMENITY) && !idType.equals(IdType.INSTRUCTION)
                && (id == 0 || id == -1)) {
            return;
        }

        tvStoreName.setMaxLines(1);
        tvParkingNote.setVisibility(View.GONE);
        rlRoute.setVisibility(View.VISIBLE);

        if(amenityDrawable != null && idType.equals(IdType.AMENITY) || idType.equals(IdType.INSTRUCTION)) {
            tvCategoryName.setVisibility(View.GONE);
            ivAmenity.setVisibility(View.VISIBLE);
            ivAmenity.setImageDrawable(amenityDrawable);
            if(idType.equals(IdType.INSTRUCTION)) {
                tvStoreName.setMaxLines(2);
                rlRoute.setVisibility(View.GONE);
            }
        } else {
            tvCategoryName.setVisibility(View.VISIBLE);
            ivAmenity.setVisibility(View.GONE);
        }


        ArrayList<KcpContentPage> dealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true);
        ArrayList<KcpContentPage> recommendedDealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true);

        dealsList.removeAll(recommendedDealsList);
        dealsList.addAll(recommendedDealsList);


        final ArrayList<KcpContentPage> dealsForThisStore = new ArrayList<KcpContentPage>();
        if(dealsList != null){
            for(int i = 0 ; i < dealsList.size(); i++){
                if(  (idType.equals(IdType.ID) && dealsList.get(i).getStore().getPlaceId() == id) ||
                        (idType.equals(IdType.EXTERNAL_CODE) && id == Integer.parseInt(dealsList.get(i).getStore().getExternalCode())) ){
                    dealsForThisStore.add(dealsList.get(i));
                }
            }
        }

        if(dealsForThisStore.size() > 0 || idType.equals(IdType.PARKING)) {
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_deals));
            llDeals.setVisibility(View.VISIBLE);
            if(dealsForThisStore.size() > 0) tvDealName.setText(dealsForThisStore.get(0).getTitle());

            if(dealsForThisStore.size() > 1){
                int moreDeals = dealsForThisStore.size() - 1;
                String textEnd = moreDeals == 1 ? " More Deal" : " More Deals";
                tvNumbOfDeals.setText("+" + moreDeals + textEnd);
                tvNumbOfDeals.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMainActivity.selectPage(MainActivity.VIEWPAGER_PAGE_HOME);
                        HomeFragment.getInstance().selectPage(1);
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
        if(mSearchItem != null) mSearchItem.collapseActionView();

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
                KcpPlaces kcpPlace = null;
                if(idType.equals(IdType.ID)) kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(id);
                else if (idType.equals(IdType.EXTERNAL_CODE)) kcpPlace = KcpPlacesRoot.getInstance().getPlaceByExternalCode(String.valueOf(id));
                if(kcpPlace == null) return;

                if(kcpPlace != null) {
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

    private void slidePanel(final boolean up){
        final RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) rlDirection.getLayoutParams();
        View rootView = getActivity().findViewById(android.R.id.content);
        if(mRootViewHeight == 0) mRootViewHeight = rootView.getHeight();
        if(up) {
            if(llDeals.getVisibility() == View.VISIBLE) {
                param.height = (int) getActivity().getResources().getDimension(R.dimen.bottom_sheet_height_extended);
            } else {
                param.height = (int) getActivity().getResources().getDimension(R.dimen.bottom_sheet_height_normal);
            }
            rlDirection.setLayoutParams(param);
            if(rlRoute.getVisibility() != View.VISIBLE) param.height = (int) getActivity().getResources().getDimension(R.dimen.bottom_sheet_height_reduced);
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


    public void showDirectionEditor(String start, String dest){
        mMainActivity.toggleDestinationEditor(false, start, dest, new FocusListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            mMainActivity.openRightDrawerLayout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(slIndoorLocationPresenter != null) slIndoorLocationPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(slIndoorLocationPresenter != null) slIndoorLocationPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(slIndoorLocationPresenter != null) slIndoorLocationPresenter.onDestroy();
    }

    @Override
    public void onStop(){
        super.onStop();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }
}
