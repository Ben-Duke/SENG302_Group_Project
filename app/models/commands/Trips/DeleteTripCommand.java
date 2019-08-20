package models.commands.Trips;

import accessors.TagAccessor;
import accessors.TripAccessor;
import accessors.VisitAccessor;
import models.Tag;
import models.Trip;
import models.Visit;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/** Command to delete a user's trip
 *  extends HomePageCommand as you undo it from the home page not the trip page
 * */
public class DeleteTripCommand extends UndoableCommand {
    private Trip trip;
    private Trip savedTrip;

    // Using sets as the items do not need to be ordered and are unique
    private List<Visit> deletedVisits = new ArrayList<>();
    private List<Tag> deletedTags = new ArrayList<>();

    /**
     * Constructor to create an DeleteTripCommand. Takes the trip to delete
     * as the parameter.
     * @param trip the trip to delete
     */
    public DeleteTripCommand(Trip trip) {
        super(CommandPage.MAP);
        this.trip = trip;
        this.savedTrip = trip;
        for (Visit visit : trip.getVisits()) {
            deletedVisits.add(new Visit(visit));
        }
        for(Tag tag: trip.getTags()){
            deletedTags.add(tag);
        }
    }

    /**
     * Deletes the trip
     */
    public void execute() {
        for (Visit visit : savedTrip.getVisits()) {
            VisitAccessor.delete(visit);
        }
        for(Tag tag: savedTrip.getTags()){
            savedTrip.removeTag(tag);
            TagAccessor.update(tag);
            TripAccessor.update(savedTrip);
        }
        TripAccessor.delete(savedTrip);
    }

    /**
     * Undoes the deletion of the trip
     */
    public void undo() {
        deletedVisits = new ArrayList<>();
        for (Visit visit : trip.getVisits()) {
            deletedVisits.add(new Visit(visit));
        }
        savedTrip = new Trip(this.trip, deletedVisits);
        TripAccessor.insert(savedTrip);
        for (Visit visit : deletedVisits) {
            visit.setTrip(savedTrip);
            VisitAccessor.insert(visit);
            TripAccessor.update(savedTrip);
        }
        for (Tag tag : deletedTags) {
            savedTrip.addTag(tag);
            TagAccessor.update(tag);
        }
        TripAccessor.insert(savedTrip);
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
