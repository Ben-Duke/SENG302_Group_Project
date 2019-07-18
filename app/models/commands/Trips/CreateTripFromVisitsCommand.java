package models.commands.Trips;

import accessors.TripAccessor;
import accessors.VisitAccessor;
import models.Trip;
import models.User;
import models.Visit;
import models.commands.General.UndoableCommand;

import java.util.List;

public class CreateTripFromVisitsCommand extends UndoableCommand {

    private List<Visit> visits;
    private String name;
    private User user;

    private Trip trip;


    public CreateTripFromVisitsCommand(List<Visit> visits, String name, User user) {

        this.visits = visits;
        this.name = name;
        this.user = user;

    }


    public void execute() {

        trip = new Trip(name, true, user);
        TripAccessor.insert(trip);
        for (Visit visit : visits) {
            visit.setTrip(trip);
            VisitAccessor.insert(visit);
        }

    }


    public void undo() {

        


    }


    public void redo() {
        execute();
    }

}
