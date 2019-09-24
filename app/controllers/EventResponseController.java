package controllers;

import accessors.EventResponseAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        if (user == null) {return unauthorized();}
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
                    eventData.get("description").toString()
            );
            newEvent.insert();
            newEvent.save();
            EventResponse eventResponse = new EventResponse(responseType, newEvent, user);
            EventResponseAccessor.insert(eventResponse);
            EventResponseAccessor.save(eventResponse);
            return ok();
        }
        EventResponse eventResponse = new EventResponse(responseType, event, user);
        EventResponseAccessor.insert(eventResponse);
        EventResponseAccessor.save(eventResponse);
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
        return ok(getJsonEventResponses(eventResponses));
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
        return ok(getJsonEventResponses(eventResponses));
    }

    /**
     * Controller method to get responses for an event and an user by response type.
     * @param request Request
     * @param userId Id of the user
     * @param externalEventId Id of the event
     * @param responseType type of response
     * @param userId Id of the user
     * @return Result
     */
    public Result getResponsesForEventAndUserByResponseType(Http.Request request, Integer externalEventId, String responseType, Integer userId) {
        User user = User.getCurrentUser(request);
        if (user == null) {return redirect(routes.UserController.userindex());}
        Event event = Event.find().query().where().eq("externalId", externalEventId).findOne();
        if (event == null) {return badRequest("No one has responded to this event yet.");}
        User queryUser = UserAccessor.getById(userId);
        if (queryUser == null) {return badRequest("No one has responded to this event yet.");}
        List<EventResponse> eventResponses = EventResponseAccessor.getByUserEventAndType(queryUser, event, responseType);
        if (eventResponses.isEmpty()) {return forbidden("This user has not responded as " + responseType + " to this event.");}
        return ok(getJsonEventResponses(eventResponses));
    }

    public ObjectNode getJsonEventResponses(List<EventResponse> eventResponses) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        ArrayNode responses = objectMapper.createArrayNode();
        for (EventResponse response : eventResponses) {
            ObjectNode responseNode = objectMapper.createObjectNode();
            ObjectNode eventNode = objectMapper.createObjectNode();
            Event event = response.getEvent();
            eventNode.put("id", event.getEventId());
            eventNode.put("externalId", event.getExternalId());
            eventNode.put("name", event.getName());
            eventNode.put("description", event.getDescription());
            eventNode.put("url", event.getUrl());
            eventNode.put("lat", event.getLatitude());
            eventNode.put("lng", event.getLongitude());
            eventNode.put("startTime", event.getStartTime().format(formatter));
            eventNode.put("endTime", event.getEndTime().format(formatter));

            responseNode.put("responseId", response.getEventResponseId());
            responseNode.put("responseType", response.getResponseType());
            responseNode.put("userId", response.getUser().getUserid());
            responseNode.set("event", eventNode);
            responseNode.put("responseDateTime", response.getResponseDateTime().format(formatter));
            responses.add(responseNode);
        }
        json.set("responses", responses);
        return json;
    }

    public Result getAllResponses(Http.Request request) {
        return ok(Json.toJson(EventResponseAccessor.getAllEventResponses()));
    }


}
