package models.commands.Trips;

import accessors.TripAccessor;
import accessors.VisitAccessor;
import models.Trip;
import models.Visit;
import models.commands.Profile.HomePageCommand;

import java.util.ArrayList;
import java.util.List;

/** Command to delete a user's trip
 *  extends HomePageCommand as you undo it from the home page not the trip page
 * */
public class DeleteTripCommand extends HomePageCommand {
    private Trip trip;
    private Trip savedTrip;

    // Using sets as the items do not need to be ordered and are unique
    private List<Visit> deletedVisits = new ArrayList<>();

    /**
     * Constructor to create an DeleteTripCommand. Takes the trip to delete
     * as the parameter.
     * @param trip the trip to delete
     */
    public DeleteTripCommand(Trip trip) {
        this.trip = trip;
        this.savedTrip = trip;
        for (Visit visit : trip.getVisits()) {
            deletedVisits.add(new Visit(visit));
        }
    }

    /**
     * Deletes the trip
     */
    public void execute() {
        for (Visit visit : savedTrip.getVisits()) {
            VisitAccessor.delete(visit);
        }
        TripAccessor.delete(savedTrip);
    }

    /**
     * Undoes the deletion of the trip
     */
    public void undo() {
        Trip undoTrip = new Trip(this.trip, deletedVisits);
        undoTrip.save();
        savedTrip = undoTrip;
        for (Visit visit : deletedVisits) {
            visit.setTrip(undoTrip);
            VisitAccessor.insert(visit);
        }
    }

    /**
     * Redos the deletion of the trip
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Trip " + this.trip.getTripName() + " deletion";
    }
}
