package accessors;

import models.User;
import models.commands.CommandManager;
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

import java.util.Map;

import static org.junit.Assert.*;

public class CommandManagerAccessorTest extends WithApplication {

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

        User user = new User("gon12_2@uclive.ac.nz", "hunter22");
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
    public void getCommandManagerByEmail() {
        Map<String, CommandManager> commandManagers = CommandManagerAccessor.getCommandManagers();
        int sizeBefore = commandManagers.size();
        CommandManagerAccessor.getCommandManagerByEmail("TestEmail1");
        int sizeAfter = commandManagers.size();
        assertEquals(sizeBefore+1, sizeAfter);
    }

    @Test
    public void resetCommandManager() {
        CommandManager oldCM = CommandManagerAccessor.getCommandManagerByEmail("TestEmail");
//        oldCM.setUser(User.find.byId(1));
        CommandManagerAccessor.resetCommandManager("TestEmail");
        CommandManager newCM = CommandManagerAccessor.getCommandManagerByEmail("TestEmail");
        assertNotEquals(oldCM.getUser(), newCM.getUser());
    }
}