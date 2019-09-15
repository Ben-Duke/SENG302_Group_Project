package controllers;

import accessors.UserPhotoAccessor;
import factories.UserFactory;
import models.Admin;
import models.User;
import models.UserPhoto;
import org.slf4j.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.*;
import views.html.users.userIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static play.mvc.Results.*;

public class UserController {

    private final Logger logger = UtilityFunctions.getLogger();

    // A thread safe boolean
    private AtomicBoolean wasRun = new AtomicBoolean(false);
    // A countdownlatch which frees when the database has been populated.
    private CountDownLatch initCompleteLatch = new CountDownLatch(1);


    /**
     * Renders the index page and populates the in memory database
     *
     * If the wasRun attribute is false the database is populated. The populate
     * database method takes a CountDownLatch and when the database has been
     * populated it frees the latch.
     *
     * Only one thread gets into the if block and all other concurrent threads
     * wait in the else block for the if block free the lock.
     *
     * Checks the .env file exists and has all required key/value pairs. Throws
     * a runtime exception if one of these is missing.
     *
     * @param request The HTTP request
     * @return the user index page
     */
    public Result userindex(Http.Request request){
        if (!wasRun.getAndSet(true)) {
            ApplicationManager.setMediaPath("/../media_");

            for (EnvVariableKeys requiredKeyEnum: EnvVariableKeys.getRequiredEnvVariables()) {

                String value = EnvironmentalVariablesAccessor.getEnvVariable(requiredKeyEnum.toString());
                if (value == null) {
                    throw new RuntimeException("Either the .env file is missing " +
                            "or the required key {" + requiredKeyEnum.toString() +
                            "} is missing or does not have a corresponding value. " +
                            "To get a more specific error check the logger error messages.");
                }
            }

            TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
            testDatabaseManager.populateDatabaseWithLatch(initCompleteLatch);
        } else {
            try {
                initCompleteLatch.await();
            } catch (InterruptedException e) {
                logger.error("initCompleteLatch interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        return ok(userIndex.render(User.getCurrentUser(request)));
    }

    /**
     * Handles the ajax request to get a user as a json object.
     *
     * @param request The HTTP request
     * @return the corresponding user as a json based on the login session.
     */
    public Result getUser(Http.Request request){
        User user = User.getCurrentUser(request);
        if(user != null) {
            return ok(Json.toJson(user.getUserid()));
        }
        else{
            return notFound();
        }
    }

    /**
     * Handles the ajax request to get a user.
     *
     * @param request The HTTP Request
     * @return the corresponding user as a json based on the login session.
     */
    public Result getUserPhotosAjax(Http.Request request){
        User user = User.getCurrentUser(request);
        if(user != null) {
            List<UserPhoto> userPhotos = user.getUserPhotos();
            List<Integer> photoIds = new ArrayList<>();
            if (userPhotos != null) {
                for (UserPhoto photo: userPhotos) {
                    photoIds.add(photo.getMediaId());
                }
            }
            return ok(Json.toJson(photoIds));
        }
        else{
            return notFound();
        }
    }

    /**
     * Handles requests to get a photo's caption
     * @param request The http request from a logged in user
     * @param photoId the id of the photo that has the caption
     * @return if successful, a 200 code with the caption.
     */
    public Result getPhotoCaption(Http.Request request, int photoId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        UserPhoto userPhoto = UserPhotoAccessor.getUserPhotoById(photoId);
        if (userPhoto == null) {
            return notFound();

        }
        if (!userPhoto.getIsPublic() && !userPhoto.getUser().equals(user) && !user.userIsAdmin()) {
            return forbidden();
        }
        String caption = userPhoto.getCaption();
        if (caption == null) {
            return ok("");
        }
        return ok(caption);

    }

    /**
     * Handles requests to change a caption of a photo
     * @param request Request with information about the caption and the logged in user
     * @param photoId the id of the photo to change
     * @return An http response with the response code.
     */
    public Result editPhotoCaption(Http.Request request, int photoId) {
        String caption = request.body().asJson().get("caption").asText();
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized();
        }
        try {
            // throws IllegalArgumentException if user forbidden or photo not found
            UserFactory.editPictureCaption(user.getUserid(), photoId, caption);
            return ok();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Forbidden")) {
                return forbidden();
            } else if (e.getMessage().equals("Not Found")) {
                return notFound();
            }
        }
        return internalServerError();
    }

}
