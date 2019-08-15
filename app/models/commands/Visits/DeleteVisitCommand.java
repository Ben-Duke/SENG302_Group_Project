package models.commands.Visits;

import accessors.TripAccessor;
import accessors.VisitAccessor;
import models.Trip;
import models.Visit;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

/**
 * Command to delete a visit
 */
public class DeleteVisitCommand extends UndoableCommand {

    private Visit visitToDelete;
    private Trip trip;


    /**
     * Create a delete visit command with a visit that has been delete
     * @param visit the visit that is being deleted
     */
    public DeleteVisitCommand(Visit visit) {
        super(CommandPage.MAP);
        this.visitToDelete = visit;
        this.trip = visit.getTrip();
    }

    /**
     * Execute the delete visit command
     */
    public void execute() {
        VisitAccessor.delete(visitToDelete);
        Integer removedVisits = 0;
        if (trip.getRemovedVisits() != null) {
            removedVisits = trip.getRemovedVisits();
        }
        trip.setRemovedVisits(removedVisits + 1);
        TripAccessor.update(trip);
    }

    /**
     * Undo the delete visit command (restoring the visit)
     */
    public void undo() {
        Visit newVisit = new Visit(visitToDelete);
        VisitAccessor.insert(newVisit);
        Integer removedVisits = 0;
        if (trip.getRemovedVisits() != null) {
            removedVisits = trip.getRemovedVisits();
        }
        trip.setRemovedVisits(removedVisits - 1);
        TripAccessor.update(trip);
        visitToDelete = newVisit;
    }

    /**
     * Redo the delete visit command
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Visit " + this.visitToDelete.getVisitid() + " deleting";
    }
}
