package controllers;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.TravellerTypeAccessor;
import accessors.TagAccessor;
import accessors.TreasureHuntAccessor;
import accessors.VisitAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.DuplicateKeyException;
import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
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
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import java.util.*;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.PRECONDITION_REQUIRED;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;

public class DestinationControllerTest extends BaseTestWithApplicationAndDatabase {

    private final Logger logger = UtilityFunctions.getLogger();

    private int REDIRECT_HTTP_STATUS = SEE_OTHER;

    /**
     * Test to render destination index with no login session
     */
    @Test
    public void indexDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render destination index with a login session
     */
    @Test
    public void indexDestinationWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to render viewing a destination with no login session
     */
    @Test
    public void displayViewDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/view/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render viewing a destination with a login session
     */
    @Test
    public void displayViewDestinationWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/view/1").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to render creating a destination with no login session
     */
    @Test
    public void displayCreateDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/create/").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render creating a destination with a login session
     */
    @Test
    public void displayCreateDestinationWithLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/create/").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to handle saving a destination with no login session
     */
    @Test
    public void saveDestinationWithNoLoginSession() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/save").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to handle saving a destination with a login session
     */
    @Test
    public void saveDestinationWithLoginSession() {
        assertEquals(3, User.find().byId(2).getDestinations().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/save").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, User.find().byId(2).getDestinations().size());
    }

    /**
     * Test to check if an album is created when a destination is saved
     */
    @Test
    public void createAlbumOnDestinationSave() throws Exception {
        Integer albumSize = Album.find.all().size();
        assertEquals(3, User.find().byId(2).getDestinations().size());
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData)
                .method(POST).uri("/users/destinations/save").session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
        assertEquals(4, User.find().byId(2).getDestinations().size());
        assertEquals(1,
                DestinationAccessor.getDestinationsbyName("Summoner's Rift").size());
        if(DestinationAccessor.getDestinationsbyName("Summoner's Rift").size() > 0) {
            Destination dest = DestinationAccessor
                    .getDestinationsbyName("Summoner's Rift").get(0);
            assertEquals(1, dest.getAlbums().size());
            assertEquals(albumSize+1, Album.find.all().size());
        }
        else{
            fail();
        }
    }

