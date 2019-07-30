package controllers;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import models.Album;
import models.Media;
import models.User;
import models.UserPhoto;
import org.junit.Test;
import play.api.libs.json.Json;

import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import testhelpers.BaseTestWithApplicationAndDatabase;

import static play.mvc.Http.Status.*;
import static play.test.Helpers.route;

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

        Album album = new Album(user, "testTitle", false);
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

        Album album = new Album(user, "testTitle", false);
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

        Album album = new Album(user, "testTitle", false);
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

        Album album = new Album(user, "testTitle", false);
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

        Album album = new Album(user, "testTitle", false);
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

        Album album = new Album(user, "testTitle", false);

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

        Album album = new Album(user, "testTitle", false);

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

        Album album = new Album(user, "testTitle", false);

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

        Album album = new Album(user, "testTitle", false);

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

        Album album = new Album(user, "testTitle", false);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);

        album.addMedia(media1);
        album.addMedia(media2);

        AlbumAccessor.insert(album);

        Album targetAlbum = new Album(user, "testTitleTarget", false);
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

        Album album = new Album(user, "testTitle", false);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);

        album.addMedia(media1);

        AlbumAccessor.insert(album);

        Album targetAlbum = new Album(user, "testTitleTarget", false);
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

        Album album = new Album(user, "testTitle", false);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, otherUser);
        MediaAccessor.insert(media2);

        album.addMedia(media1);
        album.addMedia(media2);

        AlbumAccessor.insert(album);

        Album targetAlbum = new Album(user, "testTitleTarget", false);
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

        Album album = new Album(user, "testTitle", false);
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

        Album album = new Album(user, "testTitle", false);
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
    public void getAlbumFromTitleTest() {
        User user = UserAccessor.getById(1);

        Media media = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media);
        Album album = new Album(media, user, "newAlbum", false);
        AlbumAccessor.insert(album);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(Helpers.GET)
                .uri("/users/albums/getFromTitle/newAlbum").session("connected", Integer.toString(user.getUserid()));

        Result result = route(app, request);
        assert (result.status() == OK);


    }




}
