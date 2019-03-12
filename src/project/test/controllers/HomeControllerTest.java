package controllers;

import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class HomeControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void setUpDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        //Initialises a test user with name "testUser" and saves it to the database.
        User user = new User("testUser");
        user.save();
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
     * Test to render home with no login session
     */
    @Test
    public void showHomeWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render home with a login session and user with no profile
     */
    @Test
    public void showHomeWithLoginSessionWithoutProfile() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render home with a login session and user with no traveller type
     */
    @Test
    public void showHomeWithLoginSessionWithProfileWithoutTravellerType() {
        createUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render home with a login session and user with a traveller type but no nationality
     */
    @Test
    public void showHomeWithLoginSessionWithProfileWithTravellerTypeWithoutNationality() {
        createUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", "3");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render home with a login session and user with a traveller type with a nationality
     */
    @Test
    public void showHomeWithLoginSessionWithProfileWithTravellerTypeWithNationality() {
        createUser();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", "4");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    public void createUser(){
        TravellerTypeController tTC = new TravellerTypeController();
        tTC.addTravelTypes();
        ProfileController pC = new ProfileController();
        pC.addNatandPass();
        TravellerType travellerType1 = TravellerType.find.byId(1);
        TravellerType travellerType2 = TravellerType.find.byId(2);
        Nationality nationality1 = Nationality.find.byId(1);
        Nationality nationality2 = Nationality.find.byId(2);
        Passport passport1 = Passport.find.byId(1);
        Passport passport2 = Passport.find.byId(2);
        //Initialises a test user with name "testUser" and saves it to the database.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //convert String to LocalDate
        LocalDate birthDate = LocalDate.parse("1998-08-23", formatter);
        User user = new User("gon12@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user2 = new User("gon12@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user3 = new User("gon12@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        user.getNationality().add(nationality1);
        user.getNationality().add(nationality2);
        user.getPassport().add(passport1);
        user.getPassport().add(passport2);
        user.save();
        user2.getTravellerTypes().add(travellerType1);
        user2.getTravellerTypes().add(travellerType2);
        user2.getPassport().add(passport1);
        user2.getPassport().add(passport2);
        user2.save();
        user3.getTravellerTypes().add(travellerType1);
        user3.getTravellerTypes().add(travellerType2);
        user3.getPassport().add(passport1);
        user3.getPassport().add(passport2);
        user3.getNationality().add(nationality1);
        user3.getNationality().add(nationality2);
        user3.save();
    }
}