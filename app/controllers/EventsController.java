package controllers;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.EventAccessor;
import accessors.UserPhotoAccessor;
import accessors.EventResponseAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.Event;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EnvVariableKeys;
import utilities.EnvironmentalVariablesAccessor;
import utilities.EventFindaUtilities;
import views.html.users.events.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.*;

public class EventsController {

    public Result indexEvents(Http.Request request) {
        User user = User.getCurrentUser(request);



        if (user == null) { return redirect(routes.UserController.userindex()); }

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());

        Map<Integer, String> categoryIdsToNames = EventFindaUtilities.getMainCategories();

        return ok(eventSearch.render(user, categoryIdsToNames, googleApiKey));
    }

    public Result getEventsDataByDestination(Http.Request request, double latitude, double longitude, String place, Integer offset) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        JsonNode data = EventFindaUtilities.getEvents(latitude, longitude, place, offset);
        if (data != null) {
            return ok(data);
        } else {
            return badRequest("EventFinda could not find anything matching your query");
        }
    }

    public Result getEventsDataByExternalId(Http.Request request, Integer eventId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        JsonNode data = EventFindaUtilities.getEventById(eventId);
        if (data != null) {
            return ok(data);
        } else {
            return badRequest("EventFinda could not find anything matching your query");
        }
    }


    public Result getEventsData(Http.Request request, String keyword, String category, String startDate,
                                String endDate, String minPrice, String maxPrice, String destination, String sortBy, Integer offset) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Destination destinationRetrieved = null;
        if(!destination.isEmpty()) {
            destinationRetrieved = DestinationAccessor.getDestinationById(Integer.parseInt(destination));
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            format.parse(startDate);
        } catch (ParseException e) {
            startDate = "";
        }
        try {
            format.parse(endDate);
        } catch (ParseException e) {
            endDate = "";
        }

        try {
            Integer price = Integer.parseInt(minPrice);
            if (price < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            minPrice = "";
        }

        try {
            Integer price = Integer.parseInt(maxPrice);
            if (price < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            maxPrice = "";
        }


        JsonNode data = EventFindaUtilities.getEvents(keyword, category, startDate, endDate, minPrice, maxPrice, destinationRetrieved, sortBy, offset);

        if (data == null) {
            return badRequest("EventFinda could not find anything matching your query");
        }


        return ok(data);
    }

    /**
     * Link a user's photo to an event
     * @param request the
     * @param userPhotoId
     * @param eventId
     * @return
     */
    public Result linkPhotoToEvent(Http.Request request, Integer userPhotoId, Integer eventId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        UserPhoto userPhoto = UserPhotoAccessor.getUserPhotoById(userPhotoId);
        if(userPhoto == null) {
            return badRequest();
        }
        if(userPhoto.getUser().getUserid() != user.getUserid()) {
            return forbidden();
        }
        Event event = EventAccessor.getByInternalId(eventId);
        if(event == null) {
            return badRequest();
        }
        event.getPrimaryAlbum().addMedia(userPhoto);
        AlbumAccessor.update(event.getPrimaryAlbum());
        return ok();
    }

    /**
     * Unlink a user's photo to an event
     * @param request the
     * @param userPhotoId
     * @param eventId
     * @return
     */
    public Result unlinkPhotoToEvent(Http.Request request, Integer userPhotoId, Integer eventId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        UserPhoto userPhoto = UserPhotoAccessor.getUserPhotoById(userPhotoId);
        if(userPhoto == null) {
            return badRequest();
        }
        if(userPhoto.getUser().getUserid() != user.getUserid()) {
            return forbidden();
        }
        Event event = EventAccessor.getByInternalId(eventId);
        if(event == null) {
            return badRequest();
        }
        if (event.getPrimaryAlbum().containsMedia(userPhoto)) {
            event.getPrimaryAlbum().removeMedia(userPhoto);
        }
        AlbumAccessor.update(event.getPrimaryAlbum());
        return ok();
    }

    /**
     * Get a JSON object containing a list of the event's photos
     * Each event photo has a caption, urlWithPath and isPublic attribute.
     * @param request the HTTP request
     * @param eventId the id of the event
     * @return a result JSON object with a list of event photos
     */
    public Result getEventPhotos(Http.Request request, Integer eventId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Event event = EventAccessor.getByInternalId(eventId);
        if(event == null) {
            return badRequest();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode responseNode = objectMapper.createObjectNode();
        ArrayNode eventPhotos = objectMapper.createArrayNode();
        List<Media> eventMedia = event.getPrimaryAlbum().getMedia();

        for (Media media: eventMedia) {
            ObjectNode mediaNode = objectMapper.createObjectNode();
            mediaNode.put("caption", media.getCaption());
            mediaNode.put("urlWithPath", media.getUrlWithPath());
            mediaNode.put("isPublic", media.getIsPublic());
            eventPhotos.add(mediaNode);
        }
        responseNode.set("eventPhotos", eventPhotos);

        return ok(responseNode);
    }




    public Result viewEvent(Http.Request request, int eventId) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        Event event = Event.find().byId(eventId);
        List<EventResponse> eventResponses = EventResponseAccessor.getByEvent(event);
        boolean isGoing = false;
        for (EventResponse userEvent : eventResponses) {
            if(userEvent.getUser().getUserid() == user.getUserid()) {
                isGoing = true;
            }
        }

        return ok(viewEvent.render(user, event, eventResponses, googleApiKey, isGoing));
    }

    public Result checkEventExists(Http.Request request, int eventId) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }
        for(Event event: Event.find().all()) {
            if (event.getExternalId() == eventId) {
                return ok(Json.toJson(event.getEventId()));
            }
        }
        return badRequest("Event does not exist");

    }

}
