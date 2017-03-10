package slutilities;

import com.google.android.gms.maps.model.LatLng;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenterImpl;
import com.senionlab.slutilities.geofencing.geometries.SLCircle;
import com.senionlab.slutilities.geofencing.geometries.a;
import com.senionlab.slutilities.type.FloorNr;
import com.senionlab.slutilities.type.SLCoordinate3D;

import java.util.ArrayList;
import java.util.HashMap;

public class SLSettings {

    public static final String MAP_KEY = "b77166cc-760d-4490-8647-f060dbd8b95c";
    public static final String CUSTOMER_ID = "a22c56a4-51bb-4f8d-9c82-66d52aa205e5";
    public static double INITIAL_MAP_SLOPE = 0; //how much is the map north inclined in the beginning to the left

    public static boolean ENABLE_GEOFENCING = true;
    public static boolean ENABLE_GEOMESSENGER = true;

    public static final SLPointOfInterest[] POINTS_ARRAY = {};

    public static SLGeofencingArea[] GEOFENCING_ARRAY = {};

    public static double latitude = 43.642069;
    public static double longitude = -79.374585;

    public static HashMap<String, SLIndoorLocationPresenterImpl.GeofenceLocation> GEOFENCE_LOCATIONS = new HashMap<String, SLIndoorLocationPresenterImpl.GeofenceLocation>();
    static {
        GEOFENCE_LOCATIONS.put("KineticCafe", new SLIndoorLocationPresenterImpl.GeofenceLocation("KineticCafe", latitude, longitude, 200, 0, true));
        GEOFENCE_LOCATIONS.put("IvanhoeCambridge", new SLIndoorLocationPresenterImpl.GeofenceLocation("IvanhoeCambridge", 45.502519, -73.562517, 200, 0));
    }


}
