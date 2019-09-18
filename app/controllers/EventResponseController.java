package controllers;

import accessors.EventResponseAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Event;
import models.EventResponse;
import models.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.libs.json.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;
import views.html.users.events.eventSearch;

import static play.mvc.Results.ok;

public class EventResponseController {

    private final Logger logger = LoggerFactory.getLogger("application");

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Controller method to respond to an event.
     * @param request Request
     * @param eventId Id of the event
     * @param responseType type of response
     * @return Result
     */
    public Result respondToEvent(Http.Request request, Integer eventId, String responseType) {
        User user = User.getCurrentUser(request);
        Event event;
        event = Event.find().query().where().eq("externalId", eventId).findOne();
        if (event == null) {
            JsonNode eventData = EventFindaUtilities.getEventById(eventId);
            if (eventData != null) {
                System.out.println(eventData.get("events").toString());
                event = new Event(eventData.get("events").get(0).get("name").toString());
                event.setExternalId(eventData.get("events").get(0).get("id").asInt());
                event.setUrl(eventData.get("events").get(0).get("url").toString());
                event.setLatitude(eventData.get("events").get(0).get("point").get("lat").asDouble());
                event.setLongitude(eventData.get("events").get(0).get("point").get("lng").asDouble());
                event.setStartTime(LocalDateTime.parse(eventData.get("events").get(0).get("datetime_start").toString().substring(1, 20), formatter));
                event.setEndTime(LocalDateTime.parse(eventData.get("events").get(0).get("datetime_end").toString().substring(1, 20), formatter));
//                event.setDescription(eventData.get("events").get(0).get("description").toString());
                event.insert();
                event = Event.find().byId(eventId);
                EventResponse eventResponse = new EventResponse(responseType, event, user);
                EventResponseAccessor.insert(eventResponse);
            }
        }
        EventResponse eventResponse = new EventResponse(responseType, event, user);
        EventResponseAccessor.insert(eventResponse);
        return ok();
    }

    public Result getAllResponses(Http.Request request) throws JsonParseException, IOException {
        System.out.println(EventResponseAccessor.getAllEventResponses().get(5).toString());
        return ok();
    }
}
