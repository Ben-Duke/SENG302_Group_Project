package accessors;

import models.Destination;
import models.Visit;

import java.util.List;

public class VisitAccessor {

    /** Return the destination matching the id passed */
    public static Visit getById(int id) {
        return Visit.find.query().where().eq("visitid", id).findOne();
    }

    public static List<Visit> getAll() {
        return Visit.find.all();
    }

    /** Insert the destination */
    public static void insert(Visit visit) {
        visit.setVisitid(null);
        visit.save();
    }

    /** delete the destination */
    public static void delete(Visit visit) {
        visit.delete();
    }

    /**
     * Update the visit
     * @param visit the visit to update
     */
    public static void update(Visit visit) { visit.update();}

}