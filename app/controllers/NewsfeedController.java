package controllers;

import accessors.DestinationAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import models.User;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EnvVariableKeys;
import utilities.EnvironmentalVariablesAccessor;
import utilities.EventFindaUtilities;
import views.html.users.events.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import static play.mvc.Results.*;

public class NewsfeedController {

    /**
     * This method is called when the user requests their newsfeed and
     * returns a
     * @param request
     * @return
     */
    public Result getUserNewsFeed(Http.Request request){
        User user = User.getCurrentUser(request);
        if(user == null){
            return unauthorized("You need to be logged in");
        }



        return ok();
    }

}
