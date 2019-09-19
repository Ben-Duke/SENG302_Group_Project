package controllers;

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
}
