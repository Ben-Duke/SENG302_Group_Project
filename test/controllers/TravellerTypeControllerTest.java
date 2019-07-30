package controllers;

import models.Destination;
import models.TravellerType;
import models.User;
import org.junit.Test;
import org.slf4j.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.UtilityFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class TravellerTypeControllerTest extends BaseTestWithApplicationAndDatabase {

    Logger logger = UtilityFunctions.getLogger();

    /**
     * Unit test for rendering the traveller type page
     */
    @Test
    public void updateTravellerType() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/ttypes").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
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
        for (TravellerType travellerType : Destination.find.byId(2).getTravellerTypes()) {
            assertEquals("Thrillseeker", travellerType.getTravellerTypeName());
        }
    }

    /**
     * Unit test for deleting traveller types from a user
     */
    @Test
    public void deleteUpdateTravellerType() {
        User user = User.find.byId(1);
        assert user != null;
        int initialTypes = user.getTravellerTypes().size();

        //add a "Thrillseeker" traveller type to user
        user.addTravellerType(TravellerType.find.byId(2));
        user.update();

        // Check it was added
        assertEquals(initialTypes + 1, User.find.byId(1).getTravellerTypes().size());

        Map<String, String> formData = new HashMap<>();
        formData.put("travellertypes", "2");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/delete/2").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);

        //User should be redirected to the update traveller type page
        assertEquals(303, result.status());

        // There should be one traveller type since can't remove from 1
        assertEquals(initialTypes, User.find.byId(1).getTravellerTypes().size());
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

