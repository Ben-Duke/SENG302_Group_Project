package controllers;

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
        TravellerTypeController tTC= new TravellerTypeController();
        tTC.addTravelTypes();
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
     * Unit test for adding traveller types function
     */
    @Test
    public void addTravelTypes() {
        //delete all traveller types
        List<TravellerType> travellerTypes= TravellerType.find.all();
        for(TravellerType travellerType : travellerTypes){
            travellerType.delete();
        }
        assertEquals(0, TravellerType.find.all().size());
        //Add travel types
        TravellerTypeController tTC = new TravellerTypeController();
        tTC.addTravelTypes();
        assertEquals(7, TravellerType.find.all().size());
    }
}

