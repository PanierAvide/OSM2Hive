package info.pavie.osm2hive;

import info.pavie.osm2hive.controller.TestOSMParser;
import info.pavie.osm2hive.model.xml.TestMarkup;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		TestOSMParser.class,
		TestMarkup.class
		})

/**
 * This class runs all project tests.
 * @author Adrien PAVIE
 */
public class TestSuite {;}