package accessors;

import models.Trip;

import java.util.List;

/**
 * A class to handle accessing Trips from the database
 */
public class TripAccessor {

    // Private constructor to hide the implicit public one
    private TripAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return the trip matching the id passed
     * @param id Id of a trip to find in the database
     * @return Trip
     */
    public static Trip getTripById(int id) {
        return Trip.find().byId(id);
    }

    /** Return a list of all trips
     * @return List of trips
     */
    public static List<Trip> getAllTrips() {
        return Trip.find().all();
    }

    /** Insert a trip
     * @param trip Trip to insert into the database
     */
    public static void insert(Trip trip) {
        trip.save();
    }

    /** Delete a trip
     * @param trip Trip to delete from the database
     */
    public static void delete(Trip trip) {
        trip.delete();
    }

    /** Update a trip
     * @param trip Trip to update from the database
     */
    public static void update(Trip trip) { trip.update(); }
}
