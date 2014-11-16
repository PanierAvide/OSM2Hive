package info.pavie.osm2hive.model.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A state of a finite-state automaton.
 * It has several transitions based on character recognition.
 * It is possible to capture a String with a StringBuilder.
 * @author Adrien PAVIE
 */
public class State {
//ATTRIBUTES
	/** The transitions from this state, based on character reading. **/
	private Map<Pattern,State> transitions;
	
	/** The listeners which can be called when a transition is followed. **/
	private Map<Pattern,TransitionListener> transitionsListeners;
	
	/** Is this state a final state ? **/
	private boolean isFinal;
	
	/** A string builder for capture **/
	private StringBuilder capture;
	
//CONSTRUCTORS
	/**
	 * Default constructor, creates a non-final state.
	 */
	public State() {
		this(false);
	}
	
	/**
	 * Class constructor
	 * @param isFinal Is this a final state ?
	 */
	public State(boolean isFinal) {
		this.isFinal = isFinal;
		transitions = new HashMap<Pattern,State>();
		transitionsListeners = new HashMap<Pattern,TransitionListener>();
	}
	
	/**
	 * Class constructor, with ability to capture string
	 * @param isFinal Is this a final state ?
	 * @param sb The string builder
	 */
	public State(boolean isFinal, StringBuilder sb) {
		this(isFinal);
		capture = sb;
	}

//ACCESSORS
	/**
	 * @return True if this state is final
	 */
	public boolean isFinal() {
		return isFinal;
	}
	
	/**
	 * @return The string builder
	 */
	public StringBuilder getCapture() {
		return capture;
	}
	
	/**
	 * Is the given string value, according to this state and its transitions.
	 * It also captures string if needed.
	 * @param s The string to evaluate
	 * @return True if valid
	 */
	public boolean eval(String s) {
		boolean result;
		
		//Not empty string
		if(!s.equals("")) {
			char c = s.charAt(0);
			
			//Append to string builder if needed
			if(capture != null) {
				capture.append(c);
			}
			
			//Try to find a transition
			boolean transitionFound = false;
			Iterator<Pattern> itePattern = transitions.keySet().iterator();
			Pattern current = null;
			
			while(!transitionFound && itePattern.hasNext()) {
				current = itePattern.next();
				transitionFound = current.matcher(Character.toString(c)).matches();
			}
			
			if(!transitionFound) {
				result = false;
			} else {
				//Call listener if there is one
				if(transitionsListeners.containsKey(current)) {
					transitionsListeners.get(current).performAction();
				}
				
				//Continue evaluation
				result = transitions.get(current).eval(s.substring(1));
//				System.out.println(current.pattern()+": "+result);
			}
		}
		//If empty string, OK if this state is final
		else {
			result = isFinal;
		}
		
		return result;
	}
	
//MODIFIERS
	/**
	 * Add a new transition, following a given character.
	 * @param c The character to use
	 * @param s The state to reach
	 */
	public void addTransition(char c, State s) {
		addTransition(c, s, null);
	}
	
	/**
	 * Add a new transition, following a given character.
	 * @param c The character to use
	 * @param s The state to reach
	 * @param t The transition listener
	 */
	public void addTransition(char c, State s, TransitionListener t) {
		addTransition("\\x{"+Integer.toHexString(c | 0x10000).substring(1)+"}", s, t);
	}
	
	/**
	 * Add a new transition, following a regex.
	 * The given regex should recognize one character only.
	 * @param p The regular expression to use
	 * @param s The state to reach
	 */
	public void addTransition(String p, State s) {
		addTransition(p, s, null);
	}
	
	/**
	 * Add a new transition, following a regex.
	 * The given regex should recognize one character only.
	 * @param p The regular expression to use
	 * @param s The state to reach
	 * @param t The transition listener
	 */
	public void addTransition(String p, State s, TransitionListener t) {
		Pattern current = Pattern.compile(p);
		transitions.put(current, s);
		if(t != null) {
			transitionsListeners.put(current, t);
		}
	}
}
