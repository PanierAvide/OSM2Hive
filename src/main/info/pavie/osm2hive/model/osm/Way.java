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

import java.util.ArrayList;
import java.util.List;

/**
 * A way is an OSM element, which combines several {@link Node}s to create a path.
 * @author Adrien PAVIE
 */
public class Way extends Element {
//ATTRIBUTES
	/** The list of nodes of the way **/
	private List<String> nodes;
	
//CONSTRUCTOR
	/**
	 * Default constructor
	 * @param id The object ID
	 * @param nodes Its nodes
	 */
	public Way(long id, List<String> nodes) {
		super(id);
		
		//Conditions on nodes
		if(nodes == null) {
			throw new NullPointerException("Nodes list can't be null");
		}
		if(nodes.size() < 2) {
			throw new RuntimeException("A way should have at least two nodes");
		}
		
		this.nodes = nodes;
	}
	
	/**
	 * Constructor without nodes, not safe to use !
	 * Don't forget to add at least two nodes
	 * @param id The object ID
	 */
	public Way(long id) {
		super(id);
		this.nodes = new ArrayList<String>();
	}

//ACCESSORS
	@Override
	public String getId() {
		return "W"+id;
	}

	/**
	 * @return The list of nodes of the way
	 */
	public List<String> getNodes() {
		return nodes;
	}

//MODIFIERS
	/**
	 * @param n The node to add at the end of the way
	 */
	public void addNode(String n) {
		nodes.add(n);
	}
	
	/**
	 * @param index The index of the node to remove
	 */
	public void removeNode(int index) {
		if(nodes.size() == 2) {
			throw new RuntimeException("Can't remove node, only two remaining");
		}
		nodes.remove(index);
	}
}