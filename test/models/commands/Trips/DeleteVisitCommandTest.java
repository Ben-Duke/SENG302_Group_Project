package models.commands.Trips;

import accessors.TripAccessor;
import accessors.VisitAccessor;

import models.Trip;

import models.User;
import models.Visit;
import models.commands.Visits.DeleteVisitCommand;

import org.junit.Test;

import testhelpers.BaseTestWithApplicationAndDatabase;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DeleteVisitCommandTest extends BaseTestWithApplicationAndDatabase {

    private Trip trip;


    @Override
    public void populateDatabase() {
        super.populateDatabase();
        trip = TripAccessor.getTripById(2);
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