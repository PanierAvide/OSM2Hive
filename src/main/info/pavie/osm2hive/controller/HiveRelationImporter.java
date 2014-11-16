package info.pavie.osm2hive.controller;

import info.pavie.osm2hive.model.osm.Element;
import info.pavie.osm2hive.model.osm.Relation;
import info.pavie.osm2hive.model.xml.InvalidMarkupException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * This class reads an OSM XML file, and creates rows for Hive (Ways only).
 * To use it, you need to have a JAR of this application, and in Hive :
 * ADD JAR /path/to/osm2hive.jar;
 * CREATE TEMPORARY FUNCTION OSMImportRelations AS 'info.pavie.basicosmparser.controller.hive.HiveRelationImporter';
 * CREATE TABLE osmdata(osm_content STRING) STORED AS TEXTFILE;
 * LOAD DATA LOCAL INPATH '/path/to/data.osm' OVERWRITE INTO TABLE osmdata;
 * CREATE TABLE osmrelations AS SELECT OSMImportRelations(osm_content) FROM osmdata;
 * @author Adrien PAVIE
 */
public class HiveRelationImporter extends HiveImporter {
//OTHER METHODS
	@Override
	public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
		//Check hive function call
		checkParameterOI(argOIs);
		
		//Expected output columns
		ArrayList<String> fieldNames = getCommonFieldNames();
		fieldNames.add("Members");
		
		//Expected output types
		ArrayList<ObjectInspector> fieldOIs = getCommonFieldOIs();
		fieldOIs.add(ObjectInspectorFactory.getStandardMapObjectInspector(
				PrimitiveObjectInspectorFactory.javaStringObjectInspector,
				PrimitiveObjectInspectorFactory.javaStringObjectInspector));
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}
	
	@Override
	public void process(Object[] args) throws HiveException {
		try {
			Element current = preprocess(args);
			
			//Check if element is valid and is a node
			if(current != null && current instanceof Relation) {
				//Create result
				Object[] currentRow = new Object[8];
				fillRow(currentRow, current);
				
				//Create members map
				Map<String,String> members = new HashMap<String,String>();
				for(String e : ((Relation) current).getMembers()) {
					//Role
					String role = ((Relation) current).getMemberRole(e);
					if(role.equals("")) { role = "null"; }
					
					members.put(e, role);
				}
				
				currentRow[7] = members;
				
				//Send result
				forward(currentRow);
			}
		} catch (InvalidMarkupException e) {
			throw new HiveException(e);
		}
	}
}
