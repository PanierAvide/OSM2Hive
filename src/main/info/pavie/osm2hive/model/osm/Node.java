package info.pavie.osm2hive.model.osm;

/**
 * A node is an OSM element with coordinates.
 * @author Adrien PAVIE
 */
public class Node extends Element {
//ATTRIBUTES
	/** The latitude **/
	private double lat;
	/** The longitude **/
	private double lon;
	
//CONSTRUCTOR
	/**
	 * Default constructor
	 * @param id The object ID
	 * @param lat The latitude
	 * @param lon The longitude
	 */
	public Node(long id, double lat, double lon) {
		super(id);
		this.lat = lat;
		this.lon = lon;
	}

//ACCESSORS
	@Override
	public String getId() {
		return "N"+id;
	}

	/**
	 * @return the latitude
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @return the longitude
	 */
	public double getLon() {
		return lon;
	}

//MODIFIERS
	/**
	 * @param lat the new latitude
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @param lon the new longitude
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}
}
