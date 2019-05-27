package accessors;

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
import play.test.WithApplication;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class to JUnit test the UserAccessor class.
 */
public class UserAccessorTest extends BaseTestWithApplicationAndDatabase {

    @Before
    public void setup() {
        User user = new User("gon12_2@uclive.ac.nz", "hunter22");
        user.save();
    }

    /**
     * Checks the getUsersFromEmail method returns an empty list when checking
     * for users with the empty string as an email address.
     */
    @Test
    public void getUsersFromEmail_checkHasNoUsers_emptyEmailAddress() {
        List<User> users = UserAccessor.getUsersFromEmail("");
        assertEquals(0, users.size());
    }

    /**
     * Checks the getUsersFromEmail method returns an empty list when searching
     * for users with an email address none own.
     */
    @Test
    public void getUsersFromEmail_checkHasNoUsers_NonExistantEmail() {
        List<User> users = UserAccessor.getUsersFromEmail("never@an.email");
        assertEquals(0, users.size());
    }

    /**
     * Checks the getUsersFromEmail method returns a list of length one when
     * searching for users with an email that one user has.
     */
    @Test
    public void getUsersFromEmail_checkHasOneUser_ExistingEmail() {
        List<User> users = UserAccessor.getUsersFromEmail("gon12_2@uclive.ac.nz");
        assertEquals(1, users.size());
    }
}