package utilities;

import controllers.ApplicationManager;
import models.*;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Test Database Manager class. Populates the database. NOTE: Does not create the database, so it requires the database to already be running.
 * Visit https://eng-git.canterbury.ac.nz/seng302-2019/team-800/wikis/Test-database-structure
 * for information on the layout of the test database.
 */
public class TestDatabaseManager {

    private final Logger logger = UtilityFunctions.getLogger();

    public TestDatabaseManager(){

    }

    /**
     * Method to populate the database when the application is first started.
     *
     *
     * @param initCompleteLatch A CountDownLatch to call back and unlock when the
     *                          database has been populated.
     */
    public void populateDatabase(CountDownLatch initCompleteLatch) {
        populateDatabase();
        initCompleteLatch.countDown();
    }

    /**
     * Populates the database. Call this method at the before section of each unit test.
     */
    public void populateDatabase() {

        logger.info("attempting to populate database");
        logger.info("PopulationDatabase is " + ApplicationManager.getDatabaseName());

        CountryUtils.updateCountries();
        CountryUtils.validateUsedCountries();
    }
}
