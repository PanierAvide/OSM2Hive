package info.pavie.osm2hive.model.xml;

/**
 * This transition listener edits markup type when called.
 * @author Adrien PAVIE
 */
public class TypeTransitionListener extends TransitionListener {
//ATTRIBUTES
	/** The type that the markup will have. **/
	private byte type;
	
//CONSTRUCTOR
	/**
	 * Class constructor
	 * @param m The markup
	 * @param type The type to apply on markup if called
	 */
	public TypeTransitionListener(Markup m, byte type) {
		super(m);
		this.type = type;
	}

//OTHER METHODS
	@Override
	public void performAction() {
		if(currentMarkup.getType() == Markup.UNDEFINED) {
			currentMarkup.setType(type);
		}
		else if(type == Markup.END && currentMarkup.getType() == Markup.START) {
			currentMarkup.setType(Markup.COMPLETE);
		}
		else if(currentMarkup.getType() == Markup.DECLARATION && currentMarkup.getType() != type) {
			currentMarkup.setDeclarationInvalid();
		}
	}
}
