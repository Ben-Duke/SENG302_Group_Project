package controllers;


import accessors.DestinationAccessor;
import accessors.TreasureHuntAccessor;
import accessors.UserAccessor;
import accessors.UserAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import models.Destination;
import models.TreasureHunt;
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
import utilities.exceptions.EbeanDateParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;

public class TreasureHuntControllerTest extends BaseTestWithApplicationAndDatabase {

    /**
     * Instance of the TreasureHuntController
     * */
    private TreasureHuntController treasureHuntController = new TreasureHuntController();

    /**
     * Test for getting to the index treasure hunts page.
     */
    @Test
    public void getIndexTreasureHuntPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts").session("connected", "2");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test for getting open treasure hunts.
     */
    @Test
    public void getOpenTreasureHunts() {
        List<TreasureHunt> treasureHunts = TreasureHunt.find().all();
        List<TreasureHunt> openTreasureHunts = treasureHuntController.getOpenTreasureHunts();
        assertEquals(treasureHunts.size()-1, openTreasureHunts.size());
    }

    /**
     * Test for creating public destinations map.
     */
    @Test
    public void createPublicDestinationsMap() {
        List<Destination> allDestinations = Destination.find().query().where().eq("destIsPublic", true).findList();
        treasureHuntController.createPublicDestinationsMap();
        assertEquals(treasureHuntController.destinationMap.size(), allDestinations.size());
    }

    /**
     * Test for modifying public destinations map.
     */
    @Test
    public void modifyPublicDestinationsMap() {
        createPublicDestinationsMap();
        treasureHuntController.modifyPublicDestinationsMap("Christchurch", false);
        assertFalse(treasureHuntController.destinationMap.get("Christchurch"));
    }

