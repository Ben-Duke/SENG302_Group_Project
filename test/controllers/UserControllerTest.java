package controllers;

import models.Tag;
import models.User;
import models.UserPhoto;
import org.junit.Ignore;
import org.junit.Test;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.rmi.server.ExportException;
import java.util.Collection;
import java.util.Collections;

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
    @Ignore
    public void checkAddTag(){
        UserPhoto userPhoto = new UserPhoto
                ("",true,true, new User());
        userPhoto.addTag(new Tag("Test"));
        assertEquals(1, userPhoto.getTags().size());
    }

    @Test
    @Ignore
    public void checkAddingSameTag(){
        UserPhoto userPhoto = new UserPhoto
                ("",true,true, new User());
        userPhoto.addTag(new Tag("Test"));
        userPhoto.addTag(new Tag("Test"));
        assertEquals(1, userPhoto.getTags().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAddingNullTag(){
        UserPhoto userPhoto = new UserPhoto
                ("",true,true, new User());
        userPhoto.addTag(null);
    }

    @Test
    public void checkRemoveTag(){
        UserPhoto userPhoto = new UserPhoto
                ("",true,true, new User());
        userPhoto.addTag(new Tag("Test"));
        userPhoto.removeTag(new Tag("Test"));
        assertEquals(0, userPhoto.getTags().size());
    }

    @Test
    public void checkRemoveTagOnEmptySet(){
        UserPhoto userPhoto = new UserPhoto
                ("",true,true, new User());
        assertEquals(false, userPhoto.removeTag(new Tag("Test")));
    }
}