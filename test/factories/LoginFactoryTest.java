package factories;

import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;


/**
 * JUnit 4 tests for LoginFactory.
 */
public class LoginFactoryTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void setUpDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        //Initialises a test user with name "testUser" and saves it to the database.
        User user = new User("gon12@uclive.ac.nz", "hunter22");
        user.save();
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }


    @Test
    public void getUserIdInvalidName() {
        LoginFactory loginFactory = new LoginFactory();
        User user = new User("Timmy");
        user.save();
//        int userId = user.getUserid();
        assertEquals(-1, loginFactory.getUserId("NotTimmy"));
    }

    @Test
    public void getUserIdValidName() {
        LoginFactory loginFactory = new LoginFactory();
        User user = new User("Timmy");
        user.save();
        int userId = user.getUserid();
        assertEquals(userId, loginFactory.getUserId("Timmy"));
    }

    @Test
    public void isPasswordMatchInvalidUserName() {
        LoginFactory loginFactory = new LoginFactory();
        User user = new User("Timmy", "password");
        user.save();
        assertFalse(loginFactory.isPasswordMatch("nottimmy", "notgoingtowork"));
    }

    @Test
    public void isPasswordMatch_ValidUserName_InvalidPassword() {
        LoginFactory loginFactory = new LoginFactory();
        User user = new User("Timmy", "password");
        user.save();
        assertFalse(loginFactory.isPasswordMatch("Timmy", "notpassword"));
    }

    @Test
    public void isPasswordMatch_ValidUserName_ValidPassword() {
        LoginFactory loginFactory = new LoginFactory();
        User user = new User("Timmy", "password");
        user.save();
        assertTrue(loginFactory.isPasswordMatch("Timmy", "password"));
    }
}