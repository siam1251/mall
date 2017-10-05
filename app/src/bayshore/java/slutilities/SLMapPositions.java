package slutilities;

import com.senionlab.slutilities.geofencing.interfaces.SLGeometry;

import java.util.HashMap;

public class SLMapPositions {
	private static HashMap<Integer, Integer> mapPositions;
	public static HashMap<Integer, Integer> getMapPositions(){
		if(mapPositions == null) {
			mapPositions = new HashMap<>();
			mapPositions.put(0, 0);
			mapPositions.put(1, 2);
			mapPositions.put(2, 4);
		}
		return mapPositions;
	}

	public static int getMapLevel(int senionsMapId){
		if(!getMapPositions().containsKey(senionsMapId)) return 0;
		return getMapPositions().get(senionsMapId);
	}
}
