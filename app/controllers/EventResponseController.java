package controllers;

import accessors.EventResponseAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Event;
import models.EventResponse;
import models.User;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;
import views.html.users.events.eventSearch;

import static play.mvc.Results.ok;

public class EventResponseController {

    private final Logger logger = LoggerFactory.getLogger("application");

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
                event.setExternalId(eventData.get("events").get(0).get("externalId").asInt());
                event.setName(eventData.get("events").get(0).get("name").asText());
                event.setUrl(eventData.get("events").get(0).get("url").asText());
                event.setLatitude(eventData.get("events").get(0).get("point").get("lat").asDouble());
                event.setLongitude(eventData.get("events").get(0).get("point").get("long").asDouble());
                event.setStartTime(LocalDateTime.parse(eventData.get("events").get(0).get("datetime_start").asText()));
                event.setEndTime(LocalDateTime.parse(eventData.get("events").get(0).get("datetime_end").asText()));
                event.setDescription(eventData.get("events").get(0).get("description").asText());
                event.save();
                event = Event.find().byId(eventId);
            }
        }
        EventResponse eventResponse = new EventResponse(responseType, event, user);
        EventResponseAccessor.insert(eventResponse);
        return ok();
    }
}
