package com.kineticcafe.kcpmall.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.activities.DetailActivity;
import com.kineticcafe.kcpmall.activities.MainActivity;
import com.kineticcafe.kcpmall.adapters.CategoryStoreRecyclerViewAdapter;
import com.kineticcafe.kcpmall.adapters.adapterHelper.IndexableRecylerView;
import com.kineticcafe.kcpmall.adapters.adapterHelper.SectionedLinearRecyclerViewAdapter;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.mappedin.CustomLocation;
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
import com.mappedin.sdk.Overlay;
import com.mappedin.sdk.Overlay2DLabel;
import com.mappedin.sdk.Path;
import com.mappedin.sdk.Polygon;
import com.mappedin.sdk.RawData;
import com.mappedin.sdk.Venue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Kay on 2016-06-20.
 */
public class MapFragment extends BaseFragment implements MapViewDelegate {
    private static MapFragment sMapFragment;
    public static MapFragment getInstance(){
        if(sMapFragment == null) sMapFragment = new MapFragment();
        return sMapFragment;
    }

    enum SearchMode { STORE, ROUTE_START, ROUTE_DESTINATION }
    public SearchMode mSearchMode = SearchMode.STORE;

    private ProgressBar pb;
    private View view;
    private SearchView mSearchView;
    private IndexableRecylerView rv;
    private RelativeLayout rlDirection;
    private TextView tvStoreName;
    private TextView tvCategoryName;
    private Button btnShowMap;

    private LinearLayout llDeals;
    private LinearLayout llDirection;
    private TextView tvDealName;
    private TextView tvNumbOfDeals;

    private View viewRoute;
    //    private ImageView ivRoute;
    private String mSearchString = "";
    private MenuItem mSearchItem;
    private MenuItem mFilterItem;
    public CategoryStoreRecyclerViewAdapter mPlaceRecyclerViewAdapter;





    //MAPPED IN
    private boolean accessibleDirections = false;
    private MapViewDelegate delegate = this;

    private MappedIn mappedIn = null;
    private MapView mapView = null;
    private Map[] maps = null;

    //    private Spinner mapSpinner = null;
//    private TextView titleLabel = null;
//    private TextView descriptionLabel = null;
//    private ImageView logoImageView = null;
//    private TextView selectOriginTextView = null;
    private Button goButton = null;
    private Button showLocationsButton = null;

    private HashMap<Polygon, Integer> originalColors = new HashMap<Polygon, Integer>();
    private HashMap<Overlay, LocationLabelClicker> overlays = new HashMap<Overlay, LocationLabelClicker>();
    private Venue activeVenue = null;
    private boolean navigationMode = false;
    private Path path;
    private Polygon destinationPolygon = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);
        pb = (ProgressBar) view.findViewById(R.id.pb);
        rv = (IndexableRecylerView) view.findViewById(R.id.rv);
        rlDirection = (RelativeLayout) view.findViewById(R.id.rlDirection);
        tvStoreName = (TextView) view.findViewById(R.id.tvStoreName);
        tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
        llDirection = (LinearLayout) view.findViewById(R.id.llDirection);
        llDeals = (LinearLayout) view.findViewById(R.id.llDeals);
        tvDealName = (TextView) view.findViewById(R.id.tvDealName);
        tvNumbOfDeals = (TextView) view.findViewById(R.id.tvNumbOfDeals);
        viewRoute = (View) view.findViewById(R.id.viewRoute);
        viewRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirectionEditor(tvStoreName.getText().toString(), "");
            }
        });

        mapView = new MapView();
//        mapView = (MapView) getActivity().getFragmentManager().findFragmentById(R.id.mapFragment); //another way of adding fragment
        mappedIn = new MappedIn(getActivity());

        //UI handling from sample
