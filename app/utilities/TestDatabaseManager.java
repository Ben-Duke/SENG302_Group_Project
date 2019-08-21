package utilities;

import accessors.UserAccessor;
import controllers.ApplicationManager;
import io.ebean.Ebean;
import models.*;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import utilities.ScriptRunner;



/**
 * Test Database Manager class. Populates the database. NOTE: Does not create the database, so it requires the database to already be running.
 * Visit https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/Test-database-structure
 * for information on the layout of the test database.
 */
public class TestDatabaseManager {

    private static final Logger logger = UtilityFunctions.getLogger();

    // Private constructor to hide the implicit public one
    private TestDatabaseManager() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Completes the database population that is done by the sql evolutions
     * when the application is first started.
     *
     * @param initCompleteLatch A CountDownLatch to call back and unlock when the
     *                          database has been populated.
     */
    public static void populateDatabaseWithLatch(CountDownLatch initCompleteLatch) {
        populateDatabase(null);    // not testing so no need to pass a connection
        initCompleteLatch.countDown();
    }

    /**
     * Completes the database population that is done by the sql evolutions
     */
    public static void populateDatabase(Connection connection) {
        logger.info("Making programmatic database population changes");

        // If testing - replace original data with test data
        if (ApplicationManager.isIsTest()) {
            clearAllData();
//            populateAutomatedTestData(connection);
            insertData();
        }

        // Setup which is run for both tests and sbt run
        CountryUtils.updateCountries();
        CountryUtils.validateUsedCountries();

        setUserPasswords();

        addUserPhotos();
    }

    /**
     * Runs the sql script containing the automated testing data
     * @param connection A connection to the database
     */
    private static void populateAutomatedTestData(Connection connection) {
        ScriptRunner runner = new ScriptRunner(connection, true, true);
        runner.setDelimiter(";", true);
        try {
            String filepath = Paths.get(".").toAbsolutePath().normalize().toString() + "/testData.sql";
            logger.debug(filepath);

            runner.runScript(new BufferedReader(new FileReader(filepath)));
        } catch(FileNotFoundException e) {
            logger.error("Sql testing data file not found\n Running tests without data, expect Failures");
            logger.error(e.toString());
        } catch(IOException | SQLException e) {
            logger.error("Error running sql test data file");
            logger.error(e.toString());
        }
    }

    /**
     *  Add in test user photos - only occurs during testing.
     *  Only adds file paths not actual photo files
     */
    private static void addUserPhotos() {
        // only populate photos for the tests
        if (!ApplicationManager.isIsTest()) {
            return;
        }


        UserPhoto userPhoto1 = new UserPhoto("shrek.jpeg", true, true,
                User.find().byId(2));
        userPhoto1.setCaption("Get out of my swamp");
        Tag tag = new Tag("Shrek");
        try {
            tag.save();
        } catch (Exception e) {
            logger.error("Failed to add Shrek tag", e);
        }
        userPhoto1.addTag(tag);


        UserPhoto userPhoto2 = new UserPhoto("placeholder.png", false, false,
                User.find().byId(2));

        try {
            userPhoto1.save();
        } catch (Exception e) {
            logger.error("Failed to add user1 photos", e);
        }

        try {
            userPhoto2.save();
        } catch (Exception e) {
            logger.error("Failed to add user2 photos", e);
        }

    }

    /**
     * Sets the passwords of all test users and admins
     */
    private static void setUserPasswords() {
        List<User> users = UserAccessor.getAll();
        for (User user : users) {
            if (user.userIsAdmin()) {
                user.hashAndSetPassword("FancyRock08");
            } else {
                user.hashAndSetPassword("TinyHumans57");
            }

            user.update();
        }
    }

    /** Clear data from all tables except nationality, passport and traveller type */
    public static void clearMostData() {
        List<TableName> persisted = Arrays.asList(
                TableName.nationality,
                TableName.passport,
                TableName.traveller_type);

        clearData(persisted);
    }

