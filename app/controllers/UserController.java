package controllers;

import models.Admin;
import models.User;
import play.mvc.Result;
import views.html.users.userIndex;

import java.time.LocalDate;
import java.util.List;

import static play.mvc.Results.ok;

public class UserController {

    /**
     * Renders the user index page, which currently displays all users registered and contains links to logout, login
     * or register.
     * @return the user index page
     */
    public Result userindex(){
        List<User> users = User.find.all();
        List<Admin> admins = Admin.find.all();
        if (users.size() == 0) {
            User user = new User("admin@admin.com", "admin", "admin", "admin", LocalDate.now(), "male");
            user.save();
            User user1 = new User("user1@admin.com", "user1", "user1", "user1", LocalDate.now(), "male");
            user1.save();
            User user2 = new User("user2@admin.com", "user2", "user2", "user2", LocalDate.now(), "male");
            user2.save();
            Admin admin = new Admin(user.userid, true);
            admin.save();
        }
        users = User.find.all();
        admins = Admin.find.all();
        return ok(userIndex.render(users, admins));
    }
}
