package accessors;

import models.User;
import models.commands.General.CommandManager;
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
import utilities.TestDatabaseManager;


import java.util.Map;

import static org.junit.Assert.*;

public class CommandManagerAccessorTest extends BaseTestWithApplicationAndDatabase {

    /**
     * Initialises the test database. Only contains one user.
     */
    @Override
    public void populateDatabase() {
        TestDatabaseManager.clearAllData();

        User user = new User("gon12_2@uclive.ac.nz", "hunter22");
        user.save();
    }

    @Test
    public void getCommandManagerByEmail() {
        Map<String, CommandManager> commandManagers = CommandManagerAccessor.getCommandManagers();
        int sizeBefore = commandManagers.size();
        CommandManagerAccessor.getCommandManagerByEmail("TestEmail1");
        int sizeAfter = commandManagers.size();
        assertEquals(sizeBefore+1, sizeAfter);
    }
}