package controllers;

import io.ebean.Ebean;
import io.ebean.ExpressionList;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.home.home;
import views.html.users.login;

import javax.inject.Inject;

import static play.mvc.Results.*;

public class LoginController {

    @Inject
    FormFactory formFactory;

    /**
     * Renders the login page where the user can log in.
     * @return The login page.
     */
    public Result login(){
        Form<User> userForm = formFactory.form(User.class);
        return ok(login.render(userForm));
    }


    /**
     * Handles the user's login request.
     * If the username is not found in the database then returns an error.
     * If the username is found but the password does not match then returns an error.
     * If the username and password matches then stores the user's login session and redirects the user to the home page.
     * @param request The HTTP request
     * @return The home page or login error page
     */
    public Result loginrequest(Http.Request request)
    {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        User userLoggingIn = userForm.get();
        ExpressionList<User> usersExpressionList = User.find.query().where().eq("username", userLoggingIn.getUsername().toLowerCase());

        if (usersExpressionList.findCount() > 1) { // checking if there are somehow duplicate users of the same username.
            return internalServerError("Internal server error processing login request");
        } else {
            User user = usersExpressionList.findOne();
            if(user == null){
                return notFound("User not found");

            }
            if(user.authenticate(userLoggingIn.password)){
                return redirect(routes.HomeController.showhome()).addingToSession(request, "connected", Integer.toString(user.getUserid()));
            }
            else{
                return notFound("Invalid password");
            }
        }

    }

    /**
     * Logs the user out by removing them from the session and redirecting them  to the user index page.
     * @param request The HTTP request
     * @return The user index page
     */
    public Result logoutrequest(Http.Request request){
        return redirect(routes.UserController.userindex()).removingFromSession(request, "connected");
    }
}
