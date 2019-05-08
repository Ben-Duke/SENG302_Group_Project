package accessors;

import models.Destination;

import java.util.List;

public class DestinationAccessor {

    /** Return the destination matching the id passed */
    public static Destination getDestinationById(int id) {
        return Destination.find.query().where().eq("destid", id).findOne();
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
}
