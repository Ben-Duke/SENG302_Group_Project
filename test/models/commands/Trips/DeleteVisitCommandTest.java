package models.commands.Trips;

import accessors.TripAccessor;
import accessors.VisitAccessor;
import controllers.ApplicationManager;
import models.Trip;
import models.User;
import models.Visit;
import models.commands.Visits.DeleteVisitCommand;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeleteVisitCommandTest extends BaseTestWithApplicationAndDatabase {
    private Database database;
    private Trip trip;
    private User user;


    @Override
    public void populateDatabase() {
        TestDatabaseManager.populateDatabase();
        trip = TripAccessor.getTripById(2);
        user = User.find().byId(1);


    }

    @Test
    public void testExecute() {

        int beforeSize = trip.getOrderedVisits().size();

        int visitId = trip.getOrderedVisits().get(2).getVisitid();

        DeleteVisitCommand deleteVisitCommand = new DeleteVisitCommand(trip.getOrderedVisits().get(2));
        deleteVisitCommand.execute();

        trip = TripAccessor.getTripById(trip.getTripid());

        int afterSize = trip.getOrderedVisits().size();

        assertEquals(afterSize, beforeSize - 1);

        Visit visit = VisitAccessor.getById(visitId);

        assertNull(visit);
    }

    @Test
    public void testUndo() {

        int beforeSize = trip.getOrderedVisits().size();

        DeleteVisitCommand deleteVisitCommand = new DeleteVisitCommand(trip.getOrderedVisits().get(2));
        deleteVisitCommand.execute();
        deleteVisitCommand.undo();

        trip = TripAccessor.getTripById(trip.getTripid());

        int afterSize = trip.getOrderedVisits().size();

        assertEquals(afterSize, beforeSize);


    }

    @Test
    public void testRedo() {
        int beforeSize = trip.getOrderedVisits().size();

        DeleteVisitCommand deleteVisitCommand = new DeleteVisitCommand(trip.getOrderedVisits().get(2));
        deleteVisitCommand.execute();
        deleteVisitCommand.undo();
        deleteVisitCommand.redo();

        trip = TripAccessor.getTripById(trip.getTripid());

        int afterSize = trip.getOrderedVisits().size();

        assertEquals(afterSize, beforeSize - 1);
    }

}