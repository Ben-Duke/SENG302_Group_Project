package controllers;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.album.viewAlbum;

import java.util.List;

public class AlbumController extends Controller {

    public Result indexAlbum(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        List<Album> albums = user.getAlbums();

        return ok(views.html.users.album.indexAlbum.render(albums, user));
    }


    public Result viewAlbum(Http.Request request, Integer albumId) {

        System.out.println("1.1");

        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return redirect(routes.HomeController.showhome()); }

        System.out.println("1.3");

        return ok(viewAlbum.render(album, user));
    }


    public Result createAlbum(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        Album album;

        String title = request.body().asJson().get("title").textValue();

        if (request.body().asJson().get("mediaId").textValue() == null) {
            album = new Album(user, title);
            album.save();

        } else {

            Integer mediaId = request.body().asJson().get("mediaId").intValue();

            Media media = MediaAccessor.getMediaById(mediaId);

            if (media == null) { badRequest("Not such media"); }

            album = new Album(media, user, title);
            album.save();

        }

        System.out.println(album.getAlbumId());

        return redirect(routes.AlbumController.viewAlbum(album.getAlbumId()));
    }




}
