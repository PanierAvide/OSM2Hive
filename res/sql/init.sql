-- SQL Init file for OSM2Hive
-- Initializes OSM2Hive functions
-- Command: hive -i /path/to/init.sql

-- Define project path
SET O2H_PATH=/home/adrien/Documents/Programmation/Java/OSM2Hive;

-- Add JAR
ADD JAR ${hiveconf:O2H_PATH}/OSM2Hive.jar;

-- Create functions
CREATE TEMPORARY FUNCTION OSMImportNodes AS 'info.pavie.osm2hive.controller.HiveNodeImporter';
CREATE TEMPORARY FUNCTION OSMImportWays AS 'info.pavie.osm2hive.controller.HiveWayImporter';
CREATE TEMPORARY FUNCTION OSMImportRelations AS 'info.pavie.osm2hive.controller.HiveRelationImporter';