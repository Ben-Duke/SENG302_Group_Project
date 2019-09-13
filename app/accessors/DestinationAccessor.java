package accessors;

import io.ebean.Query;
import models.Destination;

import java.util.ArrayList;
import java.util.List;

import static io.ebean.Expr.like;

/**
 * A class to handle accessing Destination from the database
 */
public class DestinationAccessor {

    // Private constructor to hide the implicit public one
    private DestinationAccessor() {
        throw new IllegalStateException("Utility class");
    }


    /** Return the destination matching the id passed
     * @param id Integer to match to a destination in the database
     * @return Destination
     */
    public static Destination getDestinationById(int id) {
        return Destination.find().query().where().eq("destid", id).findOne();
    }

    /**
     * Return the first public destination that matches this name.
     * If the database is in a consistent state this will be the only matching destination
     * @param name String name to match to a public destination in database
     * @return Destination
     */
    public static Destination getPublicDestinationbyName(String name) {
        return Destination.find().query().where()
                .eq("destIsPublic", true).and()
                .eq("destName", name)
                .findOne();
    }

    /**
     * Gets a paginated List of public destinations, with an offset and quantity to fetch.
     *
     * @param offset an integer representing the number of destinations to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return A List<Destination> of destinations.
     */
    public static List<Destination> getPaginatedPublicDestinations(int offset, int quantity){
        if (quantity < 1) {
            return new ArrayList<Destination>();
        }

        if (offset < 0) {
            offset = 0;
        }

        Query<Destination> query = Destination.find().query()
                .where()
                .eq("destIsPublic", true)
                .setFirstRow(offset).setMaxRows(quantity);
        return query.findList();
    }

    /**
     * Returns all destination in the database
     * @return List of destinations
     */
    /**
     * Used for unit tests
     * Return the first destination that matches this name.
     * Private destinations can share the same name so list size can be more than one
     */
    public static List<Destination> getDestinationsbyName(String name) {
        return Destination.find().query().where()
                .eq("destName", name)
                .findList();
    }

    /**
     * Returns all destination in the database
     * @return List of destinations
     */
    /**
     * Return destinations that matches this keyword.
     * Private destinations can share the same name so list size can be more than one
     */
    public static List<Destination> getDestinationsWithKeyword(String name) {
        return Destination.find().query().where().or(like("destName", "%" + name + "%"), like("destName", "%" + name.toUpperCase() + "%")).findList();
    }

    public static List<Destination> getAllDestinations() {
        return Destination.find().all();
    }

    /** Insert a destination
     * @param destination Destination to save
     */
    public static void insert(Destination destination) {
        destination.save();
    }

    /** Delete a destination
     * @param destination Destination to delete
     */
    public static void delete(Destination destination) {
        destination.delete();
    }

    /** Update the destination
     * @param destination Destination to update
     */
    public static void update(Destination destination) { destination.update(); }
}
