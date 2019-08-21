package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EnvVariableKeys;
import utilities.EnvironmentalVariablesAccessor;
import utilities.EventFindaUtilities;
import views.html.users.events.*;

import static play.mvc.Results.*;

public class EventsController {

    public Result indexEvents(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        return ok(eventSearch.render(user, googleApiKey));
    }

    public Result getEventsData(Http.Request request, double latitude, double longitude, String place, int offset) {
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
}
