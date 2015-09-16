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

import info.pavie.osm2hive.model.osm.Element;
import info.pavie.osm2hive.model.osm.Node;
import info.pavie.osm2hive.model.xml.InvalidMarkupException;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * This class reads an OSM XML file, and creates rows for Hive (Nodes only).
 * To use it, you need to have a JAR of this application, and in Hive :
 * ADD JAR /path/to/osm2hive.jar;
 * CREATE TEMPORARY FUNCTION OSMImportNodes AS 'info.pavie.osm2hive.controller.HiveNodeImporter';
 * CREATE TABLE osmdata(osm_content STRING) STORED AS TEXTFILE;
 * LOAD DATA LOCAL INPATH '/path/to/data.osm' OVERWRITE INTO TABLE osmdata;
 * CREATE TABLE osmnodes AS SELECT OSMImportNodes(osm_content) FROM osmdata;
 * @author Adrien PAVIE
 */
public class HiveNodeImporter extends HiveImporter {
//OTHER METHODS
	@Override
	public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
		//Check hive function call
		checkParameterOI(argOIs);
		
		//Expected output columns
		ArrayList<String> fieldNames = getCommonFieldNames();
		fieldNames.add("Latitude");
		fieldNames.add("Longitude");
		
		//Expected output types
		ArrayList<ObjectInspector> fieldOIs = getCommonFieldOIs();
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}
	
	@Override
	public void process(Object[] args) throws HiveException {
		try {
			Element current = preprocess(args, "node|tag");
			
			//Check if element is valid and is a node
			if(current != null && current instanceof Node) {
				//Create result
				Object[] currentRow = new Object[9];
				fillRow(currentRow, current);
				currentRow[7] = ((Node) current).getLat();
				currentRow[8] = ((Node) current).getLon();
				
				//Send result
				forward(currentRow);
			}
		} catch (InvalidMarkupException e) {
			throw new HiveException(e);
		}
	}
}