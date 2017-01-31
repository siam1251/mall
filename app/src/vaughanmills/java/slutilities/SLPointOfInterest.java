package slutilities;

import com.senionlab.slutilities.type.SLCoordinate3D;

public class SLPointOfInterest {
	
	private String name;
	private SLCoordinate3D coordinate;
	
	public SLPointOfInterest(String name, SLCoordinate3D coordinate) {
		this.setName(name);
		this.setCoordinate(coordinate);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SLCoordinate3D getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(SLCoordinate3D coordinate) {
		this.coordinate = coordinate;
	}
	
	public static String[] getNameArray(SLPointOfInterest[] pointsOfInterest) {
		String[] names = new String[pointsOfInterest.length];
		int i = 0;
		for (SLPointOfInterest pointOfInterest : pointsOfInterest) {
			names[i++] = pointOfInterest.name;
		}
		return names;
	}
	
}
