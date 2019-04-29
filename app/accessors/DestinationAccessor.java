package accessors;

import models.Destination;

public class DestinationAccessor {

    /** Return the destination matching the id passed */
    public static Destination getDestinationById(int id) {
        return Destination.find.query().where().eq("destid", id).findOne();
    }
}
