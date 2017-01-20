package slutilities;

import com.senionlab.slutilities.type.FloorNr;
import com.senionlab.slutilities.type.SLCoordinate3D;

public class SLSettings {

    //platform
    /*public static final String MAP_KEY = "238493a3-b390-44ef-90e3-ab65e25f94e5";
    public static final String CUSTOMER_ID = "D7881A02-138F-4159-A642-8E4F9C76669F";*/

    //from senion
//    public static final String CUSTOMER_ID = "afafd817-d849-4f6a-9190-a1db22a92bd5";
    public static final String MAP_KEY = "b77166cc-760d-4490-8647-f060dbd8b95c";
    public static final String CUSTOMER_ID = "a22c56a4-51bb-4f8d-9c82-66d52aa205e5";

    public static boolean ENABLE_GEOFENCING = true;
    public static boolean ENABLE_GEOMESSENGER = true;

    public static final SLPointOfInterest[] POINTS_ARRAY = {};

    public static final SLGeofencingArea[] GEOFENCING_ARRAY = {};

    public static HashMap<String, SLIndoorLocationPresenterImpl.GeofenceLocation> GEOFENCE_LOCATIONS = new HashMap<String, SLIndoorLocationPresenterImpl.GeofenceLocation>();
    static {
        GEOFENCE_LOCATIONS.put("KineticCafe", new SLIndoorLocationPresenterImpl.GeofenceLocation("KineticCafe", 43.642069, -79.374585, 200, 0));
        GEOFENCE_LOCATIONS.put("IvanhoeCambridge", new SLIndoorLocationPresenterImpl.GeofenceLocation("IvanhoeCambridge", 45.502519, -73.562517, 200, 0));

        GEOFENCE_LOCATIONS.put("Metropolis At Metrotown", new SLIndoorLocationPresenterImpl.GeofenceLocation("IvanhoeCambridge", 49.226278, -122.999439, 1000, 1));
        GEOFENCE_LOCATIONS.put("zone7", new SLIndoorLocationPresenterImpl.GeofenceLocation("IvanhoeCambridge", 49.224738, -122.999029, 50, 7));
        GEOFENCE_LOCATIONS.put("zone8", new SLIndoorLocationPresenterImpl.GeofenceLocation("IvanhoeCambridge", 49.225178, -122.998364, 15, 8));
    }
}
