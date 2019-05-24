package testhelpers;

import controllers.ApplicationManager;
import models.User;
import org.junit.After;
import org.junit.Before;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;
import utilities.TestDatabaseManager;

/**
 * A Generic helper class for running tests with a dummy application and database.
 *
 */
public class BaseTestWithApplicationAndDatabase extends WithApplication {
    private Database database;

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
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));

        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
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
