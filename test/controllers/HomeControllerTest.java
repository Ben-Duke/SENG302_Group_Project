package controllers;

import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.JsonNode;
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
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.apache.commons.io.FileUtils.getFile;
import static org.junit.Assert.*;
import static play.mvc.Http.HttpVerbs.POST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;

public class HomeControllerTest extends BaseTestWithApplicationAndDatabase {

    /**
     * Test to render home with no login session
     */
    @Test
    public void showHomeWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render home with a login session and user with no profile
     */
    @Test
    public void showHomeWithLoginSessionWithoutProfile() {
        User user = new User("testuser@test.com");
        user.save();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home").session("connected", "5");
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
                .uri("/users/home").session("connected", "5");
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
                .uri("/users/home").session("connected", "5");
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

    /**
     * Test of file upload with a test file imagetest.png
     * @throws IOException
     */
    @Test
    public void photoUpload() throws IOException {
        createUser();
        File file = getFile(Paths.get(".").toAbsolutePath().normalize().toString() + "/test/resources/imagetest.png");
        Http.MultipartFormData.Part<Source<ByteString, ?>> part = new Http.MultipartFormData.FilePart<>("picture", "imagetest.png", "image/png", FileIO.fromPath(file.toPath()), Files.size(file.toPath()));
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/home").session("connected", "2")
                .bodyRaw(Collections.singletonList(part),
                        play.libs.Files.singletonTemporaryFileCreator(),
                        app.asScala().materializer());
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test of uploading a profile picture
     * @throws IOException
     */
    @Test
    public void uploadProfilePicture() throws IOException {
        createUser();
        File file = getFile(Paths.get(".").toAbsolutePath().normalize().toString() + "/test/resources/imagetest.png");
        Http.MultipartFormData.Part<Source<ByteString, ?>> part = new Http.MultipartFormData.FilePart<>("picture", "imagetest.png", "image/png", FileIO.fromPath(file.toPath()), Files.size(file.toPath()));
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/home/profilePicture").session("connected", "2")
                .bodyRaw(Collections.singletonList(part),
                        play.libs.Files.singletonTemporaryFileCreator(),
                        app.asScala().materializer());
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test of uploading a profile picture
     * @throws IOException
     */
    @Test
    public void uploadProfilePictureWithInvalidLogin() throws IOException {
        createUser();
        File file = getFile(Paths.get(".").toAbsolutePath().normalize().toString() + "/test/resources/imagetest.png");
        Http.MultipartFormData.Part<Source<ByteString, ?>> part = new Http.MultipartFormData.FilePart<>("picture", "imagetest.png", "image/png", FileIO.fromPath(file.toPath()), Files.size(file.toPath()));
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/home/profilePicture").session("connected", null)
                .bodyRaw(Collections.singletonList(part),
                        play.libs.Files.singletonTemporaryFileCreator(),
                        app.asScala().materializer());
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void serveFromIdWithPublicPhoto(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveDestPicture/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void serveFromIdWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveDestPicture/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void serveFromIdWithPrivatePhotoAndValidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveDestPicture/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void serveFromIdWithPrivatePhotoAndInvalidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveDestPicture/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void serveFromIdWithPrivatePhotoAndAdmin(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveDestPicture/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void index(){
        UserPhoto photo = UserPhoto.find.byId(1);
        String path = photo.getUrlWithPath();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.index(path).url()).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        String fileAsString = convertResultFileToString(result);
        assertNotNull(fileAsString);
    }

    @Test
    public void serveProfilePictureForUserWithProfilePicture(){
        UserPhoto photo = UserPhoto.find.byId(1);
        String path = photo.getUrlWithPath();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.index(path).url()).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        String fileAsString = convertResultFileToString(result);
        assertNotNull(fileAsString);
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveProfilePicture/2").session("connected", "2");
        result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(fileAsString, convertResultFileToString(result));
    }

    /**
     * Checks the serveProfilePicture method returns an ok (200) status when getting
     * a users profile picture when they don't have one (it sends the generic
     * placeholder).
     */
    @Test
    public void serveProfilePicture_ForUserWithoutProfilePicture_check200Status(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveProfilePicture/4").session("connected", "4");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void serveProfilePictureWithoutLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveProfilePicture/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void serveOtherProfilePictureForUserWithProfilePicture(){
        UserPhoto photo = UserPhoto.find.byId(1);
        String path = photo.getUrlWithPath();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.index(path).url()).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        String fileAsString = convertResultFileToString(result);
        assertNotNull(fileAsString);
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/home/serveProfilePicture/2").session("connected", "1");
        result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(fileAsString, convertResultFileToString(result));
    }

    @Test
    public void setProfilePictureWithValidPhotoAndValidUser(){
        //userPhoto1 is the profile picture
        UserPhoto userPhoto1 = UserPhoto.find.byId(1);
        //userPhoto2 is not the profile picture
        UserPhoto userPhoto2 = UserPhoto.find.byId(2);
        assertTrue(userPhoto1.isProfile());
        assertFalse(userPhoto2.isProfile());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/users/home/setProfilePicture/2").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        userPhoto1 = UserPhoto.find.byId(1);
        userPhoto2 = UserPhoto.find.byId(2);
        //Oh how the tides have turned
        assertFalse(userPhoto1.isProfile());
        assertTrue(userPhoto2.isProfile());
    }

    @Test
    public void setProfilePictureWithValidPhotoAndInvalidUser(){
        //userPhoto1 is the profile picture
        UserPhoto userPhoto1 = UserPhoto.find.byId(1);
        //userPhoto2 is not the profile picture
        UserPhoto userPhoto2 = UserPhoto.find.byId(2);
        assertTrue(userPhoto1.isProfile());
        assertFalse(userPhoto2.isProfile());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/users/home/setProfilePicture/2").session("connected", "3");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        userPhoto1 = UserPhoto.find.byId(1);
        userPhoto2 = UserPhoto.find.byId(2);
        //Oh how the tides have not turned
        assertTrue(userPhoto1.isProfile());
        assertFalse(userPhoto2.isProfile());
    }

    @Test
    public void setProfilePictureWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/users/home/setProfilePicture/2").session("connected", null);
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void setProfilePictureWithInvalidPhoto(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/users/home/setProfilePicture/100").session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void makePicturePublicWithValidPhotoWithValidUser(){
        UserPhoto userPhoto = UserPhoto.find.byId(2);
        assertFalse(userPhoto.isPublic());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.makePicturePublic(2,1).url()).session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        userPhoto = UserPhoto.find.byId(2);
        assertTrue(userPhoto.isPublic());
    }

    @Test
    public void makePicturePrivateWithValidPhotoWithValidUser(){
        UserPhoto userPhoto = UserPhoto.find.byId(1);
        assertTrue(userPhoto.isPublic());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.makePicturePublic(1,0).url()).session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        userPhoto = UserPhoto.find.byId(1);
        assertFalse(userPhoto.isPublic());
    }

    @Test
    public void makePicturePublicWithMissingPhoto(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.makePicturePublic(100,0).url()).session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void makePicturePublicWithInvalidLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.makePicturePublic(2,1).url()).session("connected", null);
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Test
    public void makePicturePrivateWithValidPhotoWithInvalidUser(){
        UserPhoto userPhoto = UserPhoto.find.byId(1);
        assertTrue(userPhoto.isPublic());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(routes.HomeController.makePicturePublic(1,0).url()).session("connected", "3");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        userPhoto = UserPhoto.find.byId(1);
        assertTrue(userPhoto.isPublic());
    }


