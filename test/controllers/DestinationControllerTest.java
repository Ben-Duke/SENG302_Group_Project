package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.*;
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
import utilities.TestDatabaseManager;

import java.util.*;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

public class DestinationControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

    int REDIRECT_HTTP_STATUS = SEE_OTHER;

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
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();
        //Initialises a test user with name "testUser" and saves it to the database.
//        User user = new User("testUser");
//        user.save();
//        User user2 = new User("testUser2");
//        user2.save();
//        Destination destination = new Destination("University of Canterbury",
//                "University",
//                "Ilam",
//                "New Zealand",
//                -43.525450F,
//                172.582600F,
//                user);
//        destination.save();
//        Destination destination2 = new Destination("University of Banterbury",
//                "University",
//                "9",
//                "Pepestan",
//                -100,
//                100,
//                user);
//        destination2.save();
//        Destination destination3 = new Destination("Panem",
//                "Hunger Games",
//                "12",
//                "Panem",
//                100,
//                -100,
//                user2);
//        destination3.save();
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
        assertEquals(3, User.find.byId(2).getDestinations().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/save").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, User.find.byId(2).getDestinations().size());
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
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    /**
     * Test to check for an invalid longitude value as it is out of range
     */
    @Test
    public void saveDestinationOutOfRangeLongitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0");
        formData.put("longitude", "-181");
        formData.put("destType", "Country");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
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
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
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
                .uri("/users/destinations/edit/50").session("connected", "1");
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
                .uri("/users/destinations/edit/3").session("connected", "3");
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
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/50").session("connected", "1");
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
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and valid destination and valid owner
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwner() {
        assertEquals("Christchurch", User.find.byId(2).getDestinations().get(0).getDestName());
        assertEquals("Town", User.find.byId(2).getDestinations().get(0).getDestType());
        assertEquals("Canterbury", User.find.byId(2).getDestinations().get(0).getDistrict());
        assertEquals("New Zealand", User.find.byId(2).getDestinations().get(0).getCountry());
        assertEquals(-43.5321, User.find.byId(2).getDestinations().get(0).getLatitude(), 0.01);
        assertEquals(172.6362, User.find.byId(2).getDestinations().get(0).getLongitude(), 0.01);
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
        assertEquals("Summoner's Rift", User.find.byId(2).getDestinations().get(0).getDestName());
        assertEquals("Yes", User.find.byId(2).getDestinations().get(0).getDestType());
        assertEquals("Demacia", User.find.byId(2).getDestinations().get(0).getDistrict());
        assertEquals("Angola", User.find.byId(2).getDestinations().get(0).getCountry());
        assertEquals(50.0, User.find.byId(2).getDestinations().get(0).getLatitude(), 0.01);
        assertEquals(-50.0, User.find.byId(2).getDestinations().get(0).getLongitude(), 0.01);
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
                .uri("/users/destinations/delete/50").session("connected", "1");
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
                .uri("/users/destinations/delete/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session and valid destination and valid owner
     * where the destination is being used in trips. This will fail.
     */
    @Test
    public void deleteDestinationWithLoginSessionAndValidDestinationAndValidOwnerWithDestinationInTrips() {
        assertEquals(3, User.find.byId(2).getDestinations().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(PRECONDITION_REQUIRED, result.status());
        assertEquals(3, User.find.byId(2).getDestinations().size());
    }

    /**
     * Test to handle deleting a destination with a login session and valid destination and valid owner
     * where the destination is not being used by any trips. This will succeed.
     */
//    @Test
//    public void deleteDestinationWithLoginSessionAndValidDestinationAndValidOwnerWithDestinationNotInTripsButInTreasureHunt() {
//        assertEquals(3, User.find.byId(2).getDestinations().size());
//        Destination destination = Destination.find.byId(3);
//        for(Visit visit : destination.getVisits()){
//            visit.delete();
//        }
//        destination.setTravellerTypes(new ArrayList<>());
//        destination.update();
//        Http.RequestBuilder request = Helpers.fakeRequest()
//                .method(GET)
//                .uri("/users/destinations/delete/3").session("connected", "2");
//        Result result = route(app, request);
//        assertEquals(PRECONDITION_REQUIRED, result.status());
//        assertEquals(3, User.find.byId(2).getDestinations().size());
//    }

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
                .uri("/users/destinations/public/2").session("connected", "4");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle making a destination public when you own the destination.
     * Destination should be changed to public and it's owner should be changed to the default admin.
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithValidDestinationWithValidOwner(){
        Destination destination = Destination.find.byId(2);
        assertEquals(false, destination.getIsPublic());
        assertEquals(2, destination.getUser().getUserid());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
        destination = Destination.find.byId(3);
        assertEquals(true, destination.getIsPublic());
        assertEquals(2, destination.getUser().getUserid());
    }


    /**
     * Test to handle updating a destination with valid details after the destination has been made public.
     * It should still work since the owner is stil the user.
     *
     * EDIT: OOPS I misintepreted the AC. This should be done after someone else has used the public destination.
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwnerAfterBeingSetToPublicBeforeBeingAdded(){

        //Set destination 3 to public
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());


        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request2 = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "2");
        Result result2 = route(app, request2);
        assertEquals(REDIRECT_HTTP_STATUS, result2.status());
    }

    /**
     * Test to handle updating a destination with valid details after the destination has been made public.
     * It should still work since the owner is stil the user.
     *
     * EDIT: OOPS I misintepreted the AC. This should be done after someone else has used the public destination.
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwnerAfterBeingSetToPublicAfterBeingAddedByDifferentUser(){

        //Set destination 2 to public by user id 2
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
        //A different user of user id 3 uses the destination in their trip
        Trip trip = new Trip("testTrip", true, User.find.byId(3));
        trip.save();
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/2").session("connected", "3");
        result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
        //User id 2 tries to update their destination
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request2 = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/2").session("connected", "2");
        Result result2 = route(app, request2);
        assertEquals(UNAUTHORIZED, result2.status());
    }

    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwnerAfterBeingSetToPublicAfterBeingAddedBySameUser(){

        //Set destination 3 to public
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());

        Trip trip = new Trip("testTrip", true, User.find.byId(2));
        trip.save();
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/3").session("connected", "2");
        result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request2 = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "2");
        Result result2 = route(app, request2);
        assertEquals(REDIRECT_HTTP_STATUS, result2.status());
    }

    /*
    @Test
    public void setPrimaryPhotoWithLoginSessionAndValidDestinationAndValidUser(){

    }
    */

    /**
     * Unit test for ajax request to get the owner of a destination given by a destination id
     */
    @Test
    public void getDestinationOwner(){
        //user with user id 2 owns destination 2. Any user (eg user id 3) can get the owner of a destination.
        //considering changing the method to consider private destinations in the future
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/owner/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(2, Integer.parseInt(contentAsString(result)));
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/owner/4").session("connected", "3");
        result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(3, Integer.parseInt(contentAsString(result)));
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This should work because public destinations should be accessible by anyone
     */
    @Test
    public void getDestinationAsPublicDestinationWithUserWhoIsNotOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/3").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        JsonNode jsonJacksonObjectOne = Json.parse(contentAsString(result));
        assertEquals("The Wok", jsonJacksonObjectOne.get("destName").asText());

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/1").session("connected", "3");
        result = route(app, request);
        assertEquals(OK, result.status());
        JsonNode jsonJacksonObjectTwo = Json.parse(contentAsString(result));
        assertEquals("Christchurch", jsonJacksonObjectTwo.get("destName").asText());
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This should work because public destinations should be accessible by anyone including the owner
     */
    @Test
    public void getDestinationAsPublicDestinationWithUserWhoIsOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        JsonNode jsonObject = Json.parse(contentAsString(result));
        assertEquals("The Wok", jsonObject.get("destName").asText());
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This shouldn't work because private destinations should only be accessible by the owner
     */
    @Test
    public void getDestinationAsPrivateDestinationWithUserWhoIsNotOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This should work because private destinations should only be accessible by the owner
     */
    @Test
    public void getDestinationAsPrivateDestinationWithUserWhoIsOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        JsonNode jsonObject = Json.parse(contentAsString(result));
        assertEquals("Wellington", jsonObject.get("destName").asText());
    }

    @Test
    public void getDestinationTravellerTypes(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/ttypes/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        JsonNode jsonJacksonArray = Json.parse(contentAsString(result));

        JsonNode nodeOne = jsonJacksonArray.get(0);
        JsonNode nodeTwo = jsonJacksonArray.get(1);

        //Groupie and gap year so length 2
        assertEquals(2, jsonJacksonArray.size());

        assertEquals("Groupie", nodeOne.get("travellerTypeName").asText());
        assertEquals("Gap Year", nodeTwo.get("travellerTypeName").asText());
    }

    @Test
    public void getVisibleDestinationMarkersJSONNotLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/getalljson").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void destinationModificationRequestReject() {
        User user = User.find.all().get(0);
        Destination newDestination = new Destination("Test Dest", "Town", "Test District", "Test Country", 100, 100, user, true);
        newDestination.save();
        Integer destId = newDestination.getDestId();

        Destination newDestinationValues = new Destination("Test Dest2", "Town2", "Test District2", "Test Country2", 101, 101, user);

        Destination destination = Destination.find.byId(destId);

        DestinationModificationRequest modReq = new DestinationModificationRequest(destination, newDestinationValues, user);
        modReq.save();

        Integer modReqId = modReq.getId();

        Admin admin = Admin.find.all().get(0);
        Integer adminUserId = admin.getUserId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/destination_modification_request/reject/"+modReqId)
                .session("connected", adminUserId.toString());

        Result result = route(app, request);

        assertEquals(303, result.status());
        assert(destination.getDestName().equals("Test Dest"));
        assert(destination.getDestType().equals("Town"));
        assert(destination.getLatitude() == 100);
        assertEquals(null, DestinationModificationRequest.find.query().where().eq("id", modReqId).findOne());

    }

    @Test
    public void destinationModificationRequestAcceptWithoutTravellerTypes() {
        User user = User.find.all().get(0);
        Destination newDestination = new Destination("Test Dest", "Town", "Test District", "Test Country", 100, 100, user, true);
        newDestination.save();
        Integer destId = newDestination.getDestId();

        Destination newDestinationValues = new Destination("Test Dest2", "Town2", "Test District2", "Test Country2", 101, 101, user);

        DestinationModificationRequest modReq = new DestinationModificationRequest(newDestination, newDestinationValues, user);
        modReq.save();

        Integer modReqId = modReq.getId();

        Admin admin = Admin.find.all().get(0);
        Integer adminUserId = admin.getUserId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/destination_modification_request/accept/"+modReqId)
                .session("connected", adminUserId.toString());

        Result result = route(app, request);

        Destination destination = Destination.find.byId(destId);

        assertEquals(303, result.status());
        System.out.println(destination.getDestName());
        assert(destination.getDestName().equals("Test Dest2"));
        assert(destination.getDestType().equals("Town2"));
        assert(destination.getDistrict().equals("Test District2"));
        assert(destination.getCountry().equals("Test Country2"));
        assert(destination.getLatitude() == 101);
        assert(destination.getLatitude() == 101);
        assertEquals(null, DestinationModificationRequest.find.query().where().eq("id", modReqId).findOne());

    }


    @Test
    public void destinationModificationRequestAcceptWithTravellerTypes() {
        User user = User.find.all().get(0);
        Destination newDestination = new Destination("Test Dest", "Town", "Test District", "Test Country", 100, 100, user, true);
        newDestination.save();
        Integer destId = newDestination.getDestId();

        Destination newDestinationValues = new Destination("Test Dest2", "Town2", "Test District2", "Test Country2", 101, 101, user);
        Set<TravellerType> travellerTypes = new TreeSet<>();
        travellerTypes.add(new TravellerType("Backpacker"));
        travellerTypes.add(new TravellerType("Groupie"));
        newDestinationValues.setTravellerTypes(travellerTypes);

        DestinationModificationRequest modReq = new DestinationModificationRequest(newDestination, newDestinationValues, user);
        modReq.save();

        Integer modReqId = modReq.getId();

        Admin admin = Admin.find.all().get(0);
        Integer adminUserId = admin.getUserId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/destination_modification_request/accept/"+modReqId)
                .session("connected", adminUserId.toString());

        Result result = route(app, request);

        Destination destination = Destination.find.byId(destId);

        assertEquals(303, result.status());
        assert(destination.getDestName().equals("Test Dest2"));
        assert(destination.getDestType().equals("Town2"));
        assert(destination.getDistrict().equals("Test District2"));
        assert(destination.getCountry().equals("Test Country2"));
        assert(destination.getLatitude() == 101);
        assert(destination.getLatitude() == 101);

        for (TravellerType travellerType : destination.getTravellerTypes()) {
            assert(travellerType.getTravellerTypeName().equals("Backpacker") || travellerType.getTravellerTypeName().equals("Groupie"));
            assertNotEquals(null, travellerType.getTtypeid());
        }
        assertEquals(2, destination.getTravellerTypes().size());

        assertEquals(null, DestinationModificationRequest.find.query().where().eq("id", modReqId).findOne());

    }

    @Test
    public void editPublicDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/public/1")
                .session("connected", "2");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void updatePublicDestination() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");

        Http.RequestBuilder request = Helpers.fakeRequest()
            .bodyForm(formData)
            .method(POST)
            .uri("/users/destinations/update/public/1")
            .session("connected", "2");

        Result result = route(app, request);

        assertEquals(303, result.status());
    }

    /**
     * Test to add photo to destination.
     */
    @Test
    public void addPhotoToDestination() {
        boolean destPhotoExists = false;
        int destPhotoSize = Destination.find.byId(1).getUserPhotos().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());

        List<UserPhoto> destPhotos = Destination.find.byId(1).getUserPhotos();
        for (UserPhoto destPhoto : destPhotos) {
            if (destPhoto.getPhotoId() == 1) {
                destPhotoExists = true;
            }
        }
        assertEquals(destPhotoSize+1, destPhotos.size());
        assertTrue(destPhotoExists);
    }

    /**
     * Test to add the same photo twice to the same destination.
     */
    @Test
    public void addDuplicatePhotoToDestination() {
        addPhotoToDestination();
        boolean destPhotoExists = false;
        int destPhotoSize = Destination.find.byId(1).getUserPhotos().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());

        List<UserPhoto> destPhotos = Destination.find.byId(1).getUserPhotos();
        for (UserPhoto destPhoto : destPhotos) {
            if (destPhoto.getPhotoId() == 1) {
                destPhotoExists = true;
            }
        }
        assertEquals(destPhotoSize, destPhotos.size());
        assertTrue(destPhotoExists);
    }

    /**
     * Test to add some other user's photo to destination.
     */
    @Test
    public void addPhotoToDestinationInvalidUser() {
        int destPhotoSize = Destination.find.byId(1).getUserPhotos().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/1")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());

        List<UserPhoto> destPhotos = Destination.find.byId(1).getUserPhotos();
        assertEquals(destPhotoSize, destPhotos.size());
    }

    /**
     * Test to add photo to destination when no user is logged in.
     */
    @Test
    public void addPhotoToDestinationInvalidLoginSession() {
        int destPhotoSize = Destination.find.byId(1).getUserPhotos().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/1")
                .session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());

        List<UserPhoto> destPhotos = Destination.find.byId(1).getUserPhotos();
        assertEquals(destPhotoSize, destPhotos.size());
    }

    /**
     * Test to add a photo which doesn't exist to a destination.
     */
    @Test
    public void addPhotoToDestinationInvalidPhoto() {
        int destPhotoSize = Destination.find.byId(1).getUserPhotos().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/10")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());

        List<UserPhoto> destPhotos = Destination.find.byId(1).getUserPhotos();
        assertEquals(destPhotoSize, destPhotos.size());
    }

    /**
     * Test to add a photo to a destination which doesn't exist.
     */
    @Test
    public void addPhotoToDestinationInvalidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/100/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());

    }
}