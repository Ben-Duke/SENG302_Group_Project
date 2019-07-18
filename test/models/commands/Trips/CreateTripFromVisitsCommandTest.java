package models.commands.Trips;

import controllers.ApplicationManager;
import models.Destination;
import models.User;
import models.Visit;
import models.commands.Visits.EditVisitCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CreateTripFromVisitsCommandTest extends BaseTestWithApplicationAndDatabase {
    private Database database;
    private EditVisitCommand editVisitCommand;
    private List<Visit> visits = new ArrayList<>();

    @Override
    @Before
    public void setUpDatabase() {
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();
        Visit visit1 = new Visit(null, null, Destination.find.byId(1));
        Visit visit2 = new Visit(null, null, Destination.find.byId(2));
        Visit visit3 = new Visit(null, null, Destination.find.byId(3));
        visits.add(visit1);
        visits.add(visit2);
        visits.add(visit3);
    }

    @Override
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }


    @Test
    public void testExecute() {
        User user = User.find.byId(1);
        CreateTripFromVisitsCommand command = new CreateTripFromVisitsCommand(visits, "testTrip", user);
        command.execute();
        user = User.find.byId(1);
        assertEquals("testTrip", user.getTrips().get(0).getTripName());
        assertEquals(3, user.getTrips().get(0).getVisits().size());
    }

    @Test
    public void undo() {
    }

    @Test
    public void redo() {
    }
}