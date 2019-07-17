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
import factories.UserFactory;
import models.TravellerType;
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
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.route;

public class TravellerTypeTestSteps extends BaseTestWithApplicationAndDatabase {


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
        TestDatabaseManager.populateDatabase();
    }

    @After
    public void tearDown(){
        Helpers.stop(application);
    }

    @Given("A new user is to register with TravelEA with their email {string} not already taken")
    public void aNewUserIsToRegisterWithTravelEAWithTheirEmailNotAlreadyTaken(String string) {
        int isTaken = UserFactory.checkEmail(string);
        Assert.assertEquals(0, isTaken);
    }

    @When("The user signs up with first name {string}, last name {string}, email {string}, password {string}, gender {string}, DOB {string}, Passports {string}, Nationalities {string}, the user selects the traveller type {string}")
    public void theUserSignsUpWithFirstNameLastNameEmailPasswordGenderDOBPassportsNationalities(String firstName, String lastName,
                                                                                                String email, String password,
                                                                                                String gender, String dob, String passport,
                                                                                                String nationality, String travellerType)
    {
        int initialSize = User.find().all().size();

        Map<String, String> formData = new HashMap<>();
        formData.put("email", email);
        formData.put("password", password);
        formData.put("firstName", firstName);
        formData.put("lastName", lastName);
        formData.put("passports", passport);
        formData.put("nationalities", nationality);
        formData.put("travellerTypes", travellerType);
        formData.put("gender", gender);
        formData.put("dob", dob);

        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/register");
        Result result = route(application, request);
        int finalSize = User.find().all().size();

        Assert.assertEquals(initialSize + 1, finalSize);
    }


    @Then("The traveller type {string} is associated with the users profile")
    public void theTravellerTypesAreAssociatedWithTheUsersProfile(String travellerTypeOne) {
        User newUser = User.find().all().get(4);
        Boolean isCorrectTravellerTypes = true;
        for (TravellerType travellerType : newUser.getTravellerTypes()) {
            if (!travellerType.getTravellerTypeName().equals(travellerTypeOne)) {
                isCorrectTravellerTypes = false;
            }
        }
        Assert.assertTrue(isCorrectTravellerTypes);
    }

    @Given("The user with email {string}, id {string} and traveller types {string} and {string} is signed up")
    public void theUserWithFirstNameLastNameEmailPasswordGenderDOBPassportsNationalitiesAndTravellerTypesAndIsSignedUp(String email, String id, String travellerTypeOne, String travellerTypeTwo) {
        User newUser =  User.find().all().get(Integer.parseInt(id));
        Assert.assertEquals(newUser.getEmail(), email);
        Assert.assertTrue(newUser.getTravellerTypes().get(0).getTravellerTypeName().equals(travellerTypeOne));
        Assert.assertTrue(newUser.getTravellerTypes().get(1).getTravellerTypeName().equals(travellerTypeTwo));
    }

    @When("The user with id {string} removes the traveller type {string}")
    public void theUserRemovesTheTravellerType(String id, String string) {
        User newUser =  User.find().all().get(Integer.parseInt(id));
        id = Integer.toString(Integer.parseInt(id) + 1);
        TravellerType travellerType = TravellerType.find.query().where().eq("travellerTypeName", string).findOne();
        Map<String, String> formData = new HashMap<>();
        System.out.println(newUser.getUserid() + ": " + travellerType.getTravellerTypeName());
        System.out.println(travellerType.getTtypeid().toString());
        formData.put("typeId", travellerType.getTtypeid().toString());
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/delete/" + travellerType.getTtypeid()).session("connected", id);
        Result result = Helpers.route(application, fakeRequest);
        System.out.println(result.status());
        Assert.assertEquals(1, newUser.getTravellerTypes().size());

    }

    @When("The user with id {string} removes the only remaining traveller type {string}")
    public void theUserWithIdRemovesTheOnlyRemainingTravellerType(String id, String string) {
        User newUser =  User.find().all().get(Integer.parseInt(id));
        id = Integer.toString(Integer.parseInt(id) + 1);
        TravellerType travellerType = TravellerType.find.query().where().eq("travellerTypeName", string).findOne();
        Map<String, String> formData = new HashMap<>();
        formData.put("typeId", travellerType.getTtypeid().toString());
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/delete/" + travellerType.getTtypeid()).session("connected", id);
        Result result = Helpers.route(application, fakeRequest);
        Assert.assertEquals(1, newUser.getTravellerTypes().size());
    }

    @Then("The user with id {string} has only one traveller type {string}")
    public void theUserHasOnlyOneTravellerType(String id, String string) {
        User newUser =  User.find().all().get(Integer.parseInt(id));
        Assert.assertEquals(1, newUser.getTravellerTypes().size());
        Assert.assertEquals(string, newUser.getTravellerTypes().get(0).getTravellerTypeName());
    }

    @Then("The type {string} is not removed and still is associated with the profile with id {string}")
    public void theTypeIsNotRemovedAndStillIsAssociatedWithTheProfile(String string, String id) {
        User newUser = User.find().all().get(Integer.parseInt(id));
        Assert.assertEquals(1, newUser.getTravellerTypes().size());
        Assert.assertEquals(string, newUser.getTravellerTypes().get(0).getTravellerTypeName());
    }

}
