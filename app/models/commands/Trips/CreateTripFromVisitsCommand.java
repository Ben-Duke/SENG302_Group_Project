package models.commands.Trips;

import accessors.TripAccessor;
import accessors.VisitAccessor;
import models.Trip;
import models.User;
import models.Visit;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to create trips from a list of visits
 * */
public class CreateTripFromVisitsCommand extends UndoableCommand {

    private List<Visit> visits;
    private List<Visit> cleanVisits;
    private String name;
    private User user;

    private Trip trip;


    /**
     * Constructor to create a CreateTripFromVisits command. Visits must not belong a trip.
     * @param visits a list of visits which are not linked a trip
     * @param name the name of the trip
     * @param user the user that's creating the trip
     */
    public CreateTripFromVisitsCommand(List<Visit> visits, String name, User user) {
        super(CommandPage.MAP);
        this.visits = visits;
        this.cleanVisits = new ArrayList<>();
        this.name = name;
        this.user = user;

    }


    /**
     * Creates the trip and visits linked to the trip
     */
    public void execute() {

        trip = new Trip(name, true, user);
        TripAccessor.insert(trip);
        for (Visit visit : visits) {
            cleanVisits.add(new Visit(visit.getArrival(),visit.getDeparture(), visit.getDestination()));
            visit.setTrip(trip);
            VisitAccessor.insert(visit);
            TripAccessor.update(trip);
        }

    }


    /**
     * Deletes the trip and visits linked to the trip
     */
    public void undo() {
        trip.removeAllVisits();
        TripAccessor.update(trip);

        for (Visit visit : visits) {
            VisitAccessor.delete(visit);
        }

        TripAccessor.delete(trip);

    }


    /**
     * Creates the trip and visits linked to the trip
     */
    public void redo() {
        visits = new ArrayList<>(cleanVisits);
        cleanVisits = new ArrayList<>();
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Trip: " + trip.getTripName() + " Creation" ;
    }

}
