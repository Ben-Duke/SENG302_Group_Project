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
        TravellerTypeController tTC = new TravellerTypeController();
        tTC.addTravelTypes();
        ProfileController pC = new ProfileController();
        pC.addNatandPass();
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

    /**
     * Unit test for searching by attribute
     */
    @Test
    public void searchByAttribute() {
        Map<String, String> formData = new HashMap<>();
        //Assuming the user fills in the filters as "triptest123"
        formData.put("travellertype", "Groupie");
        formData.put("nationality", "1");
        formData.put("gender", "Male");
        formData.put("agerange1", "1998-08-22");
        formData.put("agerange2", "1998-08-24");


        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");

        Result result = Helpers.route(app, fakeRequest);
        //User should receive BAD REQUEST since the connected user does not see themselves in the search
        assertEquals(BAD_REQUEST, result.status());

//        fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
//        result = Helpers.route(app, fakeRequest);
//        //One user (user id 1) should be found, and the server will return an OK response.
//        assertEquals(OK, result.status());

        User user = User.find.byId(1);
        //Set user's gender to female
        user.setGender("Female");
        user.update();
        //Because gender has changed, search should no longer return a result.
        fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
        result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());

        //Reverting gender change
        user.setGender("Male");
        user.update();
        //Remove the user's nationality
        //user.getNationality().remove(Nationality.find.byId(1));
        //user.update();
        //Because nationality has changed, search should no longer return a result.
        /*fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
        result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());

        //Reverting nationality change
        user.addNationality(Nationality.find.byId(1));
        user.update();*/


//        //Reverting traveller type change
//        user.getTravellerTypes().add(TravellerType.find.byId(1));
//        user.update();
//        //Making sure it's still working
//        fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
//        result = Helpers.route(app, fakeRequest);
//        //One user (user id 1) should be found, and the server will return an OK response.
//        assertEquals(OK, result.status());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Set user's birth date to one day before the age range (out of range)
        LocalDate birthDate = LocalDate.parse("1998-08-21", formatter);
        user.setDateOfBirth(birthDate);
        user.update();
        //Because age is out of range, search should no longer return a result.
        fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
        result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());

    }

    @Test
    public void searchByTravellerType() {
        Map<String, String> formData = new HashMap<>();
        formData.put("travellertype", "Groupie");
        formData.put("nationality", "1");
        formData.put("gender", "Male");
        formData.put("agerange1", "1998-08-22");
        formData.put("agerange2", "1998-08-24");

        User user = User.find.byId(1);
        Http.RequestBuilder fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "1");
        Result result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());

        /*fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
        result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());*/

        //Removing "Groupie" from user traveller type
        /*user.getTravellerTypes().remove(0);
        user.update();
        //Because traveller type has changed, search should no longer return a result.
        fakeRequest = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/search").session("connected", "2");
        result = Helpers.route(app, fakeRequest);
        assertEquals(BAD_REQUEST, result.status());*/
    }

}