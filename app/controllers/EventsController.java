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

    public Result getEventsData(Http.Request request, String keyword, String category, String artist, String startDate,
                                String endDate, String minPrice, String maxPrice, String destination, String sortBy) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        System.out.println(destination);
        if(destination.isEmpty()) {
           //TODO change to a default destination if none selected
            return badRequest();
        }
        Destination destinationRetrieved = DestinationAccessor.getDestinationById(Integer.parseInt(destination));

        JsonNode data = EventFindaUtilities.getEvents(keyword, category, artist, startDate, endDate, minPrice, maxPrice,
                destinationRetrieved, sortBy);

        if (data == null) {
            return badRequest("EventFinda could not find anything matching your query");
        }


        return ok(data);
    }

}
