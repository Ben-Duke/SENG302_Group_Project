package controllers;

import accessors.*;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import org.junit.Test;
import play.api.libs.json.Json;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;

public class AlbumControllerTest extends BaseTestWithApplicationAndDatabase {


    @Test
    public void createAlbumWithoutMediaTest() {

        Http.RequestBuilder request = Helpers.fakeRequest()
            .bodyJson(Json.parse("{\"title\":\"testTitle\", \"mediaId\":null}"))
            .method(Helpers.POST)
            .uri("/users/albums/create").session("connected", "1");

        User user = UserAccessor.getById(1);

        int beforeSize = user.getAlbums().size();

        Result result = route(app, request);

        user = UserAccessor.getById(1);

        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize + 1);
        assert (result.status() == OK );

    }

    @Test
    public void createAlbumWithMediaTest() {

        User user = UserAccessor.getById(1);

        Media media = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media);
        Integer mediaId = media.getMediaId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"title\":\"testTitle\", \"mediaId\":"+mediaId+"}"))
                .method(Helpers.POST)
                .uri("/users/albums/create").session("connected", "1");


        int beforeSize = user.getAlbums().size();

        Result result = route(app, request);

        user = UserAccessor.getById(1);

        int afterSize = user.getAlbums().size();

        assert (result.status() == OK );
        assert (afterSize == beforeSize + 1);

    }

    @Test
    public void createAlbumWithMediaNotOwnedTest() {

        User user = UserAccessor.getById(1);
        User mediaOwner = UserAccessor.getById(2);

        Media media = new UserPhoto("/test", false, false, mediaOwner);
        MediaAccessor.insert(media);
        Integer mediaId = media.getMediaId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"title\":\"testTitle\", \"mediaId\":"+mediaId+"}"))
                .method(Helpers.POST)
                .uri("/users/albums/create").session("connected", "1");


        int beforeSize = user.getAlbums().size();

        Result result = route(app, request);

        user = UserAccessor.getById(1);

        int afterSize = user.getAlbums().size();

        assert (result.status() == UNAUTHORIZED);
        assert (afterSize == beforeSize);

    }

    @Test
    public void createAlbumWithNonExistingMediaTest() {

        User user = UserAccessor.getById(1);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"title\":\"testTitle\", \"mediaId\":\"10000\"}"))
                .method(Helpers.POST)
                .uri("/users/albums/create").session("connected", "1");


        int beforeSize = user.getAlbums().size();

        Result result = route(app, request);

        user = UserAccessor.getById(1);

        int afterSize = user.getAlbums().size();

        assert (result.status() == BAD_REQUEST);
        assert (afterSize == beforeSize);

    }

    @Test
    public void deleteAlbumTest() {

        User user = UserAccessor.getById(1);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(Helpers.DELETE)
                .uri("/users/albums/delete/"+album.getAlbumId())
                .session("connected", "1");


        int beforeSize = user.getAlbums().size();

        Result result = route(app, request);

        user = UserAccessor.getById(1);

        int afterSize = user.getAlbums().size();

        assert (result.status() == SEE_OTHER);
        assert (afterSize == beforeSize - 1);

    }

    @Test
    public void deleteAlbumThatDoesntExistTest() {

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(Helpers.DELETE)
                .uri("/users/albums/delete/"+10000)
                .session("connected", "1");


        Result result = route(app, request);

        assert (result.status() == BAD_REQUEST);
    }

    @Test
    public void deleteAlbumNotOwnedTest() {

        User user = UserAccessor.getById(1);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(Helpers.DELETE)
                .uri("/users/albums/delete/"+album.getAlbumId())
                .session("connected", "2");


        int beforeSize = user.getAlbums().size();

        Result result = route(app, request);

        user = UserAccessor.getById(1);

        int afterSize = user.getAlbums().size();

        assert (result.status() == UNAUTHORIZED);
        assert (afterSize == beforeSize);
    }

    @Test
    public void addMediaToAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/add_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == OK);
        assert (afterSize == beforeSize + 2);

    }

    @Test
    public void addUnownedMediaToAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        User otherUser = UserAccessor.getById(1);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, otherUser);
        MediaAccessor.insert(media2);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/add_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == UNAUTHORIZED);
        assert (afterSize == beforeSize);

    }

    @Test
    public void addNonExistingMediaToAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+10000+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/add_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == BAD_REQUEST);
        assert (afterSize == beforeSize);

    }

    @Test
    public void removeMediaFromAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);

        album.addMedia(media1);
        album.addMedia(media2);

        AlbumAccessor.insert(album);


        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/remove_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == OK);
        assert (afterSize == beforeSize - 2);

    }

    @Test
    public void removeUnownedMediaFromAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        User otherUser = UserAccessor.getById(1);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, otherUser);
        MediaAccessor.insert(media2);

        album.addMedia(media1);
        album.addMedia(media2);

        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/remove_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == UNAUTHORIZED);
        assert (afterSize == beforeSize);

    }

    @Test
    public void removeNonExistingMediaFromAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);

        album.addMedia(media1);

        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+10000+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/remove_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == BAD_REQUEST);
        assert (afterSize == beforeSize);

    }

    @Test
    public void removeMediaNotInAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        User otherUser = UserAccessor.getById(1);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, otherUser);
        MediaAccessor.insert(media2);

        album.addMedia(media1);

        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/remove_media/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int beforeSize = album.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int afterSize = album.getMedia().size();

        assert (result.status() == BAD_REQUEST);
        assert (afterSize == beforeSize);

    }


    @Test
    public void moveMediaToAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);

        album.addMedia(media1);
        album.addMedia(media2);

        AlbumAccessor.insert(album);

        Album targetAlbum = new Album(user, "testTitleTarget");
        AlbumAccessor.insert(targetAlbum);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/move_media/"+targetAlbum.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int albumBeforeSize = album.getMedia().size();
        int targetAlbumBeforeSize = targetAlbum.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        targetAlbum = AlbumAccessor.getAlbumById(targetAlbum.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int targetAlbumAfterSize = targetAlbum.getMedia().size();

        assert (result.status() == OK);
        assert (albumAfterSize == albumBeforeSize - 2);
        assert (targetAlbumAfterSize == targetAlbumBeforeSize + 2);

    }


    @Test
    public void moveNonExistingMediaToAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);

        album.addMedia(media1);

        AlbumAccessor.insert(album);

        Album targetAlbum = new Album(user, "testTitleTarget");
        AlbumAccessor.insert(targetAlbum);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+1000+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/move_media/"+targetAlbum.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int albumBeforeSize = album.getMedia().size();
        int targetAlbumBeforeSize = targetAlbum.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        targetAlbum = AlbumAccessor.getAlbumById(targetAlbum.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int targetAlbumAfterSize = targetAlbum.getMedia().size();

        assert (result.status() == BAD_REQUEST);
        assert (albumAfterSize == albumBeforeSize);
        assert (targetAlbumAfterSize == targetAlbumBeforeSize);

    }

    @Test
    public void moveUnownedMediaToAlbumTest() {

        User user = new User("email");
        UserAccessor.insert(user);

        User otherUser = UserAccessor.getById(1);

        Album album = new Album(user, "testTitle");

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, otherUser);
        MediaAccessor.insert(media2);

        album.addMedia(media1);
        album.addMedia(media2);

        AlbumAccessor.insert(album);

        Album targetAlbum = new Album(user, "testTitleTarget");
        AlbumAccessor.insert(targetAlbum);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"mediaIds\":["+media1.getMediaId()+","+media2.getMediaId()+"]}"))
                .method(Helpers.PUT)
                .uri("/users/albums/move_media/"+targetAlbum.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));


        int albumBeforeSize = album.getMedia().size();
        int targetAlbumBeforeSize = targetAlbum.getMedia().size();

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        targetAlbum = AlbumAccessor.getAlbumById(targetAlbum.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int targetAlbumAfterSize = targetAlbum.getMedia().size();

        assert (result.status() == UNAUTHORIZED);
        assert (albumAfterSize == albumBeforeSize);
        assert (targetAlbumAfterSize == targetAlbumBeforeSize);

    }

    @Test
    public void updateAlbumTest() {

        User user = new User("email12345");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"title\":\"newTitle\"}"))
                .method(Helpers.PUT)
                .uri("/users/albums/update/"+album.getAlbumId())
                .session("connected", Integer.toString(user.getUserid()));

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (result.status() == OK);
        assert (album.getTitle().equals("newTitle"));

    }

    @Test
    public void updateNonExistingAlbumTest() {

        User user = new User("email12345");
        UserAccessor.insert(user);


        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"title\":\"newTitle\"}"))
                .method(Helpers.PUT)
                .uri("/users/albums/update/"+100000)
                .session("connected", Integer.toString(user.getUserid()));

        Result result = route(app, request);

        assert (result.status() == BAD_REQUEST);

    }


    @Test
    public void updateUnownedAlbumTest() {

        User user = new User("email12345");
        UserAccessor.insert(user);

        User otherUser = new User("email12");
        UserAccessor.insert(otherUser);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .bodyJson(Json.parse("{\"title\":\"newTitle\"}"))
                .method(Helpers.PUT)
                .uri("/users/albums/update/"+album.getAlbumId())
                .session("connected", Integer.toString(otherUser.getUserid()));

        Result result = route(app, request);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (result.status() == UNAUTHORIZED);
        assert (album.getTitle().equals("testTitle"));

    }


    @Test
    /**
     * Tests that an unauthenticated request get a unaccepted HTTP response.
     */
    public void getUnlinkableDestinationsForPhoto_is401HTTPStatus_noActiveSession() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/albums/photos/get_linked_destinations/1").session("connected", null);
        Result result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    /**
     * Tests that an unauthenticated request get a unaccepted HTTP response.
     */
    public void getUnlinkableDestinationsForPhoto_is403HTTPStatus_otherUsersPhoto() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/albums/photos/get_linked_destinations/1").session("connected", null);
        Result result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    /**
     * Tests that a request for a photo ID that does not exist gets a not  found (404)
     * response.
     */
    public void getUnlinkableDestinationsForPhoto_is404HTTPStatus_photoForOtherUser() {
        String userEmail = "usertestwronguserphoto@test.com";
        String otherUserEmail = "otheruseremail@test.com";
        String userAlbumName = "testOtherUser";
        String photoURL = "/test/test/testotheruser.jpg";

        User user = new User(otherUserEmail);
        UserAccessor.insert(user);
        String userId = Integer.toString(user.getUserid());

        User otheruser = new User(userEmail);
        UserAccessor.insert(user);


        Album album = new Album(user, userAlbumName);
        AlbumAccessor.insert(album);
        ArrayList<Album> albums = new ArrayList<Album>();
        albums.add(album);

        ArrayList<Album> primaryPhotoDestinations = new ArrayList<Album>();

        UserPhoto photo = new UserPhoto(photoURL,
                true,
                false,
                otheruser,
                albums,
                primaryPhotoDestinations);
        UserPhotoAccessor.insert(photo);
        String photoId = Integer.toString(photo.getMediaId());

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/albums/photos/get_linked_destinations/" + photoId)
                .session("connected", userId);
        Result result = route(app, request);

        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    /**
     * Tests that a request for a valid user and a valid photo with no linked destinations
     * returns a 200 OK http status.
     */
    public void getUnlinkableDestinationsForPhoto_is200Status_photoNotLinkedToDestinations() {
        String userEmail = "userwithaphoto@test.com";
        String userAlbumName = "test";
        String photoURL = "/test/test/test.jpg";

        Result result = getResultFromGetUnlinkableDestinations_helper_method(
                                            userEmail, userAlbumName, photoURL);

        assertEquals(OK, result.status());
    }

    @Test
    /**
     * Tests that a request for a valid user and a valid photo with no linked destinations
     * returns an empty JSON array.
     */
    public void getUnlinkableDestinationsForPhoto_isEmptyJSON_photoNotLinkedToDestinations() {
        String userEmail = "userwithaphoto_1@test.com";
        String userAlbumName = "test_1";
        String photoURL = "/test/test/test_1.jpg";

        Result result = getResultFromGetUnlinkableDestinations_helper_method(
                userEmail, userAlbumName, photoURL);

        JsonNode jsonJacksonArray = play.libs.Json.parse(contentAsString(result));
        assertEquals(0, jsonJacksonArray.size());
    }

    @Test
    /**
     * Tests that a request for a valid user and a valid photo with no linked destinations
     * returns an an array with 1 destination.
     */
    public void getUnlinkableDestinationsForPhoto_checkJSONHas1Destinations_photoLinkedTo1Destination() {
        String userEmail = "userwithaphoto_2@test.com";
        String userAlbumName = "test_2";
        String photoURL = "/test/test/test_2.jpg";

        User user = new User(userEmail);
        UserAccessor.insert(user);

        Album album = new Album(user, userAlbumName);
        AlbumAccessor.insert(album);
        ArrayList<Album> albums = new ArrayList<Album>();
        albums.add(album);

        ArrayList<Album> primaryPhotoDestinations = new ArrayList<Album>();

        Destination destination2 = new Destination(
                "testtestmctest", "Town", "Wellington",
                "New Zealand", -41.26, 174.6,
                user);
        DestinationAccessor.insert(destination2);

        UserPhoto photo = new UserPhoto(photoURL,
                true,
                false,
                user);
        UserPhotoAccessor.insert(photo);
        String photoId = Integer.toString(photo.getMediaId());


        destination2.getPrimaryAlbum().addMedia(photo);
        AlbumAccessor.update(destination2.getPrimaryAlbum());




        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/albums/photos/get_linked_destinations/" + photoId)
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode jsonJacksonArray = play.libs.Json.parse(contentAsString(result));
        assertEquals(1, jsonJacksonArray.size());
    }

    @Test
    /**
     * Tests that a request for a valid user and a valid photo with no linked destinations
     * returns an an array with 2 destinations.
     */
    public void getUnlinkableDestinationsForPhoto_checkJSONHas2Destinations_photoLinkedTo1Destination() {
        String userEmail = "userwithaphoto_3@test.com";
        String userAlbumName = "test_3";
        String photoURL = "/test/test/test_3.jpg";

        User user = new User(userEmail);
        UserAccessor.insert(user);

        Album album = new Album(user, userAlbumName);
        AlbumAccessor.insert(album);
        ArrayList<Album> albums = new ArrayList<Album>();
        albums.add(album);

        ArrayList<Album> primaryPhotoDestinations = new ArrayList<Album>();

        Destination destination2 = new Destination(
                "testtestmctest2222", "Town", "Wellington",
                "New Zealand", -41.26, 174.6,
                user);
        DestinationAccessor.insert(destination2);

        Destination destination3 = new Destination(
                "testtestmctest333", "Town", "Wellington",
                "New Zealand", -41.26, 174.6,
                user);
        DestinationAccessor.insert(destination3);

        UserPhoto photo = new UserPhoto(photoURL,
                true,
                false,
                user);
        UserPhotoAccessor.insert(photo);
        String photoId = Integer.toString(photo.getMediaId());


        destination2.getPrimaryAlbum().addMedia(photo);
        AlbumAccessor.update(destination2.getPrimaryAlbum());

        destination3.getPrimaryAlbum().addMedia(photo);
        AlbumAccessor.update(destination3.getPrimaryAlbum());




        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/albums/photos/get_linked_destinations/" + photoId)
                .session("connected", Integer.toString(user.getUserid()));
        Result result = route(app, request);

        JsonNode jsonJacksonArray = play.libs.Json.parse(contentAsString(result));
        assertEquals(2, jsonJacksonArray.size());
    }

    /**
     * A helper method to return a Result object from sending a GET request to the
     * "/users/albums/photos/get_linked_destinations/" endpoint.
     *
     * @param userEmail Users email
     * @param userAlbumName Name of users album
     * @param photoURL URL to photo
     * @return A http Result
     */
    private Result getResultFromGetUnlinkableDestinations_helper_method(
                                                            String userEmail,
                                                            String userAlbumName,
                                                            String photoURL) {
        User user = new User(userEmail);
        UserAccessor.insert(user);

        Album album = new Album(user, userAlbumName);
        AlbumAccessor.insert(album);
        ArrayList<Album> albums = new ArrayList<Album>();
        albums.add(album);

        ArrayList<Album> primaryPhotoDestinations = new ArrayList<Album>();

        UserPhoto photo = new UserPhoto(photoURL,
                                        true,
                                        false,
                                        user,
                                        albums,
                                        primaryPhotoDestinations);
        UserPhotoAccessor.insert(photo);
        String photoId = Integer.toString(photo.getMediaId());

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/albums/photos/get_linked_destinations/" + photoId)
                .session("connected", Integer.toString(user.getUserid()));
        return route(app, request);
    }
}
