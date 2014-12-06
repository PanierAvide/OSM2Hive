-- SQL Test file for OSM2Hive
-- Creates and loads OSM data from res/xml/sample.osm file
-- Command: hive -f /path/to/test.sql

-- Define project path
SET O2H_PATH=/home/adrien/Documents/Programmation/Java/OSM2Hive;

-- Add JAR
ADD JAR ${hiveconf:O2H_PATH}/OSM2Hive.jar;

-- Create functions
CREATE TEMPORARY FUNCTION OSMImportNodes AS 'info.pavie.osm2hive.controller.HiveNodeImporter';
CREATE TEMPORARY FUNCTION OSMImportWays AS 'info.pavie.osm2hive.controller.HiveWayImporter';
CREATE TEMPORARY FUNCTION OSMImportRelations AS 'info.pavie.osm2hive.controller.HiveRelationImporter';

-- Load data
DROP TABLE o2h_test;
CREATE TABLE o2h_test(osm_content STRING) STORED AS TEXTFILE;
LOAD DATA LOCAL INPATH '${hiveconf:O2H_PATH}/res/xml/sample.osm' OVERWRITE INTO TABLE o2h_test;

-- Create test tables
-- Check XML file to compare with created tables
-- Nodes, must contain 4 nodes (N298884269, N261728686, N1831881213, N298884272)
DROP TABLE o2h_test_nodes;
CREATE TABLE o2h_test_nodes AS SELECT OSMImportNodes(osm_content) FROM o2h_test;
SELECT * FROM o2h_test_nodes;

-- Ways, must contain 1 way (W26659127)
DROP TABLE o2h_test_ways;
CREATE TABLE o2h_test_ways AS SELECT OSMImportWays(osm_content) FROM o2h_test;
SELECT * FROM o2h_test_ways;

-- Relations, must contain 1 relation (R56688)
DROP TABLE o2h_test_relations;
CREATE TABLE o2h_test_relations AS SELECT OSMImportRelations(osm_content) FROM o2h_test;
SELECT * FROM o2h_test_relations;