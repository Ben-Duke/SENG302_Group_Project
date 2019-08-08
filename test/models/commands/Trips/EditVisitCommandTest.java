package models.commands.Trips;

import controllers.ApplicationManager;

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

import static org.junit.Assert.assertEquals;


public class EditVisitCommandTest extends BaseTestWithApplicationAndDatabase {
    private Database database;
    private EditVisitCommand editVisitCommand;
    private Visit visit;
    private User user;


    @Override
    public void populateDatabase() {
        super.populateDatabase();
        visit = Visit.find().byId(1);
        user = User.find().byId(1);
    }

    @Test
    public void testExecute() {
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        visit.setArrival("2018-06-04");
        visit.setDeparture("2018-06-06");
        editVisitCommand = new EditVisitCommand(visit);
        user.getCommandManager().executeCommand(editVisitCommand);
        Visit updatedVisit = Visit.find().byId(1);
        assertEquals("2018-06-04", updatedVisit.getArrival());
        assertEquals("2018-06-06", updatedVisit.getDeparture());
    }

    @Test
    public void testUndo() {
        visit.setArrival("2018-06-04");
        visit.setDeparture("2018-06-06");
        editVisitCommand = new EditVisitCommand(visit);
        user.getCommandManager().executeCommand(editVisitCommand);
        user.getCommandManager().undo();
        Visit undoneVisit = Visit.find().byId(1);
        assertEquals("2018-05-04", undoneVisit.getArrival());
        assertEquals("2018-05-06", undoneVisit.getDeparture());
    }

    @Test
    public void testRedo() {
        visit.setArrival("2018-06-04");
        visit.setDeparture("2018-06-06");
        editVisitCommand = new EditVisitCommand(visit);
        user.getCommandManager().executeCommand(editVisitCommand);
        user.getCommandManager().undo();
        user.getCommandManager().redo();
        Visit redoneVisit = Visit.find().byId(1);
        assertEquals("2018-06-04", redoneVisit.getArrival());
        assertEquals("2018-06-06", redoneVisit.getDeparture());
    }
}
