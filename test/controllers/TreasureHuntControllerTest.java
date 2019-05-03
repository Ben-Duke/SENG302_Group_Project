package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import factories.TreasureHuntFactory;
import factories.TripFactory;
import factories.VisitFactory;
import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.User;
import models.Visit;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import utilities.TestDatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;

public class TreasureHuntControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;


    TreasureHuntFactory treasureHuntFactory = new TreasureHuntFactory();
    VisitFactory visitfactory = new VisitFactory();
    /**
     * Sets up the fake database before each test
     */
    @Before
    public void setupDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    /**
     * Unit test for treasure hunt creation page
     */
    @Test
    public void createTreasureHunt() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/create").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/create").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Unit test for trip creation request
     * */
    @Test
    public void createAndSaveTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should have two trips
        assertEquals(2, User.find.byId(2).getTreasureHunts().size());
        //The treasure hunt with the title "test123" should be the user's second treasure hunt
        assertEquals("test123", User.find.byId(2).getTreasureHunts().get(1).getTitle());
    }

    @Test
    public void createAndSaveTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        //User should not be authorized.
        assertEquals(UNAUTHORIZED, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    @Test
    public void createAndSaveTreasureHuntWithErrors() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the create treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    @Test
    public void createAndSaveTreasureHuntWithDateError() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2002-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the create treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    @Test
    public void createAndSaveTreasureHuntWithDuplicateTitle() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", "Surprise");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the create treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Unit test for treasure hunt creation page
     * edit/save/:id
     */
    @Test
    public void editTreasureHunt() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/edit/1").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Unit test for trip creation request
     * */
    @Test
    public void editAndSaveTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();

        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", "test123");
        formData.put("riddle", "The garden city");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2019-04-17");
        formData.put("endDate", "2019-12-25");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        assertEquals("test123", User.find.byId(2).getTreasureHunts().get(0).getTitle());
    }

    @Test
    @Ignore
    public void editAndSaveTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        //User should not be authorized.
        assertEquals(UNAUTHORIZED, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    @Test
    @Ignore
    public void editAndSaveTreasureHuntWithErrors() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    @Test
    @Ignore
    public void editAndSaveTreasureHuntWithDateError() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2002-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    @Test
    @Ignore
    public void editAndSaveTreasureHuntWithDuplicateTitle() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", "Surprise");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }


    @Test
    @Ignore
    public void loadEditVisitPageWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/visit/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void loadEditVisitPageWithLoginSessionAndUserIsTripOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/visit/edit/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    @Ignore
    public void loadEditVisitPageWithLoginSessionAndUserIsNotTripOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/visit/edit/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void updateVisitWithInvalidLoginSession(){
        //Update the first visit from Trip to New Zealand from Christchurch to The Wok.
        Map<String, String> formData = new HashMap<>();
        formData.put("destination", "3");
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", null);
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void updateVisitWithValidLoginSessionWithInvalidOwner(){
        Map<String, String> formData = new HashMap<>();
        formData.put("destination", "4");
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "3");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void updateVisitWithValidLoginSessionWithValidOwner(){
        Visit visit = Visit.find.byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        //Update the first visit from Trip to New Zealand from Christchurch to The Wok.
        Map<String, String> formData = new HashMap<>();
        formData.put("destination", "3");
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        visit = Visit.find.byId(1);
        assertEquals(SEE_OTHER, result.status());
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("The Wok", visit.getDestination().getDestName());
        assertEquals("2019-04-20", visit.getArrival());
        assertEquals("2019-06-09", visit.getDeparture());
    }

    @Test
    @Ignore
    public void updateVisitWithValidLoginSessionWithValidOwnerWithNoArrivalOrDepartureDate(){
        Visit visit = Visit.find.byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        //Update the first visit from Trip to New Zealand from Christchurch to The Wok.
        Map<String, String> formData = new HashMap<>();
        formData.put("destination", "3");
        formData.put("arrival", "");
        formData.put("departure", "");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        visit = Visit.find.byId(1);
        assertEquals(SEE_OTHER, result.status());
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("The Wok", visit.getDestination().getDestName());
        assertEquals("", visit.getArrival());
        assertEquals("", visit.getDeparture());
    }

    @Test
    @Ignore
    public void updateVisitWithValidLoginSessionWithValidOwnerThatResultsInRepeatDestination(){
        Visit visit = Visit.find.byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        //Update the first visit from Trip to New Zealand from Christchurch to Wellington
        Map<String, String> formData = new HashMap<>();
        formData.put("destination", "2");
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        visit = Visit.find.byId(1);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
    }

    /**
     * Unit test for adding a visit to a trip request
     * TO ADD: VALIDATION FOR BACK TO BACK OF THE SAME VISITS ADD (might want to refactor first though?)
     */
//    @Test
//    public void addvisit() {
//        Map<String, String> formData = new HashMap<>();
//        String arrival = "2019-04-20";
//        String departure = "2019-06-09";
//        //VisitFormData visitformdata = new VisitFormData(Destination.find.byId(1).getDestName(), arrival, departure, Trip.find.byId(1).tripName);
////        Visit visit = visitfactory.createVisit(visitformdata, Destination.find.byId(1), Trip.find.byId(1), 3 );
////        visit.save();
//        //Christchurch
//        formData.put("destName", Destination.find.byId(1).getDestName());
//        formData.put("arrival", arrival);
//        formData.put("departure", departure);
//        formData.put("visitName", Destination.find.byId(1).getDestName());
//        //Add the visit to the auto-generated trip of ID 1 belonging to user of ID 2.
//        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/edit/1").session("connected", "2");
//        CSRFTokenHelper.addCSRFToken(fakeRequest);
//        Result result = Helpers.route(app, fakeRequest);
//        assertEquals(SEE_OTHER, result.status());
//        //User should be redirected to the edit trip page
//        //"Newly created Visit with name "Christchurch" should be the third index in the trip
//        assertEquals("Christchurch", User.find.byId(2).getTrips().get(0).getVisits().get(2).getVisitName());
//        assertEquals("2019-04-20", User.find.byId(2).getTrips().get(0).getVisits().get(2).getArrival());
//        assertEquals("2019-06-09", User.find.byId(2).getTrips().get(0).getVisits().get(2).getDeparture());
//    }

    /**
     * Unit test for edit trip page
     */
    @Test
    @Ignore
    public void displayTripWithInvalidLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void displayTripWithValidLoginSessionAndInvalidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("displayTrip.scala.html"));
    }

    @Test
    @Ignore
    public void displayTripWithValidLoginSessionAndValidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("AddTripDestinationsTable.scala.html"));
    }

    /**
     * Unit test for deleting a visit from a new trip
     * TO ADD: VALIDATION FOR BACK TO BACK OF THE SAME VISITS REMOVE (might want to refactor first though?)
     */
    @Test
    @Ignore
    public void deleteVisitFromNewTrip() {
        Trip trip = new Trip("test", true, User.find.byId(1));
        trip.save();
        String arrival = "2019-04-20";
        String departure = "2019-06-09";
        //University of Canterbury, testTrip, visitOrder = 1
        VisitFormData visitformdata = new VisitFormData(Destination.find.byId(1).getDestName(), arrival, departure, Trip.find.byId(1).tripName);
        Visit visit = visitfactory.createVisit(visitformdata, Destination.find.byId(1), User.find.byId(1).getTrips().get(0), 1 );
        visit.save();
        //There should be 1 row in trips, which is the visit that was put in.
        assertEquals(1, User.find.byId(1).getTrips().get(0).getVisits().size());
        Map<String, String> formData = new HashMap<>();
        //visitID of the visit that was just put in should be 18 and trip id should be 7
        //formData.put("visitid", "18");
        //Add the visit to the auto-generated trip of ID 1 belonging to user of ID 1.
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/18").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(OK, result.status());
        //User should be redirected to the edit profile page
        //Newly created visited should have been deleted, so the size of the trip's visits should be 0.
        assertEquals(0, User.find.byId(1).getTrips().get(0).getVisits().size());
    }

    @Test
    @Ignore
    public void deleteVisitFromExistingTripWithValidOwner(){
        assertEquals(4, Trip.find.byId(2).getVisits().size());
        //visit of id 5 is in this trip
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/5").session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(OK, result.status());
        assertEquals(3, Trip.find.byId(2).getVisits().size());
    }

    @Test
    @Ignore
    public void deleteVisitFromExistingTripWithInvalidOwner(){
        assertEquals(4, Trip.find.byId(2).getVisits().size());
        //visit of id 5 is in this trip
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/5").session("connected", "3");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(4, Trip.find.byId(2).getVisits().size());
    }

    @Test
    @Ignore
    public void deleteVisitFromExistingTripWithInvalidLoginSession(){
        assertEquals(4, Trip.find.byId(2).getVisits().size());
        //visit of id 5 is in this trip
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/5").session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(4, Trip.find.byId(2).getVisits().size());
    }

    @Test
    @Ignore
    public void deleteVisitWhichCausesRepeatDestinations(){
        assertEquals(3, Trip.find.byId(4).getVisits().size());
        //visit of id 11 is in this trip. Deleting it will result in Pyramid -> Pyramid which is illegal.
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/11").session("connected", "3");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(3, Trip.find.byId(4).getVisits().size());
    }


    @Test
    @Ignore
    public void addTripDestinationsWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void addTripDestinationsWithValidLoginSessionWithInvalidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    @Ignore
    public void addTripDestinationsWithValidLoginSessionWithValidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    @Ignore
    public void addTripDestinationsWithInvalidTrip(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/420").session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithInvalidLoginSession(){
        assertEquals(2, Trip.find.byId(1).getVisits().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(2, Trip.find.byId(1).getVisits().size());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithInvalidOwner(){
        assertEquals(2, Trip.find.byId(1).getVisits().size());
        assertFalse(Destination.find.byId(5).isPublic);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/5").session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(2, Trip.find.byId(1).getVisits().size());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithValidOwner(){
        assertEquals(4, Trip.find.byId(2).getVisits().size());
        assertFalse(Destination.find.byId(2).isPublic);
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/2/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(5, Trip.find.byId(2).getVisits().size());
        //5th visit should be the newly added one (Wellington)
        assertEquals("Wellington", Trip.find.byId(2).getVisits().get(4).getVisitName());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPublicDestinationWithValidOwnerRepeatDestination(){
        assertEquals(4, Trip.find.byId(2).getVisits().size());
        assertTrue(Destination.find.byId(1).isPublic);
        //add Christchurch to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/2/1").session("connected", "2");
        Result result = route(app, request);
        //User should be redirected to the same page
        assertEquals(SEE_OTHER, result.status());
        //Visit should not be added
        assertEquals(4, Trip.find.byId(2).getVisits().size());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPublicDestinationWithValidOwner(){
        assertEquals(2, Trip.find.byId(1).getVisits().size());
        assertTrue(Destination.find.byId(1).isPublic);
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(3, Trip.find.byId(1).getVisits().size());
        //3rd visit should be the newly added one (Christchurch)
        assertEquals("Christchurch", Trip.find.byId(1).getVisits().get(2).getVisitName());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPublicDestinationWithInvalidOwner(){
        assertEquals(3, Trip.find.byId(3).getVisits().size());
        assertTrue(Destination.find.byId(1).isPublic);
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, Trip.find.byId(3).getVisits().size());
        //4th visit of World Tour should be the newly added one (Christchurch)
        assertEquals("Christchurch", Trip.find.byId(3).getVisits().get(3).getVisitName());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithInvalidDestination(){
        assertEquals(3, Trip.find.byId(3).getVisits().size());
        assertNull(Destination.find.byId(100));
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/100").session("connected", "3");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
        assertEquals(3, Trip.find.byId(3).getVisits().size());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithValidDestinationOwnerWithInvalidTrip(){
        assertEquals(3, Trip.find.byId(5).getVisits().size());
        assertFalse(Destination.find.byId(2).isPublic);
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/5/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(3, Trip.find.byId(5).getVisits().size());
    }

    @Test
    @Ignore
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithAdmin(){
        assertEquals(4, Trip.find.byId(2).getVisits().size());
        assertFalse(Destination.find.byId(2).isPublic);
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/2/2").session("connected", "1");
        Result result = route(app, request);
        System.out.println(contentAsString(result));
        assertEquals(SEE_OTHER, result.status());
        assertEquals(5, Trip.find.byId(2).getVisits().size());
        //5th visit should be the newly added one (Wellington)
        assertEquals("Wellington", Trip.find.byId(2).getVisits().get(4).getVisitName());
    }

    @Test
    @Ignore
    public void swapVisitsWithValidSwap(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find.byId(2);
        Visit visit1 = trip.getOrderedVisits().get(0);
        Visit visit2 = trip.getOrderedVisits().get(1);
        Visit visit3 = trip.getOrderedVisits().get(2);
        Visit visit4 = trip.getOrderedVisits().get(3);
        ArrayList<String> swappedVisitsList = new ArrayList<>();
        //Swap visit3 and visit2
        swappedVisitsList.add(Integer.toString(visit1.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit3.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit2.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit4.getVisitid()));
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.valueToTree(swappedVisitsList);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(array)
                .uri("/users/trips/edit/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        trip = Trip.find.byId(2);
        //2nd and third index should be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    @Ignore
    public void swapVisitsWithInvalidSwapThatResultsInRepeatDestinations(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find.byId(2);
        Visit visit1 = trip.getOrderedVisits().get(0);
        Visit visit2 = trip.getOrderedVisits().get(1);
        Visit visit3 = trip.getOrderedVisits().get(2);
        Visit visit4 = trip.getOrderedVisits().get(3);
        ArrayList<String> swappedVisitsList = new ArrayList<>();
        //Swap visit2 and visit4 which results in visit 1 and visit 4 both being Christchurch which is invalid (repeat destination)
        swappedVisitsList.add(Integer.toString(visit1.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit4.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit2.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit3.getVisitid()));
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.valueToTree(swappedVisitsList);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(array)
                .uri("/users/trips/edit/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        trip = Trip.find.byId(2);
        //2nd and third index should not be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    @Ignore
    public void swapVisitsWithValidSwapWithInvalidTripOwner(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find.byId(2);
        Visit visit1 = trip.getOrderedVisits().get(0);
        Visit visit2 = trip.getOrderedVisits().get(1);
        Visit visit3 = trip.getOrderedVisits().get(2);
        Visit visit4 = trip.getOrderedVisits().get(3);
        ArrayList<String> swappedVisitsList = new ArrayList<>();
        //Swap visit3 and visit2
        swappedVisitsList.add(Integer.toString(visit1.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit3.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit2.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit4.getVisitid()));
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.valueToTree(swappedVisitsList);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(array)
                .uri("/users/trips/edit/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        trip = Trip.find.byId(2);
        //2nd and third index should not be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    @Ignore
    public void swapVisitsWithValidSwapWithInvalidLoginSession(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find.byId(2);
        Visit visit1 = trip.getOrderedVisits().get(0);
        Visit visit2 = trip.getOrderedVisits().get(1);
        Visit visit3 = trip.getOrderedVisits().get(2);
        Visit visit4 = trip.getOrderedVisits().get(3);
        ArrayList<String> swappedVisitsList = new ArrayList<>();
        //Swap visit3 and visit2
        swappedVisitsList.add(Integer.toString(visit1.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit3.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit2.getVisitid()));
        swappedVisitsList.add(Integer.toString(visit4.getVisitid()));
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode array = mapper.valueToTree(swappedVisitsList);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(array)
                .uri("/users/trips/edit/2").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        trip = Trip.find.byId(2);
        //2nd and third index should not be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    @Ignore
    public void cancelTripWithLoginSessionWithValidOwner(){
        Trip trip = Trip.find.byId(2);
        assertNotNull(trip);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/cancel/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        trip = Trip.find.byId(2);
        assertNull(trip);
    }

    @Test
    @Ignore
    public void cancelTripWithLoginSessionWithInvalidOwner(){
        Trip trip = Trip.find.byId(2);
        assertNotNull(trip);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/cancel/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        trip = Trip.find.byId(2);
        assertNotNull(trip);
    }

    @Test
    @Ignore
    public void cancelTripWithLoginSessionWithAdmin(){
        Trip trip = Trip.find.byId(2);
        assertNotNull(trip);
        assertFalse(trip.getUser().getUserid() == 1);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/cancel/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        trip = Trip.find.byId(2);
        assertNull(trip);
    }

    @Test
    @Ignore
    public void cancelTripWithInvalidLoginSession(){
        Trip trip = Trip.find.byId(2);
        assertNotNull(trip);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/cancel/2").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        trip = Trip.find.byId(2);
        assertNotNull(trip);
    }

    @Test
    @Ignore
    public void cancelTripWithValidLoginSessionWithInvalidTrip(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/cancel/10").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }
}