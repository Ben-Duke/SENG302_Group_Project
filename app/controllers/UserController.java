package controllers;

import models.Admin;
import models.User;
import models.UserPhoto;
import org.slf4j.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.CountryUtils;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;
import views.html.users.userIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

public class UserController {
    private final Logger logger = UtilityFunctions.getLogger();
    // A thread safe boolean
    AtomicBoolean wasRun = new AtomicBoolean(false);
    // A countdownlatch which frees when the database has been populated.
    CountDownLatch initCompleteLatch = new CountDownLatch(1);

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
     * @param request The HTTP request
     * @return the user index page
     */
    public Result userindex(Http.Request request){
        if (!wasRun.getAndSet(true)) {
            ApplicationManager.setUserPhotoPath("/../user_photos/user_");
            TestDatabaseManager.populateDatabase(initCompleteLatch);
            logger.debug("populating database");

            CountryUtils.updateCountries();

        } else {
            try {
                initCompleteLatch.await();
            } catch (InterruptedException e) {
                logger.error("initCompleteLatch interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
        List<User> users = User.find.all();
        List<Admin> admins = Admin.find.all();
        return ok(userIndex.render(users, admins,User.getCurrentUser(request)));
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
                    photoIds.add(photo.photoId);
                }
            }
            return ok(Json.toJson(photoIds));
        }
        else{
            return notFound();
        }
    }

}
