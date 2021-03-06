package accessors;

import io.ebean.Query;
import models.Tag;
import models.Trip;
import models.User;

import java.util.ArrayList;
import java.util.List;

import static io.ebean.Expr.ilike;
import static io.ebean.Expr.like;

/**
 * A class to handle accessing Trips from the database
 */
public class TripAccessor {

    // Private constructor to hide the implicit public one
    private TripAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return the trip matching the id passed
     * @param name name of a trip to find in the database
     * @return Trip
     */
    public static List<Trip> getTripsByName(String name, User user, Integer offset, Integer quantity) {
        return Trip.find().query().
                where()
                .eq("user", user)
                .and()
                .ilike("trip_name", "%" + name + "%")
                .setFirstRow(offset)
                .setMaxRows(quantity).findList();

    }

    /**
     * Gets the total count of matching trips based on a trip name and user.
     *
     * @param name The trip name to search for
     * @param user The user to search trips for
     * @return An int representing the total number of matching trips for that
     * name and user.
     */
    public static int getTripsByNameTotalCount(String name, User user) {
        return Trip.find().query().
                where()
                .eq("user", user)
                .and()
                .ilike("trip_name", "%" + name + "%")
                .findCount();
    }

    public static Integer getTotalUserTripCount(User user) {
        return Trip.find().query().where().eq("user", user) .findCount();

    }


    /**
     * Gets a paginated List of a users trips, with an offset and quantity to fetch.
     *
     * @param offset an integer representing the number of destinations to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @param user the user whose trips are to be retrieved
     * @return A list of user trips matching the offset and quantity.
     */
    public static List<Trip> getPaginatedTrips(int offset, int quantity, User user){
        if (quantity < 1) {
            return new ArrayList<Trip>();
        }

        if (offset < 0) {
            offset = 0;
        }

        Query<Trip> query = Trip.find().query()
                .where()
                .eq("user", user)
                .setFirstRow(offset).setMaxRows(quantity);
        return query.findList();
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
    public static List<Tag> getAllTags() {
        return Tag.find.all();
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