//        mapSpinner = (Spinner) view.findViewById(R.id.mapSpinner);
//        logoImageView = (ImageView) view.findViewById(R.id.logoImageView);
//        titleLabel = (TextView) view.findViewById(R.id.titleLabel);
//        descriptionLabel = (TextView) view.findViewById(R.id.descriptionLabel);
//        selectOriginTextView = (TextView) view.findViewById(R.id.selectOriginTextView);
        goButton = (Button) view.findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startNavigation();
            }
        });

        showLocationsButton = (Button) view.findViewById(R.id.showLocationButton);
        showLocationsButton.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showLocations();}});

        btnShowMap = (Button) view.findViewById(R.id.btnShowMap);
        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowMap.setVisibility(View.GONE);
                btnShowMap = null;
                pb.setVisibility(View.VISIBLE);

                Log.d("TEST", "startTime time passed : " + (System.currentTimeMillis()));
                startTime = System.currentTimeMillis();
                mappedIn.getVenues(new GetVenuesCallback());

            }
        });

        setHasOptionsMenu(true);
        setupRecyclerView();
        return view;
    }

    public interface OnStoreClickListener {
        public void onStoreClick(int storeId, String storeName, String categoryName);
    }

    public void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        rv.setLayoutManager(linearLayoutManager);

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
            public void onStoreClick(int storeId, String storeName, String categoryName) {
                if(mSearchMode.equals(SearchMode.STORE)){
                    showDirectionCard(storeId, storeName, categoryName);

                } else if(mSearchMode.equals(SearchMode.ROUTE_START)){
                    mMainActivity.setDestionationNames(storeName, null);
                    if(!mMainActivity.isEditTextsEmpty()) {
                        //TODO: START ROUTING HERE
                        mMainActivity.toggleDestinationEditor(true, null, null, null, null);
                    }
                } else if(mSearchMode.equals(SearchMode.ROUTE_DESTINATION)){
                    mMainActivity.setDestionationNames(null, storeName);

                    if(!mMainActivity.isEditTextsEmpty()) {
                        //TODO: START ROUTING HERE
                        mMainActivity.toggleDestinationEditor(true, null, null, null, null);
                    }
                }
                mSearchString = "";
                setupRecyclerView();
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

        rv.setFastScrollEnabled(true);
        rv.setIndexAdapter(sectionName, sectionPosition);

        SectionedLinearRecyclerViewAdapter.Section[] dummy = new SectionedLinearRecyclerViewAdapter.Section[sections.size()];
        SectionedLinearRecyclerViewAdapter mSectionedAdapter = new SectionedLinearRecyclerViewAdapter(
                getActivity(),
                R.layout.list_section_place,
                R.id.section_text,
                rv,
                mPlaceRecyclerViewAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
        rv.setAdapter(mSectionedAdapter);
    }


    // Get the basic info for all Venues we have access to
    private class GetVenuesCallback implements MappedinCallback<Venue[]> {
        @Override
        public void onCompleted(final Venue[] venues) {
            if (venues.length == 0 ) {
                Logger.log("No venues available! Are you using the right credentials? Talk to your mappedin representative.");
                return;
            }

            activeVenue = venues[1];

            if(!mapView.isAdded()){
                android.app.FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                transaction.add(R.id.flMap, mapView);
                transaction.commit();
            }

//            mapView = (MapView) getActivity().getFragmentManager().findFragmentById(R.id.mapFragment);
            mapView.setDelegate(delegate);
            mappedIn.getVenue(activeVenue, accessibleDirections, new CustomLocationGenerator(), new GetVenueCallback());
        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading any venues. Did you set your credentials? Exception: " + e);
        }
    }

    long startTime;
    // Get the full details on a single Venue
    private class GetVenueCallback implements MappedinCallback<Venue> {
        @Override
        public void onCompleted(final Venue venue) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Log.d("TEST", "GetVenueCallback time passed : " + (System.currentTimeMillis() - startTime));

                    Map[] maps = venue.getMaps();
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

                    Log.d("TEST", "Arrays.sort time passed : " + (System.currentTimeMillis() - startTime));


                    mapView.setMap(maps[0]);

                    Log.d("TEST", "mapView.setMap time passed : " + (System.currentTimeMillis() - startTime));
                    if(pb != null) pb.setVisibility(View.GONE);
                    //mapSpinner.setAdapter(new ArrayAdapter<Map>());
                }
            });
        }

        @Override
        public void onError(Exception e) {
            Logger.log("Error loading Venue: " + e);
        }

    }

    private class CustomLocationGenerator implements LocationGenerator {
        @Override
        public Location locationGenerator(RawData rawData) throws Exception {
            return new CustomLocation(rawData);
        }
    }

    public void didTapPolygon(Polygon polygon) {
        if (navigationMode) {
            if (path != null) {
                didTapNothing();
                return;
            }
            if (polygon.getLocations().size() == 0) {
                return;
            }

            Directions directions = destinationPolygon.directionsFrom(activeVenue, polygon, destinationPolygon.getLocations().get(0).getName(), polygon.getLocations().get(0).getName());
            if (directions != null) {
                path = new Path(directions.getPath(), 0.05f, 0.05f, 0x4ca1fc);
                mapView.addPath(path);
                mapView.getCamera().focusOn(directions.getPath());
            }

            highlightPolygon(polygon, 0x007afb);
            highlightPolygon(destinationPolygon, 0xff834c);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
//                    selectOriginTextView.setVisibility(View.INVISIBLE);
                }
            });
            return;
        }
        clearHighlightedColours();
        if (polygon.getLocations().size() == 0) {
            return;
        }
        destinationPolygon = polygon;
        highlightPolygon(polygon, 0x4ca1fc);
        getActivity().runOnUiThread(new Runnable() {
            public void run () {
                showLocationDetails((CustomLocation) destinationPolygon.getLocations().get(0));
            }
        });
    }

    public void didTapMarker() {

    }

    public void didTapOverlay(Overlay overlay) {
        LocationLabelClicker clicker = overlays.get(overlay);
        if (clicker != null) {
            clicker.click();
        } else {
            Logger.log("No click");
        }
    }

    public void didTapNothing() {
        clearHighlightedColours();
        clearLocationDetails();
        stopNavigation();
        clearMarkers();

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
    }

    private void showLocationDetails(CustomLocation location) {
        clearLocationDetails();
//        titleLabel.setText(location.getName());
//        descriptionLabel.setText(location.description);
        goButton.setVisibility(View.VISIBLE);

        // This sample is using the Ion framework for easy image loading/cacheing. You can use what you like
        // https://github.com/koush/ion
        /*if (location.logo != null) {
            String url = location.logo.get(logoImageView.getWidth(), getActivity()).toString();
            if (url != null) {
                //TODO: switch Ion to Glide
                Ion.with(logoImageView)
                        //.placeholder(R.drawable.placeholder_image)
                        //.error(R.drawable.error_image)
                        //.animateLoad(Animation)
                        .load(location.logo.get(logoImageView.getWidth(), getActivity()).toString());
                *//*Glide.with(getActivity())
                        .load(location.logo.get(logoImageView.getWidth(), getActivity()).toString())
                        .into(logoImageView);*//*
            }
        }*/
    }

    private void clearLocationDetails() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //        titleLabel.setText("");
                //        descriptionLabel.setText("");
                //        logoImageView.setImageDrawable(null);
                goButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void clearMarkers() {
        mapView.removeAllMarkers();
    }

    private void startNavigation() {
        stopNavigation();
        navigationMode = true;
//        selectOriginTextView.setVisibility(View.VISIBLE);
    }

    private void stopNavigation() {
//        selectOriginTextView.setVisibility(View.INVISIBLE);
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

    private class LocationLabelClicker {
        public CustomLocation location = null;
        public void click() {
            getActivity().runOnUiThread(new Runnable() {
                public void run () {
                    didTapNothing();
                    showLocationDetails(location);
                }
            });
        };
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.action_backend_vm).setVisible(false);
        menu.findItem(R.id.action_backend_mp).setVisible(false);
        menu.findItem(R.id.action_test).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_map, menu);

        mSearchItem = menu.findItem(R.id.action_search);
        mFilterItem = menu.findItem(R.id.action_filter);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(new QueryTextListener());
        mSearchView.setQueryHint(getString(R.string.hint_search_store));
        mSearchItem.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        MenuItemCompat.setOnActionExpandListener(mSearchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        rv.setVisibility(View.INVISIBLE);
                        if(btnShowMap != null) btnShowMap.setVisibility(View.VISIBLE);
                        mFilterItem.setVisible(true);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mSearchMode = SearchMode.STORE;
                        rlDirection.setVisibility(View.GONE);
                        mFilterItem.setVisible(false);
                        rv.setVisibility(View.VISIBLE);
                        if(btnShowMap != null) btnShowMap.setVisibility(View.GONE);
                        return true;
                    }
                });
    }

    public class EditTextTextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            onTextChange(s.toString());
        }
    }

    public class FocusListener implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                int topMargin = (int) (KcpUtility.dpToPx(getActivity(), 95) - (KcpUtility.dpToPx(getActivity(), 55))); //directionEditor height - actionbar height
                rv.setPadding(0, topMargin, 0, 0);
                rv.setVisibility(View.VISIBLE);
                if(btnShowMap != null) btnShowMap.setVisibility(View.GONE);
                if(v.getId() == R.id.etDestStore) mSearchMode = SearchMode.ROUTE_DESTINATION;
                else if(v.getId() == R.id.etStartStore) mSearchMode = SearchMode.ROUTE_START;
            } else {
                rv.setPadding(0, 0, 0, 0);
                rv.setVisibility(View.INVISIBLE);
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

    public void showDirectionCard(final int storeId, String storeName, String categoryName){
        //check if deal exists for this store
        ArrayList<KcpContentPage> dealsList = KcpNavigationRoot.getInstance().getNavigationpage(Constants.EXTERNAL_CODE_DEAL).getKcpContentPageList(true);
        final ArrayList<KcpContentPage> dealsForThisStore = new ArrayList<KcpContentPage>();

        if(dealsList != null){
            for(int i = 0 ; i < dealsList.size(); i++){
                if(dealsList.get(i).getStore().getPlaceId() == storeId){
                    dealsForThisStore.add(dealsList.get(i));
                }
            }
        }

        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) rlDirection.getLayoutParams();
        if(dealsForThisStore.size() > 0){
            /*if deal exists, change the height of rlDirection from 109dp to 157dp and set deal layout's visibility to visible*/
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
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

        } else {
            llDeals.setVisibility(View.GONE);
            param.height = KcpUtility.dpToPx(getActivity(), 109);
        }
        rlDirection.setLayoutParams(param);
        mMainActivity.expandTopNav();
        mSearchItem.collapseActionView();

        tvStoreName.setText(storeName);
        tvCategoryName.setText(categoryName);

        rlDirection.setVisibility(View.VISIBLE);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.anim_slide_up);
        slideUpAnimation.reset();
        rlDirection.startAnimation(slideUpAnimation);

        llDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(storeId);
                if(kcpPlace != null) {
                    KcpContentPage kcpContentPage = new KcpContentPage();
                    kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    public void showDirectionEditor(String start, String dest){
        mMainActivity.toggleDestinationEditor(false, start, dest, new EditTextTextChangeListener(), new FocusListener());
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
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(mapView, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            mMainActivity.toggleDestinationEditor(true, null, null, null, null);
            android.app.FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
            transaction.remove(mapView);
            transaction.commit();
        } catch (Exception e) {
        }
    }

}
