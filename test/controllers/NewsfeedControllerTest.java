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

import java.rmi.server.ExportException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.NOT_FOUND;
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


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/follow/2").session("connected", "1");
        route(app, request);

        Http.RequestBuilder newsfeedRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/newsfeed").session("connected", "1");
        Result newsfeedResult = route(app, newsfeedRequest);
        JsonNode newsfeedResponse = Json.parse( contentAsString( newsfeedResult));

        assertEquals(1,newsfeedResponse.size());
    }

}
