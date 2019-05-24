package accessors;

import models.Trip;

import java.util.List;

public class TripAccessor {

    /** Return the trip matching the id passed */
    public static Trip getTripById(int id) {
        return Trip.find.byId(id);
    }

    public static List<Trip> getAllTrips() {
        return Trip.find.all();
    }

    /** Insert the trip */
    public static void insert(Trip trip) {
        trip.save();
    }

    /** delete the trip */
    public static void delete(Trip trip) {
        trip.delete();
    }

    /** Update the trip */
    public static void update(Trip trip) { trip.update(); }
}
