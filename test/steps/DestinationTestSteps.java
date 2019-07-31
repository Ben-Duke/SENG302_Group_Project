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
import models.Trip;
import models.User;
import models.Visit;
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
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;


public class DestinationTestSteps extends BaseTestWithApplicationAndDatabase {


    @Inject
    private Application application;

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
        ApplicationManager.setIsTest(true);
        ApplicationManager.setMediaPath("/test/resources/test_photos/user_");
        TestDatabaseManager.populateDatabase();
    }

    @After
    public void tearDown(){
        Helpers.stop(application);
    }

    @Given("There is a prepopulated database")
    public void thereIsAPrepopulatedDatabase()
    {
        //Assert.assertEquals(4, User.find().all().size());
        //Assert.assertTrue(true);
        throw new cucumber.api.PendingException();
    }




    @Given("I am logged in with user id {string}")
    public void iAmLoggedInWithUserId(String string) {
        // Write code here that turns the phrase above into concrete actions
        //Assert.assertEquals(true, User.find().byId(Integer.parseInt(string)) != null);
        throw new cucumber.api.PendingException();
    }

    @Given("I create a destination with name {string} of type {string} at district {string} at country {string} at latitude {string} and longitude {string}")
    public void iCreateADestinationWithNameOfTypeAtDistrictAtCountryAtLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
        // Write code here that turns the phrase above into concrete actions
        /*
        assertEquals(3, User.find().byId(2).getDestinations().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", string);
        formData.put("destType", string2);
        formData.put("district", string3);
        formData.put("country", string4);
        formData.put("latitude", string5);
        formData.put("longitude", string6);
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/save").session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, User.find().byId(2).getDestinations().size());
        */
        throw new cucumber.api.PendingException();
    }


    @When("I access my private destinations")
    public void iAccessMyPrivateDestinations() {
        // Write code here that turns the phrase above into concrete actions
//        throw new cucumber.api.PendingException();
        /*
        List<Destination> destinationList = User.find().byId(2).getDestinations();
        assertTrue(destinationList != null);
        */
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should be within my list of private destinations")
    public void shouldBeWithinMyListOfPrivateDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        //throw new cucumber.api.PendingException();
        /*
        List<Destination> destinationList = User.find().byId(2).getDestinations();
        boolean isDestinationFound = false;
        for (Destination destination : destinationList){
            if(destination.getDestName().equals(string)){
                isDestinationFound = true;
            }
        }
        assertEquals(true, isDestinationFound);
        */
        throw new cucumber.api.PendingException();
    }

    @Then("{string} should not be within my list of public destinations")
    public void shouldNotBeWithinMyListOfPublicDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        /*
        List<Destination> destinationList = Destination.find.all();
        boolean isDestinationFound = false;
        for (Destination destination : destinationList){
            if(destination.isPublic) {
                if (destination.getDestName().equals(string)) {
                    isDestinationFound = true;
                }
            }
        }
        assertEquals(false, isDestinationFound);
        */
        throw new cucumber.api.PendingException();
    }

    @When("I update my destination with name {string}, type {string}, district {string}, country {string}, latitude {string} and longitude {string}")
    public void iUpdateMyDestinationWithNameTypeDistrictCountryLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
        // Write code here that turns the phrase above into concrete actions
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", string);
        formData.put("destType", string2);
        formData.put("district", string3);
        formData.put("country", string4);
        formData.put("latitude", string5);
        formData.put("longitude", string6);
        Destination destination = User.find().byId(2).getDestinations().get(3);
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("the destination will be updated to the respective attributes of name {string}, type {string}, district {string}, country {string}, latitude {string} and longitude {string}")
    public void theDestinationWillBeUpdatedToTheRespectiveAttributesOfNameTypeDistrictCountryLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = User.find().byId(2).getDestinations().get(3);
        assertEquals(string, destination.getDestName());
        assertEquals(string2, destination.getDestType());
        assertEquals(string3, destination.getDistrict());
        assertEquals(string4, destination.getCountry());
        assertEquals(string5, Double.toString(destination.getLatitude()));
        assertEquals(string6, Double.toString(destination.getLongitude()));
    }

    @When("I delete my destination with name {string}")
    public void iDeleteMyDestinationWithName(String string) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        assertTrue(destination != null);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("the destination with name {string} will be deleted.")
    public void theDestinationWithNameWillBeDeleted(String string) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        assertTrue(destination == null);
    }


    @When("I mark {string} as public and the same public destination does not already exist")
    public void iMarkAsPublicAndTheSamePublicDestinationDoesNotAlreadyExist(String string) {
        // Write code here that turns the phrase above into concrete actions
        //throw new cucumber.api.PendingException();
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
        destination = Destination.find().query().where().eq("destName", string).findOne();
        assertTrue(destination.getIsPublic());
    }

    @Then("{string} should not be within my list of private destinations")
    public void shouldNotBeWithinMyListOfPrivateDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        List<Destination> destinationList = User.find().byId(2).getDestinations();
        boolean isDestinationFound = false;
        for (Destination destination : destinationList){
            if(!destination.getIsPublic()) {
                if (destination.getDestName().equals(string)) {
                    isDestinationFound = true;
                }
            }
        }
        assertEquals(false, isDestinationFound);
    }

    @Then("{string} should be within my list of public destinations")
    public void shouldBeWithinMyListOfPublicDestinations(String string) {
        // Write code here that turns the phrase above into concrete actions
        List<Destination> destinationList = User.find().byId(2).getDestinations();
        boolean isDestinationFound = false;
        for (Destination destination : destinationList){
            if(destination.getIsPublic()) {
                if (destination.getDestName().equals(string)) {
                    isDestinationFound = true;
                }
            }
        }
        assertEquals(true, isDestinationFound);
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

    @When("I create another destination with name {string} of type {string} at district {string} at country {string} at latitude {string} and longitude {string}")
    public void iCreateAnotherDestinationWithNameOfTypeAtDistrictAtCountryAtLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6) {
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

    @Then("the destination should not be created and there should only be one {string} in the database because it already exists.")
    public void theDestinationShouldNotBeCreatedAndThereShouldOnlyBeOneInTheDatabaseBecauseItAlreadyExists(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new cucumber.api.PendingException();
    }

    @Given("I mark {string} as public and nobody uses the destination")
    public void iMarkAsPublicAndNobodyUsesTheDestination(String string) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
        destination = Destination.find().query().where().eq("destName", string).findOne();
        assertTrue(destination.getIsPublic());
    }

    @When("I validly update the destination {string} with name {string}, type {string}, district {string}, country {string}, latitude {string}, longitude {string}")
    public void iValidlyUpdateTheDestinationWithNameTypeDistrictCountryLatitudeLongitude(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", string2);
        formData.put("destType", string3);
        formData.put("district", string4);
        formData.put("country", string5);
        formData.put("latitude", string6);
        formData.put("longitude", string7);
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Then("the destination {string} should be updated to name {string}, type {string}, district {string}, country {string}, latitude {string}, longitude {string}")
    public void theDestinationShouldBeUpdatedToNameTypeDistrictCountryLatitudeLongitude(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        //the original name should no longer exist
        assertNull(destination);
        destination = Destination.find().query().where().eq("destName", string2).findOne();
        assertNotNull(destination);
        assertEquals(string2, destination.getDestName());
        assertEquals(string3, destination.getDestType());
        assertEquals(string4, destination.getDistrict());
        assertEquals(string5, destination.getCountry());
        assertEquals(Double.parseDouble(string6), destination.getLatitude(), 0.01);
        assertEquals(Double.parseDouble(string7), destination.getLongitude(), 0.01);
    }

    @Given("I mark {string} as public and a user with user id {string} uses the destination")
    public void iMarkAsPublicAndAUserWithUserIdUsesTheDestination(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/public/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
        destination = Destination.find().query().where().eq("destName", string).findOne();
        assertTrue(destination.getIsPublic());
        //add Wellington to Christchurch to Wellington, to The Wok and back
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/trips/table/edit/3/" + destination.getDestId()).session("connected", string2);
        result = route(application, request);
        assertEquals(SEE_OTHER, result.status());
        boolean tripContainsDestination = false;
        for(Visit visit : Trip.find().byId(3).getVisits()){
            if(visit.getDestination().getDestId() == destination.getDestId()){
                tripContainsDestination = true;
            }
        }
        assertTrue(tripContainsDestination);
    }

    @When("I invalidly update the destination {string}")
    public void iInvalidlyUpdateTheDestination(String string) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/" + destination.getDestId()).session("connected", "2");
        Result result = route(application, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Then("the destination {string} should not be updated")
    public void theDestinationShouldNotBeUpdated(String string) {
        // Write code here that turns the phrase above into concrete actions
        Destination destination = Destination.find().query().where().eq("destName", string).findOne();
        assertFalse(destination.getDestName().equals("Summoner's Rift"));
        assertFalse(destination.getDestType().equals("Yes"));
        assertFalse(destination.getDistrict().equals("Demacia"));
        assertFalse(destination.getCountry().equals("Angola"));
        assertFalse(destination.getLatitude() == 50.0);
        assertFalse(destination.getLongitude() == -50.0);
    }

    @When("a user with user id {string} creates a destination with name {string} of type {string} at district {string} at country {string} at latitude {string} and longitude {string}")
    public void aUserWithUserIdCreatesADestinationWithNameOfTypeAtDistrictAtCountryAtLatitudeAndLongitude(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
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
