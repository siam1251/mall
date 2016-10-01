package com.kineticcafe.kcpmall.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.constants.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.activities.MainActivity;
import com.kineticcafe.kcpmall.activities.ParkingActivity;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.adapterHelper.IndexableRecylerView;
import com.kineticcafe.kcpmall.adapters.adapterHelper.SectionedLinearRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.managers.ThemeManager;
import com.kineticcafe.kcpmall.mappedin.Amenities;
import com.kineticcafe.kcpmall.mappedin.Amenities.OnParkingClickListener;
import com.kineticcafe.kcpmall.mappedin.AmenitiesManager;
import com.kineticcafe.kcpmall.mappedin.CustomLocation;
import com.kineticcafe.kcpmall.parking.Parking;
import com.kineticcafe.kcpmall.parking.ParkingManager;
import com.kineticcafe.kcpmall.utility.Utility;
import com.mappedin.jpct.Logger;
import com.mappedin.sdk.Coordinate;
import com.mappedin.sdk.Directions;
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
import com.mappedin.sdk.Overlay2DLabel;
import com.mappedin.sdk.Path;
import com.mappedin.sdk.Polygon;
import com.mappedin.sdk.RawData;
import com.mappedin.sdk.Venue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Kay on 2016-06-20.
 */
public class MapFragment extends BaseFragment implements MapViewDelegate, Amenities.OnAmenityClickListener, Amenities.OnDealsClickListener, OnParkingClickListener {
    private static MapFragment sMapFragment;
    public static MapFragment getInstance(){
        if(sMapFragment == null) sMapFragment = new MapFragment();
        return sMapFragment;
    }

    enum SearchMode { STORE, ROUTE_START, ROUTE_DESTINATION }
    public SearchMode mSearchMode = SearchMode.STORE;

    enum IdType { ID, EXTERNAL_CODE, AMENITY };

    private ProgressBar pb;
    private View view;
    private SearchView mSearchView;
//    private IndexableRecylerView rvMap;
    private RelativeLayout rlMap;
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
    private TextView tvLevel;
    private ImageView ivUpper;
    private ImageView ivLower;
    private ImageView ivAmenity;
    private FrameLayout flMap; ///todo: disabled for testing

    private View viewRoute;
    private String mSearchString = "";
    public MenuItem mSearchItem;
    private MenuItem mFilterItem;
    public CategoryStoreRecyclerViewAdapter mPlaceRecyclerViewAdapter;
    private ArrayList<String> mRecommendedDealsExternalCodeList;
    private Drawable mAmeityDrawable;

    //MAPPED IN
    private final int PIN_IMAGE_SIZE_DP = 95;
    private final int CAMERA_ZOOM_LEVEL_NEAREST_PARKING = 90; //
    private final int CAMERA_ZOOM_LEVEL = 30; //BIGGER - farther, SMALLER - closer
    private final int BLUR_RADIUS = 20;
    private boolean accessibleDirections = false;
    private MapViewDelegate delegate = this;

    private MappedIn mappedIn = null;
    private MapView mapView = null;
    private Map[] maps;
    private Button showLocationsButton = null;
    private HashMap<Polygon, Integer> originalColors = new HashMap<Polygon, Integer>();
    private HashMap<Overlay, LocationLabelClicker> overlays = new HashMap<Overlay, LocationLabelClicker>();
    private Venue activeVenue = null;
    private boolean navigationMode = false;
    private Path path;

    private Navigatable startPolygon = null;
    private Navigatable destinationPolygon = null;
    private int mCurrentMapLevel = 2;
    private CustomLocation mSavedParkingLocation;
    private CustomLocation mTemporaryParkingLocation; //used to show the temporary parking spot from detail activity

    public String mPendingExternalCode;
    private Overlay2DImage mSelectedPin = null;//to keep track of highlited amenity drawable to set back to the original state in clearHighlighted
    private Overlay2DImage mRemovedPin = null; //overlayImage that was replaced by mSelectedOverlayImage

