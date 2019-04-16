package controllers;

import factories.TripFactory;
import factories.VisitFactory;
import formdata.TripFormData;
import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.User;
import models.Visit;
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
import utilities.TestDatabaseManager;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class TripControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;


    TripFactory tripfactory = new TripFactory();
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
        //Initialises a test user with name "testUser" and saves it to the database.
//        User user = new User("testUser");
//        user.save();
//        //Initialises a test trip with name "testTrip" and saves it to the database.
//        TripFormData tripForm = new TripFormData("testTrip", user);
//        int tripid = tripfactory.createTrip(tripForm, user);
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
//                user);
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

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    /**
     * Unit test for trip creation page
     */
    @Test
    public void createtrip() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/create").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
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
    public void savetrip() {
        //User with id 1 should have no trips
        assertEquals(0, User.find.byId(1).getTrips().size());
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the trip name form as "triptest123"
        formData.put("tripName", "triptest123");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/create").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the create profile page
        assertEquals(SEE_OTHER, result.status());
        //User with id 1 should have one trip
        assertEquals(1, User.find.byId(1).getTrips().size());
        //Trip with name "triptest123" should be the user's first trip
        assertEquals("triptest123", User.find.byId(1).getTrips().get(0).getTripName());
    }

    /**
     * Unit test for adding a visit to a trip request
     * TO ADD: VALIDATION FOR BACK TO BACK OF THE SAME VISITS ADD (might want to refactor first though?)
     */
    @Test
    public void addvisit() {
        Map<String, String> formData = new HashMap<>();
        String arrival = "2019-04-20";
        String departure = "2019-06-09";
        //VisitFormData visitformdata = new VisitFormData(Destination.find.byId(1).getDestName(), arrival, departure, Trip.find.byId(1).tripName);
//        Visit visit = visitfactory.createVisit(visitformdata, Destination.find.byId(1), Trip.find.byId(1), 3 );
//        visit.save();
        //Christchurch
        formData.put("destName", Destination.find.byId(1).getDestName());
        formData.put("arrival", arrival);
        formData.put("departure", departure);
        formData.put("visitName", Destination.find.byId(1).getDestName());
        //Add the visit to the auto-generated trip of ID 1 belonging to user of ID 2.
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/edit/1").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(SEE_OTHER, result.status());
        //User should be redirected to the edit trip page
        //"Newly created Visit with name "Christchurch" should be the third index in the trip
        assertEquals("Christchurch", User.find.byId(2).getTrips().get(0).getVisits().get(2).getVisitName());
        assertEquals("2019-04-20", User.find.byId(2).getTrips().get(0).getVisits().get(2).getArrival());
        assertEquals("2019-06-09", User.find.byId(2).getTrips().get(0).getVisits().get(2).getDeparture());
    }

    /**
     * Unit test for edit trip page
     */
    @Test
    public void edittripWithInvalidLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void edittripWithValidLoginSessionAndInvalidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/edit/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void edittripWithValidLoginSessionAndValidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/edit/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Unit test for deleting a visit from a trip
     * TO ADD: VALIDATION FOR BACK TO BACK OF THE SAME VISITS REMOVE (might want to refactor first though?)
     */
    @Test
    public void deletevisit() {
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

    /**
     * Unit test for swapping two visits in a trip request
     * TEMPORARILY BLOCKED DUE TO NOT KNOWING MAKE FAKE AJAX REQUESTS
     */
    /*
    @Test
    public void swapvisits() {
//        String arrival1 = "2019-04-20";
//        String departure1 = "2019-06-09";
//        String arrival2 = "2018-04-20";
//        String departure2 = "2018-06-09";
//        //University of Canterbury, testTrip, visitOrder = 1
//        VisitFormData visitformdata1 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
//        Visit visit1 = Visit.makeInstance(visitformdata1, Destination.find.byId(1), Trip.find.byId(1), 1 );        visit1.save();
//        //University of Banterbury, testTrip, visitOrder = 2
//        VisitFormData visitformdata2 = new VisitFormData(Destination.find.byId(2).getDestName(), arrival2, departure2, Trip.find.byId(1).tripName);
//        Visit visit2 = Visit.makeInstance(visitformdata2, Destination.find.byId(2), Trip.find.byId(1), 2 );        visit2.save();
//        visit2.save();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String arrival1 = "2019-04-20";
        String departure1 = "2019-06-09";
        String arrival2 = "2018-04-20";
        String departure2 = "2018-06-09";
        //University of Canterbury, testTrip, visitOrder = 1
        VisitFormData visitformdata1 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit1 = visitfactory.createVisit(visitformdata1, Destination.find.byId(1), Trip.find.byId(1), 1 );
        visit1.save();
        //University of Banterbury, testTrip, visitOrder = 2
        VisitFormData visitformdata2 = new VisitFormData(Destination.find.byId(2).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit2 = visitfactory.createVisit(visitformdata2, Destination.find.byId(2), Trip.find.byId(1), 2 );
        visit2.save();
        visit2.save();
        //University of Canterbury should be on the first row and University of Banterbury should be on the second row before swap
        List<Visit> visitsBeforeSwap = User.find.byId(1).getTrips().get(0).getVisits();
        visitsBeforeSwap.sort(Comparator.comparing(Visit::getVisitOrder));
        assertEquals("University of Canterbury", visitsBeforeSwap.get(0).getVisitName());
        assertEquals("University of Banterbury", visitsBeforeSwap.get(1).getVisitName());
        Map<String, String> formData = new HashMap<>();
        //visitID of the first visit should be 1
        formData.put("visitid1", "1");
        //visitID of the second visit should be 2
        formData.put("visitid2", "2");
        //Swap the visits
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/trips/edit/1/swap").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit trip page
        assertEquals(SEE_OTHER, result.status());
        //University of Banterbury should be on the first row and University of Canterbury should be on the second row after swap
        List<Visit> visitsAfterSwap = User.find.byId(1).getTrips().get(0).getVisits();
        visitsAfterSwap.sort(Comparator.comparing(Visit::getVisitOrder));
        assertEquals("University of Banterbury", visitsAfterSwap.get(0).getVisitName());
        assertEquals("University of Canterbury", visitsAfterSwap.get(1).getVisitName());
    }
*/
    /**
     * Unit tests for method to detect repeat destinations. (Returns true if repeat destination is detected, false otherwise)
     */
    /*
    @Test
    public void hasRepeatDest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String arrival1 = "2019-04-20";
        String departure1 = "2019-06-09";
        String arrival2 = "2018-04-20";
        String departure2 = "2018-06-09";
        //University of Canterbury, testTrip, visitOrder = 1
        VisitFormData visitformdata1 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit1 = visitfactory.createVisit(visitformdata1, Destination.find.byId(1), Trip.find.byId(1), 1 );
        visit1.save();
        //University of Banterbury, testTrip, visitOrder = 2
        VisitFormData visitformdata2 = new VisitFormData(Destination.find.byId(2).getDestName(), arrival2, departure2, Trip.find.byId(1).tripName);
        Visit visit2 = visitfactory.createVisit(visitformdata2, Destination.find.byId(2), Trip.find.byId(1), 2 );
        visit2.save();
        //University of Canterbury (same destination), testTrip, visitOrder = 3, arrival and departure currently broken
        VisitFormData visitformdata3 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit3 = visitfactory.createVisit(visitformdata3, Destination.find.byId(1), Trip.find.byId(1), 3 );
        visit3.save();
        //Get the list of visits and sort it by order. Current list: [Canterbury, Banterbury, Canterbury]
        List<Visit> visits = User.find.byId(1).getTrips().get(0).getVisits();
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        //Instantiate trip controller to test methods
        TripController tripController = new TripController();
        //Test if the first row of the list can be deleted, making it [Banterbury, Canterbury] which is valid (should return false)
        assertFalse(tripfactory.hasRepeatDest(visits, visits.get(0), "DELETE"));
        //Test if the second row of the list can be deleted, making it [Canterbury, Canterbury] which is invalid (should return true)
        assertTrue(tripfactory.hasRepeatDest(visits, visits.get(1), "DELETE"));
        //Test if the second row of the list can be deleted, making it [Canterbury, Canterbury] which is valid (should return false)
        assertFalse(tripfactory.hasRepeatDest(visits, visits.get(2), "DELETE"));
        //University of Canterbury (same destination), testTrip, visitOrder = 4, arrival and departure currently broken
        VisitFormData visitformdata4 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit4 = visitfactory.createVisit(visitformdata4, Destination.find.byId(1), Trip.find.byId(1), 4 );        //Test if the University of Canterbury can be added, making it [Canterbury, Banterbury, Canterbury, Canterbury] which is invalid (should return true)
        assertTrue(tripfactory.hasRepeatDest(visits, visit4, "ADD"));
        //University of Canterbury (same destination), testTrip, visitOrder = 4, arrival and departure currently broken
        VisitFormData visitformdata5 = new VisitFormData(Destination.find.byId(2).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit5 = visitfactory.createVisit(visitformdata5, Destination.find.byId(2), Trip.find.byId(1), 5 );        //Test if the University of Banterbury can be added, making it [Canterbury, Banterbury, Canterbury, Banterbury] which is valid (should return false)
        assertFalse(tripfactory.hasRepeatDest(visits, visit5, "ADD"));
        //TO BE DONE
        //Test if the University of Canterbury can be swapped into the middle of the list (index 1). This assumes that the input list is already swapped.
        visit2.setDestination(Destination.find.byId(1));
        visit2.update();
        visits = User.find.byId(1).getTrips().get(0).getVisits();
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        //Current list is [Canterbury, Canterbury, Canterbury] which is invalid for all indices (should return true)
        assertTrue(tripfactory.hasRepeatDest(visits, visit1, "SWAP"));
        assertTrue(tripfactory.hasRepeatDest(visits, visit2, "SWAP"));
        assertTrue(tripfactory.hasRepeatDest(visits, visit3, "SWAP"));
        visit3.setDestination(Destination.find.byId(2));
        visit3.update();
        visits = User.find.byId(1).getTrips().get(0).getVisits();
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        //Current list is [Canterbury, Canterbury, Banterbury] which is invalid for index 0, index 1 and valid for index 2
        assertTrue(tripfactory.hasRepeatDest(visits, visit1, "SWAP"));
        assertTrue(tripfactory.hasRepeatDest(visits, visit2, "SWAP"));
        assertFalse(tripfactory.hasRepeatDest(visits, visit3, "SWAP"));
        visit1.setDestination(Destination.find.byId(2));
        visit1.update();
        visits = User.find.byId(1).getTrips().get(0).getVisits();
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        //Current list is [Banterbury, Canterbury, Banterbury] which is valid for index 0, index 1 and index 2
        assertFalse(tripfactory.hasRepeatDest(visits, visit1, "SWAP"));
        assertFalse(tripfactory.hasRepeatDest(visits, visit2, "SWAP"));
        assertFalse(tripfactory.hasRepeatDest(visits, visit3, "SWAP"));
    }
    */

    /**
     * Unit tests for method to detect repeat destinations when swapping.
     * Returns true if a repeat destination is formed from swapping two visits in a list, false otherwise.
     * TEMPORARILY COMMENTED OUT BECAUSE A DIFFERENT IMPLEMENTATION IS BEING USED
     */
    /*
    @Test
    public void hasRepeatDestSwap() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String arrival1 = "2019-04-20";
        String departure1 = "2019-06-09";
        String arrival2 = "2018-04-20";
        String departure2 = "2018-06-09";
        //University of Canterbury, testTrip, visitOrder = 1
        VisitFormData visitformdata1 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit1 = visitfactory.createVisit(visitformdata1, Destination.find.byId(1), Trip.find.byId(1), 1 );
        visit1.save();
        //University of Banterbury, testTrip, visitOrder = 2
        VisitFormData visitformdata2 = new VisitFormData(Destination.find.byId(2).getDestName(), arrival2, departure2, Trip.find.byId(1).tripName);
        Visit visit2 = visitfactory.createVisit(visitformdata2, Destination.find.byId(2), Trip.find.byId(1), 2 );
        visit2.save();
        //University of Canterbury (same destination), testTrip, visitOrder = 3, arrival and departure currently broken
        VisitFormData visitformdata3 = new VisitFormData(Destination.find.byId(1).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit3 = visitfactory.createVisit(visitformdata3, Destination.find.byId(1), Trip.find.byId(1), 3 );
        visit3.save();
        //Panem, testTrip, visitOrder = 4, arrival and departure currently broken
        VisitFormData visitformdata4 = new VisitFormData(Destination.find.byId(3).getDestName(), arrival1, departure1, Trip.find.byId(1).tripName);
        Visit visit4 = visitfactory.createVisit(visitformdata4, Destination.find.byId(3), Trip.find.byId(1), 4 );
        visit4.save();
        //Get the list of visits and sort it by order. Current list: Canterbury, Banterbury, Canterbury, Panem.
        List<Visit> visits = User.find.byId(1).getTrips().get(0).getVisits();
        visits.sort(Comparator.comparing(Visit::getVisitOrder));
        TripController tripController = new TripController();
        //Swaps visit1 and visit2, making [Banterbury, Canterbury, Canterbury, Panem] which is invalid so should return true
        assertTrue(tripController.hasRepeatDestSwap(visits, visit1, visit2));
        //Swaps visit2 and visit3, making [Canterbury, Canterbury, Banterbury, Panem] which is invalid so should return true
        assertTrue(tripController.hasRepeatDestSwap(visits, visit2, visit3));
        //Swaps visit1 and visit3, making [Canterbury, Banterbury, Canterbury, Panem] which is valid so should return false
        assertFalse(tripController.hasRepeatDestSwap(visits, visit1, visit3));
        //Swaps visit1 and visit4, making [Panem, Banterbury, Canterbury, Canterbury] which is invalid so should return true
        assertTrue(tripController.hasRepeatDestSwap(visits, visit1, visit4));
        //Swaps visit4 and visit1, should have the same functionality above and return true
        assertTrue(tripController.hasRepeatDestSwap(visits, visit4, visit1));
        //Swaps visit2 and visit4, making [Canterbury, Panem, Canterbury, Banterbury] which is valid so should return false
        assertFalse(tripController.hasRepeatDestSwap(visits, visit2, visit4));
        //Swaps visit3 and visit4, making [Canterbury, Banterbury, Panem, Canterbury] which is valid so should return false
        assertFalse(tripController.hasRepeatDestSwap(visits, visit3, visit4));
    }
    */
}