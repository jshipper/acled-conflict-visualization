# ACLED v5 (1997-2014) Conflict Dataset Visualization

## Setup

### Requirements
* Docker (tested with Docker 4.17 on Windows 11 host)

### First-time Setup
* Initialize mysql: `docker run --name mysqldb --network acled-mysql -v C:/mysqldb-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=mysqlnotsecure -d mysql:8`
  * `C:/mysqldb-data` can be replaced with wherever you want to store your mysql DB files
  * Set root password (`MYSQL_ROOT_PASSWORD`) as desired
* Download the dataset
  * Download ZIP file: [ACLED v5 ZIP](http://www.acleddata.com/wp-content/uploads/2015/06/ACLED-Version-5-All-Africa-1997-2014_dyadic_Updated_csv-no-notes.zip)
    * Note: This URL is no longer active, contact me if you desire this dataset
  * Extract ZIP file to get the CSV
* Copy CSV dataset into your mysql DB directory
* Once mysql is running, enter the shell: `docker exec -it mysqldb mysql -uroot -p --local-infile=1`
* At mysql prompt:
  * Enable local data loading: `set global local_infile = ON;`
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
  ```sql
  LOAD DATA LOCAL INFILE '/var/lib/mysql/ACLED-Version-5-All-Africa-1997-2014_dyadic_Updated_no_notes.csv'
  INTO TABLE Conflict
  CHARACTER SET latin1
  FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' ESCAPED BY '\\'
  LINES TERMINATED BY '\r' STARTING BY ''
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
* Stop and delete the `mysqldb` container
  * If this step is not done, `docker-compose up` will fail due to a container already existing with the same name

## Usage
* Change directory to the top-level project directory (e.g. `acled-conflict-visualization`)
* Start up the app and DB (if not already running): `docker-compose up`
  * NOTE: Make sure the `volumes` section of `docker-compose.yml` matches where your mysql DB files are stored
* Access the webapp at `localhost:8080`
