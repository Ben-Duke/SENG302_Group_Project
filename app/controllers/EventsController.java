package controllers;

import accessors.DestinationAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;
import views.html.users.events.*;

import static play.mvc.Results.*;

public class EventsController {

    public Result indexEvents(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }

        JsonNode data = EventFindaUtilities.getCategories();
        System.out.println(data.get("categories").size());

        return ok(eventSearch.render(user));
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


    public Result getEventsData(Http.Request request, String keyword, String category, String artist, String startDate,
                                String endDate, String minPrice, String maxPrice, String destination, String sortBy, Integer offset) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        Destination destinationRetrieved;
        if(destination.isEmpty()) {
           //TODO change to a default destination if none selected
            //return badRequest();
            //Temporary default destination is christchurc, does nothing right now
            int DEFAULT_DESTINATION = 1;
            destinationRetrieved = DestinationAccessor.getDestinationById(DEFAULT_DESTINATION);
        }
        else {
            destinationRetrieved = DestinationAccessor.getDestinationById(Integer.parseInt(destination));
        }

        JsonNode data = EventFindaUtilities.getEvents(keyword, category, artist, startDate, endDate, minPrice, maxPrice, destinationRetrieved, sortBy, offset);

        if (data == null) {
            return badRequest("EventFinda could not find anything matching your query");
        }


        return ok(data);
    }

}
