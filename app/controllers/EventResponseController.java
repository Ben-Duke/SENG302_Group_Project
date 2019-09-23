package controllers;

import accessors.EventResponseAccessor;
import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
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

import models.UserPhoto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;
import utilities.UtilityFunctions;
import utilities.exceptions.EbeanDateParseException;

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
            int lastTransfom = eventData.get("images").get("images").get(0).get("transforms").get("@attributes").get("count").asInt()-1;
            System.out.println(eventData);
            Event newEvent = new Event(
                    eventData.get("id").asInt(),
                    LocalDateTime.parse(eventData.get("datetime_start").toString().substring(1, 20), formatter),
                    LocalDateTime.parse(eventData.get("datetime_end").toString().substring(1, 20), formatter),
                    eventData.get("name").toString(),
                    eventData.get("category").get("name").toString(),
                    eventData.get("url").toString(),
                    eventData.get("images").get("images").get(0).get("transforms").get("transforms").get(lastTransfom).toString(),
                    eventData.get("point").get("lat").asDouble(),
                    eventData.get("point").get("lng").asDouble(),
                    eventData.get("address").toString(),
                    eventData.get("description").toString()
            );
            System.out.println("user is " + user);
            System.out.println("event is " + newEvent);
            newEvent.insert();
            newEvent.save();
            newEvent = Event.find.byId(newEvent.getEventId());
            EventResponse eventResponse = new EventResponse();
            EventResponseAccessor.save(eventResponse);
            EventResponse savedEventResponse = EventResponseAccessor.getById(eventResponse.getEventResponseId());
            savedEventResponse.setUser(user);
            savedEventResponse.setEvent(newEvent);
            savedEventResponse.setResponseType(responseType);
            EventResponseAccessor.update(savedEventResponse);
            System.out.println(EventResponseAccessor.getById(eventResponse.getEventResponseId()));
            return ok();
        }
        EventResponse eventResponse = new EventResponse();
        EventResponseAccessor.save(eventResponse);
        EventResponse savedEventResponse = EventResponseAccessor.getById(eventResponse.getEventResponseId());
        savedEventResponse.setUser(user);
        savedEventResponse.setEvent(event);
        savedEventResponse.setResponseType(responseType);
        EventResponseAccessor.update(savedEventResponse);
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

    public ObjectNode getJsonEventResponses(List<EventResponse> eventResponses) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        ArrayNode responses = objectMapper.createArrayNode();
        System.out.println(eventResponses);
        for (EventResponse response : eventResponses) {
            ObjectNode responseNode = objectMapper.createObjectNode();

            ObjectNode userNode = objectMapper.createObjectNode();
            User user = response.getUser();
            UserPhoto profilePhoto = UserAccessor.getProfilePhoto(user);
            userNode.put("id", user.getUserid());
            userNode.put("name", user.getFName() + " " + user.getLName());
            userNode.put("profilePicUrl", profilePhoto.getUrlWithPath());


            ObjectNode eventNode = objectMapper.createObjectNode();
            Event event = response.getEvent();
            eventNode.put("id", event.getEventId());
            eventNode.put("externalId", event.getExternalId());
            eventNode.put("name", event.getName());
            eventNode.put("type", event.getType());
            eventNode.put("description", event.getDescription());
            eventNode.put("url", event.getUrl());
            eventNode.put("imageUrl", event.getImageUrl());
            eventNode.put("lat", event.getLatitude());
            eventNode.put("lng", event.getLongitude());
            eventNode.put("address", event.getAddress());
            eventNode.put("startTime", event.getStartTime().format(formatter));
            eventNode.put("endTime", event.getEndTime().format(formatter));

            responseNode.put("responseId", response.getEventResponseId());
            responseNode.put("responseType", response.getResponseType());
            responseNode.set("user", userNode);
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

    public Result getEventResponses(Http.Request request, int offset, int limit, String localDateTime) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized("Must be logged in to use this API endpoint.");
        }

        LocalDateTime parsedLocalDateTime = null;
        try {
            parsedLocalDateTime = UtilityFunctions
                    .parseLocalDateTime(localDateTime);
        } catch (EbeanDateParseException e) {
           //silent catch, handled below
        }
        if (parsedLocalDateTime == null) {
            return badRequest("Invalid date time string");
        }

        List<EventResponse> eventResponses = EventResponseAccessor
                .getEventResponses(offset, limit, parsedLocalDateTime);


        return ok(getJsonEventResponses(eventResponses));
    }
}
