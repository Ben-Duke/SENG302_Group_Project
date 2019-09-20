package controllers;

import accessors.DestinationAccessor;
import accessors.MediaAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.ebeaninternal.server.type.ScalarTypeJsonMapPostgres;
import models.Destination;
import models.Follow;
import models.User;
import play.libs.Json;
import play.mvc.BodyParser;
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

public class NewsfeedController {

    /**
     * Returns a Json response of the users news-feed
     * @param request used to determine the user
     * @return
     */
    public Result getUserNewsFeed(Http.Request request){
        User user = User.getCurrentUser(request);
        if(user == null){
            return unauthorized("You need to be logged in");
        }
        List<Follow> following = user.getFollowing();
        ArrayNode userNewsfeed = (new ObjectMapper()).createArrayNode();
        for(int i = 0; i < following.size(); i++){
            ArrayNode userData = MediaAccessor.getUserMediaData(UserAccessor.getById(following.get(i).getFolowedUserId()));
            if (!userData.isEmpty(null)){
                for(int k = 0; k < userData.size(); k++ ){
                    if (!userData.get(k).isEmpty(null)){

                        userNewsfeed.add(userData.get(k));
                    }
                }
            }

        }

        return ok(Json.toJson(userNewsfeed));
    }

}
