package utilities;

import accessors.UserAccessor;
import controllers.ApplicationManager;
import models.*;
import org.slf4j.Logger;

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
     * Completes the database population that is done by the sql evolutions
     * when the application is first started.
     *
     * @param initCompleteLatch A CountDownLatch to call back and unlock when the
     *                          database has been populated.
     */
    public void populateDatabase(CountDownLatch initCompleteLatch) {
        populateDatabase();
        initCompleteLatch.countDown();
    }

    /**
     * Completes the database population that is done by the sql evolutions
     */
    public void populateDatabase() {

        logger.info("attempting to populate database");
        logger.info("PopulationDatabase is " + ApplicationManager.getDatabaseName());

        CountryUtils.updateCountries();
        CountryUtils.validateUsedCountries();

        setUserPasswords();
    }

    /** Sets the passwords of all test users and admins */
    private void setUserPasswords() {
        List<User> users = UserAccessor.getAll();
        for (User user: users) {
            if (user.userIsAdmin()) {
                user.hashAndSetPassword("admin");
            } else {
                user.hashAndSetPassword("test");
            }

            user.update();
        }
    }
}

















































