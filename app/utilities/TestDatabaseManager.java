package utilities;

import accessors.UserAccessor;
import accessors.AlbumAccessor;
import accessors.UserAccessor;
import controllers.ApplicationManager;
import io.ebean.Ebean;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import models.*;
import models.commands.Albums.CreateAlbumCommand;
import org.slf4j.Logger;
import play.ApplicationLoader;

import java.time.LocalDate;
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
                user.hashAndSetPassword(EnvironmentalVariablesAccessor.getEnvVariable(
                        EnvVariableKeys.ADMIN_USER_PASSWORD_DEFAULT.toString()));
            } else {
                user.hashAndSetPassword(EnvironmentalVariablesAccessor.getEnvVariable(
                        EnvVariableKeys.TEST_USER_PASSWORD_DEFAULT.toString()));
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

    public static void addAlbums(){
        UserPhoto userPhoto1 = new UserPhoto("card.PNG", true, false, User.find().byId(1));
        UserPhoto userPhoto2 = new UserPhoto("Capture.PNG", false, false, User.find().byId(1));
        Album album1 = new Album(User.find().byId(1), "myAlbum", false);
        try {
            userPhoto1.save();
            userPhoto2.save();

            album1.addMedia(User.find().byId(1).getUserPhotos().get(0));
            album1.addMedia(User.find().byId(1).getUserPhotos().get(1));
            album1.save();
        } catch (Exception e) {}
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
            logger.debug(tableName.toString());
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

    /**
     * Populates the database with treasure hunts added to users 2,3 and 4.
     *
     */
    public static void addTreasureHunts(){
        TreasureHunt treasureHunt1 = new TreasureHunt("Surprise", "The garden city", Destination.find().byId(1), "2019-04-17", "2019-12-25", User.find().byId(2));
        treasureHunt1.save();
        TreasureHunt treasureHunt2 = new TreasureHunt("Surprise2", "Prime example of inflation", Destination.find().byId(3), "2019-04-17", "2019-12-25", User.find().byId(3));
        treasureHunt2.save();
        TreasureHunt treasureHunt3 = new TreasureHunt("Closed Treasure Hunt", "You should not be able to view this", Destination.find().byId(4), "2019-04-17", "2019-04-25", User.find().byId(4));
        treasureHunt3.save();
    }

    /**
     * Creates a default admin.
     *
     * @return A boolean, true if successfully created the admin, false otherwise
     */
    private static boolean createDefaultAdmin(){
        boolean isInSuccessState = true;

        User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
        Album album = new Album(user, "Default", true);
        user.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user.setTravellerTypes(TravellerType.find().all().subList(5, 6)); // Business Traveller
        user.setNationality(Nationality.find().all().subList(0, 2)); // First two countries alphabetically

        try {
            user.save();
            album.save();
        } catch (Exception e) {
            isInSuccessState = false;
            logger.error("Error making admin: User is already in db", e);
        }

        if (isInSuccessState) {
            Admin admin = new Admin(user.getUserid(), true);
            try {
                admin.save();
            } catch (Exception e) {
                isInSuccessState = false;
                logger.error("Error making admin: Admin is already in db", e);
            }
        }

        return isInSuccessState;
    }
}

















































