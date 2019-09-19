package controllers;

import models.Nationality;
import models.Passport;
import models.TravellerType;
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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;



public class TravelPartnerControllerTest extends BaseTestWithApplicationAndDatabase {

    @Override
    /*
     * Populate the test data
     */
    public void populateDatabase() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearMostData();    // keep nats/pass/ttypes

        TravellerType travellerType1 = TravellerType.find().byId(1);
        TravellerType travellerType2 = TravellerType.find().byId(2);
        TravellerType travellerType3 = TravellerType.find().byId(3);
        Nationality nationality1 = Nationality.find().byId(1);
        Nationality nationality2 = Nationality.find().byId(2);
        Nationality nationality3 = Nationality.find().byId(3);
        Passport passport1 = Passport.find().byId(1);
        Passport passport2 = Passport.find().byId(2);
        Passport passport3 = Passport.find().byId(3);
        //Initialises a test user with name "testUser" and saves it to the database.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //convert String to LocalDate
        LocalDate birthDate = LocalDate.parse("1998-08-23", formatter);
        User user = new User("gon12@uclive.ac.nz", "hunter2", "Gavin", "Ong", birthDate, "Male");
        user.getTravellerTypes().add(travellerType1);
        user.getTravellerTypes().add(travellerType2);
        user.getNationality().add(nationality1);
        user.getNationality().add(nationality2);
        user.getPassport().add(passport1);
        user.getPassport().add(passport2);
        user.save();
        LocalDate birthDate2 = LocalDate.parse("1995-08-12", formatter);
        User user2 = new User("test@uclive.ac.nz", "hunter2", "Billie", "Bob", birthDate2, "Female");
        user2.getTravellerTypes().add(travellerType2);
        user2.getTravellerTypes().add(travellerType3);
        user2.getNationality().add(nationality2);
        user2.getNationality().add(nationality3);
        user2.getPassport().add(passport2);
        user2.getPassport().add(passport3);
        user2.save();
    }

    @Test
    public void testTravellerSearchPaginatedNoGenderSelected(){

        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    /**
     * Tests one particular user (testuser1) to see if the API can identify their gender.
     * WARNING: Adding user into the test database with a birthday of 1998-08-23 will
     * break this test.
     * Checks the size of the search with the male parameter, then the female parameter.
     */
    @Test
    public void testTravellerSearchPaginatedMaleGenderSelected(){
        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1998-08-22" +
                        "&bornbefore=1998-08-24&gender1=male").session("connected", "3");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(1, jsonSize );
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1998-08-22" +
                        "&bornbefore=1998-08-24&gender1=female").session("connected", "3");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    @Test
    public void testTravellerSearchPaginatedBornAfterFilter(){
        super.populateDatabase();

        //There is more than 10 users born after 1900. The query should return the default size of 10.
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1900-01-22" +
                        "&gender1=male&gender2=female&gender3=other").session("connected", "3");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(10, jsonSize );

        //There are no users born after 2025. The query will return 0 results.
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=2025-01-22" +
                        "&gender1=male&gender2=female&gender3=other").session("connected", "3");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    @Test
    public void testTravellerSearchPaginatedBornBeforeFilter(){
        super.populateDatabase();

        //There are no users born before 1900. The query will return 0 results.
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornbefore=1900-01-22" +
                        "&gender1=male&gender2=female&gender3=other").session("connected", "3");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );

        //There is more than 10 users born before 2025. The query should return the default size of 10.
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornbefore=2025-01-22" +
                        "&gender1=male&gender2=female&gender3=other").session("connected", "3");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(10, jsonSize );
    }

    /**
     * Tests one particular user (testuser2) to see if the API can identify their gender.
     * WARNING: Adding user into the test database with a birthday of 1960-08-25 will
     * break this test.
     * Checks the size of the search with the male parameter, then the female parameter.
     */
    @Test
    public void testTravellerSearchPaginatedFemaleGenderSelected(){
        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1960-12-24" +
                        "&bornbefore=1960-12-26&gender1=male").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1960-12-24" +
                        "&bornbefore=1960-12-26&gender1=female").session("connected", "2");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(1, jsonSize );
    }

    /**
     * Tests one particular user (testuser1) to see if the API can identify multiple
     * gender filters being used at once.
     * WARNING: Adding user into the test database with a birthday of 1998-08-23 will
     * break this test.
     * Checks the size of the search with the male parameter, then the female parameter.
     */
    @Test
    public void testTravellerSearchPaginatedMultipleGenderSelected(){
        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1998-08-22" +
                        "&bornbefore=1998-08-24&gender1=male").session("connected", "3");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(1, jsonSize );

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1998-08-22" +
                        "&bornbefore=1998-08-24&gender1=female&gender2=male").session("connected", "3");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(1, jsonSize );

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?bornafter=1998-08-22" +
                        "&bornbefore=1998-08-24&gender1=female&gender2=other").session("connected", "3");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    @Test
    public void testTravellerSearchPaginatedNegativeQuantity(){

        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?quantity=-1").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    /**
     * Test user 1 should be the only user with a nationality of Czechoslovakia
     */
    @Test
    public void testTravellerSearchPaginatedNationality(){

        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?nationality=Czechoslovakia&gender1=male&gender2=female&gender3=other").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(1, jsonSize );
    }

    /**
     * Test with an invalid nationality of Bbechoslovakia.
     * The system should not return any results.
     */
    @Test
    public void testTravellerSearchPaginatedInvalidNationality(){

        super.populateDatabase();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?nationality=Bbechoslovakia&gender1=male&gender2=female&gender3=other").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    @Test
    public void testTravellerSearchPaginatedTravellerType(){

        super.populateDatabase();

        //Test user 1 has a traveller type of gap year so this should return one result
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?nationality=Czechoslovakia&travellertype=gap%20year&gender1=male&gender2=female&gender3=other").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(1, jsonSize );

        //Test user 1 has a traveller type of gap year not groupie so this should return no results
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?nationality=Czechoslovakia&travellertype=groupie&gender1=male&gender2=female&gender3=other").session("connected", "2");
        result = route(app, request);
        jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    /**
     * Test with an invalid nationality of "yeet".
     * The system should not return any results.
     */
    @Test
    public void testTravellerSearchPaginatedInvalidTravellerType(){

        super.populateDatabase();

        //Test with a traveller type of
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?nationality=Czechoslovakia&travellertype=yeet&gender1=male&gender2=female&gender3=other").session("connected", "2");
        Result result = route(app, request);
        int jsonSize = Json.parse(contentAsString(result)).size();
        assertEquals(0, jsonSize );
    }

    @Test
    public void testTravellerSearchPaginatedTooManyRequested(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles?quantity=1001").session("connected", "2");
        Result result = route(app, request);

        assertEquals(BAD_REQUEST, result.status() );
    }

    @Test
    public void testTravellerSearchPaginatedNoUserLoggedIn(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/searchprofiles").session("connected", null);
        Result result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status() );
    }


    /**
     * Unit test for rendering the search profile page
     */
    @Test
    public void renderFilterPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/search").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/search").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }



    @Test
    public void searchByMaleAndFemaleOnly() {
        Map<String, String> formData = new HashMap<>();
        formData.put("gender[0", "Female");
        formData.put("gender[1", "Male");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());
    }

    @Test
    public void nationalitySearch() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationality", "New Zealand");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());
    }

    @Test
    public void searchByTravellerType() {
        Map<String, String> formData = new HashMap<>();
        formData.put("travellertype", "Groupie");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);

        Result result = Helpers.route(app, fakeRequest);
        assertEquals(OK, result.status());


    }

    @Test
    public void searchByDate() {
        Map<String, String> formData = new HashMap<>();
        formData.put("agerange1", "1998-08-22");
        formData.put("agerange2", "1998-08-24");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());

    }

    @Test
    public void searchByLastName() {
        Map<String, String> formData = new HashMap<>();
        formData.put("name", "Ong");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());

    }

    @Test
    public void searchByFirstName() {
        Map<String, String> formData = new HashMap<>();
        formData.put("name", "Gavin");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());

    }

    @Test
    public void searchByFullName() {
        Map<String, String> formData = new HashMap<>();
        formData.put("name", "Gavin Ong");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());
    }



}