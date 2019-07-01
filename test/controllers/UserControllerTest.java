package controllers;

import org.junit.Test;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;


import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void getPhotoCaptionUserLoggedInCheckResponse() {
        Result result = getCaptionMessageHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoCaptionUserLoggedInCheckCaption() {
        Result result = getCaptionMessageHelper();
        assertEquals("Get out of my swamp", contentAsString(result));
    }

    private Result getCaptionMessageHelper() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/photos/1/caption")
                .session("connected", "2");
        return route(app, request);
    }


    @Test
    public void getPhotoCaptionUserNotLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/photos/1/caption");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getPhotoCaptionWrongUserLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/photos/2/caption")
                .session("connected", "3");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getPhotoCaptionAdminLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/photos/2/caption")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoCaptionNoCaptionCheckResponse() {
        Result result = getEmptyCaptionMessageHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoCaptionNoCaptionCheckCaption() {
        Result result = getEmptyCaptionMessageHelper();
        assertEquals("", contentAsString(result));
    }

    private Result getEmptyCaptionMessageHelper() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/photos/2/caption")
                .session("connected", "2");
        return route(app, request);
    }


    @Test
    public void getCaptionOfPhotoThatDoesNotExist() {
            Http.RequestBuilder request = Helpers.fakeRequest()
                    .method(GET)
                    .uri("/users/photos/99999/caption")
                    .session("connected", "2");
            Result result = route(app, request);
            assertEquals(NOT_FOUND, result.status());

    }

    private Result editCaptionMessageHelper() {
        Map<String, String> formData = new HashMap<>();
        formData.put("caption", "Ogres have layers");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(formData)
                .method(PUT)
                .uri("/users/photos/1/caption")
                .session("connected", "2");
        return route(app, request);
    }



    @Test
    public void editPhotoCaptionCheckResponse() {
        Result result = editCaptionMessageHelper();
        assertEquals(OK, result.status());
    }

    @Test
    public void editPhotoCaptionCheckCaption() {
        editCaptionMessageHelper();
        Result result = getCaptionMessageHelper();
        assertEquals("Ogres have layers", contentAsString(result));
    }

    @Test
    public void editPhotoCaptionAsAdmin() {
        Map<String, String> form = new HashMap<>();
        form.put("caption", "Ogres have admins");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(form)
                .method(PUT)
                .uri("/users/photos/1/caption")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void editPhotoCaptionEmptyCaption() {
        Map<String, String> form = new HashMap<>();
        form.put("caption", "");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(form)
                .method(PUT)
                .uri("/users/photos/1/caption")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void editPhotoCaptionWrongUser() {
        Map<String, String> form = new HashMap<>();
        form.put("caption", "Ogre's must be the right user or admin");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(form)
                .method(PUT)
                .uri("/users/photos/1/caption")
                .session("connected", "3");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test(expected = NullPointerException.class)
    public void editPhotoCaptionNullCaption() {
        Map<String, String> form = new HashMap<>();
        form.put("caption", null);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(form)
                .method(PUT)
                .uri("/users/photos/1/caption")
                .session("connected", "2");
        route(app, request);
    }

    @Test
    public void editPhotoCaptionPhotoDoesNotExist() {
        Map<String, String> form = new HashMap<>();
        form.put("caption", "Donkey!!!");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(form)
                .method(PUT)
                .uri("/users/photos/99999/caption")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND,result.status());
    }

    @Test
    public void editPhotoCaptionUserNotLoggedIn() {
        Map<String, String> form = new HashMap<>();
        form.put("caption", "Donkey!!!");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(form)
                .method(PUT)
                .uri("/users/photos/1/caption");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED,result.status());
    }

}