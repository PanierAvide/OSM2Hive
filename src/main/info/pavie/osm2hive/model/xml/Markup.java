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
	/** Defines an undefined markup... **/
	public static final byte UNDEFINED = 0;
	
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

//ATTRIBUTES
	/** The automaton used to recognize markups. **/
	private State automaton;
	
	/** The string builder used to capture markup elements **/
	private StringBuilder sbName, sbAttrs, sbText, sbEndName;
	
	/** The markup name, for "html" for an opening HTML markup. **/
	private String name;
	
	/** The markup type, can be START, END, DECLARATION or EMPTY. **/
	private byte type;
	
	/** The markup attributes. **/
	private Map<String,String> attributes;
	
	/** Defines the current declaration markup invalid (type change) **/
	private boolean invalidDeclaration;
	
	/** The markup text. **/
	private String text;
	
//CONSTRUCTORS
	public Markup(String m) throws InvalidMarkupException {
		initAutomaton();
		
		type = UNDEFINED;
		invalidDeclaration = false;
		
//		System.out.println(m); //TODO Remove
		
		if(!automaton.eval(m)) {
			throw new InvalidMarkupException("The markup is not valid: "+m);
		}
		
		if(invalidDeclaration) {
			throw new InvalidMarkupException("The declaration markup isn't well formed: "+m);
		}
		
		/*
		 * Process name
		 */
		if(type != END) {
			name = sbName.toString().trim();
		} else {
			name = sbEndName.toString().trim();
		}
		
		//Fix for declaration name
		if(type == DECLARATION) {
			name = name.substring(1);
		}
		
		//Fix for names with markup character at end
		if(Pattern.compile(".*[/\\x{3E}]$").matcher(name).matches()) {
			name = name.substring(0, name.length()-1);
		}
		
		//Check if endName == name
		String endName = sbEndName.toString().trim();
		if(type == COMPLETE && !name.equals(endName.substring(0, endName.length()-1))) {
			throw new InvalidMarkupException("Start and end markup name differs ("+name+" and "+endName+")");
		}
		
		/*
		 * Process text
		 */
		text = sbText.toString().trim();
		if(text.length() > 0) { text = text.substring(0, text.length()-1); }
		
		parseAttributes();
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
	private void parseAttributes() {
		attributes = new HashMap<String,String>();
		
		String attrsStr = sbAttrs.toString().trim();
		
		if(attrsStr.length() > 0) {
//			//Create an array with each string containing one key="value" chain
//			String[] attrs = attrsStr.split("\\s+");
//			
//			//Create each map entry
//			for(int i=0; i < attrs.length; i++) {
//				String attr = attrs[i];
//				String[] keyVal = attr.split("\\x3D"); //Split using '='
//				if(keyVal.length == 2) {
//					String value = keyVal[1].substring(1, keyVal[1].length()-1);
//					
//					//Delete ending '"' for unspaced attributes
//					if(i == attrs.length-1 && unspacedAttributes) {
//						value = value.substring(0, value.length()-1);
//					}
//					
//					attributes.put(
//							keyVal[0],
//							value);
//				}
//			}
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
	
	/**
	 * Defines the current declaration markup as invalid.
	 * For example, when markup ends with "/>".
	 */
	void setDeclarationInvalid() {
		invalidDeclaration = true;
	}
	
	/**
	 * Initializes the finite-state automaton for markup recognition.
	 * It does simple recognition, it could be improved to read more complex markups.
	 */
	private void initAutomaton() {
		//Init string builders
		sbName = new StringBuilder();
		sbAttrs = new StringBuilder();
		sbText = new StringBuilder();
		sbEndName = new StringBuilder();
		
		//Starting state
		automaton = new State();
		automaton.addTransition("\\s", automaton); //Remove whitespaces
		
		State startMarkup = new State(false, sbName);
		automaton.addTransition('<', startMarkup); //Markup opening

		//Markup name
		State name2 = new State(false, sbName);
		startMarkup.addTransition("[a-zA-Z]", name2); //First character of the name
		name2.addTransition("\\w", name2); //Following characters of the name
		
		State endName = new State(false, sbAttrs);
		name2.addTransition("\\s", endName); //White space between name and attributes
		endName.addTransition("\\s", endName); //Idem
		
		//Declaration '?'
		State declStart = new State(false, sbName);
		TransitionListener tl1 = new TypeTransitionListener(this, DECLARATION);
		startMarkup.addTransition('?', declStart, tl1);
		declStart.addTransition("[a-zA-Z]", name2);
		
		//Attributes
		State startAttrs = new State(false, sbAttrs);
		endName.addTransition("[a-zA-Z]", startAttrs); //First character of attributes
		startAttrs.addTransition("\\w", startAttrs); //Following characters of first key
		
		State equalSign = new State(false, sbAttrs);
		startAttrs.addTransition('=', equalSign); //'=' for first attribute
		
		State startValue = new State(false, sbAttrs);
		startValue.addTransition("[^\\x{22}\\x{27}]", startValue);
		equalSign.addTransition("[\\x{22}\\x{27}]", startValue); //Escape character for attribute value
		
		State endEscChar = new State(false, sbAttrs);
		startValue.addTransition("[\\x{22}\\x{27}]", endEscChar); //The end escape character for value
		
		State whiteSpace = new State(false, sbAttrs);
		endEscChar.addTransition("\\s", whiteSpace); //White spaces between end of value and rest
		whiteSpace.addTransition("\\s", whiteSpace); //Idem
		whiteSpace.addTransition("[a-zA-Z]", startAttrs); //First char of next key
		
		//End of empty markup
		State endEmptyMarkup1 = new State();
		whiteSpace.addTransition('/', endEmptyMarkup1); //The / char of empty markup
		name2.addTransition('/', endEmptyMarkup1); //Idem, without attributes and with only one space
		endName.addTransition('/', endEmptyMarkup1); //Idem, without attributes but with spaces
		endEscChar.addTransition('/', endEmptyMarkup1); //Idem without spaces
		
		State endEmptyMarkup2 = new State(true);
		TransitionListener ttl2 = new TypeTransitionListener(this, EMPTY);
		endEmptyMarkup1.addTransition(
				'>',
				endEmptyMarkup2,
				ttl2); //The > char of empty markup
		endEmptyMarkup2.addTransition("\\s", endEmptyMarkup2); //White spaces after empty markup
		
		//End of declaration
		State endDeclarationMarkup = new State();
		whiteSpace.addTransition('?', endDeclarationMarkup); //The ? char of declaration markup
		name2.addTransition('?', endDeclarationMarkup); //Idem, without attributes and with only space
		endName.addTransition('?', endDeclarationMarkup); //Idem, without attributes but with spaces
		endEscChar.addTransition('?', endDeclarationMarkup); //Idem without spaces
		endDeclarationMarkup.addTransition('>', endEmptyMarkup2);
		
		//End of start markup
		State endStartMarkup = new State(true, sbText);
		TypeTransitionListener startListener = new TypeTransitionListener(this, START);
		whiteSpace.addTransition(
				'>',
				endStartMarkup,
				startListener); //The > char of start markup
		endStartMarkup.addTransition("\\s", endStartMarkup); //White spaces after start markup
		name2.addTransition(
				'>',
				endStartMarkup,
				startListener); //The > char of start markup without attributes
		endEscChar.addTransition(
				'>',
				endStartMarkup,
				startListener); //The > char of start markup with attributes and without space
		
		//Text
		State text = new State(true, sbText);
		endStartMarkup.addTransition("[^\\e\\x{3C}\\x{3E}]", text);
		text.addTransition("[^\\e\\x{3C}\\x{3E}]", text);
		
		//End markup
		State startEndMarkup = new State();
		text.addTransition('<', startEndMarkup);
		
		State slashEndMarkup = new State(false, sbEndName);
		TypeTransitionListener endListener = new TypeTransitionListener(this, END);
		startEndMarkup.addTransition('/', slashEndMarkup, endListener);
		startMarkup.addTransition('/', slashEndMarkup, endListener);
		
		State nameEndMarkup = new State(false, sbEndName);
		slashEndMarkup.addTransition("[a-zA-Z]", nameEndMarkup);
		nameEndMarkup.addTransition("\\w", nameEndMarkup);
		
		State endEndMarkup = new State(true);
		nameEndMarkup.addTransition('>', endEndMarkup);
		endEndMarkup.addTransition("\\s", endEndMarkup);
	}
}
