package controllers;

import accessors.NationalityAccessor;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class NationalityControllerTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void getAllNationalities() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/nationalities").session("connected", "4");
        Result result = route(app, request);
        assertEquals(NationalityAccessor.getAll().size(),
                Json.parse(contentAsString(result)).size());
    }
}