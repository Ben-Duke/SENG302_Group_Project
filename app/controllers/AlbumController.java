package controllers;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.User;
import models.commands.Albums.CreateAlbumCommand;
import models.commands.Albums.DeleteAlbumCommand;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.album.viewAlbum;

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

        return ok(views.html.users.album.indexAlbum.render(albums, user));
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

        return ok(viewAlbum.render(album, user));
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





}
