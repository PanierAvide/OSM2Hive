package info.pavie.osm2hive.model.osm;

import java.util.ArrayList;
import java.util.List;

/**
 * A relation is a meta OSM object. It allows to combine other elements.
 * @author Adrien PAVIE
 */
public class Relation extends Element {
//ATTRIBUTES
	/** The relation members **/
	private List<Member> members;

//CONSTRUCTOR
	public Relation(long id) {
		super(id);
		members = new ArrayList<Member>();
	}

//ACCESSORS
	@Override
	public String getId() {
		return "R"+id;
	}
	
	/**
	 * @param e The element
	 * @return The role of this element, or null if no role
	 */
	public String getMemberRole(String e) {
		String result = null;
		
		boolean found = false;
		int index = 0;
		while(!found && index < members.size()) {
			if(members.get(index).elem.equals(e)) {
				found = true;
				result = members.get(index).role;
			}
			index++;
		}
		
		if(!found) {
			throw new RuntimeException("Element "+e+" not found");
		}
		
		return result;
	}
	
	/**
	 * @return The list of member elements
	 */
	public List<String> getMembers() {
		List<String> elems = new ArrayList<String>(members.size());
		for(Member m : members) {
			elems.add(m.elem);
		}
		return elems;
	}

//MODIFIERS
	/**
	 * Adds a new member
	 * @param role The role of the member
	 * @param e The element
	 */
	public void addMember(String role, String e) {
		if(e == null || e.equals("")) {
			throw new NullPointerException("Element can't be null");
		}
		
		members.add(new Member(role, e));
	}
	
	/**
	 * Removes a member
	 * @param e The element to remove
	 */
	public void removeMember(String e) {
		boolean found = false;
		int index = 0;
		while(!found && index < members.size()) {
			if(members.get(index).elem == e) {
				found = true;
				members.remove(index);
			}
			index++;
		}
	}
	
//INNER CLASS Member
	private class Member {
	//ATTRIBUTES
		/** The member role **/
		private String role;
		/** The member object **/
		private String elem;
		
	//CONSTRUCTOR
		/**
		 * Default constructor
		 * @param role The member role
		 * @param elem The member object
		 */
		private Member(String role, String elem) {
			this.role = role;
			this.elem = elem;
		}
		
	//ACCESSORS
		@Override
		public String toString() {
			return elem+"=>"+role;
		}
	}
}
