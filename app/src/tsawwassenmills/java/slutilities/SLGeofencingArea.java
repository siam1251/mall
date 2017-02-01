package slutilities;

import com.senionlab.slutilities.geofencing.interfaces.SLGeometry;

public class SLGeofencingArea {

	private String name;
	private SLGeometry geometry;
	
	public SLGeofencingArea(String name, SLGeometry geometry) {
		this.setName(name);
		this.setGeometry(geometry);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SLGeometry getGeometry() {
		return geometry;
	}

	public void setGeometry(SLGeometry geometry) {
		this.geometry = geometry;
	}
	
}
