package factories;

import formdata.TripFormData;
import models.Trip;
import models.User;
import models.Visit;

import java.util.ArrayList;

public class TripFactory {

    public int createTrip(TripFormData tripFormData, User user) {
        Trip trip = new Trip();
        trip.tripName = tripFormData.tripName;
        trip.user = user;
        trip.removedVisits = 0;
        trip.visits = new ArrayList<Visit>();
        trip.save();
        return trip.tripid;
    }
}
