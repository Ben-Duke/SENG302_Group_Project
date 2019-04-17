package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Destination;
import models.Trip;
import models.User;
import models.Visit;
import org.json.JSONArray;
import org.json.JSONObject;
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
import utilities.TestDatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

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
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "1");
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
        assertEquals(SEE_OTHER, result.status());
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
    @Test
    public void deleteDestinationWithLoginSessionAndValidDestinationAndValidOwnerWithDestinationNotInTrips() {
        assertEquals(3, User.find.byId(2).getDestinations().size());
        Destination destination = Destination.find.byId(3);
        for(Visit visit : destination.getVisits()){
            visit.delete();
        }
        destination.setTravellerTypes(new ArrayList<>());
        destination.update();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(2, User.find.byId(2).getDestinations().size());
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
        assertEquals(SEE_OTHER, result.status());
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
        assertEquals(SEE_OTHER, result2.status());
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
        assertEquals(SEE_OTHER, result.status());
        //A different user of user id 3 uses the destination in their trip
        Trip trip = new Trip("testTrip", true, User.find.byId(3));
        trip.save();
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/2").session("connected", "3");
        result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
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
        assertEquals(SEE_OTHER, result.status());

        Trip trip = new Trip("testTrip", true, User.find.byId(2));
        trip.save();
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/3").session("connected", "2");
        result = route(app, request);
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
        assertEquals(SEE_OTHER, result2.status());
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
        JSONObject obj = new JSONObject(contentAsString(result));
        assertEquals("The Wok", obj.getString("destName"));
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/1").session("connected", "3");
        result = route(app, request);
        assertEquals(OK, result.status());
        obj = new JSONObject(contentAsString(result));
        assertEquals("Christchurch", obj.getString("destName"));
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
        JSONObject obj = new JSONObject(contentAsString(result));
        assertEquals("The Wok", obj.getString("destName"));
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
        JSONObject obj = new JSONObject(contentAsString(result));
        assertEquals("Wellington", obj.getString("destName"));
    }

    @Test
    public void getDestinationTravellerTypes(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/ttypes/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        JSONArray jsonArray = new JSONArray(contentAsString(result));
        //Groupie and gap year so length 2
        assertEquals(2, jsonArray.length());
        JSONObject obj1 = jsonArray.getJSONObject(0);
        JSONObject obj2 = jsonArray.getJSONObject(1);
        assertEquals("Groupie", obj1.getString("travellerTypeName"));
        assertEquals("Gap Year",obj2.getString("travellerTypeName"));
    }
}