package controllers;

import models.User;
import play.mvc.Result;
import views.html.users.userIndex;

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
        return ok(userIndex.render(users));
    }
}