    /** Clear all data from the database */
    public static void clearAllData() {
        clearData(new ArrayList<TableName>());  // pass an empty list
    }

    /**
     * Removes all data from the database while keeping the structure
     * Resets auto_increment (e.g. id)
     *
     * Uses h2 syntax so will not work on mysql
     *
     * Always runs on DEFAULT database not a database with a different name which
     * the application is connected to
     */
    private static void clearData(List<TableName> persisted) {
        logger.info("Clearing database data");

        for (TableName tableName : TableName.values()) {
            if (persisted.contains(tableName)) {
                continue;   // do not clear tables in persisted
            }

            String sql = String.format("DELETE FROM %s", tableName);
            Ebean.createSqlUpdate(sql).execute();


            // reset the auto-increment if the table auto-increments its
            // primary key
            if (tableName.isAutoIncremented()) {
                sql = String.format("ALTER TABLE %s ALTER COLUMN %s RESTART WITH 1",
                        tableName, tableName.getColumnName());
                Ebean.createSqlUpdate(sql).execute();
            }
        }
    }

    private static void insertData() {
        logger.info("Inserting database data");

        String sql = "-- Automated test data\n" +
                "-- Does not populate passports/nationalities\n" +
                "\n" +
                "-- Disable inspection for checking tables exist as they are in another file\n" +
                "-- noinspection SqlResolveForFile\n" +
                "\n" +
                "\n" +
                "-- --------------------------------------------------------------\n" +
                "\n" +
                "-- Population data for pass/nats - same rows inserted into both tables,\n" +
                "-- this is reliant on them having the same structure\n" +
                "INSERT INTO `passport` (`passid`, `country_valid`, `passport_name`) VALUES\n" +
                "(1, 1, 'Afghanistan'),\n" +
                "(2, 1, 'Albania'),\n" +
                "(3, 1, 'Algeria'),\n" +
                "(4, 1, 'American Samoa'),\n" +
                "(5, 1, 'Andorra'),\n" +
                "(6, 1, 'Angola'),\n" +
                "(7, 1, 'Anguilla'),\n" +
                "(8, 1, 'Antarctica'),\n" +
                "(9, 1, 'Antigua and Barbuda'),\n" +
                "(10, 1, 'Argentina'),\n" +
                "(11, 1, 'Armenia'),\n" +
                "(12, 1, 'Aruba'),\n" +
                "(13, 1, 'Australia'),\n" +
                "(14, 1, 'Austria'),\n" +
                "(15, 1, 'Azerbaijan'),\n" +
                "(16, 1, 'Bahamas'),\n" +
                "(17, 1, 'Bahrain'),\n" +
                "(18, 1, 'Bangladesh'),\n" +
                "(19, 1, 'Barbados'),\n" +
                "(20, 1, 'Belarus'),\n" +
                "(21, 1, 'Belgium'),\n" +
                "(22, 1, 'Belize'),\n" +
                "(23, 1, 'Benin'),\n" +
                "(24, 1, 'Bermuda'),\n" +
                "(25, 1, 'Bhutan'),\n" +
                "(26, 1, 'Bolivia (Plurinational State of)'),\n" +
                "(27, 1, 'Bonaire, Sint Eustatius and Saba'),\n" +
                "(28, 1, 'Bosnia and Herzegovina'),\n" +
                "(29, 1, 'Botswana'),\n" +
                "(30, 1, 'Bouvet Island'),\n" +
                "(31, 1, 'Brazil'),\n" +
                "(32, 1, 'British Indian Ocean Territory'),\n" +
                "(33, 1, 'Brunei Darussalam'),\n" +
                "(34, 1, 'Bulgaria'),\n" +
                "(35, 1, 'Burkina Faso'),\n" +
                "(36, 1, 'Burundi'),\n" +
                "(37, 1, 'Cabo Verde'),\n" +
                "(38, 1, 'Cambodia'),\n" +
                "(39, 1, 'Cameroon'),\n" +
                "(40, 1, 'Canada'),\n" +
                "(41, 1, 'Cayman Islands'),\n" +
                "(42, 1, 'Central African Republic'),\n" +
                "(43, 1, 'Chad'),\n" +
                "(44, 1, 'Chile'),\n" +
                "(45, 1, 'China'),\n" +
                "(46, 1, 'Christmas Island'),\n" +
                "(47, 1, 'Cocos (Keeling) Islands'),\n" +
                "(48, 1, 'Colombia'),\n" +
                "(49, 1, 'Comoros'),\n" +
                "(50, 1, 'Congo'),\n" +
                "(51, 1, 'Congo (Democratic Republic of the)'),\n" +
                "(52, 1, 'Cook Islands'),\n" +
                "(53, 1, 'Costa Rica'),\n" +
                "(54, 1, 'Croatia'),\n" +
                "(55, 1, 'Cuba'),\n" +
                "(56, 1, 'Curaçao'),\n" +
                "(57, 1, 'Cyprus'),\n" +
                "(58, 1, 'Czech Republic'),\n" +
                "(59, 1, 'Côte d''Ivoire'),\n" +
                "(60, 1, 'Denmark'),\n" +
                "(61, 1, 'Djibouti'),\n" +
                "(62, 1, 'Dominica'),\n" +
                "(63, 1, 'Dominican Republic'),\n" +
                "(64, 1, 'Ecuador'),\n" +
                "(65, 1, 'Egypt'),\n" +
                "(66, 1, 'El Salvador'),\n" +
                "(67, 1, 'Equatorial Guinea'),\n" +
                "(68, 1, 'Eritrea'),\n" +
                "(69, 1, 'Estonia'),\n" +
                "(70, 1, 'Ethiopia'),\n" +
                "(71, 1, 'Falkland Islands (Malvinas)'),\n" +
                "(72, 1, 'Faroe Islands'),\n" +
                "(73, 1, 'Fiji'),\n" +
                "(74, 1, 'Finland'),\n" +
                "(75, 1, 'France'),\n" +
                "(76, 1, 'French Guiana'),\n" +
                "(77, 1, 'French Polynesia'),\n" +
                "(78, 1, 'French Southern Territories'),\n" +
                "(79, 1, 'Gabon'),\n" +
                "(80, 1, 'Gambia'),\n" +
                "(81, 1, 'Georgia'),\n" +
                "(82, 1, 'Germany'),\n" +
                "(83, 1, 'Ghana'),\n" +
                "(84, 1, 'Gibraltar'),\n" +
                "(85, 1, 'Greece'),\n" +
                "(86, 1, 'Greenland'),\n" +
                "(87, 1, 'Grenada'),\n" +
                "(88, 1, 'Guadeloupe'),\n" +
                "(89, 1, 'Guam'),\n" +
                "(90, 1, 'Guatemala'),\n" +
                "(91, 1, 'Guernsey'),\n" +
                "(92, 1, 'Guinea'),\n" +
                "(93, 1, 'Guinea-Bissau'),\n" +
                "(94, 1, 'Guyana'),\n" +
                "(95, 1, 'Haiti'),\n" +
                "(96, 1, 'Heard Island and McDonald Islands'),\n" +
                "(97, 1, 'Holy See'),\n" +
                "(98, 1, 'Honduras'),\n" +
                "(99, 1, 'Hong Kong'),\n" +
                "(100, 1, 'Hungary'),\n" +
                "(101, 1, 'Iceland'),\n" +
                "(102, 1, 'India'),\n" +
                "(103, 1, 'Indonesia'),\n" +
                "(104, 1, 'Iran (Islamic Republic of)'),\n" +
                "(105, 1, 'Iraq'),\n" +
                "(106, 1, 'Ireland'),\n" +
                "(107, 1, 'Isle of Man'),\n" +
                "(108, 1, 'Israel'),\n" +
                "(109, 1, 'Italy'),\n" +
                "(110, 1, 'Jamaica'),\n" +
                "(111, 1, 'Japan'),\n" +
                "(112, 1, 'Jersey'),\n" +
                "(113, 1, 'Jordan'),\n" +
                "(114, 1, 'Kazakhstan'),\n" +
                "(115, 1, 'Kenya'),\n" +
                "(116, 1, 'Kiribati'),\n" +
                "(117, 1, 'Korea (Democratic People''s Republic of)'),\n" +
                "(118, 1, 'Korea (Republic of)'),\n" +
                "(119, 1, 'Kuwait'),\n" +
                "(120, 1, 'Kyrgyzstan'),\n" +
                "(121, 1, 'Lao People''s Democratic Republic'),\n" +
                "(122, 1, 'Latvia'),\n" +
                "(123, 1, 'Lebanon'),\n" +
                "(124, 1, 'Lesotho'),\n" +
                "(125, 1, 'Liberia'),\n" +
                "(126, 1, 'Libya'),\n" +
                "(127, 1, 'Liechtenstein'),\n" +
                "(128, 1, 'Lithuania'),\n" +
                "(129, 1, 'Luxembourg'),\n" +
                "(130, 1, 'Macao'),\n" +
                "(131, 1, 'Macedonia (the former Yugoslav Republic of)'),\n" +
                "(132, 1, 'Madagascar'),\n" +
                "(133, 1, 'Malawi'),\n" +
                "(134, 1, 'Malaysia'),\n" +
                "(135, 1, 'Maldives'),\n" +
                "(136, 1, 'Mali'),\n" +
                "(137, 1, 'Malta'),\n" +
                "(138, 1, 'Marshall Islands'),\n" +
                "(139, 1, 'Martinique'),\n" +
                "(140, 1, 'Mauritania'),\n" +
                "(141, 1, 'Mauritius'),\n" +
                "(142, 1, 'Mayotte'),\n" +
                "(143, 1, 'Mexico'),\n" +
                "(144, 1, 'Micronesia (Federated States of)'),\n" +
                "(145, 1, 'Moldova (Republic of)'),\n" +
                "(146, 1, 'Monaco'),\n" +
                "(147, 1, 'Mongolia'),\n" +
                "(148, 1, 'Montenegro'),\n" +
                "(149, 1, 'Montserrat'),\n" +
                "(150, 1, 'Morocco'),\n" +
                "(151, 1, 'Mozambique'),\n" +
                "(152, 1, 'Myanmar'),\n" +
                "(153, 1, 'Namibia'),\n" +
                "(154, 1, 'Nauru'),\n" +
                "(155, 1, 'Nepal'),\n" +
                "(156, 1, 'Netherlands'),\n" +
                "(157, 1, 'New Caledonia'),\n" +
                "(158, 1, 'New Zealand'),\n" +
                "(159, 1, 'Nicaragua'),\n" +
                "(160, 1, 'Niger'),\n" +
                "(161, 1, 'Nigeria'),\n" +
                "(162, 1, 'Niue'),\n" +
                "(163, 1, 'Norfolk Island'),\n" +
                "(164, 1, 'Northern Mariana Islands'),\n" +
                "(165, 1, 'Norway'),\n" +
                "(166, 1, 'Oman'),\n" +
                "(167, 1, 'Pakistan'),\n" +
                "(168, 1, 'Palau'),\n" +
                "(169, 1, 'Palestine, State of'),\n" +
                "(170, 1, 'Panama'),\n" +
                "(171, 1, 'Papua New Guinea'),\n" +
                "(172, 1, 'Paraguay'),\n" +
                "(173, 1, 'Peru'),\n" +
                "(174, 1, 'Philippines'),\n" +
                "(175, 1, 'Pitcairn'),\n" +
                "(176, 1, 'Poland'),\n" +
                "(177, 1, 'Portugal'),\n" +
                "(178, 1, 'Puerto Rico'),\n" +
                "(179, 1, 'Qatar'),\n" +
                "(180, 1, 'Republic of Kosovo'),\n" +
                "(181, 1, 'Romania'),\n" +
                "(182, 1, 'Russian Federation'),\n" +
                "(183, 1, 'Rwanda'),\n" +
                "(184, 1, 'Réunion'),\n" +
                "(185, 1, 'Saint Barthélemy'),\n" +
                "(186, 1, 'Saint Helena, Ascension and Tristan da Cunha'),\n" +
                "(187, 1, 'Saint Kitts and Nevis'),\n" +
                "(188, 1, 'Saint Lucia'),\n" +
                "(189, 1, 'Saint Martin (French part)'),\n" +
                "(190, 1, 'Saint Pierre and Miquelon'),\n" +
                "(191, 1, 'Saint Vincent and the Grenadines'),\n" +
                "(192, 1, 'Samoa'),\n" +
                "(193, 1, 'San Marino'),\n" +
                "(194, 1, 'Sao Tome and Principe'),\n" +
                "(195, 1, 'Saudi Arabia'),\n" +
                "(196, 1, 'Senegal'),\n" +
                "(197, 1, 'Serbia'),\n" +
                "(198, 1, 'Seychelles'),\n" +
                "(199, 1, 'Sierra Leone'),\n" +
                "(200, 1, 'Singapore'),\n" +
                "(201, 1, 'Sint Maarten (Dutch part)'),\n" +
                "(202, 1, 'Slovakia'),\n" +
                "(203, 1, 'Slovenia'),\n" +
                "(204, 1, 'Solomon Islands'),\n" +
                "(205, 1, 'Somalia'),\n" +
                "(206, 1, 'South Africa'),\n" +
                "(207, 1, 'South Georgia and the South Sandwich Islands'),\n" +
                "(208, 1, 'South Sudan'),\n" +
                "(209, 1, 'Spain'),\n" +
                "(210, 1, 'Sri Lanka'),\n" +
                "(211, 1, 'Sudan'),\n" +
                "(212, 1, 'Suriname'),\n" +
                "(213, 1, 'Svalbard and Jan Mayen'),\n" +
                "(214, 1, 'Swaziland'),\n" +
                "(215, 1, 'Sweden'),\n" +
                "(216, 1, 'Switzerland'),\n" +
                "(217, 1, 'Syrian Arab Republic'),\n" +
                "(218, 1, 'Taiwan'),\n" +
                "(219, 1, 'Tajikistan'),\n" +
                "(220, 1, 'Tanzania, United Republic of'),\n" +
                "(221, 1, 'Thailand'),\n" +
                "(222, 1, 'Timor-Leste'),\n" +
                "(223, 1, 'Togo'),\n" +
                "(224, 1, 'Tokelau'),\n" +
                "(225, 1, 'Tonga'),\n" +
                "(226, 1, 'Trinidad and Tobago'),\n" +
                "(227, 1, 'Tunisia'),\n" +
                "(228, 1, 'Turkey'),\n" +
                "(229, 1, 'Turkmenistan'),\n" +
                "(230, 1, 'Turks and Caicos Islands'),\n" +
                "(231, 1, 'Tuvalu'),\n" +
                "(232, 1, 'Uganda'),\n" +
                "(233, 1, 'Ukraine'),\n" +
                "(234, 1, 'United Arab Emirates'),\n" +
                "(235, 1, 'United Kingdom of Great Britain and Northern Ireland'),\n" +
                "(236, 1, 'United States Minor Outlying Islands'),\n" +
                "(237, 1, 'United States of America'),\n" +
                "(238, 1, 'Uruguay'),\n" +
                "(239, 1, 'Uzbekistan'),\n" +
                "(240, 1, 'Vanuatu'),\n" +
                "(241, 1, 'Venezuela (Bolivarian Republic of)'),\n" +
                "(242, 1, 'Viet Nam'),\n" +
                "(243, 1, 'Virgin Islands (British)'),\n" +
                "(244, 1, 'Virgin Islands (U.S.)'),\n" +
                "(245, 1, 'Wallis and Futuna'),\n" +
                "(246, 1, 'Western Sahara'),\n" +
                "(247, 1, 'Yemen'),\n" +
                "(248, 1, 'Zambia'),\n" +
                "(249, 1, 'Zimbabwe'),\n" +
                "(250, 1, 'Åland Islands'),\n" +
                "(251, 0, 'Czechoslovakia'); -- invalid country\n" +
                "\n" +
                "\n" +
                "INSERT INTO `nationality` (`natid`, `country_valid`, `nationality_name`)\n" +
                "SELECT *\n" +
                "FROM passport;\n" +
                "\n" +
                "\n" +
                "-- user\n" +
                "-- Passwords are set programmatically\n" +
                "INSERT INTO `user` (`userid`, `email`, `password_hash`, `date_of_birth`, `gender`,\n" +
                "                    `f_name`, `l_name`, `undo_redo_error`, `is_admin`,\n" +
                "                    `creation_date`) VALUES\n" +
                "(1, 'admin@admin.com', '',\n" +
                " '2019-02-18', 'male', 'admin', 'admin', 0, 0, '2019-07-26 03:59:17'),\n" +
                "\n" +
                "(2, 'testuser1@uclive.ac.nz', '',\n" +
                " '1998-08-23', 'Male', 'Gavin', 'Ong', 0, 0, '2019-07-26 03:59:17'),\n" +
                "\n" +
                "(3, 'testuser2@uclive.ac.nz', '',\n" +
                " '1960-12-25', 'Female', 'Caitlyn', 'Jenner', 0, 0, '2019-07-26 03:59:17'),\n" +
                "\n" +
                "(4, 'testuser3@uclive.ac.nz', '',\n" +
                " '2006-06-09', 'Male', 'John', 'Smith', 0, 0, '2019-07-26 03:59:17');\n" +
                "\n" +
                "\n" +
                "-- Admin\n" +
                "INSERT INTO `admin` (`user_id`, `user_id_to_edit`, `is_default`) VALUES\n" +
                "(1, NULL, 1);\n" +
                "\n" +
                "\n" +
                "-- Destination\n" +
                "INSERT INTO `destination` (`destid`, `dest_name`, `dest_type`, `district`, `country`,\n" +
                "                           `is_country_valid`, `latitude`, `longitude`, `dest_is_public`,\n" +
                "                           `user`) VALUES\n" +
                "(1, 'Christchurch', 'Town', 'Canterbury', 'New Zealand', 1, -43.5321, 172.6362, 1, 2),\n" +
                "(2, 'Wellington', 'Town', 'Wellington', 'New Zealand', 1, -41.2866, 174.7756, 0, 2),\n" +
                "(3, 'The Wok', 'Cafe/Restaurant', 'Canterbury', 'New Zealand', 1, -43.523593, 172.582971, 1, 2),\n" +
                "(4, 'Hanmer Springs Thermal Pools', 'Attraction', 'North Canterbury', 'New Zealand', 1, -42.522791, 172.828944, 1, 3),\n" +
                "(5, 'Le Mans 24 hour race', 'Event', 'Le Mans', 'France', 1, 47.956221, 0.207828, 0, 3),\n" +
                "(6, 'Great Pyramid of Giza', 'Attraction', 'Giza', 'Egypt', 1, 29.979481, 31.134159, 1, 3),\n" +
                "(7, 'Niagara Falls', 'Natural Spot', 'New York', 'United States', 0, 29.979481, 31.134159, 0, 4),\n" +
                "(8, 'Vatican City', 'Country', 'Rome', 'Vatican City', 0, 41.903133, 12.454341, 0, 4),\n" +
                "(9, 'Lincoln Memorial', 'Monument', 'Washington DC', 'United States', 0, 38.889406, -77.050155, 1, 4);\n" +
                "\n" +
                "-- Albums\n" +
                "INSERT INTO `album`(`album_id`, `user`, `destination`, `primary_photo_media_id`, `is_default`, `title`) VALUES\n" +
                "(1, 1, null, null, true,'Default'),\n" +
                "(2, 2, null, null, true,'Default'),\n" +
                "(3, 3, null, null, true,'Default'),\n" +
                "(4, 4, null, null, true,'Default'),\n" +
                "(5, null, 1, null, null,'Christchurch'),\n" +
                "(6, null, 2, null, null,'Wellington'),\n" +
                "(7, null, 3, null, null,'The Wok'),\n" +
                "(8, null, 4, null, null,'Hanmer Springs Thermal Pools'),\n" +
                "(9, null, 5, null, null,'Le Mans 24 hour race'),\n" +
                "(10, null, 6, null, null,'Great Pyramid of Giza'),\n" +
                "(11, null, 7, null, null,'Niagara Falls'),\n" +
                "(12, null, 8, null, null,'Vatican City'),\n" +
                "(13, null, 9, null, null,'Lincoln Memorial');\n" +
                "\n" +
                "-- media\n" +
                "\n" +
                "-- album_media\n" +
                "\n" +
                "-- Traveller Types\n" +
                "-- Order will look a bit weird on phpMyAdmin as it sorts alphabetically by default\n" +
                "-- The tests are hard coded to this order\n" +
                "INSERT INTO `traveller_type` (`ttypeid`, `traveller_type_name`) VALUES\n" +
                "(1, 'Groupie'),\n" +
                "(2, 'Thrillseeker'),\n" +
                "(3, 'Gap Year'),\n" +
                "(4, 'Frequent Weekender'),\n" +
                "(5, 'Holidaymaker'),\n" +
                "(6, 'Business Traveller'),\n" +
                "(7, 'Backpacker');\n" +
                "\n" +
                "\n" +
                "-- Destination_Traveller_Type\n" +
                "INSERT INTO `destination_traveller_type` (`destination_destid`,\n" +
                "                                          `traveller_type_ttypeid`) VALUES\n" +
                "(1, 1),\n" +
                "(3, 1),\n" +
                "(3, 3),\n" +
                "(4, 5),\n" +
                "(4, 7),\n" +
                "(6, 7),\n" +
                "(7, 2),\n" +
                "(9, 1),\n" +
                "(9, 4),\n" +
                "(9, 6);\n" +
                "\n" +
                "\n" +
                "-- destination_modification_request\n" +
                "-- no test data\n" +
                "\n" +
                "\n" +
                "-- destination_modification_request_traveller_type\n" +
                "-- no test data\n" +
                "\n" +
                "\n" +
                "-- treasure_hunt\n" +
                "INSERT INTO `treasure_hunt` (`thuntid`, `title`, `riddle`, `destination_destid`,\n" +
                "                             `start_date`, `end_date`, `user`) VALUES\n" +
                "(1, 'Surprise', 'The garden city', 1, '2019-04-17', '2019-12-25', 2),\n" +
                "(2, 'Surprise2', 'Prime example of inflation', 3, '2019-04-17', '2019-12-25', 3),\n" +
                "(3, 'Closed Treasure Hunt', 'You should not be able to view this', 4, '2019-04-17',\n" +
                " '2019-04-25', 4);\n" +
                "\n" +
                "\n" +
                "-- trip\n" +
                "INSERT INTO `trip` (`tripid`, `trip_name`, `removed_visits`, `is_public`, `user`) VALUES\n" +
                "(1, 'Trip to New Zealand', 0, 1, 2),\n" +
                "(2, 'Christchurch to Wellington, to The Wok and back', 0, 0, 2),\n" +
                "(3, 'World Tour', 0, 1, 3),\n" +
                "(4, 'Pyramid to Race and back again', 0, 0, 3),\n" +
                "(5, 'See the pope, the president and come back', 0, 1, 4),\n" +
                "(6, 'Waterfall walk and see the president', 0, 0, 4);\n" +
                "\n" +
                "\n" +
                "-- user_nationality\n" +
                "INSERT INTO `user_nationality` (`user_userid`, `nationality_natid`) VALUES\n" +
                "(1, 1),\n" +
                "(1, 2),\n" +
                "(2, 251),\n" +
                "(3, 71),\n" +
                "(3, 72),\n" +
                "(4, 51);\n" +
                "\n" +
                "\n" +
                "-- user_passport\n" +
                "INSERT INTO `user_passport` (`user_userid`, `passport_passid`) VALUES\n" +
                "(2, 251),\n" +
                "(3, 71),\n" +
                "(3, 72);\n" +
                "\n" +
                "\n" +
                "-- user_traveller_type\n" +
                "INSERT INTO `user_traveller_type` (`user_userid`, `traveller_type_ttypeid`) VALUES\n" +
                "(1, 5),\n" +
                "(2, 3),\n" +
                "(3, 2),\n" +
                "(4, 1),\n" +
                "(4, 2);\n" +
                "\n" +
                "-- user_treasure_hunt\n" +
                "-- no data\n" +
                "\n" +
                "\n" +
                "-- user_photo\n" +
                "-- set in code\n" +
                "\n" +
                "\n" +
                "-- user_photo_destination\n" +
                "-- no data\n" +
                "\n" +
                "-- tags\n" +
                "INSERT INTO `tag`(`tag_id`, `name`) VALUES\n" +
                "(1,'Fun place to stay'),\n" +
                "(2, 'Vacation spot'),\n" +
                "(3, 'Top Rated'),\n" +
                "(4, 'Best trip ever');\n" +
                "\n" +
                "-- pending users\n" +
                "INSERT INTO `tag_user`(`tag_tag_id`,`user_userid`) VALUES\n" +
                "(2,2);\n" +
                "\n" +
                "-- destination_tag\n" +
                "INSERT INTO `destination_tag`(`destination_destid`, `tag_tag_id`) VALUES\n" +
                "(1,1),\n" +
                "(2,2),\n" +
                "(1,3);\n" +
                "\n" +
                "INSERT INTO `trip_tag`(`trip_tripid`,`tag_tag_id`) VALUES\n" +
                "(1,1);\n" +
                "\n" +
                "-- media_tag\n" +
                "\n" +
                "-- visit\n" +
                "INSERT INTO `visit` (`visitid`, `visitorder`, `destination`, `trip`, `arrival`,\n" +
                "                     `departure`, `visit_name`) VALUES\n" +
                "(1, 1, 1, 1, '2018-05-04', '2018-05-06', 'Christchurch'),\n" +
                "(2, 2, 2, 1, '2018-05-06', '2018-05-08', 'Wellington'),\n" +
                "(3, 1, 1, 2, NULL, NULL, 'Christchurch'),\n" +
                "(4, 2, 2, 2, NULL, NULL, 'Wellington'),\n" +
                "(5, 3, 3, 2, NULL, NULL, 'The Wok'),\n" +
                "(6, 4, 1, 2, NULL, NULL, 'Christchurch'),\n" +
                "(7, 1, 4, 3, '2003-08-12', NULL, 'Hanmer Springs Thermal Pools'),\n" +
                "(8, 2, 5, 3, NULL, NULL, 'Le Mans 24 hour race'),\n" +
                "(9, 3, 6, 3, NULL, NULL, 'Great Pyramid of Giza'),\n" +
                "(10, 1, 6, 4, NULL, '2019-04-05', 'Great Pyramid of Giza'),\n" +
                "(11, 2, 5, 4, NULL, NULL, 'Le Mans 24 hour race'),\n" +
                "(12, 3, 6, 4, NULL, NULL, 'Great Pyramid of Giza'),\n" +
                "(13, 1, 8, 5, NULL, NULL, 'Vatican City'),\n" +
                "(14, 2, 9, 5, NULL, NULL, 'Lincoln Memorial'),\n" +
                "(15, 3, 8, 5, NULL, NULL, 'Vatican City'),\n" +
                "(16, 1, 7, 6, NULL, NULL, 'Niagara Falls'),\n" +
                "(17, 2, 9, 6, NULL, NULL, 'Lincoln Memorial');\n";
        Ebean.createSqlUpdate(sql).execute();
    }
}
