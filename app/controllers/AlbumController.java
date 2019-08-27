package controllers;

import accessors.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gherkin.deps.com.google.gson.JsonArray;
import io.ebean.Ebean;
import models.*;
import models.commands.Albums.*;
import models.commands.General.CommandPage;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.album.*;

import java.awt.*;
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
            if (!media.isOwner(user)) { return unauthorized("Not your media"); }

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
            if (!media.isOwner(user)) { return unauthorized("Not your media"); }

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
            if (!media.isOwner(user)) { return unauthorized("Not your media"); }

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
            if (!media.isOwner(user)) { return unauthorized("Not your media"); }

            medias.add(media);
        }

        if (!album.userIsOwner(user)) { return unauthorized("Not your album"); }


        MoveMediaToAlbumCommand cmd = new MoveMediaToAlbumCommand(album, medias);
        user.getCommandManager().executeCommand(cmd);

        return ok(viewAlbum.render(album, user));
    }

    /**
     * Process the AJAX request to get the album Id based on its title null if it doesn't exist
     * @param request the HTTP request
     * @param albumName the name of the album to retrieve
     * @return a JSON object with the album ID
     */
    public Result getAlbumFromTitle(Http.Request request, Integer userId, String albumName) {
        User user = UserAccessor.getById(userId);
        List<Album> albumList = AlbumAccessor.getAlbumsByOwner(user);
        Integer albumId = null;
        for (Album album : albumList) {
            if (albumName.equals(album.getTitle())) {
                albumId = album.getAlbumId();
            }
        }

        return ok(String.valueOf(albumId));
    }


    /**
     * Method to handle AJAX request for deleting a user photo and unlinking
     * the photo from selected destinations.
     *
     * @param request The http request
     * @return The Result, uses http status 200 for success and error codes
     * otherwise.
     */
    public Result deleteUserPhotoAndUnlinkFromSelectDests(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return new Result(Http.Status.UNAUTHORIZED);
        }
        int authenticatedUserId = user.getUserid();

        JsonNode json = request.body().asJson();
        if (json == null) {
            return new Result(Http.Status.BAD_REQUEST);
        }

        JsonNode mediaIdNode = json.get("mediaId");
        if (mediaIdNode == null) {
            return new Result(Http.Status.BAD_REQUEST);
        }

        Integer photoId = mediaIdNode.intValue();
        if (photoId == null) {
            return new Result(Http.Status.BAD_REQUEST);
        }

        JsonNode jsonArray = json.get("destinationsToUnlink");
        if (jsonArray == null) {
            return new Result(Http.Status.BAD_REQUEST);
        }

        UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
        if (photo == null) {
            return new Result(Http.Status.NOT_FOUND);
        }

        int mediaUserId = photo.getUser().getUserid();
        if (mediaUserId != authenticatedUserId && !user.userIsAdmin()) {
            return new Result(Http.Status.FORBIDDEN);
        }


        try {
            deleteUserPhotoAndUnlinkTransaction(user, jsonArray, photo);
            return new Result((Http.Status.OK));

        } catch (Exception e) {
            return new Result(Http.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method to handle the Ebeans transaction to delete a users photo and unlink
     * photo from selected destinations.
     *
     * User for deleteUserPhotoAndUnlinkFromSelectDests() method
     *
     * @param user The User who owns the photo.
     * @param jsonArray A JsonNode object containing the destid's of all destinations
     *                  to unlink from the photo.
     * @param photo The UserPhoto
     * @throws Exception Some error with ebeans, show user an error if this occurs.
     * No changes will be committed if the exception is thrown.
     */
    private void deleteUserPhotoAndUnlinkTransaction(User user, JsonNode jsonArray, UserPhoto photo) throws Exception {
        Ebean.beginTransaction();
        // TRANSACTION START

        boolean success = false;
        try {
            for (int i = 0; i < jsonArray.size(); i++) {
                int destId = jsonArray.get(i).asInt();
                Destination destination = DestinationAccessor.getDestinationById(destId);
                destination.getPrimaryAlbum().removeMedia(photo);
                AlbumAccessor.update(destination.getPrimaryAlbum());
                if ((destination.getPrimaryAlbum().getPrimaryPhoto() != null) &&
                        (photo.getMediaId() ==
                                destination.getPrimaryAlbum().getPrimaryPhoto().getMediaId())) {
                    destination.getPrimaryAlbum().setPrimaryPhoto(null);
                    AlbumAccessor.update(destination.getPrimaryAlbum());
                }
            }

            photo.user = null;
            photo.setProfile(false);
            UserPhotoAccessor.update(photo);

            List<Media> medias = new ArrayList<>();
            Media media = MediaAccessor.getMediaById(photo.getMediaId());
            for (Album album: user.getAlbums()) {
                if (album.containsMedia(media)) {
                    medias.add(media);
                    RemoveMediaFromAlbumCommand cmd = new RemoveMediaFromAlbumCommand(album, medias);
                    user.getCommandManager().executeCommand(cmd);
                }
            }


            if ((user.getUserPhotos().contains(photo))) {
                user.getUserPhotos().remove(photo);
                UserAccessor.update(user);
                if(media.getAlbums().isEmpty()) {
                    UserPhotoAccessor.delete(photo);
                    UserPhotoAccessor.update(photo);
                }
            }

            Ebean.commitTransaction();
            success = true;
            //TRANSACTION END
        } catch (Exception e) {
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }

        if (! success) {
            throw new Exception("Error occurred deleting user photo and unlinking " +
                    "from destinations.");
        }
    }

    /**
     * AJAX request to get a media item's first available album ID if it is in an album
     * @param request the HTTP request
     * @param mediaId the media item which needs its album to be found
     * @return the first available album ID the media item is in if available
     */
    public Result getFirstAlbumIdFromMediaId(Http.Request request, Integer mediaId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return new Result(Http.Status.UNAUTHORIZED);
        }
        Media mediaItem = MediaAccessor.getMediaById(mediaId);
        if (mediaItem == null) {
            System.out.println("no existo media");
            return badRequest("Media does not exist");
        }
        List<Album> mediaItemAlbums = mediaItem.getAlbums();
        if( mediaItemAlbums == null || mediaItemAlbums.size() < 1) {
            System.out.println("no existo album");
            return badRequest("Album does not exist");
        }
        ObjectNode data =  (ObjectNode) Json.toJson(mediaItemAlbums.get(0));
        return ok(Json.toJson(data));
    }
}
