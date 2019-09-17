package accessors;

import models.Event;
import models.EventResponse;
import models.User;

import java.util.List;

/**
 * A class to handle accessing Treasure Hunts from the database
 */
public class EventResponseAccessor {

    // Private constructor to hide the implicit public one
    private EventResponseAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return a Treasure Hunt matching the id passed
     * @param id Id value of a treasure hunt in the database
     * @return Treasure Hunt
     */
    public static EventResponse getById(int id) {
        return EventResponse.find().query().where().eq("eventResponseId", id).findOne();
    }

    /** Return all Treasure Hunts
     * @return List of Treasure Hunts
     */
    public static List<EventResponse> getAll() {
        return EventResponse.find().all();
    }

    public static List<EventResponse> getAllByResponseType(EventResponse eventResponseName) {
        return EventResponse.find().query().where().eq("responseType", eventResponseName).findList();
    }

    /** Return a list of Event Responses that are for a given event
     * @param event Event
     * @return List of Event Responses
     */
    public static List<EventResponse> getByEvent(Event event) {
        return EventResponse.find().query().where().eq(
                "event", event).findList();
    }

    /** Return a list of Event Responses that are for a given event of a particular type
     * @param event Event
     * @param responseType String
     * @return List of Event Responses
     */
    public static List<EventResponse> getByEventandType(Event event, String responseType) {
        return EventResponse.find().query().where().eq(
                "event", event).eq("responseType", responseType).findList();
    }

    /** Return a list of Event Responses by a given user
     * @param user User
     * @return List of Event Responses
     */
    public static List<EventResponse> getByUser(User user) {
        return EventResponse.find().query().where().eq(
                "user", user).findList();
    }

    /** Return a list of Event Responses that are by a given user of a particular type
     * @param user User
     * @param responseType String
     * @return List of Event Responses
     */
    public static List<EventResponse> getByUserandType(User user, String responseType) {
        return EventResponse.find().query().where().eq(
                "user", user).eq("responseType", responseType).findList();
    }

    /** Insert the EventResponse  */
    /** Insert a EventResponse
     * @param eventResponse EventResponse to insert into database
     */
    public static void insert(EventResponse eventResponse) {
        eventResponse.save();
    }

    /** Delete a EventResponse
     * @param eventResponse Event Response to delete from the database
     */
    public static void delete(EventResponse eventResponse) {
        eventResponse.delete();
    }

    /** Update a EventResponse
     * @param eventResponse EventResponse to update from database
     */
    public static void update(EventResponse eventResponse) {
        eventResponse.update();
    }

}
