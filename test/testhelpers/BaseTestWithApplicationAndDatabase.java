package testhelpers;

import accessors.CommandManagerAccessor;
import controllers.ApplicationManager;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

/**
 * A Generic helper class for running tests with a dummy application and database.
 *
 */
public class BaseTestWithApplicationAndDatabase extends WithApplication {
    private Database database;
    private final Logger logger = UtilityFunctions.getLogger();


    /**
     * Set's up a dummy application for the tests.
     *
     * @return The dummy Application.
     */
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    /**
     * Initilizes the test database. Only contains one user.
     */
    @Before
    public void setUpDatabase() {
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
        CommandManagerAccessor.resetCommandManagers();
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));

        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }
}
