package controllers;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import accessors.UserPhotoAccessor;
import models.*;
import models.commands.Albums.*;
import models.commands.General.CommandPage;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.album.*;

import java.util.ArrayList;
import java.util.List;


public class AlbumController extends Controller {

    /**
     * Render the index page for albums. This displays all the users albums.
     * @param request
     * @return
     */
    public Result indexAlbum(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        List<Album> albums = user.getAlbums();
        // Clear command stack
        user.getCommandManager().setAllowedPage(CommandPage.ALBUM);
        return ok(indexAlbum.render(albums, user));
    }

    /**
     * Render the page the displays the contents of one album
     * @param request
     * @param albumId the id of the album to display.
     * @return
     */
    public Result viewAlbum(Http.Request request, Integer albumId) {

        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return redirect(routes.HomeController.showhome()); }
        // Clear command stack
        user.getCommandManager().setAllowedPage(CommandPage.ALBUM);
        return ok(viewAlbum.render(album, user));
    }

    /**
     * Method to get Media Urls from the album in a JSON for ajax requests.
     * @param request Request
     * @param albumId Id of the Album
     * @param hidePrivate true if only private media is required, false otherwise
     * @return JSON object with media urls
     */
    public Result getAlbum(Http.Request request, Integer albumId, Boolean hidePrivate) {
        Album album = AlbumAccessor.getAlbumById(albumId);
        List albumDetails = new ArrayList();
        for(Media media: album.getMedia()) {
            albumDetails.add(media);
        }
        return ok(Json.toJson(albumDetails));
    }

    /**
     * Process the ajax request to create album.
     * Title and optionally a initial piece of media (by id) are given.
     * Command is used to create album.
     * The page displaying the created album is rendered.
     * @param request
     * @return
     */
    public Result createAlbum(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        CreateAlbumCommand cmd;

        String title = request.body().asJson().get("title").textValue();

        if (request.body().asJson().get("mediaId").asText().equals("null")) {
            cmd = new CreateAlbumCommand(title, user, null);

        } else {

            Integer mediaId = request.body().asJson().get("mediaId").intValue();

            Media media = MediaAccessor.getMediaById(mediaId);

            if (media == null) { return badRequest("Not such media"); }
            if (!media.userIsOwner(user)) { return unauthorized("Not your media"); }

            cmd = new CreateAlbumCommand(title, user, media);

        }

        user.getCommandManager().executeCommand(cmd);

        Album album = cmd.getAlbum();

        return ok(viewAlbum.render(album, user));
    }

    /**
     * Process the request to delete an album.
     * All media in album will be deleted, unless
     * it is also apart of another album.
     * Redirect to album index page on success.
     * @param request
     * @param albumId
     * @return
     */
    public Result deleteAlbum(Http.Request request, Integer albumId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return badRequest("Album does not exist"); }

        if (!album.userIsOwner(user)) { return unauthorized("Not your album"); }

        DeleteAlbumCommand cmd = new DeleteAlbumCommand(album);
        user.getCommandManager().executeCommand(cmd);

        return redirect(routes.AlbumController.indexAlbum());
    }

    public Result updateAlbum(Http.Request request, Integer albumId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return badRequest("Album does not exist"); }

        if (!album.userIsOwner(user)) { return unauthorized("Not your album"); }

        String title = request.body().asJson().get("title").textValue();

        UpdateAlbumCommand cmd = new UpdateAlbumCommand(album, title);
        user.getCommandManager().executeCommand(cmd);

        return ok(viewAlbum.render(album, user));
    }

    /**
     * Process the ajax request to add multiple media
     * to an album. Takes a list of media ids, if the
     * user owns them all, a command is used to add them
     * to the album.
     * @param albumId The id of the album the media is being
     *                added to.
     */
    public Result addMediaToAlbum(Http.Request request, Integer albumId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return badRequest("Album does not exist"); }

        List<Integer> mediaIds = Json.fromJson(request.body().asJson().get("mediaIds"), List.class);
        List<Media> medias = new ArrayList<>();

        for (Integer mediaId : mediaIds) {
            Media media = MediaAccessor.getMediaById(mediaId);
            if (media == null) { return badRequest("Media does not exist"); }
            if (!media.userIsOwner(user)) { return unauthorized("Not your media"); }

            medias.add(media);
        }

        if (!album.userIsOwner(user)) { return unauthorized("Not your album"); }


        AddMediaToAlbumCommand cmd = new AddMediaToAlbumCommand(album, medias);
        user.getCommandManager().executeCommand(cmd);

        return ok(viewAlbum.render(album, user));
    }

    /**
     * Get's a JSON array containing all the destinations that a photo was linked to.
     *
     * Used when the user deletes a photo from a personal album and must be prompted
     * to either unlink or leave it linked to destination(s).
     *
     * @param request
     * @param photoId
     * @return
     */
    public Result getUnlinkableDestinationsForPhoto(Http.Request request, Integer photoId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return new Result(Http.Status.UNAUTHORIZED);
        }

        int authenticatedUserId = user.getUserid();
        UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);

        if (photo == null) {
            return new Result(Http.Status.NOT_FOUND);
        }

        if (photo.getUser() == null) {
            return new Result(Http.Status.FORBIDDEN);
        }

        int photoUserId = photo.getUser().getUserid();
        if (photoUserId != authenticatedUserId) {
            return new Result(Http.Status.FORBIDDEN);
        }

        List<Destination> destinations = MediaAccessor.getDestinations(photo);

        return ok(Json.toJson(destinations));
    }

    /**
     * Process the ajax request to remove multiple media
     * from an album. Takes a list of media ids, if the
     * user owns them all, a command is used to remove them
     * from the album.
     * @param albumId The id of the album the media is being
     *                removed from.
     */
    public Result removeMediaFromAlbum(Http.Request request, Integer albumId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return badRequest("Album does not exist"); }

        List<Integer> mediaIds = Json.fromJson(request.body().asJson().get("mediaIds"), List.class);
        List<Media> medias = new ArrayList<>();

        for (Integer mediaId : mediaIds) {
            Media media = MediaAccessor.getMediaById(mediaId);
            if (media == null) { return badRequest("Media does not exist"); }
            if (!album.containsMedia(media)) { return badRequest("Media is not in album"); }
            if (!media.userIsOwner(user)) { return unauthorized("Not your media"); }

            medias.add(media);
        }

        if (!album.userIsOwner(user)) { return unauthorized("Not your album"); }


        RemoveMediaFromAlbumCommand cmd = new RemoveMediaFromAlbumCommand(album, medias);
        user.getCommandManager().executeCommand(cmd);

        return ok(viewAlbum.render(album, user));
    }

    /**
     * Process the ajax request to move multiple media
     * from their album to another. Takes a list of media ids, if the
     * user owns them all, a command is used to move them
     * from their current albums to the given album.
     * @param albumId The id of the album the media is being
     *                moved to.
     */
    public Result moveMediaToAlbum(Http.Request request, Integer albumId) {

        User user = User.getCurrentUser(request);

        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return badRequest("Album does not exist"); }

        List<Integer> mediaIds = Json.fromJson(request.body().asJson().get("mediaIds"), List.class);
        List<Media> medias = new ArrayList<>();

        for (Integer mediaId : mediaIds) {
            Media media = MediaAccessor.getMediaById(mediaId);
            if (media == null) { return badRequest("Media does not exist"); }
            if (!media.userIsOwner(user)) { return unauthorized("Not your media"); }

            medias.add(media);
        }

        if (!album.userIsOwner(user)) { return unauthorized("Not your album"); }


        MoveMediaToAlbumCommand cmd = new MoveMediaToAlbumCommand(album, medias);
        user.getCommandManager().executeCommand(cmd);

        return ok(viewAlbum.render(album, user));
    }





}
