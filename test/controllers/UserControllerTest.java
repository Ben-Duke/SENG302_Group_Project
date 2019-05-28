package controllers;

import org.junit.Test;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;


import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class UserControllerTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void userindex() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void getUserWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/get").session("connected", null);
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void getUserWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/get").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(3, Integer.parseInt(contentAsString(result)));
    }
}