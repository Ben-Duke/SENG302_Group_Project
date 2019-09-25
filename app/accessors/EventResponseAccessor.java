package accessors;

import models.Event;
import models.EventResponse;
import models.ResponseType;
import models.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A class to handle accessing Event Responses from the database
 */
public class EventResponseAccessor {

    // Private constructor to hide the implicit public one
    private EventResponseAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return a Event Response matching the id passed
     * @param id Id value of a Event Response in the database
     * @return Event Response
     */
    public static EventResponse getById(int id) {
        return EventResponse.find().query().where().eq("eventResponseId", id).findOne();
    }

    /** Return all Event Responses
     * @return List of Event Responses
     */
    public static List<EventResponse> getAllEventResponses() {
        return EventResponse.find().all();
    }

    public static List<EventResponse> getAllByResponseType(String eventResponseName) {
        return EventResponse.find().query().where().eq("response_type", eventResponseName).findList();
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
    public static List<EventResponse> getByEventAndType(Event event, ResponseType responseType) {
        return EventResponse.find().query().where().eq(
                "event", event).eq("response_type", responseType).findList();
    }

    public static int getCountByEventAndType(Event event, ResponseType responseType) {
        return EventResponse.find().query().where().eq(
                "event", event).eq("response_type", responseType).findCount();
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
    public static List<EventResponse> getByUserAndType(User user, ResponseType responseType) {
        return EventResponse.find().query().where().eq(
                "user", user).eq("response_type", responseType).findList();
    }

    /** Return a list of Event Responses that are by a given user for an event.
     * @param user User
     * @param event Event
     * @return List of Event Responses
     */
    public static EventResponse getByUserAndEvent(User user, Event event) {
        return EventResponse.find().query().where().eq(
                "user", user).eq("event", event).findOne();
    }

    /** Return a list of Event Responses that are by a given user for an event of a particular type
     * @param user User
     * @param event Event
     * @param responseType String
     * @return List of Event Responses
     */
    public static List<EventResponse> getByUserEventAndType(User user, Event event, ResponseType responseType) {
        return EventResponse.find().query().where().eq(
                "user", user).eq("response_type", responseType).eq("event", event).findList();
    }


    /** Insert the EventResponse  */
    /** Insert a EventResponse
     * @param eventResponse EventResponse to insert into database
     */
    public static void insert(EventResponse eventResponse) {
        eventResponse.save();
    }

    public static void save(EventResponse eventResponse) {
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


    public static List<EventResponse> getEventResponses(int offset, int limit, LocalDateTime dateTime) {
        return EventResponse.find().query().where()
                .lt("responseDateTime", dateTime)
                .order().desc("responseDateTime")
                .setFirstRow(offset)
                .setMaxRows(limit)
                .findList();
    }

    public static List<EventResponse> getEventResponsesOfFollowing(User user, int limit) {
        return EventResponse.find().query().where()
                .in("user_userid", user.getFollowingIds())
                .order().desc("responseDateTime")
                .setMaxRows(limit)
                .findList();
    }
}
