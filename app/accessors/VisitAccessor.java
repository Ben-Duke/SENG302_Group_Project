package accessors;

import models.Destination;
import models.Visit;

import java.util.List;

/**
 * A class to handle accessing Visits from the database
 */
public class VisitAccessor {

    // Private constructor to hide the implicit public one
    private VisitAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return the visit matching the id passed
     * @param id Id of a visit to find in the database
     * @return Visit
     */
    public static Visit getById(int id) {
        return Visit.find.query().where().eq("visitid", id).findOne();
    }

    /** Return a list of all visits in the databse
     * @return List of visits
     */
    public static List<Visit> getAll() {
        return Visit.find.all();
    }

    /** Insert the destination
     * @param visit the visit to insert
     */
    public static void insert(Visit visit) {
        visit.setVisitid(null);
        visit.save();
    }

    /** delete the destination
     * @param visit the visit to delete
     */
    public static void delete(Visit visit) {
        visit.delete();
    }

    /**
     * Update the visit
     * @param visit the visit to update
     */
    public static void update(Visit visit) { visit.update();}

}
