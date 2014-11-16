package info.pavie.osm2hive.controller;

import info.pavie.osm2hive.model.osm.Element;
import info.pavie.osm2hive.model.xml.InvalidMarkupException;

import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
 
/**
 * Abstract class, containing common functions to all Hive importers.
 * See each heriting class for more details.
 * @author Adrien PAVIE
 */
public abstract class HiveImporter extends GenericUDTF {
//ATTRIBUTES
	/** Hive String Handler **/
	protected StringObjectInspector stringOI;
	
	/** OSM XML Parser **/
	protected OSMParser parser;
	
	/** Parsed OSM elements **/
	protected Map<String,Element> elements;
	
//OTHER METHODS
	/**
	 * Checks if hive function call is valid, and defines stringOI attribute.
	 * @param argOIs The hive function arguments
	 * @throws UDFArgumentException If function call is invalid
	 */
	protected void checkParameterOI(ObjectInspector[] argOIs) throws UDFArgumentException {
		if(argOIs.length != 1) {
			throw new UDFArgumentException("HiveImporter UDTF takes 1 argument: STRING");
		}
		
		ObjectInspector arg1 = argOIs[0]; //First parameter, corresponding to OSM XML file path
		
		if(!(arg1 instanceof StringObjectInspector)) {
			throw new UDFArgumentException("HiveImporter UDTF takes 1 argument: STRING");
		}
		
		this.stringOI = (StringObjectInspector) arg1;
		this.parser = new OSMParser();
	}
	
	/**
	 * @return The common field names (ID, UserID, Timestamp, ...).
	 */
	protected ArrayList<String> getCommonFieldNames() {
		ArrayList<String> fieldNames = new ArrayList<String>();
		fieldNames.add("ID");
		fieldNames.add("UserID");
		fieldNames.add("Timestamp");
		fieldNames.add("IsVisible");
		fieldNames.add("Version");
		fieldNames.add("ChangesetID");
		fieldNames.add("Tags");
		
		return fieldNames;
	}
	
	/**
	 * @return The common field object inspectors (for ID, UserID, Timestamp, ...)
	 */
	protected ArrayList<ObjectInspector> getCommonFieldOIs() {
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		fieldOIs.add(ObjectInspectorFactory.getStandardMapObjectInspector(
				PrimitiveObjectInspectorFactory.javaStringObjectInspector,
				PrimitiveObjectInspectorFactory.javaStringObjectInspector));
		
		return fieldOIs;
	}
	
	/**
	 * Parses the given line from arguments and returns the read Element
	 * @param args The Hive command arguments
	 * @param markups The markups to read, separated with |, for example: "node|tag|nd|relation"
	 * @return The read OSM element, or null if not ready
	 * @throws InvalidMarkupException If the line isn't a well-formed XML markup
	 */
	protected Element preprocess(Object[] args, String markups) throws InvalidMarkupException {
		Element result = null;
		
		//Parse the received line if necessary
		String line = (String) stringOI.getPrimitiveJavaObject(args[0]);
		
		if(line.matches(".*("+markups+").*")) {
			parser.parse(line);
			result = (parser.isElementReady()) ? parser.getCurrentElement() : null;
		}
		
		return result;
	}
	
	/**
	 * This methods fills a row array with common data extracted from the given element.
	 * @param row The row array (size should be > 7)
	 * @param elem The element to use
	 */
	protected void fillRow(Object[] row, Element elem) {
		row[0] = elem.getId();
		row[1] = elem.getUid();
		row[2] = elem.getTimestamp();
		row[3] = elem.isVisible();
		row[4] = elem.getVersion();
		row[5] = elem.getChangeset();
		row[6] = elem.getTags();
	}
	
	@Override
	public void close() throws HiveException {;}
}