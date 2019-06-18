package controllers;

import accessors.AlbumAccessor;
import models.Album;
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

//        return ok(views.html.users.album.indexAlbum.render(albums, user));

        return ok();

//        Album album = AlbumAccessor.getAlbumById(albumId);
//        if (album == null) { return redirect(routes.HomeController.showhome()); }
//
//        return ok(viewAlbum.render(album, user));
    }

    public Result viewAlbum(Http.Request request, Integer albumId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Album album = AlbumAccessor.getAlbumById(albumId);
        if (album == null) { return redirect(routes.HomeController.showhome()); }

        return ok(viewAlbum.render(album, user));
    }


//    public Result createAlbum(Http.Request request) {
//        User user = User.getCurrentUser(request);
//        if (user == null) { return redirect(routes.UserController.userindex()); }
//
//
//
//
//            Form<DestinationFormData> destFormData;
//            destFormData = formFactory.form(DestinationFormData.class);
//
//            Map<String, Boolean> countryList = CountryUtils.getCountriesMap();
//
//            return ok(createEditDestination.render(destFormData, null, countryList , Destination.getTypeList(),user));
//
//    }




}
