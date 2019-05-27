package controllers;

import factories.UserFactory;
import models.User;
import models.UserPhoto;
import models.commands.photos.UploadPhotoCommand;
import play.data.FormFactory;
import play.libs.Files;
import play.mvc.Http;
import play.mvc.Result;
import utilities.CountryUtils;
import utilities.UtilityFunctions;
import views.html.home.home;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
        List<User> users = User.getCurrentUser(request, true);

        if (! users.isEmpty()){
            if(users.get(0).hasEmptyField()){
                return redirect(routes.ProfileController.updateProfile());
            } else if (! users.get(0).hasTravellerTypes()) {
                return redirect(routes.TravellerTypeController.updateTravellerType());
            } else if(! users.get(0).hasNationality()){
                return redirect(routes.ProfileController.updateNatPass());
            } else {
                // Load countries from api and update validity of pass/nat/destinations
                CountryUtils.updateCountries();
                return ok(home.render(users.get(0), users.get(1)));
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
            Map<String, String[]> dataPart = request.body().asMultipartFormData().asFormUrlEncoded();
            boolean isPublic = false;
            if (dataPart.get("private") == null) {
                isPublic = true;
            }

            //Get the photo data from the multipart form data encoding
            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
            if (picture != null) {
                return getResultFromSaveUserPhoto(user, isPublic, picture);
            } else {
                return badRequest("Error uploading the picture.");
            }
        } else {
            return unauthorized("Unauthorized: Can not upload picture.");
        }
    }

    /**
     * Gets a Result for saving a user photo, Given the user is authenticated and the picture is not null.
     *
     * Should only be called from a place where the user authentication and picture not null checks have been done.
     *
     * @param user An authenticated User
     * @param isPublic A boolean representing the privacy of the photo
     * @param picture The FilePart of the picture, not null.
     * @return A Result from trying to save the photo.
     */
    private Result getResultFromSaveUserPhoto(User user, boolean isPublic, Http.MultipartFormData.FilePart<Files.TemporaryFile> picture) {
        String origionalFilePath = picture.getFilename();
        long fileSize = picture.getFileSize();
        String contentType = picture.getContentType();
        Files.TemporaryFile fileObject = picture.getRef();


        if (contentType.contains("image")) {
            //Add the path to the filename given by the uploaded picture

            // finding unused photo url
            UserPhoto newPhoto = new UserPhoto(origionalFilePath, isPublic, false, user);
            String unusedPhotoUrl = newPhoto.getUnusedUserPhotoFileName();
            newPhoto.setUrl(unusedPhotoUrl);
            UploadPhotoCommand uploadPhotoCommand = new UploadPhotoCommand(newPhoto, fileObject);
            user.getCommandManager().executeCommand(uploadPhotoCommand);
            return redirect(routes.HomeController.showhome());
        } else {
            return badRequest();
        }
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
            boolean isPublic = true;

            //Get the photo data from the multipart form data encoding
            Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
            Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");
            if (picture != null) {
                String originalFilePath = picture.getFilename();
                //String fileName = datapart.get("filename")[0];
                long fileSize = picture.getFileSize();
                String contentType = picture.getContentType();
                Files.TemporaryFile file = picture.getRef();
                if (contentType.contains("image")) {
                    // finding unused photo url
                    UserPhoto newPhoto = new UserPhoto(originalFilePath, isPublic, true, user);
                    String unusedPhotoUrl = newPhoto.getUnusedUserPhotoFileName();
                    newPhoto.setUrl(unusedPhotoUrl);
                    //Add the path to the filename given by the uploaded picture

                    String unusedAbsoluteFilePath = Paths.get(".").toAbsolutePath().normalize().toString() + ApplicationManager.getUserPhotoPath() + user.getUserid() + "/" + unusedPhotoUrl;
                    //Save the file, replacing the existing one if the name is taken
                    try {
                        java.nio.file.Files.createDirectories(Paths.get(Paths.get(".").toAbsolutePath().normalize().toString() + ApplicationManager.getUserPhotoPath() + user.getUserid() + "/"));
                        file.copyTo(Paths.get(unusedAbsoluteFilePath), true);

                        BufferedImage thumbnailImage = UtilityFunctions.resizeImage(unusedAbsoluteFilePath);
                        ImageIO.write(thumbnailImage, "png", new File(Paths.get(".").toAbsolutePath().normalize().toString() + ApplicationManager.getUserPhotoPath() + user.getUserid() + "/profilethumbnail.png"));
                    } catch (IOException e) {
                        System.out.println(e);
                        return internalServerError("Oops, something went wrong.");
                    }
                    //DB saving
                    UserFactory.replaceProfilePicture(user.getUserid(), newPhoto);
                    return redirect(routes.HomeController.showhome());
                }
            }
            return badRequest();
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**Serve an image file with a get request
     * @param request the HTTP request
     * @param photoId the id of the photo
     * @return a java file with the photo
     */
    public Result serveFromId(Http.Request request, Integer photoId)
    {
        User user = User.getCurrentUser(request);
        if(user != null) {
            UserPhoto photo = UserPhoto.find.byId(photoId);
            if(!photo.isPublic() && user.getUserid() != photo.getUser().getUserid() && !user.userIsAdmin()){
                return unauthorized("Oops, this is a private photo.");
            }
            else{
                return ok(new File(photo.getUrlWithPath()));
            }
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**Serve an image file with a get request
     * @param httpRequest the HTTP request
     * @param path the full path name of the file to serve
     * @return a java file with the photo
     */
    public Result index(Http.Request httpRequest, String path) {
        return ok(new File(path));
    }

    /**
     * Serve the profile picture with a get request
     * @param httpRequest the HTTP request
     * @param userId the user whose profile we want to get
     * @return a java file with the profile photo
     */
    public Result serveProfilePicture(Http.Request httpRequest, Integer userId) {
        User user = User.getCurrentUser(httpRequest);
        if(user != null) {

            User otherUser = User.find.byId(userId);
            if (otherUser != null) {
                UserPhoto profilePicture = UserFactory.getUserProfilePicture(userId);
                if (profilePicture != null) {
                    return ok(new File(profilePicture.getUrlWithPath()));
                } else {
                    //should be 404 but then console logs an error
                    return redirect(routes.HomeController.showhome());
                }
            } else {
                return badRequest("User not found");
            }
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }



    /**
     * Replaces the profile picture with the photo corresponding to the photoId given.
     * @param request the HTTP request
     * @param photoId the id of the photo
     * @return Renders the home page.
     */
    public Result setProfilePicture(Http.Request request, Integer photoId) {
        User user = User.getCurrentUser(request);
        UserPhoto profilePhoto = UserPhoto.find.byId(photoId);
        if(user != null) {
            if (profilePhoto != null) {
                if(user.getUserid() == profilePhoto.getUser().getUserid() || user.userIsAdmin()) {
                    UserFactory.replaceProfilePicture(user.getUserid(), profilePhoto);
                    return redirect(routes.HomeController.showhome());
                }
                else{
                    return unauthorized("Oops! This is not your photo.");
                }
            }
            else {
                return notFound("Invalid Picture selected");
            }
        }
        return redirect(routes.UserController.userindex());
    }

    /**
     * Changes the privacy of the picture corresponding to the photoId given.
     * @param request the HTTP request
     * @param photoId the id of the photo
     * @param setPublic true to make public, false to make private
     * @return Renders the home page.
     */
    public Result makePicturePublic(Http.Request request, Integer photoId, Integer setPublic) {
        User user = User.getCurrentUser(request);
        UserPhoto photo = UserPhoto.find.byId(photoId);
        if(user != null) {
            if (photo != null) {
                if(user.getUserid() == photo.getUser().getUserid() || user.userIsAdmin()) {
                    if (setPublic == 0) {
                        UserFactory.makePicturePublic(user.getUserid(), photo, false);
                        return redirect(routes.HomeController.showhome());
                    } else if (setPublic == 1) {
                        UserFactory.makePicturePublic(user.getUserid(), photo, true);
                        return redirect(routes.HomeController.showhome());
                    } else {
                        return badRequest("Invalid request.");
                    }
                }
                else{
                    return unauthorized("Oops! This is not your photo.");
                }
            }
            return notFound("Invalid Picture selected");
        }
        return redirect(routes.UserController.userindex());
    }
}
