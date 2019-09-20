package controllers;

import accessors.TagAccessor;
import accessors.TripAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import play.libs.Json;
import factories.TripFactory;
import factories.VisitFactory;
import formdata.VisitFormData;
import models.*;
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
import testhelpers.BaseTestWithApplicationAndDatabase;
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

public class TripControllerTest extends BaseTestWithApplicationAndDatabase {

    private VisitFactory visitfactory = new VisitFactory();

    /**
     * Unit test for trip creation page
     */
    @Test
    public void createtrip() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/create").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/create").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Unit test for trip creation request
     * */
    @Test
    public void saveTrip() {
        //User with id 2 should have two trips
        assertEquals(2, User.find().byId(2).getTrips().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the trip name form as "triptest123"
        formData.put("tripName", "triptest123");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/create").session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the create profile page
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should have three trips
        assertEquals(3, User.find().byId(2).getTrips().size());
        //Trip with name "triptest123" should be the user's third trip
        assertEquals("triptest123", User.find().byId(2).getTrips().get(2).getTripName());
    }

    /**
     * Unit test for trip creation request
     * */
    @Test
    public void saveTripWithDuplicateName() {
        //User with id 2 should have two trips
        assertEquals(2, User.find().byId(2).getTrips().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the trip name form as "Trip to New Zealand", which already exists
        formData.put("tripName", "Trip to New Zealand");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/create").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User with id 2 should still have two trips
        assertEquals(2, User.find().byId(2).getTrips().size());
    }

    @Test
    public void saveTripWithEmptyName() {
        //User with id 2 should have two trips
        assertEquals(2, User.find().byId(2).getTrips().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the trip name form as "Trip to New Zealand", which already exists
        formData.put("tripName", "");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/create").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should still have two trips
        assertEquals(2, User.find().byId(2).getTrips().size());
    }

    /**
     * Unit test for trip creation request
     * */
    @Test
    public void saveTripWithInvalidLoginSession() {
        Map<String, String> formData = new HashMap<>();
        formData.put("tripName", "Trip to New Zealand");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/create").session("connected", null);
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User with id 2 should still have two trips
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void loadEditVisitPageWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/visit/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void loadEditVisitPageWithLoginSessionAndUserIsTripOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/visit/edit/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void loadEditVisitPageWithLoginSessionAndUserIsNotTripOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/visit/edit/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updateVisitWithInvalidLoginSession(){
        //Update the first visit from Trip to New Zealand from Christchurch to The Wok.
        Map<String, String> formData = new HashMap<>();
        formData.put("destination", "3");
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", null);
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
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
    public void updateVisitWithValidLoginSessionWithValidOwner(){
        Visit visit = Visit.find().byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        //Update the first visit from Trip to New Zealand from Christchurch to The Wok.
        Map<String, String> formData = new HashMap<>();
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        visit = Visit.find().byId(1);
        assertEquals(SEE_OTHER, result.status());
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2019-04-20", visit.getArrival());
        assertEquals("2019-06-09", visit.getDeparture());
    }

    @Test
    public void updateVisitWithValidLoginSessionWithValidOwnerWithNoArrivalOrDepartureDate(){
        Visit visit = Visit.find().byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        //Update the first visit from Trip to New Zealand from Christchurch to The Wok.
        Map<String, String> formData = new HashMap<>();
        formData.put("arrival", "");
        formData.put("departure", "");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        visit = Visit.find().byId(1);
        assertEquals(SEE_OTHER, result.status());
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("", visit.getArrival());
        assertEquals("", visit.getDeparture());
    }

    @Test
    public void updateVisitWithValidLoginSessionWithValidOwnerThatResultsInRepeatDestination(){
        Visit visit = Visit.find().byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2018-05-04", visit.getArrival());
        assertEquals("2018-05-06", visit.getDeparture());
        //Update the first visit from Trip to New Zealand from Christchurch to Wellington
        Map<String, String> formData = new HashMap<>();
        formData.put("arrival", "2019-04-20");
        formData.put("departure", "2019-06-09");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/visit/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        visit = Visit.find().byId(1);
        assertEquals("Trip to New Zealand", visit.getTrip().getTripName());
        assertEquals("Christchurch", visit.getDestination().getDestName());
        assertEquals("2019-04-20", visit.getArrival());
        assertEquals("2019-06-09", visit.getDeparture());
    }

    @Test
    public void checkAddVistToTripJSRequestNoUserSignedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/trips/1/addVisit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

   @Test
   public void checkAddVistToTripJSRequestTripIsNotFound() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/trips/100000/addVisit/1").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
   }

    @Test
    public void checkAddVistToTripJSRequestDestinationIsNotFound() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/trips/1/addVisit/10000000").session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void checkAddVistToTripJSRequestNotTripOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/trips/6/addVisit/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void checkAddVistToTripJSRequestNotDestinationOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/trips/1/addVisit/5").session("connected", "1");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void checkAddVistToTripJSRequestOK() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/trips/1/addVisit/1").session("connected", "2");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


    /**
     * Unit test for edit trip page
     */
    @Test
    public void displayTripWithInvalidLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void displayTripWithValidLoginSessionAndInvalidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("displayTrip.scala.html"));
    }

    @Test
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
    public void deleteVisitFromNewTrip() {
        Trip trip = new Trip("test", true, User.find().byId(1));
        trip.save();
        String arrival = "2019-04-20";
        String departure = "2019-06-09";
        //University of Canterbury, testTrip, visitOrder = 1
        VisitFormData visitformdata = new VisitFormData(arrival, departure);
        Visit visit = visitfactory.createVisit(visitformdata, Destination.find().byId(1), User.find().byId(1).getTrips().get(0), 1 );
        visit.save();
        //There should be 1 row in trips, which is the visit that was put in.
        assertEquals(1, User.find().byId(1).getTrips().get(0).getVisits().size());
        Map<String, String> formData = new HashMap<>();
        //visitID of the visit that was just put in should be 18 and trip id should be 7
        //formData.put("visitid", "18");
        //Add the visit to the auto-generated trip of ID 1 belonging to user of ID 1.
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/18").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(OK, result.status());
        //User should be redirected to the edit profile page
        //Newly created visited should have been deleted, so the size of the trip's visits should be 0.
        assertEquals(0, User.find().byId(1).getTrips().get(0).getVisits().size());
    }

    @Test
    public void deleteVisitFromExistingTripWithValidOwner(){
        assertEquals(4, Trip.find().byId(2).getVisits().size());
        //visit of id 5 is in this trip
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/5").session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(OK, result.status());
        assertEquals(3, Trip.find().byId(2).getVisits().size());
    }

    @Test
    public void deleteVisitFromExistingTripWithInvalidOwner(){
        assertEquals(4, Trip.find().byId(2).getVisits().size());
        //visit of id 5 is in this trip
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/5").session("connected", "3");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(4, Trip.find().byId(2).getVisits().size());
    }

    @Test
    public void deleteVisitFromExistingTripWithInvalidLoginSession(){
        assertEquals(4, Trip.find().byId(2).getVisits().size());
        //visit of id 5 is in this trip
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/5").session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, Trip.find().byId(2).getVisits().size());
    }

    @Test
    public void deleteVisitWhichCausesRepeatDestinations(){
        assertEquals(3, Trip.find().byId(4).getVisits().size());
        //visit of id 11 is in this trip. Deleting it will result in Pyramid -> Pyramid which is illegal.
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.DELETE).uri("/users/trips/edit/11").session("connected", "3");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());
        assertEquals(3, Trip.find().byId(4).getVisits().size());
    }


    @Test
    public void addTripDestinationsWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void addTripDestinationsWithValidLoginSessionWithInvalidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void addTripDestinationsWithValidLoginSessionWithValidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void addTripDestinationsWithInvalidTrip(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/addDestinations/420").session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void addVisitFromTableWithInvalidLoginSession(){
        assertEquals(2, Trip.find().byId(1).getVisits().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(2, Trip.find().byId(1).getVisits().size());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithInvalidOwner(){
        assertEquals(2, Trip.find().byId(1).getVisits().size());
        assertFalse(Destination.find().byId(5).getIsPublic());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/5").session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(2, Trip.find().byId(1).getVisits().size());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithValidOwner(){
        assertEquals(4, Trip.find().byId(2).getVisits().size());
        assertFalse(Destination.find().byId(2).getIsPublic());
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/2/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(5, Trip.find().byId(2).getVisits().size());
        //5th visit should be the newly added one (Wellington)
        assertEquals("Wellington", Trip.find().byId(2).getVisits().get(4).getVisitName());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPublicDestinationWithValidOwnerRepeatDestination(){
        assertEquals(4, Trip.find().byId(2).getVisits().size());
        assertTrue(Destination.find().byId(1).getIsPublic());
        //add Christchurch to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/2/1").session("connected", "2");
        Result result = route(app, request);
        //User should be redirected to the same page
        assertEquals(SEE_OTHER, result.status());
        //Visit should not be added
        assertEquals(4, Trip.find().byId(2).getVisits().size());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPublicDestinationWithValidOwner(){
        assertEquals(2, Trip.find().byId(1).getVisits().size());
        assertTrue(Destination.find().byId(1).getIsPublic());
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/1/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(3, Trip.find().byId(1).getVisits().size());
        //3rd visit should be the newly added one (Christchurch)
        assertEquals("Christchurch", Trip.find().byId(1).getVisits().get(2).getVisitName());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPublicDestinationWithInvalidOwner(){
        assertEquals(3, Trip.find().byId(3).getVisits().size());
        assertTrue(Destination.find().byId(1).getIsPublic());
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, Trip.find().byId(3).getVisits().size());
        //4th visit of World Tour should be the newly added one (Christchurch)
        assertEquals("Christchurch", Trip.find().byId(3).getVisits().get(3).getVisitName());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithInvalidDestination(){
        assertEquals(3, Trip.find().byId(3).getVisits().size());
        assertNull(Destination.find().byId(100));
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/100").session("connected", "3");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
        assertEquals(3, Trip.find().byId(3).getVisits().size());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithValidDestinationOwnerWithInvalidTrip(){
        assertEquals(3, Trip.find().byId(5).getVisits().size());
        assertFalse(Destination.find().byId(2).getIsPublic());
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/5/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(3, Trip.find().byId(5).getVisits().size());
    }

    @Test
    public void addVisitFromTableWithValidLoginSessionWithPrivateDestinationWithAdmin(){
        assertEquals(4, Trip.find().byId(2).getVisits().size());
        assertFalse(Destination.find().byId(2).getIsPublic());
        //add Wellington to Christchurch to Wellington, to The Wok and back
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/2/2").session("connected", "1");
        Result result = route(app, request);

        assertEquals(SEE_OTHER, result.status());
        assertEquals(5, Trip.find().byId(2).getVisits().size());
        //5th visit should be the newly added one (Wellington)
        assertEquals("Wellington", Trip.find().byId(2).getVisits().get(4).getVisitName());
    }

    @Test
    public void swapVisitsWithValidSwap(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find().byId(2);
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
        trip = Trip.find().byId(2);
        //2nd and third index should be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    public void swapVisitsWithInvalidSwapThatResultsInRepeatDestinations(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find().byId(2);
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
        trip = Trip.find().byId(2);
        //2nd and third index should not be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    public void swapVisitsWithValidSwapWithInvalidTripOwner(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find().byId(2);
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
        trip = Trip.find().byId(2);
        //2nd and third index should not be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    public void swapVisitsWithValidSwapWithInvalidLoginSession(){
        //Christchurch to Wellington to the Wok and back
        Trip trip = Trip.find().byId(2);
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
        assertEquals(SEE_OTHER, result.status());
        trip = Trip.find().byId(2);
        //2nd and third index should not be swapped
        assertEquals(visit1.getVisitid(), trip.getOrderedVisits().get(0).getVisitid());
        assertEquals(visit2.getVisitid(), trip.getOrderedVisits().get(1).getVisitid());
        assertEquals(visit3.getVisitid(), trip.getOrderedVisits().get(2).getVisitid());
        assertEquals(visit4.getVisitid(), trip.getOrderedVisits().get(3).getVisitid());
    }

    @Test
    public void deleteTripWithLoginSessionWithValidOwner(){
        Trip trip = Trip.find().byId(2);
        assertNotNull(trip);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/trips/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        trip = Trip.find().byId(2);
        assertNull(trip);
    }

    @Test
    public void deleteTripWithLoginSessionWithInvalidOwner(){
        Trip trip = Trip.find().byId(2);
        assertNotNull(trip);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/trips/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        trip = Trip.find().byId(2);
        assertNotNull(trip);
    }

    @Test
    public void deleteTripWithLoginSessionWithAdmin(){
        Trip trip = Trip.find().byId(2);
        assertNotNull(trip);
        assertFalse(trip.getUser().getUserid() == 1);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/trips/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        trip = Trip.find().byId(2);
        assertNull(trip);
    }

    @Test
    public void deleteTripWithInvalidLoginSession(){
        Trip trip = Trip.find().byId(2);
        assertNotNull(trip);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/trips/2").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        trip = Trip.find().byId(2);
        assertNotNull(trip);
    }

    @Test
    public void deleteTripWithValidLoginSessionWithInvalidTrip(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/trips/10").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void getUserTripLengths(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/userTrips").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
    
    @Test
    public void getTripPhoto() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/2/tripPicture").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void getTripPhotoNotFound() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/15/tripPicture").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void getTripsByNameOne () {
        String urlFormat = "/users/trips/matching/Trip?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 1);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        JsonNode json = Json.parse(contentAsString(result));
        JsonNode trips = json.get("trips");

        assertEquals(1, trips.size());
        assertEquals(OK, result.status());
    }

    @Test
    public void getPaginatedUserTripsOne () {
        String url = "/users/trips/paginated/0/1";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        JsonNode json = Json.parse(contentAsString(result));
        JsonNode trips = json.get("trips");

        assertEquals(1, trips.size());
        assertEquals(OK, result.status());
    }

    @Test
    public void getPaginatedUserTripsMany () {
        String url = "/users/trips/paginated/0/5";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        JsonNode json = Json.parse(contentAsString(result));
        JsonNode trips = json.get("trips");
        Integer numTrips = UserAccessor.getById(2).getTrips().size();
        assertTrue(trips.size() <= numTrips);
        assertTrue(trips.size() <= 5);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPaginatedUserTripsNone () {
        String url = "/users/trips/paginated/0/0";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        JsonNode json = Json.parse(contentAsString(result));
        JsonNode trips = json.get("trips");

        assertEquals(0, trips.size());
        assertEquals(OK, result.status());
    }

    @Test
    public void getPaginatedUserTripsTotalCount () {
        String url = "/users/trips/paginated/0/5";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        JsonNode json = Json.parse(contentAsString(result));
        JsonNode count = json.get("tripCount");

        assertTrue(0 < count.asInt());
        assertEquals(OK, result.status());
    }

    @Test
    public void getTripsAsJson () {
        String url = "/users/trips/1/asJson";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        JsonNode json = Json.parse(contentAsString(result));
        Integer retrievedTripId = json.get("tripId").asInt();
        assertTrue(retrievedTripId == 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void getTripsAsJsonForbidden () {
        String url = "/users/trips/1/asJson";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "3");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getTripsAsJsonBadRequest () {
        String url = "/users/trips/-1/asJson";

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }
}