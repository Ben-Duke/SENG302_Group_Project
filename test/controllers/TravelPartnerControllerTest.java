package controllers;

import models.*;
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
import utilities.UtilityFunctions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;



public class TravelPartnerControllerTest extends WithApplication {
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
        UtilityFunctions.addAllNationalities();
        UtilityFunctions.addAllPassports();
        UtilityFunctions.addTravellerTypes();
        TravellerType travellerType1 = TravellerType.find.byId(1);
        TravellerType travellerType2 = TravellerType.find.byId(2);
        TravellerType travellerType3 = TravellerType.find.byId(3);
        Nationality nationality1 = Nationality.find.byId(1);
        Nationality nationality2 = Nationality.find.byId(2);
        Nationality nationality3 = Nationality.find.byId(3);
        Passport passport1 = Passport.find.byId(1);
        Passport passport2 = Passport.find.byId(2);
        Passport passport3 = Passport.find.byId(3);
        //Initialises a test user with name "testUser" and saves it to the database.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //convert String to LocalDate
        LocalDate birthDate = LocalDate.parse("1998-08-23", formatter);
        User user = new User("gon12@uclive.ac.nz", "hunter2", "Gavin", "Ong", birthDate, "Male");
        user.getTravellerTypes().add(travellerType1);
        user.getTravellerTypes().add(travellerType2);
        user.getNationality().add(nationality1);
        user.getNationality().add(nationality2);
        user.getPassport().add(passport1);
        user.getPassport().add(passport2);
        user.save();
        LocalDate birthDate2 = LocalDate.parse("1995-08-12", formatter);
        User user2 = new User("test@uclive.ac.nz", "hunter2", "Billie", "Bob", birthDate2, "Female");
        user2.getTravellerTypes().add(travellerType2);
        user2.getTravellerTypes().add(travellerType3);
        user2.getNationality().add(nationality2);
        user2.getNationality().add(nationality3);
        user2.getPassport().add(passport2);
        user2.getPassport().add(passport3);
        user2.save();
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    /**
     * Unit test for rendering the search profile page
     */
    @Test
    public void renderFilterPage() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/search").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/search").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
    }



    @Test
    public void searchByMaleAndFemaleOnly() {
        Map<String, String> formData = new HashMap<>();
        formData.put("gender[0", "Female");
        formData.put("gender[1", "Male");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());
    }

    @Test
    public void nationalitySearch() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationality", "New Zealand");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());
    }

    @Test
    public void searchByTravellerType() {
        Map<String, String> formData = new HashMap<>();
        formData.put("travellertype", "Groupie");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);

        Result result = Helpers.route(app, fakeRequest);
        assertEquals(OK, result.status());


    }

    @Test
    public void searchByDate() {
        Map<String, String> formData = new HashMap<>();
        formData.put("agerange1", "1998-08-22");
        formData.put("agerange2", "1998-08-24");

        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(fakeRequest);
        Result result = Helpers.route(app, fakeRequest);

        assertEquals(OK, result.status());

    }


}