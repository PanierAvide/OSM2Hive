OSM2Hive
========

Read-me
-------

OSM2Hive is a collection of User-defined functions for Hive to allow OSM XML data import.
It reads a XML file in a Hive table, and parses it to create new tables, in an easier to use
format. The application tests use JUnit 4 framework.

Usage
-----

OSM2Hive has to be called directly in Hive. To do so, use the following commands (in Hive) :

```
ADD JAR /path/to/osm2hive.jar;
CREATE TEMPORARY FUNCTION OSMImportNodes AS 'info.pavie.osm2hive.controller.HiveNodeImporter';
CREATE TEMPORARY FUNCTION OSMImportWays AS 'info.pavie.osm2hive.controller.HiveWayImporter';
CREATE TEMPORARY FUNCTION OSMImportRelations AS 'info.pavie.osm2hive.controller.HiveRelationImporter';
CREATE TABLE osmdata(osm_content STRING) STORED AS TEXTFILE;
LOAD DATA LOCAL INPATH '/path/to/data.osm' OVERWRITE INTO TABLE osmdata;
CREATE TABLE osmnodes AS SELECT OSMImportNodes(osm_content) FROM osmdata;
CREATE TABLE osmways AS SELECT OSMImportWays(osm_content) FROM osmdata;
CREATE TABLE osmrelations AS SELECT OSMImportRelations(osm_content) FROM osmdata;
```

That's all.

License
-------

Copyright 2014 Adrien PAVIE

Licensed under Apache License 2.0. See LICENSE for complete license.