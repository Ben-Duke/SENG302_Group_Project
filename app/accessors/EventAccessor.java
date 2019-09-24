package accessors;

import models.Event;

public class EventAccessor {

    // Private constructor to hide the implicit public one
    private EventAccessor() {
        throw new IllegalStateException("Utility class");
    }


    /** Return a Event  matching the id passed
     * @param id Id value of a Event  in the database
     * @return Event
     */
    public static Event getByInternalId(int id) {
        return Event.find().query().where().eq("eventId", id).findOne();
    }

    /** Return a Event  matching the id passed
     * @param id Id value of a Event  in the database
     * @return Event
     */
    public static Event getByExternalId(int id) {
        return Event.find().query().where().eq("externalId", id).findOne();
    }

    /**
     * Insert an event
     * @param event the event to insert
     */
    public static void insert(Event event) {
        event.save();
    }

    /**
     * Update an event
     * @param event the event to update
     */
    public static void update(Event event) {
        event.update();
    }

    /**
     * Delete an event
     * @param event the event to delete
     */
    public static void delete(Event event) {
        event.delete();
    }

}
