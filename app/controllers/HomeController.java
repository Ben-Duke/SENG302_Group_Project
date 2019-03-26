package controllers;

import models.Admin;
import models.User;
import models.UserPhoto;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import views.html.home.home;
import views.html.users.userIndex;

import javax.inject.Inject;

import java.io.IOException;
import java.nio.file.Paths;

import static play.mvc.Controller.request;
import java.util.List;

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
        List<Admin> admins = Admin.find.all();
        if (user != null){
            if(user.hasEmptyField()){
                return redirect(routes.ProfileController.updateprofile());
            } else if (! user.hasTravellerTypes()) {
                return redirect(routes.TravellerTypeController.updateTravellerType());
            } else if(! user.hasNationality()){
                return redirect(routes.ProfileController.updateNatPass());
            } else {
                return ok(home.render(user, admins));
            }
        }
        return unauthorized("Oops, you are not logged in");
    }

    /**
     * The upload POST action to upload a picture taking the image multipart form data and mapping it to file data and then
     * adding this to the images directory.
     * @param request the HTTP request
     * @return the homepage or an error page
     */
    public Result upload(Http.Request request) {
        User user = User.getCurrentUser(request);
        //Get the photo data from the multipart form data encoding
        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
        if (picture != null) {
            String fileName = picture.getFilename();
            long fileSize = picture.getFileSize();
            String contentType = picture.getContentType();
            Files.TemporaryFile file = picture.getRef();
            if (contentType.contains("image")) {
                //Add the path to the filename given by the uploaded picture

                String pathName = Paths.get(".").toAbsolutePath().normalize().toString() + "/public/images/user_photos/user_" + user.getUserid() + "/" + fileName;
                //Save the file, replacing the existing one if the name is taken
                try {
                    java.nio.file.Files.createDirectories(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/public/images/user_photos/user_" + user.getUserid() + "/"));
                } catch (IOException e) {
                    System.out.println(e);
                }
                file.copyTo(Paths.get(pathName ), true);
                //DB saving
                UserPhoto newPhoto = new UserPhoto(fileName, false, user);
                newPhoto.save();
                return ok(home.render(user));
            }
        }
        return badRequest(home.render(user));
    }
}
