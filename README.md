# ACLED v5 (1997-2014) Conflict Dataset Visualization

## Requirements
* Java 8
* Maven 3 (tested with 3.3)
* MySQL 5 (tested with 5.7.10)
* Git

## Setup
* Clone the git repo: `git clone https://github.com/jshipper/acled-conflict-visualization.git`
* Download the dataset
  * Download ZIP file: [ACLED v5 ZIP](http://www.acleddata.com/wp-content/uploads/2015/06/ACLED-Version-5-All-Africa-1997-2014_dyadic_Updated_csv-no-notes.zip)
  * Extract ZIP file to get the CSV
  * Replace carriage returns with new line characters: `sed 's/\r/\n/g' ACLED-Version-5-All-Africa-1997-2014_dyadic_Updated_no_notes.csv > ACLED-Version-5-All-Africa-1997-2014_dyadic_Updated_no_notes-modified.csv` 
* Set up MySQL
  * Create database: `CREATE DATABASE acled;`
  * Create table
  ```sql
  USE acled;
  CREATE TABLE Conflict (
    GWNO INTEGER,
    EVENT_ID_CNTY VARCHAR(256),
    EVENT_ID_NO_CNTY BIGINT PRIMARY KEY,
    EVENT_DATE DATE,
    YEAR INTEGER,
    TIME_PRECISION INTEGER,
    EVENT_TYPE VARCHAR(256),
    ACTOR1 VARCHAR(256),
    ALLY_ACTOR_1 VARCHAR(256),
    INTER1 INTEGER,
    ACTOR2 VARCHAR(256),
    ALLY_ACTOR_2 VARCHAR(256),
    INTER2 INTEGER,
    INTERACTION INTEGER,
    COUNTRY VARCHAR(256),
    ADMIN1 VARCHAR(256),
    ADMIN2 VARCHAR(256),
    ADMIN3 VARCHAR(256),
    LOCATION VARCHAR(256),
    LATITUDE DOUBLE,
    LONGITUDE DOUBLE,
    GEO_PRECIS INTEGER,
    SOURCE VARCHAR(256),
    FATALITIES INTEGER
  );
  ```
  * Load data into table
    * NOTE: If you get an error with the below, you probably need to enable local file loading in your MySQL instance.  This can be done by adding `--local-infile=1` when starting your MySQL client.  More details here: http://dev.mysql.com/doc/refman/5.7/en/load-data-local.html
  ```sql
  LOAD DATA LOCAL INFILE '/path/to/csv/ACLED-Version-5-All-Africa-1997-2014_dyadic_Updated_no_notes-modified.csv'
  INTO TABLE Conflict
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '\\'
  LINES TERMINATED BY '\n' STARTING BY ''
  IGNORE 1 LINES
  (
    GWNO,
    EVENT_ID_CNTY,
    EVENT_ID_NO_CNTY,
    @EVENT_DATE,
    YEAR,
    TIME_PRECISION,
    EVENT_TYPE,
    ACTOR1,
    ALLY_ACTOR_1,
    INTER1,
    ACTOR2,
    ALLY_ACTOR_2,
    INTER2,
    INTERACTION,
    COUNTRY,
    ADMIN1,
    ADMIN2,
    ADMIN3,
    LOCATION,
    LATITUDE,
    LONGITUDE,
    GEO_PRECIS,
    SOURCE,
    FATALITIES
  )
  SET EVENT_DATE=str_to_date(@EVENT_DATE, '%d/%m/%Y');
  ```
  * Create test database: `CREATE DATABASE test;`

## Usage
* Change the values in `dao/src/test/resources/test-app.properties`, `rest-services/src/main/resources/app.properties`, and `rest-services/src/test/resources/test-app.properties` to connect to your MySQL instance
* Build the project with `mvn clean install` in the project's root directory
* Change to the UI directory and start jetty with `mvn jetty:run`
* Access the webapp at localhost:8080
  * If running this in a VM, you can use port forwarding to access the UI from your host machine
