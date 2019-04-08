package controllers;

import models.Destination;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;

public class DestinationControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    /**
     * Sets up the fake database before each test
     */
    @Before
    public void setUpDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));


        //Initialises a test user with name "testUser" and saves it to the database.
        User user = new User("testUser");
        user.save();
        User user2 = new User("testUser2");
        user2.save();
        Destination destination = new Destination("University of Canterbury",
                "University",
                "Ilam",
                "New Zealand",
                -43.525450F,
                172.582600F,
                user);
        destination.save();
        Destination destination2 = new Destination("University of Banterbury",
                "University",
                "9",
                "Pepestan",
                -100,
                100,
                user);
        destination2.save();
        Destination destination3 = new Destination("Panem",
                "Hunger Games",
                "12",
                "Panem",
                100,
                -100,
                user2);
        destination3.save();
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    /**
     * Test to render destination index with no login session
     */
    @Test
    public void indexDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render destination index with a login session
     */
    @Test
    public void indexDestinationWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to render viewing a destination with no login session
     */
    @Test
    public void displayViewDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render viewing a destination with a login session
     */
    @Test
    public void displayViewDestinationWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to render creating a destination with no login session
     */
    @Test
    public void displayCreateDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/create/").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render creating a destination with a login session
     */
    @Test
    public void displayCreateDestinationWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/create/").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to handle saving a destination with no login session
     */
    @Test
    public void saveDestinationWithNoLoginSession() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/save").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle saving a destination with a login session
     */
    @Test
    public void saveDestinationWithLoginSession() {
        assertEquals(2, User.find.byId(1).getDestinations().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/save").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(3, User.find.byId(1).getDestinations().size());
    }

    /**
     * Test to see if a non number value will be picked up by the validation
     */
    @Test
    public void saveDestinationBadLatitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0a");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_ACCEPTABLE, result.status());
    }

    /**
     * Test to check for an invalid longitude value as it is out of range
     */
    @Test
    public void saveDestinationOutOfRangeLongitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0");
        formData.put("longitude", "-181");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_ACCEPTABLE, result.status());
    }

    /**
     * Test to check if boundary longitude value is not picked up by the validation
     */
    @Test
    public void saveDestinationBoundaryRangeLongitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0");
        formData.put("longitude", "-180.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render editing a destination with no login session
     */
    @Test
    public void editDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render editing a destination with a login session but invalid destination
     */
    @Test
    public void editDestinationWithLoginSessionAndInvalidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/5").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }


    /**
     * Test to render editing a destination with a login session and valid destination but invalid owner
     */
    @Test
    public void editDestinationWithLoginSessionAndValidDestinationAndInvalidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/3").session("connected", "1");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render editing a destination with a login session and valid destination and valid owner
     */
    @Test
    public void editDestinationWithLoginSessionAndValidDestinationAndValidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to handle updating a destination with no login session
     */
    @Test
    public void updateDestinationWithNoLoginSession() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and invalid destination
     */
    @Test
    public void updateDestinationWithLoginSessionAndInvalidDestination() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/5").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and valid destination but invalid owner
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndInvalidOwner() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "1");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and valid destination and valid owner
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwner() {
        assertEquals("University of Canterbury", User.find.byId(1).getDestinations().get(0).getDestName());
        assertEquals("University", User.find.byId(1).getDestinations().get(0).getDestType());
        assertEquals("Ilam", User.find.byId(1).getDestinations().get(0).getDistrict());
        assertEquals("New Zealand", User.find.byId(1).getDestinations().get(0).getCountry());
        assertEquals(-43.525450F, User.find.byId(1).getDestinations().get(0).getLatitude(), 0.01);
        assertEquals(172.582600F, User.find.byId(1).getDestinations().get(0).getLongitude(), 0.01);
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/1").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals("Summoner's Rift", User.find.byId(1).getDestinations().get(0).getDestName());
        assertEquals("Yes", User.find.byId(1).getDestinations().get(0).getDestType());
        assertEquals("Demacia", User.find.byId(1).getDestinations().get(0).getDistrict());
        assertEquals("Angola", User.find.byId(1).getDestinations().get(0).getCountry());
        assertEquals(50.0, User.find.byId(1).getDestinations().get(0).getLatitude(), 0.01);
        assertEquals(-50.0, User.find.byId(1).getDestinations().get(0).getLongitude(), 0.01);
    }

    /**
     * Test to handle deleting a destination with no login session
     */
    @Test
    public void deleteDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session but invalid destination
     */
    @Test
    public void deleteDestinationWithLoginSessionAndInvalidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/5").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session and valid destination but invalid owner
     */
    @Test
    public void deleteDestinationWithLoginSessionAndValidDestinationAndInvalidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session and valid destination and valid owner
     */
    @Test
    public void deleteDestinationWithLoginSessionAndValidDestinationAndValidOwner() {
        assertEquals(1, User.find.byId(2).getDestinations().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(0, User.find.byId(2).getDestinations().size());
    }

    /**
     * Test to handle making a destination public with no login session
     */
    @Test
    public void makeDestinationPublicWithoutLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle making an invalid destination public
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithInvalidDestination(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/20").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to handle making a destination public when you do not own the destination
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithValidDestinationWithInvalidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle making a destination public when you own the destination.
     * Destination should be changed to public and it's owner should be changed to the default admin.
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithValidDestinationWithValidOwner(){
        Destination destination = Destination.find.byId(3);
        assertEquals(false, destination.getIsPublic());
        assertEquals(2, destination.getUser().getUserid());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        destination = Destination.find.byId(3);
        assertEquals(true, destination.getIsPublic());
        assertEquals(1, destination.getUser().getUserid());
    }

    /**
     * Test to handle updating a destination with valid details after the destination has been made public.
     * It should no longer work since the owner is now the default admin.
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwnerAfterBeingSetToPublic(){

        //Set destination 3 to public
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());


        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request2 = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "2");
        Result result2 = route(app, request2);
        assertEquals(UNAUTHORIZED, result2.status());
    }
}