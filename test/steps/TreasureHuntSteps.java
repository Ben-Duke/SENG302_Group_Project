package steps;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import controllers.ApplicationManager;
import controllers.TreasureHuntController;
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
import testhelpers.BaseCumumberTestHelper;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;

public class TreasureHuntSteps extends BaseCumumberTestHelper {

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
        Result result = Helpers.route(BaseCumumberTestHelper.getApplication(), fakeRequest);
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
        Result result = Helpers.route(BaseCumumberTestHelper.getApplication(), fakeRequest);
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
        Result result = Helpers.route(BaseCumumberTestHelper.getApplication(), fakeRequest);
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
        Result result = Helpers.route(BaseCumumberTestHelper.getApplication(), fakeRequest);
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

    @Given("There are three treasure hunts in the database")
    public void there_are_three_treasure_hunts_in_the_database() {
        List<TreasureHunt> treasureHunts = TreasureHunt.find.all();
        assertEquals(3, treasureHunts.size());
    }

    @Given("Two of the existing treasure hunts are open and one is closed")
    public void two_of_the_existing_treasure_hunts_are_open_and_one_is_closed() {
        TreasureHunt treasureHunt1 = TreasureHunt.find.byId(1);
        TreasureHunt treasureHunt2 = TreasureHunt.find.byId(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate1 = LocalDate.parse(treasureHunt1.getEndDate(), formatter);
        LocalDate endDate2 = LocalDate.parse(treasureHunt2.getEndDate(), formatter);
        LocalDate currentDate = LocalDate.now();
        assertTrue(endDate1.isAfter(currentDate));
        assertTrue(endDate2.isAfter(currentDate));
    }

    @Then("The user should only be shown two treasure hunts")
    public void the_user_should_only_be_shown_two_treasure_hunts() {
        TreasureHuntController treasureHuntController = new TreasureHuntController();
        assertEquals(2, treasureHuntController.getOpenTreasureHunts().size());
    }

    @When("I edit those open treasure hunts to make it closed")
    public void i_edit_those_open_treasure_hunts_to_make_it_closed() {
        TreasureHunt treasureHunt1 = TreasureHunt.find.byId(1);
        TreasureHunt treasureHunt2 = TreasureHunt.find.byId(2);
        treasureHunt1.setStartDate("2019-01-01");
        treasureHunt1.setEndDate("2019-01-02");
        treasureHunt1.update();
        treasureHunt2.setStartDate("2019-01-01");
        treasureHunt2.setEndDate("2019-01-02");
        treasureHunt2.update();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endDate1 = LocalDate.parse(treasureHunt1.getEndDate(), formatter);
        LocalDate endDate2 = LocalDate.parse(treasureHunt2.getEndDate(), formatter);
        LocalDate currentDate = LocalDate.now();
        assertTrue(endDate1.isBefore(currentDate));
        assertTrue(endDate2.isBefore(currentDate));
    }

    @Then("The user should not be shown any treasure hunts")
    public void the_user_should_not_be_shown_any_treasure_hunts() {
        TreasureHuntController treasureHuntController = new TreasureHuntController();
        assertEquals(0, treasureHuntController.getOpenTreasureHunts().size());
    }

    @When("I delete one my treasure hunt with the title {string}")
    public void i_delete_one_my_treasure_hunt_with_the_title(String string) {
        int tHuntId = TreasureHunt.find.query().where().eq("title", string).findOne().getThuntid();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "2");
        Result result = Helpers.route(BaseCumumberTestHelper.getApplication(), fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("There should be no treasure hunt with the title {string} in the database")
    public void there_should_be_no_treasure_hunt_with_the_title_in_the_database(String string) {
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        assertNull(treasureHunt);
    }

    @When("I try to delete other users treasure hunt with the title {string}")
    public void i_try_to_delete_other_users_treasure_hunt_with_the_title(String string) {
        int tHuntId = TreasureHunt.find.query().where().eq("title", string).findOne().getThuntid();
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().method(Helpers.GET).uri("/users/treasurehunts/delete/" + tHuntId).session("connected", "2");
        Result result = Helpers.route(BaseCumumberTestHelper.getApplication(), fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Then("The treasure hunt with the title {string} should still be in the database")
    public void the_treasure_hunt_with_the_title_should_still_be_in_the_database(String string) {
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        assertNotNull(treasureHunt);
    }

}
