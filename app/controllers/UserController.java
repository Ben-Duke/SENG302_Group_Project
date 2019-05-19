package controllers;

import models.*;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import utilities.CountryUtils;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;
import views.html.users.userIndex;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

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

            CountryUtils.updateCountries();

        } else {
            try {
                initCompleteLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        List<User> users = User.find.all();
        List<Admin> admins = Admin.find.all();
        return ok(userIndex.render(users, admins,User.getCurrentUser(request)));
    }

    /**
     * Handles the ajax request to get a user.
     * Returns the corresponding user as a json based on the login session.
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