    public void createUser(){
        UtilityFunctions.addAllNationalities();
        UtilityFunctions.addAllPassports();
        UtilityFunctions.addTravellerTypes();
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
        User user = new User("gon12_1@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user2 = new User("gon12_2@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
        User user3 = new User("gon12_3@uclive.ac.nz", "hunter22", "Gavin", "Ong", birthDate, "Male");
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

    public String convertResultFileToString(Result result){
        ActorSystem actorSystem = ActorSystem.create("TestSystem");
        try {
            Materializer mat = ActorMaterializer.create(actorSystem);
            String contentAsString = Helpers.contentAsString(result, mat);
            return contentAsString;
        } catch (Exception e){
            e.printStackTrace();
            fail();
        }
        finally {
            Future<Terminated> future = actorSystem.terminate();
            try {
                Await.result(future, Duration.create("5s"));
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        }
        return null;
    }

    /**
     * Tests the setProfilePhotoToNormalPhoto method returns a status 200 (OK)
     * when a user with an existing profile photo removes it.
     */
    @Test
    public void setProfilePhotoToNormalPhoto_withExistingProfilePhoto_checkStatus200() {
        UserPhoto profilePic = new UserPhoto("/test/url", true,
                                            true, User.find.byId(1));
        profilePic.save();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(Helpers.POST)
                .uri("/users/home/profilePicture1/removeProfilePictureStatus1")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Tests the setProfilePhotoToNormalPhoto method returns a status 400 (bad request)
     * when a user without an existing profile photo attempts to remove it.
     */
    @Test
    public void setProfilePhotoToNormalPhoto_withNoProfilePhoto_checkStatus400() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(Helpers.POST)
                .uri("/users/home/profilePicture1/removeProfilePictureStatus1")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }
}