    /**
     * Test to see if a non number value will be picked up by the validation
     */
    @Test
    public void saveDestinationBadLatitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0a");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    /**
     * Test to check for an invalid longitude value as it is out of range
     */
    @Test
    public void saveDestinationOutOfRangeLongitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0");
        formData.put("longitude", "-181");
        formData.put("destType", "Country");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        CSRFTokenHelper.addCSRFToken(request);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }


    /**
     * Tests the unlinking of a photo and deleting it.
     */
    @Test
    public void checkUnlinkFromDestinationAndDelete(){
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData)
                .method(POST).uri("/users/destinations/save").session("connected", "2");
        route(app, request);
        Destination destination = DestinationAccessor
                .getDestinationsbyName("Summoner's Rift").get(0);
        UserPhoto photo = new UserPhoto("/test",true,false,User.find().byId(2));
        photo.save();
        destination.getPrimaryAlbum().addMedia(photo);
        AlbumAccessor.update(destination.getPrimaryAlbum());

        int beforeDeletion = UserPhoto.find().all().size();

        request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/unlinkAndDeletePicture/" + photo.getMediaId())
                .session("connected", "2");
        CSRFTokenHelper.addCSRFToken(request);
        route(app, request);

        assertEquals(beforeDeletion - 1, UserPhoto.find().all().size());
    }


    /**
     * Test to check if boundary longitude value is not picked up by the validation
     */
    @Test
    public void saveDestinationBoundaryRangeLongitude() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "10.0");
        formData.put("longitude", "-180.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(Helpers.POST).
                uri("/users/destinations/save").session("connected", "1");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
    }

    /**
     * Test to render editing a destination with no login session
     */
    @Test
    public void editDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to render editing a destination with a login session but invalid destination
     */
    @Test
    public void editDestinationWithLoginSessionAndInvalidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/50").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }


    /**
     * Test to render editing a destination that is private and belongs to another user
     */
    @Test
    public void editPrivateDestinationWithInvalidOwner() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to render editing a destination with a login session and valid destination and valid owner
     */
    @Test
    public void editDestinationWithLoginSessionAndValidDestinationAndValidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/edit/2").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    /**
     * Test to handle updating a destination with no login session
     */
    @Test
    public void updateDestinationWithNoLoginSession() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and invalid destination
     */
    @Test
    public void updateDestinationWithLoginSessionAndInvalidDestination() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/50").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and valid destination but invalid owner
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndInvalidOwner() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/3").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle updating a destination with a login session and valid destination and valid owner
     */
    @Test
    public void updateDestinationWithLoginSessionAndValidDestinationAndValidOwner() {
        assertEquals("Christchurch", User.find().byId(2).getDestinations().get(0).getDestName());
        assertEquals("Town", User.find().byId(2).getDestinations().get(0).getDestType());
        assertEquals("Canterbury", User.find().byId(2).getDestinations().get(0).getDistrict());
        assertEquals("New Zealand", User.find().byId(2).getDestinations().get(0).getCountry());
        assertEquals(-43.5321, User.find().byId(2).getDestinations().get(0).getLatitude(), 0.01);
        assertEquals(172.6362, User.find().byId(2).getDestinations().get(0).getLongitude(), 0.01);
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder request = Helpers.fakeRequest().bodyForm(formData).method(POST).uri("/users/destinations/update/1").session("connected", "2");
        Result result = route(app, request);
        assertEquals(REDIRECT_HTTP_STATUS, result.status());
        assertEquals("Summoner's Rift", User.find().byId(2).getDestinations().get(0).getDestName());
        assertEquals("Yes", User.find().byId(2).getDestinations().get(0).getDestType());
        assertEquals("Demacia", User.find().byId(2).getDestinations().get(0).getDistrict());
        assertEquals("Angola", User.find().byId(2).getDestinations().get(0).getCountry());
        assertEquals(50.0, User.find().byId(2).getDestinations().get(0).getLatitude(), 0.01);
        assertEquals(-50.0, User.find().byId(2).getDestinations().get(0).getLongitude(), 0.01);
    }

    /**
     * Test to handle deleting a destination with no login session
     */
    @Test
    public void deleteDestinationWithNoLoginSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session but invalid destination
     */
    @Test
    public void deleteDestinationWithLoginSessionAndInvalidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/50").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session and valid destination but invalid owner
     */
    @Test
    public void deleteDestinationWithLoginSessionAndValidDestinationAndInvalidOwner() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/1").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle deleting a destination with a login session and valid destination and valid owner
     * where the destination is being used in trips. This will fail.
     */
    @Test
    public void deleteDestinationWithLoginSessionAndValidDestinationAndValidOwnerWithDestinationInTrips() {
        assertEquals(3, User.find().byId(2).getDestinations().size());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(PRECONDITION_REQUIRED, result.status());
        assertEquals(3, User.find().byId(2).getDestinations().size());
    }


    private void deleteDestinationAndUndo(int destId, String userId) {
        // delete the destination
        Http.RequestBuilder deleteRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/" + destId).session("connected", userId);
        route(app, deleteRequest);

        // undo the deletion
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/undo").session("connected", userId);
        route(app, undoRequest);
    }

    @Test
    /* Undo the deletion of a destination and check the destination is not deleted
    *  Admin user used to check that treasure hunts/visits are remade
    *  Covers normal user flow */
    public void deleteDestination_asAdmin_undo_checkDestinationExists() {

        int destinationSize = DestinationAccessor.getAllDestinations().size();
        int visitSize = VisitAccessor.getAll().size();
        int treasureHuntSize = TreasureHuntAccessor.getAll().size();
        int albumSize = AlbumAccessor.getAll().size();
        int destId = 1;
        String adminId = "1";

        deleteDestinationAndUndo(destId, adminId);

        assertEquals(destinationSize, DestinationAccessor.getAllDestinations().size());
        assertEquals(treasureHuntSize, TreasureHuntAccessor.getAll().size());
        assertEquals(albumSize, AlbumAccessor.getAll().size());
        assertEquals(visitSize, VisitAccessor.getAll().size());
    }


    @Test
    /* Undo the deletion of a destination and check the destination is not deleted
     *  Admin user used to check that treasure hunts/visits are remade
     *  Covers normal user flow */
    public void deleteDestination_asAdmin_undo_redo_checkDestinationDeleted() {
        int destinationSize = DestinationAccessor.getAllDestinations().size();
        int visitSize = VisitAccessor.getAll().size();
        int albumSize = AlbumAccessor.getAll().size();
        int treasureHuntSize = TreasureHuntAccessor.getAll().size();

        int destId = 1;
        String adminId = "1";

        Destination destination = DestinationAccessor.getDestinationById(destId);
        int destinationVisits = destination.getVisits().size();
        int destinationTreasureHunts = TreasureHuntAccessor.getByDestination(
                DestinationAccessor.getDestinationById(destId)).size();

        deleteDestinationAndUndo(destId, adminId);

        // redo the deletion
        Http.RequestBuilder redoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/redo").session("connected", adminId);
        route(app, redoRequest);

        assertEquals(destinationSize-1, DestinationAccessor.getAllDestinations().size());
        assertEquals(albumSize-1, AlbumAccessor.getAll().size());
        assertEquals(treasureHuntSize-destinationTreasureHunts, TreasureHuntAccessor.getAll().size());
        assertEquals(visitSize-destinationVisits, VisitAccessor.getAll().size());
    }




    /**
     * Test to handle making a destination public with no login session
     */
    @Test
    public void makeDestinationPublicWithoutLoginSession(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/update/make_public/1").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to handle making an invalid destination public
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithInvalidDestination(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/update/make_public/20").session("connected", "1");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to handle making a destination public when you do not own the destination
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithValidDestinationWithInvalidOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/update/make_public/2").session("connected", "4");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to handle making a destination public when you own the destination.
     * Destination should be changed to public and it's owner should be changed to the default admin.
     */
    @Test
    public void makeDestinationPublicWithLoginSessionWithValidDestinationWithValidOwner(){
        Destination destination = Destination.find().byId(2);
        assertEquals(false, destination.getIsPublic());
        assertEquals(2, destination.getUser().getUserid());
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/update/make_public/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        destination = Destination.find().byId(3);
        assertEquals(true, destination.getIsPublic());
        assertEquals(2, destination.getUser().getUserid());
    }

    private Result makeDestinationPublic(int destId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/update/make_public/" + destId).session("connected", "2"); // 2 = userId
        return route(app, request);
    }

    @Test
    public void makeDestinationPublic_noMerge_checkDestinationOwnerIsNull(){
        int destId = 2;

        // Set destination to public (will not merge with existing public destination)
        Result result = makeDestinationPublic(destId);
        assertEquals(OK, result.status());

        // Check that the destination owner is null
        Destination destination = DestinationAccessor.getDestinationById(destId);
        assertNull(destination.getUser());
    }

    @Test
    public void makeDestinationPublic_withMerge_checkDestinationOwnerIsNull() {
        int destId = 2;

        // Create a similar destination to the destination
        Destination destCopy = new Destination(DestinationAccessor.getDestinationById(destId));
        destCopy.save();

        // Set destination to public
        Result result = makeDestinationPublic(destId);
        assertEquals(OK, result.status());

        // Set the copy to public (the existing public destination will be merged into it)
        int copyId = destCopy.getDestId();
        Result copyResult = makeDestinationPublic(copyId);
        assertEquals(OK, result.status());

        // Check that the destination owner is null - destinations are merged into the one being made public
        Destination destination = DestinationAccessor.getDestinationById(copyId);
        assertNull(destination.getUser());
    }

    /**
     * Unit test for ajax request to get the owner of a destination given by a destination id
     */
    @Test
    public void getDestinationOwner(){
        //user with user id 2 owns destination 2. Any user (eg user id 3) can get the owner of a destination.
        //considering changing the method to consider private destinations in the future
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/owner/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(2, Integer.parseInt(contentAsString(result)));
        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/owner/4").session("connected", "3");
        result = route(app, request);
        assertEquals(OK, result.status());
        assertEquals(3, Integer.parseInt(contentAsString(result)));
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This should work because public destinations should be accessible by anyone
     */
    @Test
    public void getDestinationAsPublicDestinationWithUserWhoIsNotOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/3").session("connected", "3");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        JsonNode jsonJacksonObjectOne = Json.parse(contentAsString(result));
        assertEquals("The Wok", jsonJacksonObjectOne.get("destName").asText());

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/1").session("connected", "3");
        result = route(app, request);
        assertEquals(OK, result.status());
        JsonNode jsonJacksonObjectTwo = Json.parse(contentAsString(result));
        assertEquals("Christchurch", jsonJacksonObjectTwo.get("destName").asText());
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This should work because public destinations should be accessible by anyone including the owner
     */
    @Test
    public void getDestinationAsPublicDestinationWithUserWhoIsOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        JsonNode jsonObject = Json.parse(contentAsString(result));
        assertEquals("The Wok", jsonObject.get("destName").asText());
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This shouldn't work because private destinations should only be accessible by the owner
     */
    @Test
    public void getDestinationAsPrivateDestinationWithUserWhoIsNotOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/2").session("connected", "3");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Unit test for ajax request to get a destination given by a destination id
     * This should work because private destinations should only be accessible by the owner
     */
    @Test
    public void getDestinationAsPrivateDestinationWithUserWhoIsOwner(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/get/2").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
        JsonNode jsonObject = Json.parse(contentAsString(result));
        assertEquals("Wellington", jsonObject.get("destName").asText());
    }

    @Test
    public void getDestinationTravellerTypes(){
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/ttypes/3").session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        JsonNode jsonJacksonArray = Json.parse(contentAsString(result));

        JsonNode nodeOne = jsonJacksonArray.get(0);
        JsonNode nodeTwo = jsonJacksonArray.get(1);

        //Groupie and gap year so length 2
        assertEquals(2, jsonJacksonArray.size());

        assertEquals("Groupie", nodeOne.get("travellerTypeName").asText());
        assertEquals("Gap Year", nodeTwo.get("travellerTypeName").asText());
    }

    @Test
    public void getVisibleDestinationMarkersJSONNotLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/getalljson").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    /** THIS TEST IS EXPECTED TO FAIL LOCALLY
     *
     *  However it will pass on the pipeline, for some reason the public field only
     *  gets added on the server.
     */
    @Test
    public void getVisibleDestinationMarkersJSONLoggedIn() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/getalljson").session("connected", "2");
        Result result = route(app, request);

        JsonNode jsonJacksonNodeArray= Json.parse(contentAsString(result));

        assertEquals(6, jsonJacksonNodeArray.size());
    }

    @Test
    public void destinationModificationRequestReject() {
        User user = User.find().all().get(0);
        Destination newDestination = new Destination("Test Dest", "Town", "Test District", "Test Country", 100, 100, user, true);
        newDestination.save();
        Integer destId = newDestination.getDestId();

        Destination newDestinationValues = new Destination("Test Dest2", "Town2", "Test District2", "Test Country2", 101, 101, user);

        Destination destination = Destination.find().byId(destId);

        DestinationModificationRequest modReq = new DestinationModificationRequest(destination, newDestinationValues, user);
        modReq.save();

        Integer modReqId = modReq.getId();

        Admin admin = Admin.find().all().get(0);
        Integer adminUserId = admin.getUserId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/destination_modification_request/reject/"+modReqId)
                .session("connected", adminUserId.toString());

        Result result = route(app, request);

        assertEquals(303, result.status());
        assert(destination.getDestName().equals("Test Dest"));
        assert(destination.getDestType().equals("Town"));
        assert(destination.getLatitude() == 100);
        assertEquals(null, DestinationModificationRequest.find().query().where().eq("id", modReqId).findOne());

    }

    @Test
    public void destinationModificationRequestAcceptWithoutTravellerTypes() {
        User user = User.find().all().get(0);
        Destination newDestination = new Destination("Test Dest", "Town", "Test District", "Test Country", 100, 100, user, true);
        newDestination.save();
        Integer destId = newDestination.getDestId();

        Destination newDestinationValues = new Destination("Test Dest2", "Town2", "Test District2", "Test Country2", 101, 101, user);

        DestinationModificationRequest modReq = new DestinationModificationRequest(newDestination, newDestinationValues, user);
        modReq.save();

        Integer modReqId = modReq.getId();

        Admin admin = Admin.find().all().get(0);
        Integer adminUserId = admin.getUserId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/destination_modification_request/accept/"+modReqId)
                .session("connected", adminUserId.toString());

        Result result = route(app, request);

        Destination destination = Destination.find().byId(destId);

        assertEquals(303, result.status());

        assert(destination.getDestName().equals("Test Dest2"));
        assert(destination.getDestType().equals("Town2"));
        assert(destination.getDistrict().equals("Test District2"));
        assert(destination.getCountry().equals("Test Country2"));
        assert(destination.getLatitude() == 101);
        assert(destination.getLatitude() == 101);
        assertEquals(null, DestinationModificationRequest.find().query().where().eq("id", modReqId).findOne());

    }


    @Test
    public void destinationModificationRequestAcceptWithTravellerTypes() {
        User user = User.find().all().get(0);
        Destination newDestination = new Destination("Test Dest", "Town", "Test District", "Test Country", 100, 100, user, true);
        newDestination.save();
        Integer destId = newDestination.getDestId();

        Destination newDestinationValues = new Destination("Test Dest2", "Town2", "Test District2", "Test Country2", 101, 101, user);
        Set<TravellerType> travellerTypes = new TreeSet<>();
        travellerTypes.add(TravellerTypeAccessor.getByName("Backpacker"));
        travellerTypes.add(TravellerTypeAccessor.getByName("Groupie"));

        newDestinationValues.setTravellerTypes(travellerTypes);

        DestinationModificationRequest modReq = new DestinationModificationRequest(newDestination, newDestinationValues, user);
        modReq.save();

        Integer modReqId = modReq.getId();

        Admin admin = Admin.find().all().get(0);
        Integer adminUserId = admin.getUserId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/admin/destination_modification_request/accept/"+modReqId)
                .session("connected", adminUserId.toString());

        Result result = route(app, request);

        Destination destination = Destination.find().byId(destId);

        assertEquals(303, result.status());
        assert(destination.getDestName().equals("Test Dest2"));
        assert(destination.getDestType().equals("Town2"));
        assert(destination.getDistrict().equals("Test District2"));
        assert(destination.getCountry().equals("Test Country2"));
        assert(destination.getLatitude() == 101);
        assert(destination.getLatitude() == 101);

        for (TravellerType travellerType : destination.getTravellerTypes()) {
            assert(travellerType.getTravellerTypeName().equals("Backpacker") || travellerType.getTravellerTypeName().equals("Groupie"));
            assertNotEquals(null, travellerType.getTtypeid());
        }
        assertEquals(2, destination.getTravellerTypes().size());

        assertEquals(null, DestinationModificationRequest.find().query().where().eq("id", modReqId).findOne());

    }


    @Test
    public void updatePublicDestination() {
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "Summoner's Rift");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyForm(formData)
                .method(POST)
                .uri("/users/destinations/update/public/1")
                .session("connected", "2");

        Result result = route(app, request);

        assertEquals(OK, result.status());
    }

    /**
     * Test to add photo to destination.
     */
    @Test
    public void addPhotoToDestination() {
        boolean destPhotoExists = false;
        int destMediaSize = Destination.find().byId(3).getPrimaryAlbum().getMedia().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/3/add_photo/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());

        List<Media> destMediums = Destination.find().byId(3).getPrimaryAlbum().getMedia();
        for (Media destMedia : destMediums) {
            if (destMedia.getMediaId() == 1) {
                destPhotoExists = true;
            }
        }
        assertEquals(destMediaSize+1, destMediums.size());
        assertTrue(destPhotoExists);
    }

    /**
     * Test to add the same photo twice to the same destination.
     */
    @Test
    public void addDuplicatePhotoToDestination() {
        UserPhoto userPhoto1 = UserPhoto.find().byId(1);
        Destination christchurch = Destination.find().byId(1);
        Destination wellington = Destination.find().byId(2);
        christchurch.getPrimaryAlbum().addMedia(userPhoto1);
        AlbumAccessor.update(christchurch.getPrimaryAlbum());
        wellington.getPrimaryAlbum().addMedia(userPhoto1);
        AlbumAccessor.update(wellington.getPrimaryAlbum());




        addPhotoToDestination();
        boolean destPhotoExists = false;
        int destPhotoSize = Destination.find().byId(1).getPrimaryAlbum().getMedia().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1/add_photo/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());

        List<Media> destMediums = Destination.find().byId(1).getPrimaryAlbum().getMedia();
        for (Media destMedia : destMediums) {
            if (destMedia.getMediaId() == 1) {
                destPhotoExists = true;
            }
        }
        assertEquals(destPhotoSize, destMediums.size());
        assertTrue(destPhotoExists);
    }

    /**
     * Test to add some other user's photo to destination.
     */
    @Test
    public void addPhotoToDestinationInvalidUser() {
        int destPhotoSize = Destination.find().byId(1).getPrimaryAlbum().getMedia().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1/add_photo/1")
                .session("connected", "1");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());

        List<Media> destMediums = Destination.find().byId(1).getPrimaryAlbum().getMedia();
        assertEquals(destPhotoSize, destMediums.size());
    }

    /**
     * Test to add photo to destination when no user is logged in.
     */
    @Test
    public void addPhotoToDestinationInvalidLoginSession() {
        int destPhotoSize = Destination.find().byId(1).getPrimaryAlbum().getMedia().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1/add_photo/1")
                .session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());

        List<Media> destMediums = Destination.find().byId(1).getPrimaryAlbum().getMedia();
        assertEquals(destPhotoSize, destMediums.size());
    }

    /**
     * Test to add a photo which doesn't exist to a destination.
     */
    @Test
    public void addPhotoToDestinationInvalidPhoto() {
        int destPhotoSize = Destination.find().byId(1).getPrimaryAlbum().getMedia().size();
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/10")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());

        List<Media> destMediums = Destination.find().byId(1).getPrimaryAlbum().getMedia();
        assertEquals(destPhotoSize, destMediums.size());
    }

    /**
     * Test to add a photo to a destination which doesn't exist.
     */
    @Test
    public void addPhotoToDestinationInvalidDestination() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/100/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());

    }

    /**
     * Test to remove a photo from a destination, checking the response code
     */
    @Test
    public void unlinkPhotoFromDestinationValidCheckResponseOk() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1/add_photo/1")
                .session("connected", "2");
        route(app, linkRequest);

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        Result result = route(app, unlinkRequest);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to remove a photo from a destination, checking the database
     */
    @Test
    public void unlinkPhotoFromDestinationCheckPhotoRemoved() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/1/add_photo/1")
                .session("connected", "2");
        route(app, linkRequest);

        UserPhoto photo = UserPhoto.find().byId(1);
        assert photo != null;
        int nDestinations = 0;
        List<Media> destMediaList = Destination.find().byId(1).getPrimaryAlbum().getMedia();
        if (destMediaList.iterator().hasNext()) {
            if (destMediaList.iterator().next().getMediaId() == 1) {nDestinations+=1;}
        }

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        route(app, unlinkRequest);

        photo = UserPhoto.find().byId(1);
        assert photo != null;
        List<Media> newDestMediaList = Destination.find().byId(1).getPrimaryAlbum().getMedia();
        assertEquals(nDestinations - 1, newDestMediaList.size());
    }

    /**
     * Test to remove a photo from a destination that does not have that photo, checking the response code
     */
    @Test
    public void unlinkPhotoFromDestinationNotLinkedCheckBadRequest() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    /**
     * Test to remove a photo from a destination that does not have that photo, checking the database
     */
    @Test
    public void unlinkPhotoFromDestinationNotLinkedCheckDataNotChanged() {
        UserPhoto photo = UserPhoto.find().byId(1);
        assert photo != null;
        int nDestinations = photo.getDestinations().size();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        route(app, request);

        photo = UserPhoto.find().byId(1);
        assert photo != null;
        assertEquals(nDestinations, photo.getDestinations().size());
    }

    /**
     * Test to remove a photo from a destination that doesn't exist, checking the response code
     */
    @Test
    public void unlinkPhotoFromDestinationNoDestinationCheckNotFound() {
        int destId = Destination.find().all().size() + 10; // give it a few extra to be safe
        Http.RequestBuilder request = Helpers.fakeRequest()
                .uri("/users/destinations/1/" + destId)
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to remove a photo from a destination that doesn't exist, checking the database
     */
    @Test
    public void unlinkPhotoFromDestinationNoDestinationCheckDataNotChanged() {
        UserPhoto photo = UserPhoto.find().byId(1);
        assert photo != null;
        int nDestinations = photo.getDestinations().size();

        int destId = Destination.find().all().size() + 10; // give it a few extra to be safe
        Http.RequestBuilder request = Helpers.fakeRequest()
                .uri("/users/destinations/1/" + destId)
                .session("connected", "2");
        route(app, request);
        photo = UserPhoto.find().byId(1);
        assert photo != null;

        assertEquals(nDestinations, photo.getDestinations().size());
    }

    /**
     * Test to remove a photo from a destination that doesn't exist, checking the response status code
     */
    @Test
    public void unlinkPhotoFromDestinationNoPhotoCheckNotFound() {
        int photoId = UserPhoto.find().all().size() + 10; // give it a few extra to be safe
        Http.RequestBuilder request = Helpers.fakeRequest()
                .uri("/users/destinations/" + photoId + "/1")
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * Test to remove a photo from a destination that doesn't exist, checking the
     */
    @Test
    public void unlinkPhotoFromDestinationNoPhotoCheckDataNotChanged() {
        UserPhoto photo = UserPhoto.find().byId(1);
        assert photo != null;
        int nDestinations = photo.getDestinations().size();

        int photoId = UserPhoto.find().all().size() + 10; // give it a few extra to be safe
        Http.RequestBuilder request = Helpers.fakeRequest()
                .uri("/users/destinations/" + photoId + "/1")
                .session("connected", "2");
        route(app, request);

        photo = UserPhoto.find().byId(1);
        assert photo != null;

        assertEquals(nDestinations, photo.getDestinations().size());

    }

    /**
     * Test to remove a photo from a destination, where the user is not the owner Of the
     * Destination nor the Photo. Checking the response code.
     */
    @Test
    public void unlinkPhotoFromDestinationUserNotOwnerOfPhotoCheckResponse() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        route(app, linkRequest);

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/1")
                .session("connected", "1");
        Result result = route(app, unlinkRequest);
        assertEquals(UNAUTHORIZED, result.status());
    }

    /**
     * Test to remove a photo from a destination, where the user is not the owner Of the
     * Destination nor the Photo. Checks the photo is still linked to the destination.
     */
    @Test
    public void unlinkPhotoFromDestinationUserNotOwnerOfPhotoCheckPhotoNotRemoved() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(POST)
                .uri("/users/destinations/1/1")
                .session("connected", "2");
        route(app, linkRequest);

        Destination destination = Destination.find().byId(1);
        int destPhotosSize = destination.getPrimaryAlbum().getMedia().size();

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/1")
                .session("connected", "1");
        route(app, unlinkRequest);
        Destination destinationAfterDelete = Destination.find().byId(1);

        assertEquals(destPhotosSize,
                destinationAfterDelete.getPrimaryAlbum().getMedia().size());
    }

    /**
     * Test to remove a photo from a destination, where the user is the owner Of the
     * Destination but not the Photo. Checking the response code.
     */
    @Test
    public void unlinkPhotoFromDestinationUserOwnerOfDestinationNotPhotoCheckResponse() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/4/add_photo/1")
                .session("connected", "2");
        route(app, linkRequest);

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/4")
                .session("connected", "3");
        Result result = route(app, unlinkRequest);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to remove a photo from a destination, where the user is the owner Of the
     * Destination but not the Photo. Checks the photo is still linked to the destination.
     */
    @Test
    public void unlinkPhotoFromDestinationUserOwnerOfDestinationNotPhotoCheckPhotoRemoved() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/4/add_photo/1")
                .session("connected", "2");
        route(app, linkRequest);

        Destination destination = Destination.find().byId(4);
        int destPhotosSize = destination.getPrimaryAlbum().getMedia().size();

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/4")
                .session("connected", "3");
        route(app, unlinkRequest);
        Destination destinationAfterDelete = Destination.find().byId(4);

        assertEquals(destPhotosSize-1,
                destinationAfterDelete.getPrimaryAlbum().getMedia().size());
    }

    /**
     * Test to remove a photo from a destination, where the user is not the owner Of the
     * Destination but of the Photo. Checking the response code.
     */
    @Test
    public void unlinkPhotoFromDestinationUserOwnerOfPhotoNotDestinationCheckResponse() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/4/add_photo/1")
                .session("connected", "2");
        route(app, linkRequest);

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/4")
                .session("connected", "2");
        Result result = route(app, unlinkRequest);
        assertEquals(SEE_OTHER, result.status());
    }

    /**
     * Test to remove a photo from a destination, where the user is not the owner Of the
     * Destination but of the Photo. Checks the photo is still linked to the destination.
     */
    @Test
    public void unlinkPhotoFromDestinationUserOwnerOfPhotoNotDestinationCheckPhotoRemoved() {
        // Send request to link a photo to a destination
        Http.RequestBuilder linkRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/4/add_photo/1")
                .session("connected", "2");
        route(app, linkRequest);

        Destination destination = Destination.find().byId(4);
        int destPhotosSize = destination.getPrimaryAlbum().getMedia().size();

        Http.RequestBuilder unlinkRequest = Helpers.fakeRequest()
                .method(DELETE)
                .uri("/users/destinations/1/4")
                .session("connected", "2");
        route(app, unlinkRequest);
        Destination destinationAfterDelete = Destination.find().byId(4);

        assertEquals(destPhotosSize-1,
                destinationAfterDelete.getPrimaryAlbum().getMedia().size());
    }

    /**
     *
     */
    @Test
    public void checkAddTag(){
        Destination destination;
        destination = new Destination
                ("Ben's Happy place", "Attraction","Unknown", "The Void", 25.00, 71.00,null,true);
        DestinationAccessor.insert(destination);
        Tag tag = new Tag("Places to see");
        TagAccessor.insert(tag);
        destination.addTag(tag);
        DestinationAccessor.update(destination);
        Destination clone = DestinationAccessor.getDestinationById(destination.getDestId());
        assertEquals(1, clone.getTags().size());
    }

    @Test(expected = DuplicateKeyException.class)
    public void checkAddingSameTag(){

        Tag tag = new Tag("Clone");
        TagAccessor.insert(tag);
        Tag tagClone = new Tag("Clone");
        TagAccessor.insert(tagClone);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAddingNullTag(){
        Destination destination;
        destination = new Destination
                ("Ben's Happy place", "Attraction","Unknown", "The Void", 25.00, 71.00,null,true);
        DestinationAccessor.insert(destination);
        new Tag(null); //This will throw an exception
    }

    @Test

    public void checkRemoveTag(){
        Destination destination;
        destination = new Destination
                ("Ben's Happy place", "Attraction","Unknown", "The Void", 25.00, 71.00,null,true);
        DestinationAccessor.insert(destination);
        Tag tag = new Tag("Delete me");
        TagAccessor.insert(tag);

        destination.addTag(tag);
        DestinationAccessor.update(destination);

        destination.removeTag(tag);
        DestinationAccessor.update(destination);

        assertEquals(0, DestinationAccessor.getDestinationById(destination.getDestId()).getTags().size());
    }

    @Test
    public void checkRemoveTagOnEmptySet(){
        Destination destination = new Destination
                ("Ben's Happy place", "Attraction","Unknown", "The Void", 25.00, 71.00,null,true);

        Tag tag = new Tag("Test");

        assertEquals(false, destination.removeTag(tag));
    }

    @Test
    /**
     * Checks the getPaginatedPublicDestinations method returns 200 status under
     * normal conditions.
     */
    public void getPaginatedPublicDestinations_check_200_status() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 10);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    /**
     * Checks a negative quantity for the getPaginatedPublicDestinations parameter
     * results in zero destinations send in the json body.
     */
    public void getPaginatedPublicDestinations_negativeQuantity_checkZeroDestinations() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, -1);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        JsonNode destinations = json.get("destinations");
        assertEquals(0, destinations.size());
    }

    @Test
    /**
     * Checks a zero quantity for the getPaginatedPublicDestinations parameter
     * results in zero destinations send in the json body.
     */
    public void getPaginatedPublicDestinations_zeroQuantity_checkZeroDestinations() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 0);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        JsonNode destinations = json.get("destinations");
        assertEquals(0, destinations.size());
    }

    @Test
    /**
     * Checks a positive quantity argument is respected by the getPaginatedPublicDestinations
     * method (returns at most [quantity] destinations).
     */
    public void getPaginatedPublicDestinations_oneQuantity_oneDestintion() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 1);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        JsonNode destinations = json.get("destinations");
        assertEquals(1, destinations.size());
    }

    @Test
    /**
     * Checks the offset works correctly in the getPaginatedPublicDestinations
     * method, a huge offset should return zero destinations.
     */
    public void getPaginatedPublicDestinations_hugeOffset_checkZeroDestinations() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 100000, 10);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        JsonNode destinations = json.get("destinations");
        assertEquals(0, destinations.size());
    }

    @Test
    /**
     * Checks the getPaginatedPublicDestinations method returns a totalCountPublic
     * key:value field in the json response, with a valid number. (non zero).
     */
    public void getPaginatedPublicDestinations_normal_checkHasTotalCount() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 100000, 10);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        JsonNode countNode = json.get("totalCountPublic");
        assertTrue(0 < countNode.asInt());
    }

    @Test
    /**
     * When giving a quantity above the MAX_QUANTITY allowable by the getPaginatedPublicDestinations
     * method, check the response is 400 (bad request).
     */
    public void getPaginatedPublicDestinations_quantityAboveMax_checkBadReq() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 1000000000);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    /**
     * When giving a quantity above the MAX_QUANTITY allowable by the getPaginatedPublicDestinations
     * method, check the response json body has a valid quantityLimit key and value.
     */
    public void getPaginatedPublicDestinations_badQuantity_checkHasQuantityLimit() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 1000000000);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        JsonNode quantityLimit = json.get("quantityLimit");
        assertTrue(0 < quantityLimit.asInt());
    }

    @Test
    /**
     * When giving a quantity above the MAX_QUANTITY allowable by the getPaginatedPublicDestinations
     * method, check the response json body has a valid error message key.
     */
    public void getPaginatedPublicDestinations_badQuantity_checkHasErrorStr() {
        String urlFormat = "/users/destinations/getpublicpaginatedjson?offset=%d&quantity=%d";
        String url = String.format(urlFormat, 0, 1000000000);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url)
                .session("connected", "2");
        Result result = route(app, request);


        JsonNode json = Json.parse(contentAsString(result));
        String errorStr = json.get("error").asText();
        assertTrue(0 < errorStr.length());
    }
}
