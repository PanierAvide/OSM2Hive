package info.pavie.osm2hive.model.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link State}.
 * @author Adrien PAVIE
 */
public class TestState {
//ATTRIBUTES
	private State s1, s2, s3, s4, s5;
	private StringBuilder sb1;

//SETUP
	@Before
	public void setUp() throws Exception {
		sb1 = new StringBuilder();
		
		s1 = new State();
		s2 = new State(true);
		s3 = new State(false, sb1);
		s4 = new State(false, sb1);
		s5 = new State(true);
	}

//TESTS
/*
 * Constructors
 */
	@Test
	public void testState() {
		assertFalse(s1.isFinal());
		assertNull(s1.getCapture());
	}

	@Test
	public void testStateBoolean() {
		assertTrue(s2.isFinal());
		assertNull(s2.getCapture());
	}

	@Test
	public void testStateBooleanStringBuilder() {
		assertFalse(s3.isFinal());
		assertEquals(sb1, s3.getCapture());
	}

/*
 * eval()
 */
	@Test
	public void testEvalOneFinalState() {
		assertTrue(s2.eval(""));
	}
	
	@Test
	public void testEvalOneNonFinalState() {
		assertFalse(s1.eval(""));
		assertFalse(s1.eval("You shall not pass !"));
	}
	
	@Test
	public void testEvalTwoStatesSimple() {
		s1.addTransition('a', s2);
		s1.addTransition('b', s3);
		s3.addTransition('c', s2);
		
		assertTrue(s1.eval("a"));
		assertTrue(s1.eval("bc"));
		assertFalse(s1.eval("b"));
		assertFalse(s1.eval("aa"));
	}
	
	@Test
	public void testEvalTwoStatesUnicode() {
		s1.addTransition('<', s2);
		
		assertTrue(s1.eval("<"));
		assertFalse(s1.eval(">"));
		assertFalse(s1.eval("<>"));
	}

/*
 * Tests related to capture
 */
	@Test
	public void testCapture() {
		s3.addTransition("[a-z]", s4);
		s4.addTransition("[0-9]", s5);
		
		String result = "c4";
		assertTrue(s3.eval(result));
		assertEquals(result, sb1.toString());
	}
/*
 * addTransition()
 */
	@Test
	public void testAddTransitionStringState() {
		s1.addTransition("[a-z]", s2);
		
		assertTrue(s1.eval("a"));
		assertTrue(s1.eval("x"));
		assertFalse(s1.eval("T"));
	}

	@Test
	public void testAddTransitionCharState() {
		s1.addTransition('é', s2);
		assertTrue(s1.eval("é"));
		assertFalse(s1.eval("b"));
	}

}
