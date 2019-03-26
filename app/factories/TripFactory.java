package factories;

import formdata.TripFormData;
import models.Trip;
import models.Visit;

import java.util.ArrayList;

public class TripFactory {

    public int createTrip(TripFormData tripFormData) {
        Trip trip = new Trip();
        trip.tripName = tripFormData.tripName;
        trip.user = tripFormData.user;
        trip.removedVisits = 0;
        trip.visits = new ArrayList<Visit>();
        trip.save();
        return trip.tripid;
    }
}
