package models.commands.Trips;

import models.Trip;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.*;

public class DeleteTripCommandTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void execute() {
        Trip tripToDelete = Trip.find().query().where().eq("tripName",
                "Trip to New Zealand").findOne();
        assertEquals(2, tripToDelete.getVisits().size());
        DeleteTripCommand deleteTripCommand = new DeleteTripCommand(tripToDelete);
        deleteTripCommand.execute();
        Trip deletedTrip = Trip.find().query().where().eq("tripName",
                "Trip to New Zealand").findOne();
        assertNull(deletedTrip);
    }

    @Test
    public void undo() {
        Trip tripToDelete = Trip.find().query().where().eq("tripName",
                "Trip to New Zealand").findOne();
        DeleteTripCommand deleteTripCommand = new DeleteTripCommand(tripToDelete);
        deleteTripCommand.execute();
        deleteTripCommand.undo();
        Trip restoredTrip = Trip.find().query().where().eq("tripName",
                "Trip to New Zealand").findOne();
        assertNotNull(restoredTrip);
        assertEquals(2, restoredTrip.getVisits().size());
        assertEquals(1, restoredTrip.getTags().size());
    }

    @Test
    public void redo() {
        Trip tripToDelete = Trip.find().query().where().eq("tripName",
                "Trip to New Zealand").findOne();
        DeleteTripCommand deleteTripCommand = new DeleteTripCommand(tripToDelete);
        deleteTripCommand.execute();
        deleteTripCommand.undo();
        deleteTripCommand.redo();
        Trip deletedTrip = Trip.find().query().where().eq("tripName",
                "Trip to New Zealand").findOne();
        assertNull(deletedTrip);
    }
}