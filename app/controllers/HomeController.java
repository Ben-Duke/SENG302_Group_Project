package controllers;

import akka.http.javadsl.model.HttpRequest;
import factories.UserFactory;
import models.Admin;
import models.User;
import models.UserPhoto;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import utilities.UtilityFunctions;
import views.html.home.home;
import views.html.users.userIndex;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static play.mvc.Controller.request;
import java.util.List;
import java.util.Map;

import static play.mvc.Http.Status.OK;
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
        if (user != null){
            if(user.hasEmptyField()){
                return redirect(routes.ProfileController.updateProfile());
            } else if (! user.hasTravellerTypes()) {
                return redirect(routes.TravellerTypeController.updateTravellerType());
            } else if(! user.hasNationality()){
                return redirect(routes.ProfileController.updateNatPass());
            } else {
                return ok(home.render(user));
            }
        }
        return redirect(routes.UserController.userindex());
    }

    /**
     * The upload POST action to upload a picture taking the image multipart form data and mapping it to file data and then
     * adding this to the images directory.
     * @param request the HTTP request
     * @return the homepage or an error page
     */
    public Result upload(Http.Request request) {
        User user = User.getCurrentUser(request);
        if(user != null) {
            Map<String, String[]> datapart = request.body().asMultipartFormData().asFormUrlEncoded();
            boolean isPublic = false;
            if (datapart.get("private") == null) {
                isPublic = true;
            }

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

                    String pathName = Paths.get(".").toAbsolutePath().normalize().toString() + "/../user_photos/user_" + user.getUserid() + "/" + fileName;
                    //Save the file, replacing the existing one if the name is taken
                    try {
                        java.nio.file.Files.createDirectories(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/../user_photos/user_" + user.getUserid() + "/"));
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                    file.copyTo(Paths.get(pathName), true);
                    //DB saving
                    UserPhoto newPhoto = new UserPhoto(fileName, isPublic, false, user);
                    newPhoto.save();
                    return ok(home.render(user));
                }
            }
        }
        return badRequest(home.render(user));
    }



    /**
     * The upload POST action to upload a profile picture taking the image multipart form data and mapping it to file data and then
     * adding this to the images directory. The current user's existing profile picture should be overwritten by this profile picture.
     * If the overwritten picture was a personal photo, it should persist as a personal photo.
     * If the overwritten picture was previously uploaded with this method (not a personal photo) it should not persist.
     *
     * //TODO not persisting part
     * @param request the HTTP request
     * @return the homepage or an error page
     */
    public Result uploadProfilePicture(Http.Request request) {
        User user = User.getCurrentUser(request);
        if(user != null) {
            Map<String, String[]> datapart = request.body().asMultipartFormData().asFormUrlEncoded();
            boolean isPublic = false;
            if (datapart.get("private") == null) {
                isPublic = true;
            }

            //Get the photo data from the multipart form data encoding
            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
            if (picture != null) {
                //String fileName = picture.getFilename();
                String fileName = datapart.get("filename")[0];
                long fileSize = picture.getFileSize();
                String contentType = picture.getContentType();
                Files.TemporaryFile file = picture.getRef();
                if (contentType.contains("image")) {
                    //Add the path to the filename given by the uploaded picture

                    String pathName = Paths.get(".").toAbsolutePath().normalize().toString() + "/../user_photos/user_" + user.getUserid() + "/" + fileName;
                    //Save the file, replacing the existing one if the name is taken
                    try {
                        java.nio.file.Files.createDirectories(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + "/../user_photos/user_" + user.getUserid() + "/"));
                        file.copyTo(Paths.get(pathName), true);

                        BufferedImage thumbnailImage = UtilityFunctions.resizeImage(pathName);
                        ImageIO.write(thumbnailImage, "png", new File(Paths.get(".").toAbsolutePath().normalize().toString() + "/../user_photos/user_" + user.getUserid() + "/profilethumbnail.png"));

                    } catch (IOException e) {
                        System.out.println(e);
                    }




                    //DB saving
                    UserFactory.replaceProfilePicture(user.getUserid(), new UserPhoto(fileName, isPublic, true, user));
                    return ok(home.render(user));
                }
            }
        }
        return badRequest(home.render(user));
    }

    /**Serve an image file with a get request
     * @param httpRequest the HTTP request
     * @param path the full path name of the file to serve
     * @return a java file with the photo
     */
    public Result index(Http.Request httpRequest, String path) {
        return ok(new java.io.File(path));
    }

    /**
     * Serve the profile picture with a get request
     * @param httpRequest the HTTP request
     * @return a java file with the profile photo
     */
    public Result serveProfilePicture(Http.Request httpRequest) {
        User user = User.getCurrentUser(httpRequest);
        UserPhoto profilePicture = UserFactory.getUserProfilePicture(user.getUserid());
        if(profilePicture != null) {
            return ok(new java.io.File(profilePicture.getUrlWithPath()));
        }
        else{
            //should be 404 but then console logs an error
            return ok();
        }
    }
}
