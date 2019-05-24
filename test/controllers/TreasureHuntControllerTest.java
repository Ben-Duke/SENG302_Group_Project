package controllers;


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
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.TestDatabaseManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    /**
     * Instance of the TreasureHuntController
     * */
    TreasureHuntController treasureHuntController = new TreasureHuntController();

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
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
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
     * Gives the built GUI application
     */
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

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
        List<TreasureHunt> treasureHunts = TreasureHunt.find.all();
        List<TreasureHunt> openTreasureHunts = treasureHuntController.getOpenTreasureHunts();
        assertEquals(treasureHunts.size()-1, openTreasureHunts.size());
    }

    /**
     * Test for creating public destinations map.
     */
    @Test
    public void createPublicDestinationsMap() {
        List<Destination> allDestinations = Destination.find.query().where().eq("is_public", true).findList();
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

    /**
     * Test for creating the treasure hunt with user not logged in.
     */
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
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for creating the treasure hunt with errors such that title, riddle, destination fields are left blank.
     */
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

    /**
     * Test for creating the treasure hunt with the date error where the start date is after the end date.
     */
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

    /**
     * Test for creating the treasure hunt with duplicate title.
     */
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
    public void editAndSaveTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        assertEquals("test123", User.find.byId(2).getTreasureHunts().get(0).getTitle());
        assertEquals("The garden city", User.find.byId(2).getTreasureHunts().get(0).getRiddle());
        assertEquals("Christchurch", User.find.byId(2).getTreasureHunts().get(0).getDestination().getDestName());
        assertEquals("2019-04-17", User.find.byId(2).getTreasureHunts().get(0).getStartDate());
        assertEquals("2019-12-25", User.find.byId(2).getTreasureHunts().get(0).getEndDate());
    }

    /**
     * Test for editing the treasure hunt that doesn't exist.
     */
    @Test
    public void editAndSaveInvalidTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with user not logged in.
     */
    @Test
    public void editAndSaveTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with errors such that title, riddle, destination fields are left blank.
     */
    @Test
    public void editAndSaveTreasureHuntWithErrors() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("startDate", "2000-01-01");
        formData.put("endDate", "2001-01-01");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the edit treasure hunts page with errors.
        assertEquals(BAD_REQUEST, result.status());
        //User with id 2 should have only one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with the date error where the start date is after the end date.
     */
    @Test
    public void editAndSaveTreasureHuntWithDateError() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for editing the treasure hunt with duplicate title.
     */
    @Test
    public void editAndSaveTreasureHuntWithDuplicateTitle() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for deleting the treasure hunt.
     */
    @Test
    public void deleteTreasureHunt() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "2");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(0, User.find.byId(2).getTreasureHunts().size());

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
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
        Map<String, String> formData = new HashMap<>();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "3");
        Result result = Helpers.route(app, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }

    /**
     * Test for deleting the treasure hunt without logging in.
     */
    @Test
    public void deleteTreasureHuntWithInvalidLoginSession() {
        //User with id 2 should have one trip
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        int tHuntId = User.find.byId(2).getTreasureHunts().get(0).getThuntid();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", null);
        Result result = Helpers.route(app, fakeRequest);
        //User should not be authorized.
        assertEquals(SEE_OTHER, result.status());
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
    }
}