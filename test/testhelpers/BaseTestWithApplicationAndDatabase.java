package testhelpers;

import accessors.CommandManagerAccessor;
import com.google.inject.Guice;
import controllers.ApplicationManager;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Transaction;
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

    /* Prevents DataSource null database error even though it appears unused */
    @Inject
    private EbeanDynamicEvolutions ebeanDynamicEvolutions;

    @Before
    public void setupDatabase() {
        ApplicationManager.setTesting();    // set the app in a testing state

        Map<String, String> configuration = new HashMap<>();
        configuration.put("db.default.driver", "org.h2.Driver");
        configuration.put("db.default.url", "jdbc:h2:mem:play");
        configuration.put("ebean.default", "models.*");
        configuration.put("play.evolutions.db.default.enabled", "true");
//        configuration.put("play.evolutions.db.default.autoApply", "false");
//        configuration.put("play.evolutions.db.default.autoApplyDowns", "false");


        // Dummy database to generate ebean ddl
//        configuration.put("db.default.driver", "org.h2.Driver");
//        configuration.put("db.default.url", "jdbc:h2:mem:defaultDB;MODE=MYSQL;");
//        configuration.put("ebean.default", "models.*");

        //Set up the fake application to use the in memory database config
        application = fakeApplication(configuration);

        database = application.injector().instanceOf(Database.class);

        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        CommandManagerAccessor.resetCommandManagers();

        // setup tables
        Evolutions.applyEvolutions(database);

        // populate data
        populateDatabase();

        Helpers.start(application);
    }

    /**
     * Populates the database with test data
     * Can be overridden in subclasses if they want to provide their own test data
     */
    public void populateDatabase() {
        TestDatabaseManager.populateDatabase();
    }

    /**
     * Runs after each test scenario.
     * Cleans up the database by cleaning up evolutions and shutting it down.
     * Stops running the fake application.
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
        Helpers.stop(application);
    }
}
