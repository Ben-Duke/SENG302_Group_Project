package controllers;

import accessors.DestinationAccessor;
import accessors.EventResponseAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import models.Event;
import models.EventResponse;
import models.User;
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
