package controllers;

import accessors.AlbumAccessor;
import accessors.EventResponseAccessor;
import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;

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
import utilities.*;
import utilities.exceptions.EbeanDateParseException;

import javax.persistence.PersistenceException;

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
        if (user == null) {return redirect(routes.UserController.userindex());}
        Event event = Event.find().query().where().eq("externalId", externalEventId).findOne();
        if (event == null) {
            JsonNode jsonData = EventFindaUtilities.getEventById(externalEventId).get("events");
            if (jsonData.size() == 0) {return badRequest();}
            JsonNode eventData = jsonData.get(0);
            int lastTransfom = eventData.get("images").get("images").get(0).get("transforms").get("@attributes").get("count").asInt()-1;
            Event newEvent = new Event(
                    eventData.get("id").asInt(),
                    LocalDateTime.parse(eventData.get("datetime_start").toString().substring(1, 20), formatter),
                    LocalDateTime.parse(eventData.get("datetime_end").toString().substring(1, 20), formatter),
                    eventData.get("name").asText(),
                    eventData.get("category").get("name").asText(),
                    eventData.get("url").asText(),
                    eventData.get("images").get("images").get(0).get("transforms").get("transforms").get(lastTransfom).get("url").asText(),
                    eventData.get("point").get("lat").asDouble(),
                    eventData.get("point").get("lng").asDouble(),
                    eventData.get("address").asText(),
                    eventData.get("description").asText()
            );
            newEvent.insert();
            newEvent.save();
            newEvent = Event.find.byId(newEvent.getEventId());
            Album album = new Album(newEvent, newEvent.getName(), true);
            AlbumAccessor.insert(album);
            newEvent.update();
            EventResponse eventResponse = new EventResponse(ResponseType.valueOf(responseType), newEvent, user);
            EventResponse existingEventResponse = EventResponseAccessor.getByUserAndEvent(user, newEvent);
            if (existingEventResponse == null) {
                try {
                    EventResponseAccessor.insert(eventResponse);
                    EventResponseAccessor.save(eventResponse);
                    return ok();
                } catch (PersistenceException p) {
                    return forbidden();
                }
            } else {
                existingEventResponse.setResponseType(ResponseType.valueOf(responseType));
                existingEventResponse.setResponseDateTime(LocalDateTime.now());
                EventResponseAccessor.update(existingEventResponse);
                EventResponseAccessor.save(existingEventResponse);
                return ok();
            }
        }
        EventResponse eventResponse = new EventResponse(ResponseType.valueOf(responseType), event, user);
        EventResponse existingEventResponse = EventResponseAccessor.getByUserAndEvent(user, event);
        if (existingEventResponse == null) {
            try {
                EventResponseAccessor.insert(eventResponse);
                EventResponseAccessor.save(eventResponse);
                return ok();
            } catch (PersistenceException p) {
                return forbidden();
            }
        } else {
            existingEventResponse.setResponseType(ResponseType.valueOf(responseType));
            existingEventResponse.setResponseDateTime(LocalDateTime.now());
            EventResponseAccessor.update(existingEventResponse);
            EventResponseAccessor.save(existingEventResponse);
            return ok();
        }
    }

//    public Result getLimited

    /**
     * Controller method to get events the user has responded to by response type.
     * @param request Request
     * @param responseType type of response
     * @return Result
     */
    public Result getEventResponsesByResponseType(Http.Request request, String responseType) {
        User user = User.getCurrentUser(request);
        if (user == null) {return redirect(routes.UserController.userindex());}
        List<EventResponse> eventResponses = EventResponseAccessor.getByUserAndType(user, ResponseType.valueOf(responseType));
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
        List<EventResponse> eventResponses = EventResponseAccessor.getByEventAndType(event, ResponseType.valueOf(responseType));
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
        List<EventResponse> eventResponses = EventResponseAccessor.getByUserEventAndType(queryUser, event,
                ResponseType.valueOf(responseType));
        if (eventResponses.isEmpty()) {return forbidden("This user has not responded as " + responseType + " to this event.");}
        return ok(getJsonEventResponses(eventResponses));
    }

    public ObjectNode getJsonEventResponses(List<EventResponse> eventResponses) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode json = objectMapper.createObjectNode();
        ArrayNode responses = objectMapper.createArrayNode();
        for (EventResponse response : eventResponses) {
            ObjectNode responseNode = objectMapper.createObjectNode();

            ObjectNode userNode = objectMapper.createObjectNode();
            User user = response.getUser();
            UserPhoto profilePhoto = UserAccessor.getProfilePhoto(user);
            userNode.put("id", user.getUserid());
            userNode.put("name", user.getFName() + " " + user.getLName());
            if (profilePhoto != null) {
                userNode.put("profilePicUrl", profilePhoto.getUrlWithPath());
            } else {
                userNode.put("profilePicUrl", "null");
            }


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
            eventNode.put("startTime", UtilityFunctions.getStringFromDateTime(event.getStartTime()));
            eventNode.put("endTime", UtilityFunctions.getStringFromDateTime(event.getEndTime()));

            responseNode.put("responseId", response.getEventResponseId());
            responseNode.put("responseType", response.getResponseType().toString());
            responseNode.set("user", userNode);
            responseNode.set("event", eventNode);
            responseNode.put("responseDateTime", UtilityFunctions.getStringFromDateTime(response.getResponseDateTime()));
            responses.add(responseNode);
        }
        json.set("responses", responses);
        return json;
    }

    public Result getAllResponses(Http.Request request) {
        return ok(Json.toJson(EventResponseAccessor.getAllEventResponses()));
    }

    /** Get event responses only for users you are following */
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
                .getEventResponsesOfFollowingPaginated(user, offset, limit, parsedLocalDateTime);


        return ok(getJsonEventResponses(eventResponses));
    }
}
