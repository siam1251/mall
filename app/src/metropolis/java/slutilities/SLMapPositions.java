package slutilities;

import com.senionlab.slutilities.geofencing.interfaces.SLGeometry;

import java.util.HashMap;

public class SLMapPositions {
	private static HashMap<Integer, Integer> mapPositions;
	public static HashMap<Integer, Integer> getMapPositions(){
		if(mapPositions == null) {
			mapPositions = new HashMap<Integer, Integer>();
			mapPositions.put(0, 0);
			mapPositions.put(1, 2);
			mapPositions.put(2, 4);
			mapPositions.put(3, 5);
			mapPositions.put(4, 1);
			mapPositions.put(5, 0);
			mapPositions.put(6, 3);
			mapPositions.put(7, 4);
			mapPositions.put(8, 5);
			mapPositions.put(9, 2);
		}
		return mapPositions;
	}

	public static int getMapLevel(int senionsMapId){
		if(!getMapPositions().containsKey(senionsMapId)) return 0;
		return getMapPositions().get(senionsMapId);
	}
}
