/*
 * Copyright 2015 Adrien PAVIE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
