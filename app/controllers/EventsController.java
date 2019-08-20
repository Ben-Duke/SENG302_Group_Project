package controllers;

import accessors.DestinationAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EventFindaUtilities;
import views.html.users.events.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static play.mvc.Results.*;

public class EventsController {

    public Result indexEvents(Http.Request request) {
        User user = User.getCurrentUser(request);



        if (user == null) { return redirect(routes.UserController.userindex()); }

        Map<Integer, String> categoryIdsToNames = EventFindaUtilities.getMainCategories();

        return ok(eventSearch.render(user, categoryIdsToNames));
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


    public Result getEventsData(Http.Request request, String keyword, String category, String startDate,
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

}
