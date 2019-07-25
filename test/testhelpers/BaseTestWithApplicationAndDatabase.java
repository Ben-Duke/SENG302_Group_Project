package testhelpers;

import accessors.CommandManagerAccessor;
import com.google.inject.Guice;
import controllers.ApplicationManager;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.config.ServerConfig;
import io.ebeaninternal.dbmigration.migration.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.api.db.evolutions.DynamicEvolutions;
import play.db.Database;
import play.db.Databases;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.fakeApplication;

/**
 * A Generic helper class for running tests with a dummy application and database.
 *
 */
public class BaseTestWithApplicationAndDatabase extends WithApplication {
    private Database database;

    private final Logger logger = UtilityFunctions.getLogger();

    public static Application application;

    @Inject
    private EbeanDynamicEvolutions ebeanDynamicEvolutions;


//    /**
//     * Set's up a dummy application for the tests.
//     *
//     * @return The dummy Application.
//     */
//    @Override
//    protected Application provideApplication() {
//        return new GuiceApplicationBuilder()
//                .build();
//    }

    @Before
    public void setUpDatabase() {
        ApplicationManager.setTesting();    // use the test database

        Map<String, String> configuration = new HashMap<>();
        configuration.put("play.db.config", "db");
        configuration.put("play.db.default", "test");
        configuration.put("db.test.driver", "org.h2.Driver");
        configuration.put("db.test.url", "jdbc:h2:mem:testDB;MODE=MYSQL;");
        configuration.put("ebean.test", "models.*");
        configuration.put("play.evolutions.db.test.enabled", "true");
        configuration.put("play.evolutions.autoApply", "false");

        //Set up the fake application to use the in memory database config
        application = fakeApplication(configuration);

        database = application.injector().instanceOf(Database.class);

        applyEvolutions();

        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
        CommandManagerAccessor.resetCommandManagers();
        //database = Databases.inMemory();

        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();

        Helpers.start(application);
    }

    /**
     * Applies down evolutions to the database from the test/evolutions/default directory.
     *
     * This drops tables and data from the database.
     */
    private void applyEvolutions() {
        Evolutions.applyEvolutions(database);
    }

    /**
     * Runs after each test scenario.
     * Cleans up the database by cleaning up evolutions and shutting it down.
     * Stops running the fake application.
     */
    @After
    public void shutdownDatabase() {
        cleanEvolutions();
        database.shutdown();
        Helpers.stop(application);

    }

    /**
     * Applies up evolutions to the database from the test/evolutions/default directory.
     *
     * This populates the database with necessary tables and values.
     */
    private void cleanEvolutions() {
        Evolutions.cleanupEvolutions(database);
    }



//    @BeforeClass
//    public static void startApp() {
//        System.setProperty("config.resource", "test.conf");
//        app = Helpers.fakeApplication();
//        Helpers.start(app);
//    }

//    /**
//     * Initilizes the test database. Only contains one user.
//     */
//    @Before
//    public void setUpDatabase() {
//        ApplicationManager.setTesting();    // use the test database
//        logger.debug("Database name");
//        logger.debug("setupDatabase " + ApplicationManager.getDatabaseName());
//
//        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
//        ApplicationManager.setIsTest(true);
//        CommandManagerAccessor.resetCommandManagers();
//        database = Databases.inMemory();
//
//        Evolutions.applyEvolutions(database);
//
////        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
////        testDatabaseManager.populateDatabase();
//    }
//
//    /**
//     * Clears the fake database after each test
//     */
//    @After
//    public void shutdownDatabase() {
//        Evolutions.cleanupEvolutions(database);
//        database.shutdown();
//    }
//
//    @AfterClass
//    public static void stopApp() {
//        Helpers.stop(app);
//    }
}
