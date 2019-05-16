package controllers;

import models.Admin;
import models.Destination;
import models.User;
import models.UserPhoto;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.TestDatabaseManager;
import views.html.users.userIndex;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static play.mvc.Results.*;

public class UserController {
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
     * @return the user index page
     */
    public Result userindex(Http.Request request){
        if (!wasRun.getAndSet(true)) {
            ApplicationManager.setUserPhotoPath("/../user_photos/user_");
            TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
            testDatabaseManager.populateDatabase(initCompleteLatch);
            System.out.println("populating database");
        } else {
            try {
                initCompleteLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("database already populated");
        }
        List<User> users = User.find.all();
        List<Admin> admins = Admin.find.all();
        return ok(userIndex.render(users, admins,User.getCurrentUser(request)));
    }

    /**
     * Removes the given destination from the list of destinations in the photos
     * @param request unused http request information
     * @param photoId the id iof the photo to unlink
     * @param destId the id of the destination to unlink
     * @return response for if the removal worked
     */
    public Result removeDestPhotoLink(Http.Request request, int photoId, int destId){
        UserPhoto photo = UserPhoto.find.byId(photoId);
        Destination destination = Destination.find.byId(destId);
        if (photo != null) {
            photo.removeDestination(destination);
            if (destination != null && photo.equals(destination.getPrimaryPhoto())) {
                destination.setPrimaryPhoto(null);
                destination.update();
            }

            photo.update();
            return ok();
        } else {
            return badRequest("That destination is not linked to that photo");
        }
    }

    /**
     * Handles the ajax request to get a user.
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

}
