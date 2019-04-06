package controllers;

import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import static play.mvc.Http.Status.*;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

/**
 * A class containing methods to JUnit4 test the RegisterController class.
 *
 * Note on wording:
 *
 * Username refers to text of an formEmail address before the @
 * Domain refers to the text of an formEmail address after the @
 */
public class RegisterControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    private RegisterController registerController;

    @Before
    public void setUp() throws Exception {
        this.registerController = new RegisterController();
    }

    @After
    public void tearDown() throws Exception {
        this.registerController = null;
    }

    @Test
    public void isValidPasswordEmptyString() {
        assertFalse(this.registerController.isValidPassword(""));
    }

    @Test
    public void isValidPasswordTooShort() {
        assertFalse(this.registerController.isValidPassword("a"));
    }

    @Test
    public void isValidPasswordWithMinLengthPassword() {
        assertTrue(this.registerController.isValidPassword("12345678"));
    }

    @Test
    public void isValidPasswordMiddleLengthPassword() {
        assertTrue(this.registerController.isValidPassword("123helloworld456"));
    }

    @Test
    public void isValidPasswordMaxLengthPassword128() {
        assertTrue(this.registerController.isValidPassword("Y%q?B9K495o#c?Qhy" +
                "wN6Gu%2vqwiGxGWhp@k*#WDg?O6NvpGHcVDIhbVaCNBdLEeIAyBBzI9BN1RL" +
                "sHBJHUW6gc1xuprv4sABKZEIA9zKczr9f1e#TAnAbA_7CMv%s3c"));
    }

    @Test
    public void isValidPasswordTooLong129() {
        assertFalse(this.registerController.isValidPassword("Y%q?B9K495o#c?Qhy" +
                "wN6Gu%2vqwiGxGWhp@k*#WDg?O6NvpGHcVDIhbVaCNBdLEeIAyBBzI9BN1RL" +
                "sHBJHUW6gc1xuprv4sABKZEIA9zKczr9f1e#TAnAbA_7CMv%s3cc"));
    }



    @Test
    public void createuser() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/register");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

//    @Test
//    public void saveuserWithUniqueUsername() {
//        database = Databases.inMemory();
//        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
//                1,
//                "create table test (id bigint not null, name varchar(255));",
//                "drop table test;"
//        )));
//        User user = new User("testAccount@uclive.ac.nz");
//        user.save();
//        User user2 = new User("testAccount2@uclive.ac.nz");
//        user2.save();
//        assertEquals(2, User.find.all().size());
//        Map<String, String> formData = new HashMap<>();
//
//
//        formData.put("formEmail", "testAccount3@uclive.ac.nz");
//        formData.put("password", "hunter22");
//        //Added in
//        formData.put("firstName", "doe");
//        formData.put("lastName", "test");
//        formData.put("passports", "");
//        formData.put("nationalities", "");
//        formData.put("travellerTypes", "");
//        formData.put("gender", "Male");
//        formData.put("dob", "1990-09-09");
//
//        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/register");
//        Result result = Helpers.route(app, request);
//        assertEquals(SEE_OTHER, result.status());
//        assertEquals(3, User.find.all().size());
//        Evolutions.cleanupEvolutions(database);
//        database.shutdown();
//    }
}


