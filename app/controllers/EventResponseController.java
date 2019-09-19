package controllers;

import accessors.EventResponseAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Event;
import models.EventResponse;
import models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;

import static play.mvc.Results.*;

public class EventResponseController {

    private final Logger logger = LoggerFactory.getLogger("application");

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Controller method to respond to an event.
     * @param request Request
     * @param externalEventId Id of the event
     * @param responseType type of response
     * @return Result
     */
    public Result respondToEvent(Http.Request request, Integer externalEventId, String responseType) {
        User user = User.getCurrentUser(request);
//        if (user == null) {return unauthorized();}
        Event event = Event.find().query().where().eq("externalId", externalEventId).findOne();
        if (event == null) {
            JsonNode jsonData = EventFindaUtilities.getEventById(externalEventId).get("events");
            if (jsonData.size() == 0) {return badRequest();}
            JsonNode eventData = jsonData.get(0);
            Event newEvent = new Event(
                    eventData.get("id").asInt(),
                    LocalDateTime.parse(eventData.get("datetime_start").toString().substring(1, 20), formatter),
                    LocalDateTime.parse(eventData.get("datetime_end").toString().substring(1, 20), formatter),
                    eventData.get("name").toString(),
                    eventData.get("url").toString(),
                    eventData.get("point").get("lat").asDouble(),
                    eventData.get("point").get("lng").asDouble(),
                    eventData.get("description").toString(), null
            );
//            event.setName(eventData.get("name").asText());
//            event.setExternalId(eventData.get("id").asInt());
//            event.setUrl(eventData.get("url").toString());
//            event.setLatitude(eventData.get("point").get("lat").asDouble());
//            event.setLongitude(eventData.get("point").get("lng").asDouble());
//            event.setStartTime(LocalDateTime.parse(eventData.get("datetime_start").toString().substring(1, 20), formatter));
//            event.setEndTime(LocalDateTime.parse(eventData.get("datetime_end").toString().substring(1, 20), formatter));
//            event.setDescription(eventData.get("description").toString());
            newEvent.save();
            EventResponse eventResponse = new EventResponse(responseType, newEvent, user);
            EventResponseAccessor.insert(eventResponse);
        }
        EventResponse eventResponse = new EventResponse(responseType, event, user);
        EventResponseAccessor.insert(eventResponse);
        return ok();
    }

    /**
     * Controller method to get events the user has responded to by response type.
     * @param request Request
     * @param responseType type of response
     * @return Result
     */
    public Result getEventResponsesByResponseType(Http.Request request, String responseType) {
        User user = User.getCurrentUser(request);
        if (user == null) {return redirect(routes.UserController.userindex());}
        List<EventResponse> eventResponses = EventResponseAccessor.getByUserAndType(user, responseType);
        List<Event> respondedEvents = new ArrayList<>();
        for (EventResponse response: eventResponses) {
            respondedEvents.add(response.getEvent());
        }
        return ok(Json.toJson(eventResponses));
    }

    /**
     * Controller method to get responses for an event by response type.
     * @param request Request
     * @param externalEventId Id of the event
     * @param responseType type of response
     * @return Result
     */
    public Result getResponsesForEventByResponseType(Http.Request request, Integer externalEventId, String responseType) {
        User user = User.getCurrentUser(request);
        if (user == null) {return redirect(routes.UserController.userindex());}
        Event event = Event.find().query().where().eq("externalId", externalEventId).findOne();
        if (event == null) {return badRequest("No one has responded to this event yet.");}
        List<EventResponse> eventResponses = EventResponseAccessor.getByEventAndType(event, responseType);
        if (eventResponses.isEmpty()) {return badRequest("No one has responded as " + responseType + " to this event.");}
        List<String> jsonList = new ArrayList<>();
        for (EventResponse eventResponse: eventResponses) {
            String json = "response: " + eventResponse.toString();
            jsonList.add(json);
        }
        return ok(Json.toJson(jsonList));
    }

    public Result getAllResponses(Http.Request request) {
        return ok(Json.toJson(EventResponseAccessor.getAllEventResponses()));
    }


}
