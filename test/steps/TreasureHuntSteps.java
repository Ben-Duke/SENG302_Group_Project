package steps;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import controllers.ApplicationManager;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.Destination;
import models.TreasureHunt;
import models.User;
import org.junit.Assert;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.api.test.CSRFTokenHelper;
import play.db.Database;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.TestDatabaseManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.SEE_OTHER;

public class TreasureHuntSteps extends WithApplication {
    /**
     * The fake database
     */
    //Database database = Databases.inMemory();
    Database database;
    TestDatabaseManager testDatabaseManager = new TestDatabaseManager();

    @Inject
    private Application application;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void setup(){
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
            }
        };
        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);
        Helpers.start(application);
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        testDatabaseManager.populateDatabase();
    }

    @After
    public void tearDown(){
        Helpers.stop(application);
    }


    @Given("There are no treasure hunts with the title {string}")
    public void thereAreNoTreasureHuntsWithTheTitle(String string) {
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        Assert.assertEquals(null, treasureHunt);
    }

    @When("I create a valid treasure hunt with the title {string}, destination {string}, riddle {string}, start date {string} and end date {string}")
    public void iCreateAValidTreasureHuntWithTheTitleDestinationRiddleStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        //User with id 2 should have one trip
        Map<String, String> formData = new HashMap<>();
        formData.put("title", string);
        formData.put("destination", string2);
        formData.put("riddle", string3);
        formData.put("startDate", string4);
        formData.put("endDate", string5);
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(application, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("There should be a treasure hunt in the database with title {string}, destination {string}, riddle {string}, start date {string} and end date {string}")
    public void thereShouldBeATreasureHuntInTheDatabaseWithTitleDestinationRiddleStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        Assert.assertNotNull(treasureHunt);
        assertEquals(2, User.find.byId(2).getTreasureHunts().size());
        assertEquals(string2, treasureHunt.getDestination().getDestName());
        assertEquals(string3, treasureHunt.getRiddle());
        assertEquals(string4, treasureHunt.getStartDate());
        assertEquals(string5, treasureHunt.getEndDate());

    }

    @Given("There is a treasure hunt with the title {string}")
    public void thereIsATreasureHuntWithTheTitle(String string) {
        TreasureHunt existingTreasureHunt = new TreasureHunt(string, "asd",
                Destination.find.byId(1), "2018-12-05", "2019-12-05", User.find.byId(2));
        existingTreasureHunt.save();
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        assertNotNull(treasureHunt);
    }

    @When("I create an invalid treasure hunt with the title {string}, destination {string}, riddle {string}, start date {string} and end date {string}")
    public void iCreateAnInvalidTreasureHuntWithTheTitleDestinationRiddleStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        Map<String, String> formData = new HashMap<>();
        formData.put("title", string);
        formData.put("destination", string2);
        formData.put("riddle", string3);
        formData.put("startDate", string4);
        formData.put("endDate", string5);
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(application, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(BAD_REQUEST, result.status());
    }

    @Then("There should be no treasure hunts in the database with title {string}")
    public void thereShouldBeNoTreasureHuntsInTheDatabaseWithTitle(String string) {
        List<TreasureHunt> treasureHunts = TreasureHunt.find.query().where().eq("title", string).findList();
        assertEquals(1, treasureHunts.size());
        assertEquals(2, User.find.byId(2).getTreasureHunts().size());
    }

    @When("I edit a treasure hunt with the title {string} by changing the destination to {string}, riddle to {string}, start date {string} and end date {string}")
    public void iEditATreasureHuntWithTheTitleByChangingTheDestinationToRiddleToStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        int tHuntId = TreasureHunt.find.query().where().eq("title", string).findOne().getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", string);
        formData.put("destination", string2);
        formData.put("riddle", string3);
        formData.put("startDate", string4);
        formData.put("endDate", string5);
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(application, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("The treasure hunt with the title {string} in the database should have the destination as {string}, riddle to {string}, start date {string} and end date {string}")
    public void theTreasureHuntWithTheTitleInTheDatabaseShouldHaveTheDestinationAsRiddleToStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        iEditATreasureHuntWithTheTitleByChangingTheDestinationToRiddleToStartDateAndEndDate(string, string2, string3, string4, string5);
        //User with id 2 should still have only one treasure hunt
        assertEquals(1, User.find.byId(2).getTreasureHunts().size());
        assertEquals(string, User.find.byId(2).getTreasureHunts().get(0).getTitle());
        assertEquals(string2, User.find.byId(2).getTreasureHunts().get(0).getDestination().getDestName());
        assertEquals(string3, User.find.byId(2).getTreasureHunts().get(0).getRiddle());
        assertEquals(string4, User.find.byId(2).getTreasureHunts().get(0).getStartDate());
        assertEquals(string5, User.find.byId(2).getTreasureHunts().get(0).getEndDate());
    }

    @When("I edit a treasure hunt with the title {string} by changing the title to an already existing {string}")
    public void iEditATreasureHuntWithTheTitleByChangingTheTitleToAnAlreadyExisting(String string, String string2) {
        int tHuntId = TreasureHunt.find.query().where().eq("title", string).findOne().getThuntid();
        Map<String, String> formData = new HashMap<>();
        formData.put("title", string2);
        formData.put("riddle", "The garden city");
        formData.put("destination", "Christchurch");
        formData.put("startDate", "2019-04-17");
        formData.put("endDate", "2019-12-25");
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/edit/save/" + tHuntId).session("connected", "2");
        fakeRequest = CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(application, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(BAD_REQUEST, result.status());
    }

    @Then("There should be only one treasure hunt with the title {string} in the database")
    public void thereShouldBeOnlyOneTreasureHuntWithTheTitleInTheDatabase(String string) {
        List<TreasureHunt> treasureHuntList = TreasureHunt.find.query().where().eq("title", string).findList();
        assertEquals(1, treasureHuntList.size());
    }

    @Then("There should be a treasure hunt with the title {string}")
    public void thereShouldBeATreasureHuntWithTheTitle(String string) {
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        assertEquals(string, treasureHunt.getTitle());
    }

}
