package controllers;

import models.*;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
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
     * Renders the user index page, which currently displays all users registered and contains links to logout, login
     * or register.
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
     * Deprecated by TestDatabaseManager which populates the database.
     */
    /*
    public void addDefaultUsers() {
        User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
        user.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically
        user.save();

        User user1 = new User("user1@admin.com", "user1", "John", "Doe", LocalDate.now(), "male");
        user1.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user1.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user1.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically
        user1.save();

        User user2 = new User("user2@admin.com", "user2", "James", "Doe", LocalDate.now(), "male");
        user2.setDateOfBirth(LocalDate.of(2019, 2, 18));
        user2.setTravellerTypes(TravellerType.find.all().subList(5, 6)); // Business Traveller
        user2.setNationality(Nationality.find.all().subList(0, 2)); // First two countries alphabetically
        user2.save();

        Admin admin = new Admin(user.userid, true);
        admin.save();
    }
    */

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
