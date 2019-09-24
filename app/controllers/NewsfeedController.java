package controllers;

import accessors.MediaAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.*;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.UtilityFunctions;
import utilities.exceptions.EbeanDateParseException;
import views.html.users.newsfeed.newsfeed;

import java.time.LocalDateTime;
import java.util.List;

import static play.mvc.Results.*;

public class NewsfeedController {


    /**
     * Renders the news feed page
     * @param request the HTTP request
     * @return the news feed page
     */
    public Result newsfeedPage(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized("You need to be logged in");
        }

        return ok(newsfeed.render(user));
    };


    /**
     * Returns a Json response of the users news-feed for media
     * @param request used to determine the user
     * @return Json object in response
     */
    public Result getMediaForNewsfeed(Http.Request request, int offset, int limit, String datetimeString) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized("You need to be logged in");
        }


        LocalDateTime parsedLocalDateTime = null;
        try {
            parsedLocalDateTime = UtilityFunctions
                    .parseLocalDateTime(datetimeString);
        } catch (EbeanDateParseException e) {
            //silent catch, handled below
        }
        if (parsedLocalDateTime == null) {
            return badRequest("Invalid date time string");
        }

        List<Media> media = MediaAccessor.getFollowingMedia(user, offset, limit, parsedLocalDateTime);
        ArrayNode userNewsfeed = (new ObjectMapper()).createArrayNode();
        for(int i = 0; i < media.size(); i++) {
            userNewsfeed.add(MediaAccessor.getUserMediaData(media.get(i)));
        }

        return ok(Json.toJson(userNewsfeed));
    }

}