    public boolean isGetVenueAlreadyCalled = false; //getvenue takes long so run getVenue when the tab is selected. this flag saves whether getVenue has already been called
    public boolean runGetVenue = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);

        pb = (ProgressBar) view.findViewById(R.id.pb);
        pb.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.themeColor),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        rlMap = (RelativeLayout) view.findViewById(R.id.rlMap);
        rlDirection = (RelativeLayout) view.findViewById(R.id.rlDirection);
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
        tvLevel = (TextView) view.findViewById(R.id.tvLevel);
        ivUpper = (ImageView) view.findViewById(R.id.ivUpper);
        ivLower = (ImageView) view.findViewById(R.id.ivLower);
        ivAmenity = (ImageView) view.findViewById(R.id.ivAmenity);
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

        showLocationsButton = (Button) view.findViewById(R.id.showLocationButton);
        showLocationsButton.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showLocations();}});

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

        initializeMap();

        return view;
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
            kcpPlacesFiltered = new ArrayList<>();
            for(int i = 0; i < kcpPlaces.size(); i++){

                if(kcpPlaces.get(i).getPlaceName().toLowerCase().contains(mSearchString.toLowerCase())) {
                    kcpPlacesFiltered.add(kcpPlaces.get(i));
                }
            }
        }

        mPlaceRecyclerViewAdapter = new CategoryStoreRecyclerViewAdapter(
                getActivity(),
                kcpPlacesFiltered, KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE, new OnStoreClickListener() {
            @Override
            public void onStoreClick(int storeId, String externalCode, String storeName, String categoryName) {
                try {
                    if(mSearchMode.equals(SearchMode.STORE)){
                        stopNavigation();
                    } else if(mSearchMode.equals(SearchMode.ROUTE_START)){
                        mMainActivity.setDestionationNames(storeName, null);
                        if(!mMainActivity.isEditTextsEmpty()) {
                            startNavigation();
                        } else stopNavigation();
                    } else if(mSearchMode.equals(SearchMode.ROUTE_DESTINATION)){
                        mMainActivity.setDestionationNames(null, storeName);
                        if(!mMainActivity.isEditTextsEmpty()) {
                            startNavigation();
                        } else stopNavigation();
                    }

                    ArrayList<Polygon> polygons = CustomLocation.getPolygonsFromLocation(externalCode);
                    if(polygons != null && polygons.size() > 0) {
                        //TODO: loop through to highlight all the polygons found in getPolygons()
//                        setMapLevel(0, CustomLocation.getLocationHashMap().get(externalCode).getPolygons().get(0).getMap().getShortName()); //for map with multi levels
                        mapView.getCamera().focusOn(CustomLocation.getPolygonsFromLocation(externalCode).get(0));
                        didTapPolygon(CustomLocation.getPolygonsFromLocation(externalCode).get(0));
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
        });


        List<SectionedLinearRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedLinearRecyclerViewAdapter.Section>();
        List<String> sectionName = new ArrayList<String>();
        List<Integer> sectionPosition = new ArrayList<Integer>();
        String startLetter = null;
        for(int i = 0; i < kcpPlacesFiltered.size(); i++){
            String currentStoreNameStartLetter = String.valueOf(kcpPlacesFiltered.get(i).getPlaceName().toUpperCase().charAt(0));
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
            if(activeVenue == null) return;

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
//            if(runGetVenue) {
//                isGetVenueAlreadyCalled = true;
                mappedIn.getVenue(activeVenue, accessibleDirections, new CustomLocationGenerator(), new GetVenueCallback());
//            }
        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading any venues. Did you set your credentials? Exception: " + e);
        }
    }

    /*public void runGetVenue() {
        isGetVenueAlreadyCalled = true;
        mappedIn.getVenue(activeVenue, accessibleDirections, new CustomLocationGenerator(), new GetVenueCallback());
    }*/


    // Get the full details on a single Venue
    private class GetVenueCallback implements MappedinCallback<Venue> {
        @Override
        public void onCompleted(final Venue venue) {
            maps = venue.getMaps();
            if (maps.length == 0) {
                Logger.log("No maps! Make sure your venue is set up correctly!");
                return;
            }
            Arrays.sort(maps, new Comparator<Map>() {
                @Override
                public int compare(Map a, Map b) {
                    return (int) (a.getElevation() - b.getElevation());
                }
            });

            mapView.setMap(maps[0]);
            setMapLevel(maps.length / 2, null); //todo: disabled for testing
            if(pb != null) pb.setVisibility(View.GONE);
            if(maps.length > 1) {
                ivUpper.setVisibility(View.VISIBLE);
                ivLower.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading Venue: " + e);
        }

    }

    private void setLevelImageView(final Map[] maps) {
        final int upperLevel = mCurrentMapLevel + 1;
        final int lowerLevel = mCurrentMapLevel - 1;

        if(upperLevel >= maps.length) {
            ivUpper.setSelected(false);
            ivUpper.setOnClickListener(null);
        } else {
            ivUpper.setSelected(true);
            ivUpper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMapLevel(upperLevel, null);
                }
            });
        }

        if(lowerLevel < 0) {
            ivLower.setSelected(false);
            ivLower.setOnClickListener(null);
        } else {
            ivLower.setSelected(true);
            ivLower.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setMapLevel(lowerLevel, null);
                }
            });
        }
    }

    /**
     *
     * @param level
     * @param shortName
     *
     * drop amenities / deals / parking lots - do stuffs that need to be done on map load
     */
    private void setMapLevel (int level, @Nullable String shortName){
        if(shortName != null){
            for(int i = 0; i < maps.length; i++){
                Map map = maps[i];
                if(map.getShortName().equals(shortName)){
                    mCurrentMapLevel = i;
                    break;
                }
            }
        } else {
            mCurrentMapLevel = level;
        }
        mapView.setMap(maps[mCurrentMapLevel]);
        tvLevel.setText(maps[mCurrentMapLevel].getName());
        setLevelImageView(maps);

        //LOAD AMENITIES, DEALS
        //TODO: TEST - disabled for testing
        onDealsClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_DEAL), false);
        for(int i = 0; i < AmenitiesManager.sAmenities.getAmenityList().size(); i++){
            Amenities.Amenity amenity = AmenitiesManager.sAmenities.getAmenityList().get(i);
            final String externalID = amenity.getExternalIds()[0];
//            onAmenityClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_AMENITY + amenity.getTitle()), amenity.getExternalIds()[0]);
            onAmenityClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_AMENITY + externalID), amenity.getExternalIds()[0]);
        }

        //mark the store that's been
        if(mPendingExternalCode != null) {
            ArrayList<Polygon> polygons = CustomLocation.getPolygonsFromLocation(mPendingExternalCode);
            mPendingExternalCode = null;
            if(polygons != null && polygons.size() > 0) {
                showStoreOnTheMapFromDetailActivity(polygons.get(0));
            }
        }

        if(ParkingManager.isParkingLotSaved(getActivity())) {
            onParkingClick(Amenities.isToggled(getActivity(), Amenities.GSON_KEY_PARKING), false);
        }
    }

    private class CustomLocationGenerator implements LocationGenerator {
        @Override
        public Location locationGenerator(RawData rawData) throws Exception {
            return new CustomLocation(rawData);
        }
    }

    public void showStoreOnTheMapFromDetailActivity(Polygon polygon){
        mapView.getCamera().focusOn(polygon);
        MapFragment.getInstance().didTapPolygon(polygon);
    }

    public void didTapPolygon(Polygon polygon) {
        try {
            if(path != null || polygon == null) return; //map shouldn't be clicakble when the paths drawn
            if (polygon.getLocations().size() == 0) { //TODO: clearHighlightedColours() used to be above this line - polygon.getLocation().size() was sometimes 0 resulting in skipping highlightPolygon (it returned)
                return;
            }

//            clearHighlightedColours();

            //tapping the same polygon should dismiss the detail and remove the highlights
            if(destinationPolygon != null && destinationPolygon == polygon) {
                destinationPolygon = null;
                showDirectionCard(false, null, 0, null, null, null);
                clearHighlightedColours();
                return;
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
                    return;
                }

                if (polygon.getLocations().size() == 0) {
                    return;
                }

                //because only destinationPolygon can either be polygon or location
                Directions directions = null;
                if(destinationPolygon instanceof Polygon){
                    directions = destinationPolygon.directionsFrom(activeVenue, startPolygon, ((Polygon) startPolygon).getLocations().get(0).getName(), ((Polygon) destinationPolygon).getLocations().get(0).getName());
                } else if(destinationPolygon instanceof CustomLocation){
                    directions = destinationPolygon.directionsFrom(activeVenue, startPolygon, ((Polygon) startPolygon).getLocations().get(0).getName(), ((CustomLocation) destinationPolygon).getName());
                }

                if (directions != null) {
                    path = new Path(directions.getPath(), 0.2f, 0.2f, getResources().getColor(R.color.map_destination_store));
                    mapView.addPath(path);
                    mapView.getCamera().focusOn(directions.getPath());
                }
                if(destinationPolygon instanceof Polygon) highlightPolygon((Polygon) destinationPolygon, getResources().getColor(R.color.themeColor));
                /*if(mTemporaryParkingLocation != null) {

                    Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car);
                    dropPin(coordinate, mTemporaryParkingLocation, amenityDrawable);

                }*/
                highlightPolygon((Polygon) startPolygon, getResources().getColor(R.color.map_destination_store));
                showDirectionCard(false, null, 0, null, null, null);
                return;
            } else {

                clearHighlightedColours();



                if(destinationPolygon instanceof Polygon){
                    highlightPolygon((Polygon) destinationPolygon, getResources().getColor(R.color.themeColor));

                    Log.d("MapFragment", "first : " + mapView.getCamera().getRotation().first + " second : " + mapView.getCamera().getRotation().second);
                    Log.d("MapFragment", "BEFORE camera zoom level : " + mapView.getCamera().getZoom());


                    //allow the camera to zoom in if it's too far away or keep its current zoom if it's close enough
                    if(mapView.getCamera().getZoom() <= CAMERA_ZOOM_LEVEL) { //already zoomed in - keep the same zoom
                        mapView.getCamera().setZoomTo(mapView.getCamera().getZoom());
                    } else {
                        mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL); //camera's currently too zoomed out
                        Log.d("MapFragment", "AFTER camera zoom level : " + mapView.getCamera().getZoom());
                    }


                    try {
                        if(destinationPolygon instanceof Polygon){
                            showLocationDetails( (CustomLocation) ((Polygon) destinationPolygon).getLocations().get(0));
                        }
                        /*if(destinationPolygon instanceof CustomLocation ) {
                            showAmenityDetail( (CustomLocation) ((Polygon) destinationPolygon).getLocations().get(0));
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void didTapMarker() {
    }

    public void didTapOverlay(Overlay overlay) {
        LocationLabelClicker clicker = overlays.get(overlay);
        if (clicker != null) {
            clicker.onClick();
        } else {
            Logger.log("No onClick");
        }
    }

    public void didTapNothing() {
        clearHighlightedColours();
        clearLocationDetails();
        stopNavigation();

        //todo: disabled for testing
        startPolygon = null;
        destinationPolygon = null;
        mSearchMode = SearchMode.STORE;
    }

    private void highlightPolygon(Polygon polygon, int color) {
        if (!originalColors.containsKey(polygon)) {
            originalColors.put(polygon, polygon.getColor());
        }
        polygon.setColor(color);
    }

    private void clearHighlightedColours() {
        Set<java.util.Map.Entry<Polygon, Integer>> colours = originalColors.entrySet();
        for  (java.util.Map.Entry<Polygon, Integer> pair : colours) {
            pair.getKey().setColor(pair.getValue());
        }

        originalColors.clear();

        if(mRemovedPin != null) {
            mapView.addMarker(mRemovedPin, false);
            mRemovedPin = null;
        }

        if(mSelectedPin != null) {
            mapView.removeMarker(mSelectedPin);
            mSelectedPin = null;
        }

        if(mTemporaryParkingLocation != null) removePin(mTemporaryParkingLocation);
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
    private void showParkingDetail(final CustomLocation location, boolean isThisTempParkingSpot, String parkingLotName, String entranceName, String parkingNote, final int parkingPosition){
        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) rlDirection.getLayoutParams();

        String llDealsTitle = "";

        if(isThisTempParkingSpot) {
            llDealsTitle = getResources().getString(R.string.parking_polygon_store_name_temporary);
        } else {
            llDealsTitle = getResources().getString(R.string.parking_polygon_store_name);
        }

        showDirectionCard(true, IdType.AMENITY, Integer.valueOf(location.getExternalID()), llDealsTitle, parkingLotName + ", " + entranceName, null);
        param.height = KcpUtility.dpToPx(getActivity(), 157);

        if(isThisTempParkingSpot) {
            tvParkingNote.setVisibility(View.GONE);
            tvDealName.setText(getResources().getString(R.string.parking_save_as_my_parking_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(parkingPosition == -1) return;
                    ParkingManager.saveParkingSpotAndEntrance(getActivity(), "", parkingPosition, 0);
                    mMainActivity.setUpRightSidePanel();
                    onParkingClick(true, true);
                    InfoFragment.getInstance().setParkingSpotCTA();
                }
            });
            ivDeal.setVisibility(View.GONE);
        } else {
            if(!parkingNote.equals("")) {
                tvParkingNote.setVisibility(View.VISIBLE);
                tvParkingNote.setText(parkingNote);
            }
            tvDealName.setText(getResources().getString(R.string.parking_remove_my_spot));
            llDeals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    didTapNothing();
                    removePin(location);
                    ParkingManager.removeParkingLot(getActivity());
                    Amenities.saveToggle(getActivity(), Amenities.GSON_KEY_PARKING, false); //make sure to set this as false otherwise everytime map fragment's tapped, it will start parkingActivity
                    mMainActivity.setUpRightSidePanel();
                    Toast.makeText(getActivity(), "Removed Parking Spot", Toast.LENGTH_SHORT).show();
                }
            });
            ivDeal.setVisibility(View.VISIBLE);
        }

        llDeals.setVisibility(View.VISIBLE);
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

    private void showAmenityDetail(final CustomLocation location, final Drawable amenityDrawable){
        logger.debug("Location: name = " + location.getName() + " externalID = " + location.getExternalID());
        String categoryName = "";
        try {
            categoryName = location.getCategories().get(0).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showDirectionCard(true, IdType.AMENITY, Integer.valueOf(location.getExternalID()), location.getName(), categoryName, amenityDrawable);
        clearHighlightedColours();
    }

    private void showLocationDetails(CustomLocation location) {
        logger.debug("Location: name = " + location.getName() + " externalID = " + location.getExternalID());

        String categoryName = "";
        try {
            categoryName = location.getCategories().get(0).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showDirectionCard(true, IdType.EXTERNAL_CODE, Integer.valueOf(location.getExternalID()), location.getName(), categoryName, null);
    }

    private void clearLocationDetails() {
        mMainActivity.toggleDestinationEditor(true, null, null, null);
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
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

    private void showLocations() {
        for (Location location : activeVenue.getLocations()) {

            List<Coordinate> coords = location.getNavigatableCoordinates();
            if (coords.size() > 0) {
                Overlay2DLabel label = new Overlay2DLabel(location.getName(), 36, Typeface.DEFAULT);
                label.setPosition(coords.get(0));
                LocationLabelClicker clicker = new LocationLabelClicker();
                clicker.location = (CustomLocation) location;
                overlays.put(label, clicker);
                mapView.addMarker(label);
            }
        }
    }


    @Override
    public void onAmenityClick(boolean enabled, String amenityName) {
        try {
            HashMap<String, ArrayList<CustomLocation>> amenityHashmap = CustomLocation.getAmenityHashMap();
            ArrayList<CustomLocation> amenityList = amenityHashmap.get(amenityName);
            if(amenityList != null){
                for(final CustomLocation location : amenityList) {
                    if(enabled){
                        List<Coordinate> coords = location.getNavigatableCoordinates();
                        for(final Coordinate coordinate : coords) {
                            Glide
                                    .with(getActivity())
                                    .load(location.logo.get(PIN_IMAGE_SIZE_DP))
                                    .asBitmap()
                                    .into(new SimpleTarget<Bitmap>(PIN_IMAGE_SIZE_DP, PIN_IMAGE_SIZE_DP) {
                                        @Override
                                        public void onResourceReady(final Bitmap resource, GlideAnimation glideAnimation) {
                                            mAmeityDrawable = new BitmapDrawable(getResources(), resource);
                                            mAmeityDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                                            dropPin(coordinate, location, mAmeityDrawable);
                                        }
                                    });
                        }
                    } else {
                        removePin(location);
                    }
                }
            }


        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void dropPinWithColor(final Coordinate coordinate, final Location location, final Drawable pinDrawable){
        //TODO: only add pins to the current floor
        Drawable clone = pinDrawable.getConstantState().newDrawable();
        clone.mutate(); //prevent all instance of drawables from being affected
        clone.setColorFilter(getResources().getColor(R.color.themeColor), PorterDuff.Mode.MULTIPLY);
        Overlay2DImage label = new Overlay2DImage(PIN_IMAGE_SIZE_DP, PIN_IMAGE_SIZE_DP, clone);
        label.setPosition(coordinate);
        //if I remove the lable at the same spot and add another label with new LocationLabelClicker,
        //removed label's LocationLabelClicker gets called again so shouldn't add another labelClicker
        mSelectedPin = label;

        mapView.addMarker(label, false);
    }


    public void dropPin(final Coordinate coordinate, final Location location, final Drawable pinDrawable){
        //TODO: only add pins to the current floor
        Overlay2DImage label = new Overlay2DImage(PIN_IMAGE_SIZE_DP, PIN_IMAGE_SIZE_DP, pinDrawable);
        label.setPosition(coordinate);
        LocationLabelClicker clicker = new LocationLabelClicker();
        clicker.location = (CustomLocation) location;
        clicker.drawable = pinDrawable;
        clicker.label = label;
        clicker.coordinate = coordinate;
        overlays.put(label, clicker);
        mapView.addMarker(label, false);
    }

    public void removePin(final Location location) {
        if(location == null) return;
        for (HashMap.Entry<Overlay, LocationLabelClicker> entry : overlays.entrySet()) {
            Overlay overlay = entry.getKey();
            LocationLabelClicker locationLabelClicker = entry.getValue();
            if(locationLabelClicker.location == location){
                mapView.removeMarker(overlay);
            }
        }
    }

    @Override
    public void onDealsClick(boolean enabled, boolean resetDealsList) {
        //TODO: when deals list's refreshed, this mRecommendedDealsExternalCodeList should be set to null to refresh this list too

//          ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true); //ALL DEALS
        ArrayList<KcpContentPage> dealContentPages = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_RECOMMENDED).getKcpContentPageList(true); //RECOMMENDED DEALS
        if(mRecommendedDealsExternalCodeList == null) {
            if(dealContentPages == null) return;
            mRecommendedDealsExternalCodeList = new ArrayList<String>();
            for(KcpContentPage kcpContentPage : dealContentPages) {
                mRecommendedDealsExternalCodeList.add(kcpContentPage.getExternalCode());
            }
        }

        for( String externalCode: mRecommendedDealsExternalCodeList) {
            Location location = CustomLocation.getLocation(externalCode);
            if(location != null) removePin(location);
        }

        if(enabled && dealContentPages != null) {
            for(KcpContentPage kcpContentPage : dealContentPages) {
                Location location = CustomLocation.getLocation(kcpContentPage.getExternalCode());
                if(location != null){
                    List<Coordinate> coords = location.getNavigatableCoordinates();
                    for(Coordinate coordinate : coords) {
                        Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_deals, 0);
                        dropPin(coordinate, location, amenityDrawable);
                    }
                }
                mRecommendedDealsExternalCodeList = new ArrayList<String>();
                mRecommendedDealsExternalCodeList.add(kcpContentPage.getExternalCode());
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
        amientyView.layout(0, 0, KcpUtility.dpToPx(getActivity(), PIN_IMAGE_SIZE_DP), KcpUtility.dpToPx(getActivity(), PIN_IMAGE_SIZE_DP));

        amientyView.buildDrawingCache(true);
        Bitmap bitmap = amientyView.getDrawingCache();

        Drawable d = new BitmapDrawable(getResources(), bitmap);
        return d;
    }


    public void showParkingSpotFromDetailActivity(int parkingLotPosition, Polygon polygon){
        try {
            Parking storeParking = ParkingManager.sParkings.getParkings().get(parkingLotPosition);
            String parkingId = storeParking.getChildParkings().get(0).getParkingId();
            HashMap<String, CustomLocation> parkingHashMap = CustomLocation.getParkingHashMap();
            if(parkingHashMap.containsKey(parkingId)){
                if(mTemporaryParkingLocation != null) removePin(mTemporaryParkingLocation);
                mTemporaryParkingLocation = parkingHashMap.get(parkingId); //have a single instance of parking so it's easy to delete
                List<Coordinate> coords = mTemporaryParkingLocation.getNavigatableCoordinates();
                for(final Coordinate coordinate : coords) {

                    String parkingLotName = storeParking.getName();
                    String entranceName = storeParking.getChildParkings().get(0).getName();
                    showParkingDetail(mTemporaryParkingLocation, true, parkingLotName, entranceName, "", parkingLotPosition);

                    Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, R.drawable.circle_imageview_background_parking);
                    dropPin(coordinate, mTemporaryParkingLocation, amenityDrawable);
                    mapView.getCamera().focusOn(polygon);
//                    mapView.getCamera().focusOn(mTemporaryParkingLocation.getNavigatableCoordinates().get(0));//ocusOn(coordinate);
                    mapView.getCamera().setZoomTo(CAMERA_ZOOM_LEVEL_NEAREST_PARKING);

                    destinationPolygon = mTemporaryParkingLocation;
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public void onParkingClick(boolean enabled, boolean focus) {
        try {
            if(!ParkingManager.isParkingLotSaved(getActivity())){
                final Intent intent = new Intent (getActivity(), ParkingActivity.class);
                intent.putExtra("image", "bitmap.png");
                getActivity().startActivityForResult(intent, Constants.REQUEST_CODE_SAVE_PARKING_SPOT);
            } else {
                HashMap<String, CustomLocation> parkingHashMap = CustomLocation.getParkingHashMap();
                String parkingId = ParkingManager.getMyEntrance(getActivity()).getParkingId();
                if(parkingHashMap.containsKey(parkingId)){
                    if(mSavedParkingLocation != null) removePin(mSavedParkingLocation);
                    mSavedParkingLocation = parkingHashMap.get(parkingId); //have a single instance of parking so it's easy to delete
                        if(enabled) {
                        List<Coordinate> coords = mSavedParkingLocation.getNavigatableCoordinates();
                        for(final Coordinate coordinate : coords) {
//                            if(focus) mapView.getCamera().focusOn(mSavedParkingLocation.getNavigatableCoordinates().get(0)); //need to zoom to the parking pin?
                            String parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
                            String entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
                            String parkingNote = ParkingManager.getParkingNotes(getActivity());
                            showParkingDetail(mSavedParkingLocation, false, parkingLotName, entranceName, parkingNote, -1);
                            Drawable amenityDrawable = getDrawableFromView(R.drawable.icn_car, 0);
                            dropPin(coordinate, mSavedParkingLocation, amenityDrawable);
                            destinationPolygon = mSavedParkingLocation;
                        }
                    } else {
                        removePin(mSavedParkingLocation);
                        didTapNothing();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private class LocationLabelClicker {
        public CustomLocation location = null;
        public Drawable drawable = null;
        public Overlay2DImage label = null;
        public Coordinate coordinate = null;
        public void onClick() {
            if(path != null) return; //map shouldn't be clicakble when the paths drawn
            if(location != null) {
                //remove the detail card if this amenity was selected before - to enable
                //should implement function to check whether this amenity was clicked previously. destinationPolygon == location is always true as it location is not for a specific amenity
                /*if(destinationPolygon != null && destinationPolygon == location) {
                    destinationPolygon = null;
                    showDirectionCard(false, null, 0, null, null, null);
                    return;
                }*/

                if(location == mSavedParkingLocation) {
                    String parkingLotName = ParkingManager.getMyParkingLot(getActivity()).getName();
                    String entranceName = ParkingManager.getMyEntrance(getActivity()).getName();
                    String parkingNote = ParkingManager.getParkingNotes(getActivity());
                    showParkingDetail(location, false, parkingLotName, entranceName, parkingNote, -1);
                } /* else if(location == mTemporaryParkingLocation ){ //unnecessary
                    showParkingDetail(location, true, "", "", "");
                } */else {
                    showAmenityDetail((CustomLocation) location, drawable);
                }
                destinationPolygon = location;

                if(!location.getAmenityType().equals(CustomLocation.TYPE_AMENITY_PARKING)){
                    //highlight the polygon
                    dropPinWithColor(coordinate, location, drawable);
                    mRemovedPin = label;
                    mapView.removeMarker(label);
                }

            }
        };
    }


    private void didTapMarker (Overlay2DImage label, Coordinate coordinate, Drawable drawable, Location location) {
        dropPin(coordinate, location, mAmeityDrawable);
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
                } else if(v.getId() == R.id.etStartStore) {
                    mSearchMode = SearchMode.ROUTE_START;
                }
            } else {
                mMainActivity.rvMap.setPadding(0, 0, 0, 0);
                mMainActivity.rvMap.setVisibility(View.INVISIBLE);
                if(btnShowMap != null) btnShowMap.setVisibility(View.VISIBLE);
            }
        }
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
                rlDirection.setVisibility(View.GONE);
                Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.anim_slide_down_out_of_screen);
                slideUpAnimation.reset();
                rlDirection.startAnimation(slideUpAnimation);
            }
            return;
        }

        if( !idType.equals(IdType.AMENITY) && (id == 0 || id == -1)) return;

        if(amenityDrawable != null && idType.equals(IdType.AMENITY)) {
            ivAmenity.setVisibility(View.VISIBLE);
            ivAmenity.setImageDrawable(amenityDrawable);
        } else {
            ivAmenity.setVisibility(View.GONE);
        }

        tvParkingNote.setVisibility(View.GONE);

        //check if deal exists for this store
        ArrayList<KcpContentPage> dealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true);
        final ArrayList<KcpContentPage> dealsForThisStore = new ArrayList<KcpContentPage>();

        if(dealsList != null){
            for(int i = 0 ; i < dealsList.size(); i++){
                if(  (idType.equals(IdType.ID) && dealsList.get(i).getStore().getPlaceId() == id) ||
                        (idType.equals(IdType.EXTERNAL_CODE) && id == Integer.parseInt(dealsList.get(i).getStore().getExternalCode())) ){
                    dealsForThisStore.add(dealsList.get(i));
                }
            }
        }

        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) rlDirection.getLayoutParams();

        if(dealsForThisStore.size() > 0){
            /*if deal exists, change the height of rlDirection from 109dp to 157dp and set deal layout's visibility to visible*/
            ivDeal.setImageDrawable(getResources().getDrawable(R.drawable.icn_deals));
            llDeals.setVisibility(View.VISIBLE);
            tvDealName.setText(dealsForThisStore.get(0).getTitle());
            param.height = KcpUtility.dpToPx(getActivity(), 157);

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
            param.height = KcpUtility.dpToPx(getActivity(), 109);
        }
        rlDirection.setLayoutParams(param);
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
