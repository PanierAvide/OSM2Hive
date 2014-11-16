package info.pavie.osm2hive.model.xml;

/**
 * Allows to launch actions when a transition in {@link State} class is followed.
 * @author Adrien PAVIE
 */
public abstract class TransitionListener {
//ATTRIBUTES
	/** The markup to edit. **/
	protected Markup currentMarkup;

//CONSTRUCTOR
	/**
	 * Default constructor
	 * @param m The markup to edit
	 */
	public TransitionListener(Markup m) {
		currentMarkup = m;
	}

//MODIFIERS
	/**
	 * Changes the markup to edit.
	 * @param m The new markup
	 */
	public void setMarkup(Markup m) {
		currentMarkup = m;
	}
	
//OTHER METHODS
	/**
	 * The action to perform when a transition is followed.
	 */
	public abstract void performAction();
}
