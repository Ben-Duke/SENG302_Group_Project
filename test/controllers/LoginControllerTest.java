package controllers;

import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.api.test.CSRFTokenHelper;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class LoginControllerTest extends BaseTestWithApplicationAndDatabase {

    @Override
    /*
     * Populate test data
     */
    public void populateDatabase() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User("gon12@uclive.ac.nz", "hunter22");
        user.save();
    }

    @Test
    public void loadLoginPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/login");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void loginrequestWithInvalidUsername() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "testAccount3@uclive.ac.nz");
        formData.put("password", "hunter22");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/login");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = Helpers.route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void loginrequestWithValidUsernameButInvalidPassword() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "gon12@uclive.ac.nz");
        formData.put("password", "hunter234");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/login");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = Helpers.route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void loginrequestWithValidUsernameWithValidPassword() {
        Map<String, String> formData = new HashMap<>();
        formData.put("email", "gon12@uclive.ac.nz");
        formData.put("password", "hunter22");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/login");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = Helpers.route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void logoutrequest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/logout")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }
}