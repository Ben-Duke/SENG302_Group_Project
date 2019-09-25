package controllers;

import accessors.MediaAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Media;
import models.Tag;
import models.User;
import models.UserPhoto;
import org.junit.Ignore;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class NewsfeedControllerTest extends BaseTestWithApplicationAndDatabase{

    @Test
    public void testUserLoggedIn(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/newsfeed").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testUserIsNotLoggedIn(){
            Http.RequestBuilder request = Helpers.fakeRequest()
                    .method(GET)
                    .uri("/users/newsfeed").session("connected", null);
            Result result = route(app, request);
            assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void checkContentForFollowingUserIsThere(){
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy%20HH:mm:ss");
        String formattedString = time.format(formatter);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/follow/2").session("connected", "1");
        route(app, request);

        Http.RequestBuilder newsfeedRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/newsfeed/media?offset=0&limit=1000&localDateTime="+formattedString).session("connected", "1");
        Result newsfeedResult = route(app, newsfeedRequest);
        JsonNode newsfeedResponse = Json.parse( contentAsString( newsfeedResult));
        assertEquals(1,newsfeedResponse.size());
    }

}
