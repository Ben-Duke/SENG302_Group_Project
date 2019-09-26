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
import org.slf4j.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EnvVariableKeys;
import utilities.EnvironmentalVariablesAccessor;
import utilities.EventFindaUtilities;
import utilities.UtilityFunctions;
import views.html.users.events.*;

import javax.rmi.CORBA.Util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.*;

public class EventsController {

    private Logger logger = UtilityFunctions.getLogger();

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        Event event = EventAccessor.getEventById(eventId);
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
        Event event = EventAccessor.getEventById(eventId);
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
        Event event = EventAccessor.getEventById(eventId);
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

    private Event createEvent(int eventId) {
        JsonNode jsonData = EventFindaUtilities.getEventById(eventId).get("events");
        if (jsonData.size() == 0) {return null;}
        JsonNode eventData = jsonData.get(0);
        int lastTransfom = eventData.get("images").get("images").get(0).get("transforms").get("@attributes").get("count").asInt()-1;
        return new Event(
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
    }


    public Result viewEvent(Http.Request request, int eventId) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());

        Event event = Event.find().query().where().eq("externalId", eventId).findOne();
        boolean isStored = false;
        List<EventResponse> eventResponses = new ArrayList<>();
        if (event == null) {
            event = createEvent(eventId);
            List<Album> albumList = new ArrayList<>();
            Album eventAlbum = new Album(event, event.getName(), false);
            Media eventPhoto = new UserPhoto(event.getImageUrl(), true, false, user);
            eventAlbum.addMedia(eventPhoto);
            albumList.add(eventAlbum);
            event.setAlbums(albumList);
        } else {
            eventResponses = event.getLimitedResponses(user);
            isStored = true;
        }

        return ok(viewEvent.render(user, event, eventResponses, googleApiKey, isStored));
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