    /**
     * Test for getting treasure hunt creation page.
     */
    @Test
    public void getCreateTreasureHuntPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/create").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/create").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test for creating the treasure hunt.
     */
    @Test
    public void createAndSaveTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
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
        assertEquals(2, User.find().byId(2).getTreasureHunts().size());
        //The treasure hunt with the title "test123" should be the user's second treasure hunt
        assertEquals("test123", User.find().byId(2).getTreasureHunts().get(1).getTitle());
    }

    /**
     * Test for creating the treasure hunt with user not logged in.
     */
    @Test
    public void createAndSaveTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        //User should not be authorized.
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for creating the treasure hunt with errors such that title, riddle, destination fields are left blank.
     */
    @Test
    public void createAndSaveTreasureHuntWithErrors() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the create treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for creating the treasure hunt with the date error where the start date is after the end date.
     */
    @Test
    public void createAndSaveTreasureHuntWithDateError() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
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
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for creating the treasure hunt with duplicate title.
     */
    @Test
    public void createAndSaveTreasureHuntWithDuplicateTitle() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
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
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for getting to the edit treasure hunt page.
     */
    @Test
    public void getEditTreasureHuntPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/edit/1").session("connected", "2");
        result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test for getting to the edit treasure hunt page for an invalid treasure hunt.
     */
    @Test
    public void getEditTreasureHuntPageInvalidTreasureHunt() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/treasurehunts/edit/10").session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test for getting to the edit the treasure hunt page for a treasure hunt that doesn't belong to the user logged in.
     */
    @Test
    public void getEditTreasureHuntPageWithUnauthorizedUser() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/edit/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test for editing the treasure hunt.
     */
    @Test
    public void editAndSaveTreasureHunt() throws EbeanDateParseException {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
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
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        assertEquals("test123", User.find().byId(2).getTreasureHunts().get(0).getTitle());
        assertEquals("The garden city", User.find().byId(2).getTreasureHunts().get(0).getRiddle());
        assertEquals("Christchurch", User.find().byId(2).getTreasureHunts().get(0).getDestination().getDestName());
        assertEquals(UtilityFunctions.parseLocalDate("2019-04-17"), User.find().byId(2).getTreasureHunts().get(0).getStartDate());
        assertEquals(UtilityFunctions.parseLocalDate("2019-12-25"), User.find().byId(2).getTreasureHunts().get(0).getEndDate());
    }

    /**
     * Test for editing the treasure hunt that doesn't exist.
     */
    @Test
    public void editAndSaveInvalidTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "The garden city");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2019-04-17");
        formData.put("endDate", "2019-12-25");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/10").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test for editing the treasure hunt which belongs to a user different to the user logged in.
     */
    @Test
    public void editAndSaveTreasureHuntWithUnauthorizedUser() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "The garden city");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2019-04-17");
        formData.put("endDate", "2019-12-25");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "3");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with user not logged in.
     */
    @Test
    public void editAndSaveTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        //User should not be authorized.
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with errors such that title, riddle, destination fields are left blank.
     */
    @Test
    public void editAndSaveTreasureHuntWithErrors() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with the date error where the start date is after the end date.
     */
    @Test
    public void editAndSaveTreasureHuntWithDateError() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2002-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with duplicate title.
     */
    @Test
    public void editAndSaveTreasureHuntWithDuplicateTitle() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "Surprise2");
        formData.put("riddle", "test");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for deleting the treasure hunt.
     */
    @Test
    public void deleteTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(0, User.find().byId(2).getTreasureHunts().size());

    }

    /**
     * Test for deleting an invalid treasure hunt.
     */
    @Test
    public void deleteInvalidTreasureHunt() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/delete/10").session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test for deleting the treasure hunt which doesn't belong to the current logged in user.
     */
    @Test
    public void deleteTreasureHuntWithUnauthorizedUser() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "3");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    /**
     * Test for deleting the treasure hunt without logging in.
     */
    @Test
    public void deleteTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
        int tHuntId = User.find().byId(2).getTreasureHunts().get(0).getThuntid();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        //User should not be authorized.
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find().byId(2).getTreasureHunts().size());
    }

    @Test
    public void undoEditTreasureHunt() {
        User user = UserAccessor.getById(2);
        TreasureHunt treasureHunt = user.getTreasureHunts().get(0);
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "The garden city");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2019-04-17");
        formData.put("endDate", "2019-12-25");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest()
                .bodyForm(formData)
                .method(Helpers.POST)
                .uri("/users/treasurehunts/edit/save/" + treasureHunt.getThuntid())
                .session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Helpers.route(app, fakeRequest);

        // Undo the edit
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/undo").session("connected", "2");
        Helpers.route(app, undoRequest);

        user = UserAccessor.getById(2);
        assertEquals(new TreasureHunt(treasureHunt), new TreasureHunt(user.getTreasureHunts().get(0)));
    }

    @Test
    public void redoEditTreasureHunt() throws EbeanDateParseException {
        User user = UserAccessor.getById(2);
        int tHuntId = user.getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", "test123");
        formData.put("riddle", "The garden city");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2019-04-17");
        formData.put("endDate", "2019-12-25");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest()
                .bodyForm(formData)
                .method(Helpers.POST)
                .uri("/users/treasurehunts/edit/save/" + tHuntId)
                .session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Helpers.route(app, fakeRequest);

        // Undo the edit
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/undo").session("connected", "2");
        Helpers.route(app, undoRequest);

        // Redo the edit
        Http.RequestBuilder redoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/redo").session("connected", "2");
        Helpers.route(app, redoRequest);

        user = UserAccessor.getById(2);
        TreasureHunt treasureHunt = getTreasureHuntFromMap(formData);
        assertEquals(treasureHunt, new TreasureHunt(user.getTreasureHunts().get(0)));
    }

    private TreasureHunt getTreasureHuntFromMap(Map<String, String> formMap) throws EbeanDateParseException {
        TreasureHunt treasureHunt = new TreasureHunt();
        treasureHunt.setTitle( formMap.get("title"));
        treasureHunt.setDestination(
                DestinationAccessor.getPublicDestinationbyName(formMap.get("destination"))
        );
        treasureHunt.setRiddle(formMap.get("riddle"));
        treasureHunt.setStartDate(formMap.get("startDate"));
        treasureHunt.setEndDate(formMap.get("endDate"));
        return treasureHunt;
    }

    @Test
    public void undoDeleteTreasureHunt() {
        User user = UserAccessor.getById(2);
        int nTreasureHunts = user.getTreasureHunts().size();
        Integer tHuntId = user.getTreasureHunts().get(0).getThuntid();

        // Delete
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "2");
        Helpers.route(app, fakeRequest);
        // Undo the delete
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/undo").session("connected", "2");
        Helpers.route(app, undoRequest);
        user = UserAccessor.getById(2);
        assertEquals(nTreasureHunts, user.getTreasureHunts().size());
    }

    @Test
    public void redoDeleteTreasureHuntCommand() {
        User user = UserAccessor.getById(2);
        int nTreasureHunts = user.getTreasureHunts().size();
        Integer tHuntId = user.getTreasureHunts().get(0).getThuntid();

        // Delete
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "2");
        Helpers.route(app, fakeRequest);
        user = UserAccessor.getById(2);

        // Undo the delete
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/undo").session("connected", "2");
        Helpers.route(app, undoRequest);

        user = UserAccessor.getById(2);


        // Redo the delete
        Http.RequestBuilder redoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/redo").session("connected", "2");
        Helpers.route(app, redoRequest);

        user = UserAccessor.getById(2);


        user = UserAccessor.getById(2);
        assertEquals(nTreasureHunts-1, user.getTreasureHunts().size());
    }

    @Test
    public void getPaginatedUserTreasureHuntsNoUser() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20");
        Result result = Helpers.route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void getPaginatedOpenTreasureHuntsNoUser() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/open?offset=0&quantity=20");
        Result result = Helpers.route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Checks that a user with one treasure hunt, with users other other treasure hunts,
     * gets back their treasure hunt when calling getPaginatedUserTreasureHunts.
     */
    @Test
    public void getPaginatedUserTreasureHunts_mixedUsersHaveHunts_checkHuntSame()
            throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        User userOther = new User("other@test.com", "sasdsad");
        UserAccessor.insert(userOther);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", userOther);
        TreasureHuntAccessor.insert(tHunt);

        TreasureHunt ownTHunt = new TreasureHunt("ownHunt", "test",
                destination, "2019-01-01", "2019-12-12", user);
        TreasureHuntAccessor.insert(ownTHunt);


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20")
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode json = Json.parse(contentAsString(result));
        JsonNode treasureHunts = json.get("ownTreasureHunts");

        assertEquals(1, treasureHunts.size());
        assertTrue("ownHunt".equals(treasureHunts.get(0).get("title").asText()));
    }

    /**
     * Checks that a user with one treasure hunt, with users other other treasure hunts,
     * gets a count of 1 in the json response.
     */
    @Test
    public void getPaginatedUserTreasureHunts_mixedUsersHaveHunts_checkCountOne()
            throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        User userOther = new User("other@test.com", "sasdsad");
        UserAccessor.insert(userOther);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", userOther);
        TreasureHuntAccessor.insert(tHunt);

        TreasureHunt ownTHunt = new TreasureHunt("ownHunt", "test",
                destination, "2019-01-01", "2019-12-12", user);
        TreasureHuntAccessor.insert(ownTHunt);


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20")
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode json = Json.parse(contentAsString(result));
        int countOwnHunts = json.get("totalCountOpenTreasureHunts").asInt();

        assertEquals(1, countOwnHunts);
    }

    /**
     * Checks that getPaginatedUsersTreasureHunts returns a list of size 0
     * when the application has no treasure hunts.
     */
    @Test
    public void getPaginatedUserTreasureHunts_noHunts_checkListEmpty() {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20")
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode json = Json.parse(contentAsString(result));
        JsonNode treasureHunts = json.get("ownTreasureHunts");

        assertEquals(0, treasureHunts.size());
    }

    /**
     * Checks that getPaginatedUsersTreasureHunts returns a count of 0
     * when the application has no treasure hunts.
     */
    @Test
    public void getPaginatedUserTreasureHunts_noHunts_checkCountZero() {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20")
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode json = Json.parse(contentAsString(result));
        int countOwnHunts = json.get("totalCountOpenTreasureHunts").asInt();

        assertEquals(0, countOwnHunts);
    }

    /**
     * Checks that a user with no treasure hunts, but other users do have them,
     * returns an empty list.
     */
    @Test
    public void getPaginatedUserTreasureHunts_onlyOtherHasHunts_checkListEmpty()
            throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        User userOther = new User("other@test.com", "sasdsad");
        UserAccessor.insert(userOther);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", userOther);
        TreasureHuntAccessor.insert(tHunt);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20")
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode json = Json.parse(contentAsString(result));
        JsonNode treasureHunts = json.get("ownTreasureHunts");

        assertEquals(0, treasureHunts.size());
    }

    /**
     * Checks that a user with zero treasure hunts, but other users have them,
     * returns a count of zero.
     */
    @Test
    public void getPaginatedUserTreasureHunts_onlyOtherHasHunts_checkCountZero()
            throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        User userOther = new User("other@test.com", "sasdsad");
        UserAccessor.insert(userOther);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", userOther);
        TreasureHuntAccessor.insert(tHunt);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/treasurehunts/user?offset=0&quantity=20")
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode json = Json.parse(contentAsString(result));
        int countOwnHunts = json.get("totalCountOpenTreasureHunts").asInt();

        assertEquals(0, countOwnHunts);
    }
}