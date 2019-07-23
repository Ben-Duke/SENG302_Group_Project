package accessors;

import models.Destination;

import java.util.List;

public class DestinationAccessor {

    /** Return the destination matching the id passed */
    public static Destination getDestinationById(int id) {
        return Destination.find.query().where().eq("destid", id).findOne();
    }

    /**
     * Return the first public destination that matches this name.
     * If the database is in a consistent state this will be the only matching destination
     */
    public static Destination getPublicDestinationbyName(String name) {
        return Destination.find.query().where()
                .eq("isPublic", true).and()
                .eq("destName", name)
                .findOne();
    }

    /**
     * Used for unit tests
     * Return the first destination that matches this name.
     * Private destinations can share the same name so list size can be more than one
     */
    public static List<Destination> getDestinationsbyName(String name) {
        return Destination.find.query().where()
                .eq("destName", name)
                .findList();
    }

    public static List<Destination> getAllDestinations() {
        return Destination.find.all();
    }

    /** Insert the destination */
    public static void insert(Destination destination) {
        destination.save();
    }

    /** delete the destination */
    public static void delete(Destination destination) {
        destination.delete();
    }

    /** Update the destination */
    public static void update(Destination destination) { destination.update(); }
}
