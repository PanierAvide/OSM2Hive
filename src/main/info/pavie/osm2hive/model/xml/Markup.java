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

package info.pavie.osm2hive.model.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A markup is the base component of a XML file.
 * It has a name, several attributes, and can have text in it.
 * This class only supports single markup, you can't have a markup in another one.
 * The purpose is to allow XML reading line by line.
 * @author Adrien PAVIE
 */
public class Markup {
//CONSTANTS
	/*
	 * Markup types.
	 */
	/** Defines a start markup, for example <node>. **/
	public static final byte START = 1;
	
	/** Defines an end markup, for example </node>. **/
	public static final byte END = 2;
	
	/** Defines a declaration markup, generally <?xml version="1.0" encoding="UTF-8"?>. **/
	public static final byte DECLARATION = 3;
	
	/** Defines an empty markup, for example <node />. **/
	public static final byte EMPTY = 4;
	
	/** Defines a complete markup, for example <node>data</node>. **/
	public static final byte COMPLETE = 5;
	
	/*
	 * Regexes for markups
	 */
	/** Regex for attributes **/
	private static final String RGX_ATTRS = "(?<attrs>(\\s+[a-zA-Z]\\w*\\x3D(?<q>[\\x22\\x27])[^\\x22\\x27]*?\\k<q>)*)?";
	
	/** Regex for start markup **/
	private static final String RGX_START = "\\x3C(?<name>[a-zA-Z]\\w*)"+RGX_ATTRS+"\\s*\\x3E";
	
	/** Pattern for start markup **/
	private static final Pattern PTN_START = Pattern.compile("^"+RGX_START+"$");
	
	/** Pattern for end markup **/
	private static final Pattern PTN_END = Pattern.compile("^\\x3C\\x2F(?<name>[a-zA-Z]\\w*)\\x3E$");
	
	/** Pattern for complete markup **/
	private static final Pattern PTN_COMPLETE = Pattern.compile("^"+RGX_START+"(?<text>[^\\e\\x{3C}\\x{3E}]*)\\x3C\\x2F\\k<name>\\x3E$");
	
	/** Pattern for declaration markup **/
	private static final Pattern PTN_DECLARATION = Pattern.compile("^\\x3C\\x3F(?<name>[a-zA-Z]\\w*)"+RGX_ATTRS+"\\s*\\x3F\\x3E$");
	
	/** Pattern for empty markup **/
	private static final Pattern PTN_EMPTY = Pattern.compile("^\\x3C(?<name>[a-zA-Z]\\w*)"+RGX_ATTRS+"\\s*\\x2F\\x3E$");

//ATTRIBUTES
	/** The markup name, for "html" for an opening HTML markup. **/
	private String name;
	
	/** The markup type, can be START, END, COMPLETE, DECLARATION or EMPTY. **/
	private byte type;
	
	/** The markup attributes. **/
	private Map<String,String> attributes;
	
	/** The markup text. **/
	private String text;
	
//CONSTRUCTORS
	public Markup(String m) throws InvalidMarkupException {
		//NEW PROC
		//Remove surrounding white spaces
		m = m.trim();
		
		//Create pattern matchers
		Matcher mStart = PTN_START.matcher(m);
		Matcher mEnd = PTN_END.matcher(m);
		Matcher mComplete = PTN_COMPLETE.matcher(m);
		Matcher mDeclaration = PTN_DECLARATION.matcher(m);
		Matcher mEmpty = PTN_EMPTY.matcher(m);
		
		String attrs;
		/*
		 * Test markup with each matcher
		 */
		//Case of start markup
		if(mStart.matches()) {
			type = START;
			name = mStart.group("name");
			text = "";
			attrs = mStart.group("attrs");
		}
		//Case of end markup
		else if(mEnd.matches()) {
			type = END;
			name = mEnd.group("name");
			text = "";
			attrs = "";
		}
		//Case of complete markup
		else if(mComplete.matches()) {
			type = COMPLETE;
			name = mComplete.group("name");
			text = mComplete.group("text");
			attrs = mComplete.group("attrs");
		}
		//Case of declaration markup
		else if(mDeclaration.matches()) {
			type = DECLARATION;
			name = mDeclaration.group("name");
			text = "";
			attrs = mDeclaration.group("attrs");
		}
		//Case of empty markup
		else if(mEmpty.matches()) {
			type = EMPTY;
			name = mEmpty.group("name");
			text = "";
			attrs = mEmpty.group("attrs");
		}
		else {
			throw new InvalidMarkupException("The markup is not valid: "+m);
		}
		
		parseAttributes(attrs);
	}

//ACCESSORS
	/**
	 * @return The markup name (for example, if "<node>", the result will be "node").
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The markup type (see class constants)
	 */
	public byte getType() {
		return type;
	}
	
	/**
	 * @return The markup text (for example, if "<node>ABC</node>", the result will be "ABC").
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Get an attribute value
	 * @param key The attribute key
	 * @return The attribute value
	 */
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	/**
	 * Is the attribute defined ?
	 * @param key The attribute key
	 * @return True if defined
	 */
	public boolean hasAttribute(String key) {
		return attributes.containsKey(key);
	}
	
//MODIFIERS
	/**
	 * Set the markup type.
	 * @param t The markup type (use class constants)
	 */
	void setType(byte t) {
		type = t;
	}
	
//OTHER METHODS
	/**
	 * Fills the attributes map with the read attributes from the markup
	 * @param attrs The attributes as a string
	 */
	private void parseAttributes(String attrs) {
		attributes = new HashMap<String,String>();
		
		String attrsStr = attrs.trim();
		
		if(attrsStr.length() > 0) {
			Pattern regex = Pattern.compile(
				    "(\\w+)" +
				    "=" +
				    "(" +
				    "  (?<p>[\\x22\\x27])" +
				    "  (.*?)" +
				    "  \\k<p>" +
				    ")", 
				    Pattern.COMMENTS);
			Matcher regexMatcher = regex.matcher(attrsStr);
			while (regexMatcher.find()) {
			    attributes.put(regexMatcher.group(1), regexMatcher.group(4));
			} 
		}
	}
}
