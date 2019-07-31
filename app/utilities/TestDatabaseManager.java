package utilities;

import accessors.UserAccessor;
import controllers.ApplicationManager;
import io.ebean.Ebean;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import models.*;
import org.slf4j.Logger;
import play.ApplicationLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;



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
    public static void populateDatabase(CountDownLatch initCompleteLatch) {
        populateDatabase();
        initCompleteLatch.countDown();
    }

    /**
     * Completes the database population that is done by the sql evolutions
     */
    public static void populateDatabase() {
        logger.info("Making programmatic database population changes");

        CountryUtils.updateCountries();
        CountryUtils.validateUsedCountries();

        setUserPasswords();

        addUserPhotos();
    }

    /** Add in test user photos - only occurs during testing
     */
    private static void addUserPhotos() {
        // only populate photos for the tests
        if (!ApplicationManager.isIsTest()) {
            return;
        }

        UserPhoto userPhoto1 = new UserPhoto("shrek.jpeg", true, true,
                User.find().byId(2));
        userPhoto1.setCaption("Get out of my swamp");

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
}

















































