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
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class ProfileControllerTest extends WithApplication {

    /**
     * The fake database
     */
    Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    /**
     * Sets up the fake database before each test
     */
    @Before
    public void setUpDatabase() {
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        createUser();
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Test
    public void createprofileWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/update").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void createprofileWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/update").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void updateprofile() {
        User user = User.find.byId(1);
        //user.setAdmin(true);
        user.update();
        assertEquals("Gavin", user.getfName());
        assertEquals("Ong", user.getlName());
        assertEquals("Male", user.getGender());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate expectedBirthDate = LocalDate.parse("1998-08-23", formatter);
        assertEquals(expectedBirthDate, user.getDateOfBirth());
        Map<String, String> formData = new HashMap<>();
        formData.put("fName", "John");
        formData.put("lName", "Cena");
        formData.put("gender", "Female");
        formData.put("dateOfBirth", "1969-04-20");
        formData.put("admin", "true");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find.byId(1);
        assertEquals("John", user.getfName());
        assertEquals("Cena", user.getlName());
        assertEquals("Female", user.getGender());
        LocalDate expectedBirthDate2 = LocalDate.parse("1969-04-20", formatter);
        assertEquals(expectedBirthDate2, user.getDateOfBirth());
    }

    @Test
    public void showProfileWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void showProfileWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/1").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void updateNatPass() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/profile/update/natpass").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void submitUpdateNationalityWithDifferentNationality() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationality", "3");
        User user = User.find.byId(1);
        assertEquals(2, user.nationality.size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addnat").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find.byId(1);
        assertEquals(3, user.nationality.size());
    }

    @Test
    public void submitUpdateNationalityWithSameNationality() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationality", "2");
        User user = User.find.byId(1);
        assertEquals(2, user.nationality.size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addnat").session("connected", "1");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        assertEquals(2, user.nationality.size());
    }

    @Test
    public void submitUpdatePassportWithDifferentPassport() {
        Map<String, String> formData = new HashMap<>();
        formData.put("passport", "3");
        User user = User.find.byId(1);
        assertEquals(2, user.passports.size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addpass").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find.byId(1);
        assertEquals(3, user.passports.size());
    }

    @Test
    public void submitUpdatePassportWithSamePassport() {
        Map<String, String> formData = new HashMap<>();
        formData.put("passport", "2");
        User user = User.find.byId(1);
        assertEquals(2, user.passports.size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/addpass").session("connected", "1");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void deleteNationality() {
        Map<String, String> formData = new HashMap<>();
        formData.put("nationalitydelete", "2");
        User user = User.find.byId(1);
        assertEquals(2, user.nationality.size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/delnat").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find.byId(1);
        assertEquals(1, user.nationality.size());
    }

    @Test
    public void deletePassport() {
        Map<String, String> formData = new HashMap<>();
        formData.put("passportdelete", "2");
        User user = User.find.byId(1);
        assertEquals(2, user.passports.size());
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).uri("/users/profile/update/natpass/delpass").session("connected", "1");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        user = User.find.byId(1);
        assertEquals(1, user.passports.size());
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
        User user2 = new User("gon23@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user3 = new User("gon34@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
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