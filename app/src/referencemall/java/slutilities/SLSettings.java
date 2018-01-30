package slutilities;

import com.ivanhoecambridge.mall.bluedot.SLIndoorLocationPresenterImpl;
import com.ivanhoecambridge.mall.slutilities.SLGeofencingArea;
import com.ivanhoecambridge.mall.slutilities.SLPointOfInterest;
import com.senionlab.slutilities.type.FloorNr;
import com.senionlab.slutilities.type.SLCoordinate3D;

import java.util.HashMap;

public class SLSettings {

    public static final String MAP_KEY = "125f37a4-6449-43ab-82bf-1fb79152e511";
    public static final String CUSTOMER_ID = "D7881A02-138F-4159-A642-8E4F9C76669F";
    public static double INITIAL_MAP_SLOPE = 32; //how much is the map north inclined in the beginning


    public static boolean ENABLE_GEOFENCING = true;
    public static boolean ENABLE_GEOMESSENGER = true;

    public static final SLPointOfInterest[] POINTS_ARRAY = {};

    public static final SLGeofencingArea[] GEOFENCING_ARRAY = {};

    public static double latitude = 45.3471994;
    public static double longitude = -75.8067581;

    public static HashMap<String, SLIndoorLocationPresenterImpl.GeofenceLocation> GEOFENCE_LOCATIONS = new HashMap<String, SLIndoorLocationPresenterImpl.GeofenceLocation>();
    static {
        GEOFENCE_LOCATIONS.put("KineticCafe", new SLIndoorLocationPresenterImpl.GeofenceLocation("KineticCafe", 43.642069, -79.374585, 200, 0));
        GEOFENCE_LOCATIONS.put("IvanhoeCambridge", new SLIndoorLocationPresenterImpl.GeofenceLocation("IvanhoeCambridge", 45.502519, -73.562517, 200, 0));
        GEOFENCE_LOCATIONS.put("Bayshore", new SLIndoorLocationPresenterImpl.GeofenceLocation("Bayshore", latitude, longitude, 500, 1, true));

    }
}
