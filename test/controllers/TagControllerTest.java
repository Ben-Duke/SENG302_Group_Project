package controllers;

import accessors.TagAccessor;
import accessors.TripAccessor;
import accessors.UserAccessor;
import models.Tag;
import models.Trip;
import models.User;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.FORBIDDEN;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.*;

public class TagControllerTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void addValidNewRawTagCheckResponse() {
        Result result = addRawTagHelper("Brand new original tag", 2);
        assertEquals(CREATED, result.status());
    }

    @Test
    public void addValidNewRawTagForMultipleUsers() {
        Integer userOneId = 2;
        Integer userTwoId = 3;
        String tagName = "Brand new original tag";

        addRawTagHelper(tagName, userOneId);
        addRawTagHelper(tagName, userTwoId);

        assertEquals(2, TagAccessor.findUsersTagIsPendingFor(
                TagAccessor.getTagByName(tagName).getTagId()).size());
    }

    @Test
    public void addValidNewRawTagCheckDatabase() {
        String tagName = "Brand new original tag";
        addRawTagHelper(tagName, 2);
        assertTrue(TagAccessor.exists(new Tag(tagName)));
    }

    @Test
    public void addValidExistingRawTagCheckResponse() {
        Result result = addRawTagHelper("Vacation Spot", 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addValidExistingRawTagCheckDatabase() {
        String tagName = "Vacation Spot";
        addRawTagHelper(tagName, 2);
        assertTrue(TagAccessor.exists(new Tag(tagName)));
    }

    @Test
    public void addRawTagNoUserLoggedIn() {
        Result result = addRawTagHelper("Great tag", null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void addRawTagWithEmptyTagName() {
        Result result = addRawTagHelper("", 2);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test(expected = NullPointerException.class)
    public void addRawTagNoTag() {
       addRawTagHelper(null, 2);
    }

    @Test
    public void getItems() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/tags/items/" + 1)
                .session("connected", "1");
        Result result =  route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void getItemsNotLoggedIn() {

    }

    @Test
    public void getItemsLoggedInAsAdmin() {

    }

    @Test
    public void getItemsNoSuchTag() {

    }

    @Test
    public void getItemsCaseInsenstive() {

    }

    @Test
    public void searchTagsForOneItemCheckResponse() {
        Result result = searchTagsHelper("e", 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void searchTagsForOneItemCheckData() {
        Result result = searchTagsHelper("Fun place to stay", 2);
        assertEquals("[{\"tagId\":1,\"name\":\"Fun place to stay\"}]", contentAsString(result));
    }

    @Test
    public void searchTagsCaseInsensitiveCheckData() {
        Result result = searchTagsHelper("fUn pLaCe To sTaY", 2);
        assertEquals("[{\"tagId\":1,\"name\":\"Fun place to stay\"}]", contentAsString(result));
    }

    @Test
    public void searchTagsMultipleTagsCheckData() {
        Result result = searchTagsHelper("t", 2);
        assertEquals("[{\"tagId\":1,\"name\":\"Fun place to stay\"},{\"tagId\":2,\"name\":\"Vacation spot\"}" +
                ",{\"tagId\":3,\"name\":\"Top Rated\"},{\"tagId\":4,\"name\":\"Best trip ever\"}]", contentAsString(result));
    }

    @Test
    public void searchTagsEmptySearchCheckData() {
        Result result = searchTagsHelper("", 2);
        assertEquals("[{\"tagId\":1,\"name\":\"Fun place to stay\"},{\"tagId\":2,\"name\":\"Vacation spot\"}," +
                "{\"tagId\":3,\"name\":\"Top Rated\"},{\"tagId\":4,\"name\":\"Best trip ever\"}]", contentAsString(result));
    }

    @Test
    public void searchTagsOnNonExistentTag() {
        Result result = searchTagsHelper(
                "According to all known laws of aviation, there is no way a bee should be able to fly.",
                2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void searchTagsNotLoggedIn() {
        Result result = searchTagsHelper("Shrek", null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getPhotoTagSuccessCheckResponse() {
        Result result = getPhotoTagHelper(1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagSuccessCheckData() {
        Result result = getPhotoTagHelper(1, 2);
        assertEquals("[{\"tagId\":3,\"name\":\"Shrek\"}]", contentAsString(result));
    }

    @Test
    public void getPhotoTagMultipleCheckResponse() {
        addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        //Add a second tag
        addRemovePhotoTagHelper(PUT, "Second Tag", 2, 2);

        Result result = getPhotoTagHelper(2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagMultipleCheckData() {
        addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        //Add a second tag
        addRemovePhotoTagHelper(PUT, "Second Tag", 2, 2);

        Result result = getPhotoTagHelper(2, 2);
        assertEquals(
                "[{\"tagId\":5,\"name\":\"UC\"},{\"tagId\":6,\"name\":\"Second Tag\"}]",
                contentAsString(result));
    }

    @Test
    public void getPhotoTagNoTagsCheckResponse() {
        Result result = getPhotoTagHelper(2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getPhotoTagNoTagsCheckData(){
        Result result = getPhotoTagHelper(2, 2);
        assertEquals("[]", contentAsString(result));
    }

    @Test
    public void getPhotoTagNotLoggedIn() {
        Result result = getPhotoTagHelper(2, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getPhotoTagLoggedInAsWrongUser() {
        Result result = getPhotoTagHelper(2, 1);

        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getPhotoLoggedInAsAdmin() {
        Result result = getPhotoTagHelper(2, 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagNoTagExisting() {
        Result result = addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        assertEquals(CREATED, result.status());
    }

    @Test
    public void addPhotoTagTagExisting() {
        addRemovePhotoTagHelper(PUT, "UC", 2,2 );
        Result result = addRemovePhotoTagHelper(PUT, "UC", 1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagTagAlreadyLinked() {
        addRemovePhotoTagHelper(PUT, "UC", 2, 2);
        Result result = addRemovePhotoTagHelper(PUT, "UC", 2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addPhotoTagEmptyTag() {
        Result result = addRemovePhotoTagHelper(PUT, "", 1, 2);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void addPhotoTagNoPhoto() {
        Result result = addRemovePhotoTagHelper(PUT, "Unknown photo", 90, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void addPhotoTagNotLoggedIn() {
        Result result = addRemovePhotoTagHelper(PUT, "NZ", 1, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void addPhotoTagWrongUser() {
        Result result = addRemovePhotoTagHelper(PUT, "Self Portrait", 2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void addPhotoTagAdmin() {
        Result result = addRemovePhotoTagHelper(PUT, "New Photo", 1, 1);
        assertEquals(CREATED, result.status());
    }

    @Test
    public void removePhotoTag() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void removePhotoTagTagNotLinked() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void removePhotoTagTagNotExists() {
        Result result = addRemovePhotoTagHelper(DELETE, "Finland", 1, 2);
        assertEquals(NOT_FOUND, result.status());

    }

    @Test
    public void removePhotoTagAsAdmin() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 1, 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void removePhotoTagNoSuchPhoto() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 900, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void removePhotoTagWrongUser() {
        Result result = addRemovePhotoTagHelper(DELETE,"Shrek", 2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void removePhotoTagNotLoggedIn() {
        Result result = addRemovePhotoTagHelper(DELETE, "Shrek", 1, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getDestTagSuccessCheckResponse() {
        Result result = getDestTagHelper(1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getDestTagSuccessCheckData() {
        Result result = getDestTagHelper(1, 2);
        assertEquals("[{\"tagId\":1,\"name\":\"Fun place to stay\"},{\"tagId\":3,\"name\":\"Top Rated\"}]", contentAsString(result));
    }

    @Test
    public void getDestTagMultipleCheckResponse() {
        addRemoveDestTagHelper(PUT, "UC", 2,2 );
        //Add a second tag
        addRemoveDestTagHelper(PUT, "Second Tag", 2, 2);

        Result result = getDestTagHelper(2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getDestTagMultipleCheckData() {
        addRemoveDestTagHelper(PUT, "Cool spot", 2,2 );
        //Add a second tag
        addRemoveDestTagHelper(PUT, "Dream spot", 2, 2);

        Result result = getDestTagHelper(2, 2);
        assertEquals(
                "[{\"tagId\":2,\"name\":\"Vacation spot\"},{\"tagId\":4,\"name\":\"Cool spot\"},{\"tagId\":5,\"name\":\"Dream spot\"}]",
                contentAsString(result));
    }

    @Test
    public void getDestTagNoTagsCheckResponse() {
        Result result = getDestTagHelper(2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void getDestTagNoTagsCheckData(){
        Result result = getDestTagHelper(4, 2);
        assertEquals("[]", contentAsString(result));
    }

    @Test
    public void getDestTagNotLoggedIn() {
        Result result = getDestTagHelper(2, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getDestTagLoggedInAsWrongUser() {
        Result result = getDestTagHelper(2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getDestLoggedInAsAdmin() {
        Result result = getDestTagHelper(2, 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void addDestTagNoTagExisting() {
        Result result = addRemoveDestTagHelper(PUT, "UC", 2,2 );
        assertEquals(CREATED, result.status());
    }

    @Test
    public void addDestTagTagExisting() {
        addRemoveDestTagHelper(PUT, "UC", 2,2 );
        Result result = addRemoveDestTagHelper(PUT, "UC", 1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addDestTagTagAlreadyLinked() {
        addRemoveDestTagHelper(PUT, "UC", 2, 2);
        Result result = addRemoveDestTagHelper(PUT, "UC", 2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void addDestTagEmptyTag() {
        Result result = addRemoveDestTagHelper(PUT, "", 1, 2);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void addDestTagNoDest() {
        Result result = addRemoveDestTagHelper(PUT, "Unknown destination", 90, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void addDestTagNotLoggedIn() {
        Result result = addRemoveDestTagHelper(PUT, "NZ", 1, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void addDestTagWrongUser() {
        Result result = addRemoveDestTagHelper(PUT, "Self Portrait", 2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void addDestTagAdmin() {
        Result result = addRemoveDestTagHelper(PUT, "New Destination", 1, 1);
        assertEquals(CREATED, result.status());
    }

    @Test
    public void removeDestTag() {
        Result result = addRemoveDestTagHelper(DELETE, "Fun place to stay", 1, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void removeDestTagTagNotLinked() {
        Result result = addRemoveDestTagHelper(DELETE, "Fun place to stay", 2, 2);
        assertEquals(OK, result.status());
    }

    @Test
    public void removeDestTagTagNotExists() {
        Result result = addRemoveDestTagHelper(DELETE, "Finland", 1, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void removeDestTagAsAdmin() {
        Result result = addRemoveDestTagHelper(DELETE, "Fun place to stay", 1, 1);
        assertEquals(OK, result.status());
    }

    @Test
    public void removeDestTagNoSuchDest() {
        Result result = addRemoveDestTagHelper(DELETE, "Shrek", 900, 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void removeDestTagWrongUser() {
        Result result = addRemoveDestTagHelper(DELETE,"Shrek", 2, 3);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void removeDestTagNotLoggedIn() {
        Result result = addRemoveDestTagHelper(DELETE, "Shrek", 1, null);
        assertEquals(UNAUTHORIZED, result.status());
    }

    private Result searchTagsHelper(String searchQuery, Integer userId) {
        Map<String, String> bodyForm = new HashMap<>();
        bodyForm.put("search", searchQuery);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(bodyForm))
                .uri("/tags/search");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    private Result addRemovePhotoTagHelper(String method, String tagName, Integer photoId, Integer userId) {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", tagName);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(method)
                .bodyJson(Json.toJson(tagData))
                .uri("/photos/" + photoId + "/tags");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    private Result getPhotoTagHelper(Integer photoId, Integer userId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/photos/" + photoId + "/tags");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    private Result addRemoveDestTagHelper(String method, String tagName, Integer destId, Integer userId) {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", tagName);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(method)
                .bodyJson(Json.toJson(tagData))
                .uri("/destinations/" + destId + "/tags");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    private Result getDestTagHelper(Integer destId, Integer userId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/destinations/" + destId + "/tags");
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    ///Trips
    @Test
    public void getTagsForTrip() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/1/tags").session("connected", "2");
        Result result = route(app, request);

        assertEquals("[{\"tagId\":4,\"name\":\"Best trip ever\"}]", contentAsString(result));
    }

    @Test
    public void getTagsForTripNoUserAuthenticated() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/1/tags");
        Result result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getTagsForTripNotFound() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/trips/99999/tags").session("connected", "2");
        Result result = route(app, request);

        assertEquals(NOT_FOUND, result.status());
    }


    private Result tripTagHelper(String method,  Map<String,String> tagData, int tripId, int userId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(method)
                .bodyJson(Json.toJson(tagData))
                .uri("/trips/" + tripId + "/tags").session("connected", ""+ userId);
        return route(app, request);
    }


    @Test
    public void checkAddTripTag() {
        Trip trip = TripAccessor.getTripById(1);
        int beforeSize = trip.getTags().size();
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever 2");
        tripTagHelper("PUT", tagData,  trip.getTripid() , 2);
        trip = TripAccessor.getTripById(1);
        int afterSize = trip.getTags().size();
        assertEquals(beforeSize + 1, afterSize);
    }

    @Test
    public void checkAddTripTagWithNonexistentTrip() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever 2");
        Result result = tripTagHelper("PUT", tagData,  9999999 , 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void checkAddTripTagWithNonAuthorisedUser() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Not my trip tag");
        Result result = tripTagHelper("PUT", tagData,  1 , 4);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void checkAddTripWithNoUserSignedIn() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "No user tag");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .bodyJson(Json.toJson(tagData))
                .uri("/trips/1/tags");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void checkAddTagThatAlreadyExists() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever");
        Result result = tripTagHelper("PUT", tagData,  1 , 2);
        assertEquals("The tag Best trip ever appears to already be on this trip", contentAsString(result));
    }


    @Test
    public void checkAddTagEmptyString() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "");
        Result result = tripTagHelper("PUT", tagData,  1 , 2);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void checkAddTagAsAdmin() {
        Trip trip = TripAccessor.getTripById(1);
        int beforeSize = trip.getTags().size();
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Admin Tag TEST");
        tripTagHelper("PUT", tagData,  1 , 1);
        trip = TripAccessor.getTripById(1);
        int afterSize = trip.getTags().size();
        assertEquals(beforeSize + 1, afterSize);
    }

    @Test
    public void checkRemoveTripTag() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever");
        tripTagHelper("DELETE", tagData,  1 , 2);
        assertEquals(0, TripAccessor.getTripById(1).getTags().size());
    }

    @Test
    public void checkRemoveTripTagTripDoesNotExist() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever");
        Result result = tripTagHelper("DELETE", tagData,  100000 , 2);
        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void checkRemoveTripTagWithNoSignedInUser() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever");
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .bodyJson(Json.toJson(tagData))
                .uri("/trips/1/tags");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void checkRemoveTripTagUserIsNotTheOwnerOrAdmin() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Best trip ever");
        Result result = tripTagHelper("DELETE", tagData,  1 , 4);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void checkRemoveTripTagWhenTagNameIsEmpty() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "");
        Result result = tripTagHelper("DELETE", tagData,  1 , 2);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void checkRemoveTripTagWhenTagNameIsNotInDatabase() {
        Map<String, String> tagData = new HashMap<>();
        tagData.put("tag", "Not in database");
        Result result = tripTagHelper("DELETE", tagData,  1 , 2);
        assertEquals(NOT_FOUND, result.status());
    }

    /**
     * This test is more a sanity check that ebeans is working with the ebeans objects properly
     */
    @Test
    public void checkAddTag(){
        Trip trip = new Trip("Underworld Ventures", true, null);
        TripAccessor.insert(trip);
        Tag tag = new Tag("Nice spot");
        TagAccessor.insert(tag);
        trip.addTag(tag);
        assertEquals(1, trip.getTags().size());
    }

    /**
     * This test is more a sanity check that ebeans is working with the ebeans objects properly
     */
    @Test
    public void checkRemoveTagFromTrip(){
        Trip trip = new Trip("GOAT trip", true, null);
        TripAccessor.insert(trip);
        Tag tag = new Tag("remove tag");
        TagAccessor.insert(tag);
        trip.addTag(tag);

        trip.removeTag(tag);
        TagAccessor.update(tag);
        TripAccessor.update(trip);

        assertEquals(0, trip.getTags().size());
    }

    @Test
    public void removeUsersPendingTagsForUsedTag() {
        //Vacation spot should already be an existing tag which is tagged to Wellington
        String tagName = "Vacation spot";
        Integer userId = 2;

        addRawTagHelper(tagName, userId);
        int userPendingTagSizeBefore = TagAccessor.findPendingTagsFromUserId(2).size();
        Map<String, String> data = new HashMap<>();
        data.put("tag", tagName);

        removeUsersPendingTagHelper(tagName, userId);

        Tag tag = TagAccessor.getTagByName(tagName);
        int userPendingTagSizeAfter = TagAccessor.findPendingTagsFromUserId(2).size();

        assertNotNull(tag);

        assertEquals(userPendingTagSizeBefore - 1, userPendingTagSizeAfter);
    }

    @Test
    public void removeUsersPendingTagsForUnusedTag() {
        //Definitely not used tag should not be in the database
        String tagName = "Definitely not used tag";
        Integer userId = 2;

        addRawTagHelper(tagName, userId);
        int userPendingTagSizeBefore = TagAccessor.findPendingTagsFromUserId(2).size();

        removeUsersPendingTagHelper(tagName, userId);

        Tag tag = TagAccessor.getTagByName(tagName);
        int userPendingTagSizeAfter = TagAccessor.findPendingTagsFromUserId(2).size();

        assertNull(tag);

        assertEquals(userPendingTagSizeBefore - 1, userPendingTagSizeAfter);
    }

    @Test
    public void removeUsersPendingTagsCheckOnlyBelongingToUserRemoved() {
        String tagName = "Super cool new exciting tag";
        addRawTagHelper(tagName, 2);
        addRawTagHelper(tagName, 3);
        int beforeSize = TagAccessor.getTagByName(tagName).getPendingUsers().size();
        removeUsersPendingTagHelper(tagName, 2);
        assertNotNull(TagAccessor.getTagByName(tagName));
        assertEquals(beforeSize - 1, TagAccessor.getTagByName(tagName).getPendingUsers().size());
    }

    private Result addRawTagHelper(String tagName, Integer userId) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/tags");
        if (tagName != null) {
            Map<String, String> data = new HashMap<>();
            data.put("tag", tagName);
            request.bodyJson(Json.toJson(data));
        }
        if (userId != null) {
            request.session("connected", userId.toString());
        }
        return route(app, request);
    }

    private Result removeUsersPendingTagHelper(String tagName, Integer userId) {
        Map<String, String> data = new HashMap<>();
        data.put("tag", tagName);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(DELETE)
                .bodyJson(Json.toJson(data))
                .uri("/tags")
                .session("connected", userId.toString());

        return route(app, request);
    }

}
