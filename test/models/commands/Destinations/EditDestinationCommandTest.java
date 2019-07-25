package models.commands.Destinations;

import controllers.ApplicationManager;
import models.Destination;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import static org.junit.Assert.*;

public class EditDestinationCommandTest extends BaseTestWithApplicationAndDatabase {
    private EditDestinationCommand editDestinationCommand;
    private Database database;

    @Override
    /* Populate the database */
    public void populateDatabase() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();

        Destination christchurch = Destination.find.byId(1);
        christchurch.setDestName("Auckland");
        christchurch.setDistrict("District 12");
        christchurch.setCountry("New Zealand");
        christchurch.setLatitude(-36.8485);
        christchurch.setLongitude(174.7633);
        christchurch.setDestType("Attraction");
         editDestinationCommand =
                new EditDestinationCommand(christchurch);
    }

    @Test
    public void testExecute(){
        editDestinationCommand.execute();
        Destination updatedDestination = Destination.find.byId(1);
        assertEquals("Auckland", updatedDestination.getDestName());
        assertEquals("District 12", updatedDestination.getDistrict());
        assertEquals("New Zealand", updatedDestination.getCountry());
        assertEquals(-36.8485, updatedDestination.getLatitude(), 0.01);
        assertEquals(174.7633, updatedDestination.getLongitude(), 0.01);
        assertEquals("Attraction", updatedDestination.getDestType());
    }

    @Test
    public void testUndo(){
        testExecute();
        editDestinationCommand.undo();
        Destination undoneDestination = Destination.find.byId(1);
        assertEquals("Christchurch", undoneDestination.getDestName());
        assertEquals("Canterbury", undoneDestination.getDistrict());
        assertEquals("New Zealand", undoneDestination.getCountry());
        assertEquals(-43.5321, undoneDestination.getLatitude(), 0.01);
        assertEquals(172.6362, undoneDestination.getLongitude(), 0.01);
        assertEquals("Town", undoneDestination.getDestType());
    }

    @Test
    public void testRedo(){
        editDestinationCommand.execute();
        editDestinationCommand.undo();
        editDestinationCommand.redo();
        Destination updatedDestination = Destination.find.byId(1);
        assertEquals("Auckland", updatedDestination.getDestName());
        assertEquals("District 12", updatedDestination.getDistrict());
        assertEquals("New Zealand", updatedDestination.getCountry());
        assertEquals(-36.8485, updatedDestination.getLatitude(), 0.01);
        assertEquals(174.7633, updatedDestination.getLongitude(), 0.01);
        assertEquals("Attraction", updatedDestination.getDestType());
    }
}