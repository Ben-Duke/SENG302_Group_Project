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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
///users/treasurehunts/save
        //User with id 2 should have one trip
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the title form as "triptest123"
        formData.put("title", string);
        formData.put("destination", string2);
        formData.put("riddle", string3);
        formData.put("startDate", string4);
        formData.put("endDate", string5);
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/treasurehunts/save").session("connected", "2");
        Result result = Helpers.route(application, fakeRequest);
        //User should be redirected to the index treasure hunts page
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("There should be a treasure hunt in the database with title {string}, destination {string}, riddle {string}, start date {string} and end date {string}")
    public void thereShouldBeATreasureHuntInTheDatabaseWithTitleDestinationRiddleStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        // Write code here that turns the phrase above into concrete actions
        //User with id 2 should have two trips
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        Assert.assertNotNull(treasureHunt);
        assertEquals(2, User.find.byId(2).getTreasureHunts().size());
        //The treasure hunt with the title "test123" should be the user's second treasure hunt
        assertEquals(string2, treasureHunt.getDestination().getDestName());
        assertEquals(string3, treasureHunt.getRiddle());
        assertEquals(string4, treasureHunt.getStartDate());
        assertEquals(string5, treasureHunt.getEndDate());

    }

    @Given("There is a treasure hunt with the title {string}")
    public void thereIsATreasureHuntWithTheTitle(String string) {
        // Write code here that turns the phrase above into concrete actions
        TreasureHunt existingTreasureHunt = new TreasureHunt("NewTreasureHunt", "asd",
                Destination.find.byId(1), "2018-12-05", "2019-12-05", User.find.byId(2));
        existingTreasureHunt.save();
        TreasureHunt treasureHunt = TreasureHunt.find.query().where().eq("title", string).findOne();
        assertNotNull(treasureHunt);
    }

    @When("I create an invalid treasure hunt with the title {string}, destination {string}, riddle {string}, start date {string} and end date {string}")
    public void iCreateAnInvalidTreasureHuntWithTheTitleDestinationRiddleStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("There should be no treasure hunts in the database with title {string}")
    public void thereShouldBeNoTreasureHuntsInTheDatabaseWithTitle(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I edit a treasure hunt with the title {string} by changing the destination to {string}, riddle to {string}, start date {string} and end date {string}")
    public void iEditATreasureHuntWithTheTitleByChangingTheDestinationToRiddleToStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The treasure hunt with the title {string} in the database should have the destination as {string}, riddle to {string}, start date {string} and end date {string}")
    public void theTreasureHuntWithTheTitleInTheDatabaseShouldHaveTheDestinationAsRiddleToStartDateAndEndDate(String string, String string2, String string3, String string4, String string5) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I edit a treasure hunt with the title {string} by changing the title to {string}")
    public void iEditATreasureHuntWithTheTitleByChangingTheTitleTo(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("There should be only one treasure hunt with the title {string} in the database")
    public void thereShouldBeOnlyOneTreasureHuntWithTheTitleInTheDatabase(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("There should be a treasure hunt with the title {string}")
    public void thereShouldBeATreasureHuntWithTheTitle(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I invalidly update the destination {string} with name {string}, type {string}, district {string}, country {string}, latitude {string}, longitude {string}")
    public void iInvalidlyUpdateTheDestinationWithNameTypeDistrictCountryLatitudeLongitude(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("user id {string} is the owner of the profile")
    public void userIdIsTheOwnerOfTheProfile(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I upload a new photo with")
    public void iUploadANewPhotoWith() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have just created a new account")
    public void iHaveJustCreatedANewAccount() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I visit the home page")
    public void iVisitTheHomePage() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The placeholder profile picture is displayed in place of a profile picture")
    public void thePlaceholderProfilePictureIsDisplayedInPlaceOfAProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The aspect ratio of the image is square")
    public void theAspectRatioOfTheImageIsSquare() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have not uploaded any pictures to my profile")
    public void iHaveNotUploadedAnyPicturesToMyProfile() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have uploaded pictures to my profile")
    public void iHaveUploadedPicturesToMyProfile() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have not set a profile picture")
    public void iHaveNotSetAProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have set a profile picture")
    public void iHaveSetAProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The image I set as my profile picture is displayed in the place of a profile picture")
    public void theImageISetAsMyProfilePictureIsDisplayedInThePlaceOfAProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have photos already uploaded to my account")
    public void iHavePhotosAlreadyUploadedToMyAccount() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I set one of these photos to be my profile picture")
    public void iSetOneOfThesePhotosToBeMyProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I upload a new image to be set as my profile picture")
    public void iUploadANewImageToBeSetAsMyProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("This new image is displayed in the place of a profile picture")
    public void thisNewImageIsDisplayedInThePlaceOfAProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I am uploading a new image to be my profile picture")
    public void iAmUploadingANewImageToBeMyProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The image is cropped into a square aspect ratio")
    public void theImageIsCroppedIntoASquareAspectRatio() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The image is not stretched to fit this aspect ratio")
    public void theImageIsNotStretchedToFitThisAspectRatio() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I have existing images in my profile")
    public void iHaveExistingImagesInMyProfile() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I select one of these to be my profile picture")
    public void iSelectOneOfTheseToBeMyProfilePicture() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The profile picture version of the image is also stored")
    public void theProfilePictureVersionOfTheImageIsAlsoStored() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("This version of the image has the same aspect ratio as the orifinal image")
    public void thisVersionOfTheImageHasTheSameAspectRatioAsTheOrifinalImage() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The cropped version of this image is stored")
    public void theCroppedVersionOfThisImageIsStored() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I view a treasure hunt of title {string}")
    public void iViewATreasureHuntOfTitle(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("I should be shown a riddle of {string} with a start date of {string} and end date of {string}")
    public void iShouldBeShownARiddleOfWithAStartDateOfAndEndDateOf(String string, String string2, String string3) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The treasure hunt should have a destination with name {string}")
    public void theTreasureHuntShouldHaveADestinationWithName(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I view the list of available treasure hunts")
    public void iViewTheListOfAvailableTreasureHunts() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("I should be shown a list of two treasure hunts")
    public void iShouldBeShownAListOfTwoTreasureHunts() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("The titles of the treasure hunt should be {string} and {string}")
    public void theTitlesOfTheTreasureHuntShouldBeAnd(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("There are no available treasure hunts in the database")
    public void thereAreNoAvailableTreasureHuntsInTheDatabase() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("I should be shown an empty list of treasure hunts")
    public void iShouldBeShownAnEmptyListOfTreasureHunts() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }
}
