package controllers;

import models.Destination;
import models.TravellerType;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class TravellerTypeControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

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


        //Initialises a test user with name "testUser" and saves it to the database.
        User user = new User("testUser");
        user.save();
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


    /**
     * Unit test for rendering the traveller type page
     */
    @Test
    public void updateTravellerType() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/ttypes").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/ttypes").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Unit test for rendering the destination traveller type page
     */
    @Test
    public void updateDestinationTravellerType() {
        //invalid user
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/ttypes/display/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/ttypes/display/1").session("connected", "2");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Unit test for adding a traveller type to a user
     */
    @Test
    public void submitUpdateTravellerType() {
        Map<String, String> formData = new HashMap<>();
        //Assuming the user selects traveller type with id "2" which is "Thrillseeker"
        formData.put("travellertypes", "2");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/ttypes").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the update traveller type page
        assertEquals(SEE_OTHER, result.status());
        //"TravellerType with name "Thrillseeker" should be the first index in the user's traveller types
        assertEquals("Thrillseeker", User.find.byId(1).getTravellerTypes().get(0).getTravellerTypeName());
    }

    /**
     * Unit test for adding a traveller type to a destination
     */
    @Test
    public void submitUpdateDestinationTravellerType() {
        Map<String, String> formData = new HashMap<>();
        //Assuming the user selects traveller type with id "2" which is "Thrillseeker"
        formData.put("travellertypes", "2");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/destinations/ttypes/1").session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the update traveller type page
        assertEquals(SEE_OTHER, result.status());
        //"TravellerType with name "Thrillseeker" should be the second index in the user's traveller types, first being groupie
        assertEquals("Thrillseeker", Destination.find.byId(1).getTravellerTypes().get(1).getTravellerTypeName());
    }

    /**
     * Unit test for deleting traveller types from a user
     */
    @Test
    public void deleteUpdateTravellerType() {
        //add a "Thrillseeker" traveller type to user
        User user = User.find.byId(1);
        user.addTravellerType(TravellerType.find.byId(2));
        user.update();
        //There should be 1 traveller type
        assertEquals(1, User.find.byId(1).getTravellerTypes().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("travellertypesdelete", "2");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/delete").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the update traveller type page
        //There should be one traveller type since can't remove from 1
        assertEquals(1, User.find.byId(1).getTravellerTypes().size());
    }

    /**
     * Unit test for deleting traveller types from a destination
     */
    @Test
    public void deleteUpdateDestinationTravellerType() {
        //There should be 1 traveller type
        assertEquals(1, Destination.find.byId(1).getTravellerTypes().size());
        //add a "Thrillseeker" traveller type to the destination with id 1
        Destination destination = Destination.find.byId(1);
        destination.addTravellerType(TravellerType.find.byId(2));
        destination.update();
        //There should be 2 traveller types
        assertEquals(2, Destination.find.byId(1).getTravellerTypes().size());
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/destinations/ttypes/1/2").session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the update traveller type page
        assertEquals(SEE_OTHER, result.status());
        assertEquals(1, Destination.find.byId(1).getTravellerTypes().size());
    }
}

