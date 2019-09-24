package accessors;

import io.ebean.Finder;
import models.Event;

public class EventAccessor {

    static public Finder<Integer, Event> EventFinder(){
        return Event.find();
    }

    public static Event getEventById(Integer eventId) {
        return EventFinder().query().where().eq("eventId", eventId).findOne();
    }

    /** Insert an event
     * @param event event to save
     */
    public static void insert(Event event) {
        event.save();
    }

    /** Delete an event
     * @param event event to delete
     */
    public static void delete(Event event) {
        event.delete();
    }

    /** Update the event
     * @param event event to update
     */
    public static void update(Event event) { event.update(); }

}
