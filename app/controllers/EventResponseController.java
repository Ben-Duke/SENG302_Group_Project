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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.libs.json.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;
import views.html.users.events.eventSearch;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.unauthorized;

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
            JsonNode eventData = jsonData.get("events").get(0);
            event = new Event(eventData.get("name").toString());
            event.setExternalId(eventData.get("id").asInt());
            event.setUrl(eventData.get("url").toString());
            event.setLatitude(eventData.get("point").get("lat").asDouble());
            event.setLongitude(eventData.get("point").get("lng").asDouble());
            event.setStartTime(LocalDateTime.parse(eventData.get("datetime_start").toString().substring(1, 20), formatter));
            event.setEndTime(LocalDateTime.parse(eventData.get("datetime_end").toString().substring(1, 20), formatter));
            event.setDescription(eventData.get("description").toString());
            event.insert();
            event = Event.find().byId(externalEventId);
            EventResponse eventResponse = new EventResponse(responseType, event, user);
            EventResponseAccessor.insert(eventResponse);
        }
        EventResponse eventResponse = new EventResponse(responseType, event, user);
        EventResponseAccessor.insert(eventResponse);
        return ok();
    }

    public Result getAllResponses(Http.Request request) throws JsonParseException, IOException {
        for (EventResponse eventResponse: EventResponseAccessor.getAllEventResponses()) {
            System.out.println(eventResponse);
        }
        return ok();
    }


}
