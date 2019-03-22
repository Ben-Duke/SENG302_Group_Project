package controllers;

import models.User;
import models.UserPhoto;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import views.html.home.home;
import views.html.users.userIndex;

import javax.inject.Inject;

import java.nio.file.Paths;

import static play.mvc.Controller.request;
import static play.mvc.Results.*;

public class HomeController {

    @Inject
    FormFactory formFactory;

    /**
     * The home page where currently users can access other creation pages (also displays their profile).
     * If the user has completed his profile, renders the home page.
     * If the user has not completed his profile, renders the profile creation page.
     * If the user it not logged in (doesn't have a login session), display error message.
     *
     * @param request the HTTP request
     * @return homepage, profile page or error page
     */
    public Result showhome(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            if (user.hasEmptyField()) {
                return redirect(routes.ProfileController.createprofile());
            } else if (!user.hasTravellerTypes()) {
                return redirect(routes.TravellerTypeController.updateTravellerType());
            } else if (!user.hasNationality()) {
                return redirect(routes.ProfileController.updateNatPass());
            } else {
                return ok(home.render(user));
            }
        }
        return unauthorized("Oops, you are not logged in");
    }

    public Result upload(Http.Request request) {
        User user = User.getCurrentUser(request);
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
        System.out.println("Uploaded");
        if (picture != null) {
            String fileName = picture.getFilename();
            System.out.println(fileName);
            long fileSize = picture.getFileSize();
            String contentType = picture.getContentType();
            System.out.println(contentType);
            Files.TemporaryFile file = picture.getRef();
            System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
            file.copyTo(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/public/images/" + fileName ), true);
            System.out.println("Done!");
            //UserPhoto newPhoto = new UserPhoto(fileName, false, user);
            //newPhoto.update();
            user.update();
            return ok(home.render(user));
        }
        return unauthorized("Oops, you are not logged in");
    }
}
