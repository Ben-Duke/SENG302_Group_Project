package controllers;

import accessors.MediaAccessor;
import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
import com.fasterxml.jackson.databind.JsonNode;
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
}
