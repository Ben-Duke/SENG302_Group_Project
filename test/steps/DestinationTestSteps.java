package steps;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import models.User;
import org.junit.Assert;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.inject.guice.GuiceApplicationLoader;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.TestDatabaseManager;

import javax.inject.Inject;


public class DestinationTestSteps extends WithApplication {

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
        testDatabaseManager.populateDatabase();
    }

    @After
    public void tearDown(){
        Helpers.stop(application);
    }

    @Given("There is a prepopulated database")
    public void thereIsAPrepopulatedDatabase() {
        Assert.assertEquals(4, User.find.all().size());
    }




    @Given("I am logged in with user id {string}")
    public void iAmLoggedInWithUserId(String string) {
        // Write code here that turns the phrase above into concrete actions
        Assert.assertEquals(true, User.find.byId(Integer.parseInt(string)) != null);
    }

    @Given("I create a destination with name {string} of type {string} at district {string} at country {string}{double}{string}{double}\"")
    public void iCreateADestinationWithNameOfTypeAtDistrictAtCountry(String string, String string2, String string3, String string4, Double double1, String string5, Double double2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I access my private destinations")
    public void iAccessMyPrivateDestinations() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should be within my list of private destinations")
    public void shouldBeWithinMyListOfPrivateDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should not be within my list of public destinations")
    public void shouldNotBeWithinMyListOfPublicDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I update my destination with name {string}, type {string}, district {string}, country {string}, latitude {string} and longitude {string}")
    public void iUpdateMyDestinationWithNameTypeDistrictCountryLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the destination will be updated to the respective attributes.")
    public void theDestinationWillBeUpdatedToTheRespectiveAttributes() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I delete my destination with name {string}")
    public void iDeleteMyDestinationWithName(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the destination will be deleted.")
    public void theDestinationWillBeDeleted() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I mark {string} as public and the same public destination does not already exist")
    public void iMarkAsPublicAndTheSamePublicDestinationDoesNotAlreadyExist(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should not be within my list of private destinations")
    public void shouldNotBeWithinMyListOfPrivateDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should be within my list of public destinations")
    public void shouldBeWithinMyListOfPublicDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I mark {string} as public and the same public destination already exists")
    public void iMarkAsPublicAndTheSamePublicDestinationAlreadyExists(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should not be updated to a public destination")
    public void shouldNotBeUpdatedToAPublicDestination(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I create another destination with name {string} of type {string} at district {string} at country {string}{double}{string}{double}\"")
    public void iCreateAnotherDestinationWithNameOfTypeAtDistrictAtCountry(String string, String string2, String string3, String string4, Double double1, String string5, Double double2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the destination should not be created and there should only be one {string} in the database.")
    public void theDestinationShouldNotBeCreatedAndThereShouldOnlyBeOneInTheDatabase(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("there exists a public destination with name {string}, type {string}, district {string}, country {string}, latitude {string} and longitude {string}")
    public void thereExistsAPublicDestinationWithNameTypeDistrictCountryLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I create a destination with name {string}, type {string}, district {string}, country {string}, latitude {string} and longitude {string}")
    public void iCreateADestinationWithNameTypeDistrictCountryLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I mark {string} as public and nobody uses the destination")
    public void iMarkAsPublicAndNobodyUsesTheDestination(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("I update the destination {string}")
    public void iUpdateTheDestination(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the destination should be updated")
    public void theDestinationShouldBeUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I mark {string} as public and a user with user id {string} uses the destination")
    public void iMarkAsPublicAndAUserWithUserIdUsesTheDestination(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("the destination should be not be updated")
    public void theDestinationShouldBeNotBeUpdated() {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @When("a user with user id {string} creates a destination with name {string} of type {string} at district {string} at country {string}{double}{string}{double}\"")
    public void aUserWithUserIdCreatesADestinationWithNameOfTypeAtDistrictAtCountry(String string, String string2, String string3, String string4, String string5, Double double1, String string6, Double double2) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("I will no longer have {string} in my private list of destinations because it will have been merged")
    public void iWillNoLongerHaveInMyPrivateListOfDestinationsBecauseItWillHaveBeenMerged(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Then("{string} will be in a public list of destinations.")
    public void willBeInAPublicListOfDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

}
