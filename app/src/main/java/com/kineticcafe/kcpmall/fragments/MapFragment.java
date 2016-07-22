package com.kineticcafe.kcpmall.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kineticcafe.kcpmall.R;
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

    private Toolbar toolbar;

    //MAPPED IN
    private boolean accessibleDirections = false;
    private MapViewDelegate delegate = this;
    private MappedIn mappedIn = null;
    private MapView mapView = null;
    private HashMap<Polygon, Integer> originalColors = new HashMap<Polygon, Integer>();
    private HashMap<Overlay, LocationLabelClicker> overlays = new HashMap<Overlay, LocationLabelClicker>();
    private Venue activeVenue = null;
    private Context context;
    private boolean navigationMode = false;
    private Path path;
    private Polygon destinationPolygon = null;
    private ProgressBar pb;
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);

        pb = (ProgressBar) view.findViewById(R.id.pb);
        mappedIn = new MappedIn(getActivity());
        mappedIn.getVenues(new GetVenuesCallback());

        return view;
    }

    private class GetVenuesCallback implements MappedinCallback<Venue[]> {
        @Override
        public void onCompleted(final Venue[] venues) {
            Logger.log("++++++ GetVenuesCallback");
            if (venues.length == 0 ) {
                Logger.log("No venues available! Are you using the right credentials? Talk to your mappedin representative.");
                return;
            }
//            activeVenue = venues[0]; // Grab the first venue, which is likely all you have
            activeVenue = venues[1]; // Grab the first venue, which is likely all you have
            showLocations();
            mapView = (MapView) getActivity().getFragmentManager().findFragmentById(R.id.mapFragment);


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
            mapView.setMap(maps[0]);
            //mapSpinner.setAdapter(new ArrayAdapter<Map>());
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

    @Override
    public void didTapPolygon(final Polygon polygon) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                        path = new Path(directions.getPath(), 5f, 5f, 0x4ca1fc);
                        mapView.addPath(path);
                        mapView.getCamera().focusOn(directions.getPath());
                    }

                    highlightPolygon(polygon, 0x007afb);
                    highlightPolygon(destinationPolygon, 0xff834c);

                    return;
                }
                clearHighlightedColours();
                if (polygon.getLocations().size() == 0) {
                    return;
                }
                destinationPolygon = polygon;
                highlightPolygon(polygon, 0x4ca1fc);
//                showLocationDetails((CustomLocation) polygon.getLocations().get(0));
            }
        });
    }

    @Override
    public void didTapOverlay(Overlay overlay) {
        LocationLabelClicker clicker = overlays.get(overlay);
        if (clicker != null) {
            clicker.click();
        } else {
            Logger.log("No click");
        }
    }

    @Override
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

    private void clearLocationDetails() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    private void clearMarkers() {
        mapView.removeAllMarkers();
    }

    private void startNavigation() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopNavigation();
                navigationMode = true;
            }
        });
    }

    private void stopNavigation() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView.removeAllPaths();
                navigationMode = false;
                path = null;
            }
        });
    }


    private void clearHighlightedColours() {
        Set<java.util.Map.Entry<Polygon, Integer>> colours = originalColors.entrySet();
        for  (java.util.Map.Entry<Polygon, Integer> pair : colours) {
            pair.getKey().setColor(pair.getValue());
        }

        originalColors.clear();
    }

    private class LocationLabelClicker {
        public Location location = null;
        public void click() {
            didTapNothing();
            Coordinate start = activeVenue.getLocations()[5].getNavigatableCoordinates().get(0);
            Directions directions = location.directionsFrom(activeVenue, start, null, null);
            if (directions != null) {
                path = new Path(directions.getPath(), 5f, 5f, 0x4ca1fc);
                mapView.addPath(path);
                mapView.getCamera().focusOn(directions.getPath());
            }
        };
    }

    private void showLocations() {
        if(overlays.size() > 0) {
            pb.setVisibility(View.GONE);
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Location location : activeVenue.getLocations()) {
                    List<Coordinate> coords = location.getNavigatableCoordinates();
                    if (coords.size() > 0) {
                        Overlay2DLabel label = new Overlay2DLabel(location.getName(), 36, Typeface.DEFAULT);
                        label.setPosition(coords.get(0));
                        LocationLabelClicker clicker = new LocationLabelClicker();
                        clicker.location = location;
                        overlays.put(label, clicker);
                        mapView.addMarker(label);
                    }
                }
                pb.setVisibility(View.GONE);

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();
    }


}
