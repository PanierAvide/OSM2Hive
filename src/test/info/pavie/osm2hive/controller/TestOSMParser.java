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

package info.pavie.osm2hive.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import info.pavie.osm2hive.model.osm.Element;
import info.pavie.osm2hive.model.osm.Node;
import info.pavie.osm2hive.model.osm.Relation;
import info.pavie.osm2hive.model.osm.Way;
import info.pavie.osm2hive.model.xml.InvalidMarkupException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link OSMParser}.
 * @author Adrien PAVIE
 */
public class TestOSMParser {
//ATTRIBUTES
	private OSMParser p1;
	private Map<String,Element> result;
	private List<String> readLines;
	
//SETUP
	@Before
	public void setUp() throws Exception {
		p1 = new OSMParser();
		result = new HashMap<String,Element>();
	}

//OTHER METHODS
	/**
	 * Reads a text file, and returns lines as a list
	 * @param f The file to read
	 * @return The list of read lines
	 * @throws FileNotFoundException If file isn't found
	 */
	private List<String> readFileLines(File f) throws FileNotFoundException {
		List<String> lines = new ArrayList<String>();
		Scanner s = new Scanner(f);
		while(s.hasNextLine()) {
			lines.add(s.nextLine());
		}
		s.close();
		return lines;
	}

//TESTS
// parse()
	/**
	 * Test data from sample.osm
	 */
	private void testSampleFile() {
		//First node
		Node n1 = (Node) result.get("N298884269");
		assertEquals(54.0901746, n1.getLat(), 0);
		assertEquals(12.2482632, n1.getLon(), 0);
		assertEquals("SvenHRO", n1.getUser());
		assertEquals(46882, n1.getUid());
		assertTrue(n1.isVisible());
		assertEquals(1, n1.getVersion());
		assertEquals(676636, n1.getChangeset());
		assertEquals("2008-09-21T21:37:45Z", n1.getTimestamp());
		assertEquals(0, n1.getTags().size());
		
		//Tagged node
		Node n2 = (Node) result.get("N1831881213");
		assertEquals(54.0900666, n2.getLat(), 0);
		assertEquals(12.2539381, n2.getLon(), 0);
		assertEquals("lafkor", n2.getUser());
		assertEquals(75625, n2.getUid());
		assertTrue(n2.isVisible());
		assertEquals(1, n2.getVersion());
		assertEquals(12370172, n2.getChangeset());
		assertEquals("2012-07-20T09:43:19Z", n2.getTimestamp());
		assertEquals(2, n2.getTags().size());
		assertEquals("Neu Broderstorf", n2.getTags().get("name"));
		assertEquals("city_limit", n2.getTags().get("traffic_sign"));
		
		//Way
		Way w1 = (Way) result.get("W26659127");
		assertEquals("Masch", w1.getUser());
		assertEquals(55988, w1.getUid());
		assertTrue(w1.isVisible());
		assertEquals(5, w1.getVersion());
		assertEquals(4142606, w1.getChangeset());
		assertEquals("2010-03-16T11:47:08Z", w1.getTimestamp());
		assertEquals(2, w1.getTags().size());
		assertEquals("unclassified", w1.getTags().get("highway"));
		assertEquals("Pastower Straße", w1.getTags().get("name"));
		assertEquals(3, w1.getNodes().size());
		assertEquals("N298884269", w1.getNodes().get(0));
		assertEquals("N298884272", w1.getNodes().get(1));
		assertEquals("N261728686", w1.getNodes().get(2));
		
		//Relation
		Relation r1 = (Relation) result.get("R56688");
		assertEquals("kmvar", r1.getUser());
		assertEquals(56190, r1.getUid());
		assertTrue(r1.isVisible());
		assertEquals(28, r1.getVersion());
		assertEquals(6947637, r1.getChangeset());
		assertEquals("2011-01-12T14:23:49Z", r1.getTimestamp());
		assertEquals(6, r1.getTags().size());
		assertEquals("Küstenbus Linie 123", r1.getTags().get("name"));
		assertEquals("VVW", r1.getTags().get("network"));
		assertEquals("Regionalverkehr Küste", r1.getTags().get("operator"));
		assertEquals("123", r1.getTags().get("ref"));
		assertEquals("bus", r1.getTags().get("route"));
		assertEquals("route", r1.getTags().get("type"));
		assertEquals(4, r1.getMembers().size());
		assertEquals("N298884269", r1.getMembers().get(0));
		assertEquals("N261728686", r1.getMembers().get(1));
		assertEquals("W26659127", r1.getMembers().get(2));
		assertEquals("N298884272", r1.getMembers().get(3));
		assertEquals("stop", r1.getMemberRole("N298884269"));
		assertEquals("path", r1.getMemberRole("W26659127"));
	}
	
	@Test
	public void testParse() throws IOException, InvalidMarkupException {
		readLines = readFileLines(new File("res/xml/sample.osm"));
		
		for(String line : readLines) {
			p1.parse(line);
			if(p1.isElementReady()) {
				result.put(p1.getCurrentElement().getId(), p1.getCurrentElement());
			}
		}
		
		testSampleFile();
	}
	
	@Test
	public void testParseVillage() throws IOException, InvalidMarkupException {
		readLines = readFileLines(new File("res/xml/bleruais.osm"));
		
		for(String line : readLines) {
			p1.parse(line);
			if(p1.isElementReady()) {
				result.put(p1.getCurrentElement().getId(), p1.getCurrentElement());
			}
		}
		
		Way single1 = (Way) result.get("W108790362");
		assertEquals(2, single1.getNodes().size());
		
		Way single4_1 = (Way) result.get("W108790361");
		assertEquals(2, single4_1.getNodes().size());
		
		Way single4_2 = (Way) result.get("W108790366");
		assertEquals(2, single4_2.getNodes().size());
		assertEquals(single4_1.getNodes().get(1), single4_2.getNodes().get(0));
	}
}