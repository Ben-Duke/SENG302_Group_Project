package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import play.Application;
import play.api.test.CSRFTokenHelper;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

public class ProfileControllerTest extends BaseTestWithApplicationAndDatabase {

    private final Logger logger = UtilityFunctions.getLogger();

    @Override
    /*
     * Setup the data for this test class
     * Runs before each test and cleared afterwards
     */
    public void populateDatabase() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearMostData();    // keep nats/pass/ttypes

        TravellerType travellerType1 = TravellerType.find().byId(1);
        TravellerType travellerType2 = TravellerType.find().byId(2);
        Nationality nationality1 = Nationality.find().byId(1);
        Nationality nationality2 = Nationality.find().byId(2);
        Passport passport1 = Passport.find().byId(1);
        Passport passport2 = Passport.find().byId(2);
        //Initialises a test user with name "testUser" and saves it to the database.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //convert String to LocalDate
        LocalDate birthDate = LocalDate.parse("1998-08-23", formatter);
        User user = new User("gon12@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user2 = new User("gon23@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user3 = new User("gon34@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        user.getNationality().add(nationality1);
        user.getNationality().add(nationality2);
        user.getPassport().add(passport1);
        user.getPassport().add(passport2);
        user.save();
        user2.getTravellerTypes().add(travellerType1);
        user2.getTravellerTypes().add(travellerType2);
        user2.getPassport().add(passport1);
        user2.getPassport().add(passport2);
        user2.save();
        user3.getTravellerTypes().add(travellerType1);
        user3.getTravellerTypes().add(travellerType2);
        user3.getPassport().add(passport1);
        user3.getPassport().add(passport2);
        user3.getNationality().add(nationality1);
        user3.getNationality().add(nationality2);
        user3.save();
    }

    @Test
    public void createprofileWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/update").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void createprofileWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/update").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void showProfileWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void showProfileWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/1").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void updateNatPass() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/update/natpass").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void submitUpdateNationalityWithDifferentNationality() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationality", "3");
        User user = User.find().byId(1);
        assertEquals(2, user.getNationality().size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addnat").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find().byId(1);
        assertEquals(3, user.getNationality().size());
    }

    @Test
    public void submitUpdateNationalityWithSameNationality() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationality", "2");
        User user = User.find().byId(1);
        assertEquals(2, user.getNationality().size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addnat").session("connected", "1");
        Result result = route(app, request);

        assertEquals(2, user.getNationality().size());
    }

    @Test
    public void submitUpdatePassportWithDifferentPassport() {
        Map<String, String> formData = new HashMap<>();
        formData.put("passport", "3");
        User user = User.find().byId(1);
        assertEquals(2, user.getPassports().size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addpass").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find().byId(1);
        assertEquals(3, user.getPassports().size());
    }

    @Test
    public void submitUpdatePassportWithSamePassport() {
        Map<String, String> formData = new HashMap<>();
        formData.put("passport", "2");
        User user = User.find().byId(1);
        assertEquals(2, user.getPassports().size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addpass").session("connected", "1");
        Result result = route(app, request);
        assertEquals(303, result.status());
    }

    @Test
    public void deleteNationality() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationalitydelete", "2");
        formData.put("userId", "1");
        User user = User.find().byId(1);
        assertEquals(2, user.getNationality().size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/delnat").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find().byId(1);
        assertEquals(1, user.getNationality().size());
    }

    @Test
    public void deletePassport() {
        Map<String, String> formData = new HashMap<>();
        formData.put("passportdelete", "2");
        User user = User.find().byId(1);
        assertEquals(2, user.getPassports().size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/delpass").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find().byId(1);
        assertEquals(1, user.getPassports().size());
    }

    /**
     * Test which checks the isProfilePictureSet method returns a JSON body
     * with the "isProfilePicSet" attribute mapping to the boolean false, for a
     * user that has no profile picture.
     */
    @Test
    public void isProfilePictureSet_withNoProfilePic_checkJsonHasFalseField() {
        Http.RequestBuilder request = Helpers.fakeRequest().method(Helpers.GET)
                                            .uri("/users/profilepicture/isSet")
                                            .session("connected", "1");
        Result result = route(app, request);
        JsonNode jsonJacksonObject = Json.parse(contentAsString(result));
        assertFalse(jsonJacksonObject.get("isProfilePicSet").asBoolean());
    }

    /**
     * Test which checks the isProfilePictureSet method returns a JSON body
     * with the "isProfilePicSet" attribute mapping to the boolean true, for a
     * user that does have a profile picture.
     */
    @Test
    public void isProfilePictureSet_withProfilePic_checkJsonHasTrueField() {
        UserPhoto profilePic = new UserPhoto("/test/url", true, true, User.find().byId(1));
        profilePic.save();

        Http.RequestBuilder request = Helpers.fakeRequest().method(Helpers.GET)
                .uri("/users/profilepicture/isSet")
                .session("connected", "1");
        Result result = route(app, request);
        JsonNode jsonJacksonObject = Json.parse(contentAsString(result));
        assertTrue(jsonJacksonObject.get("isProfilePicSet").asBoolean());
    }
}