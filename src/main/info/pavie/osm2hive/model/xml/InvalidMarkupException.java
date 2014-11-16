package info.pavie.osm2hive.model.xml;

/**
 * This exception is thrown when a {@link Markup} is not well-formed.
 * @author Adrien PAVIE
 */
public class InvalidMarkupException extends Exception {
//CONSTANTS
	private static final long serialVersionUID = 7634412370328792623L;

//CONSTRUCTORS
	public InvalidMarkupException() {
		super();
	}
	
	public InvalidMarkupException(String message) {
		super(message);
	}
}
