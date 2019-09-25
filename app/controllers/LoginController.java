package controllers;

import accessors.CommandManagerAccessor;
import accessors.UserAccessor;
import factories.LoginFactory;
import formdata.LoginFormData;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import utilities.UtilityFunctions;
import views.html.users.loginpage.*;
import views.html.users.userIndex;

import javax.inject.Inject;

import java.util.List;

import static play.mvc.Controller.flash;
import static play.mvc.Results.*;

/**
 * A controller class for handing user logins.
 */
public class LoginController {

    @Inject
    FormFactory formFactory;

    private final Logger logger = UtilityFunctions.getLogger();

    /**
     * Renders the login page where the user can log in.
     *
     * @return The login page.
     */
    public Result login(){
        Form<LoginFormData> loginFormData = formFactory.form(LoginFormData.class);
        return ok(loginPage.render(loginFormData,null));
    }


    /**
     * Handles the user's login request.
     * If the email is not found in the database then returns an error.
     * If the email is found but the password does not match then returns an error.
     * If the email and password matches then stores the user's login session
     *      and redirects the user to the home page.
     * If somehow multiple users have the same email address then redirects
     *              to login page with a "internal server error" message
     * @param request The HTTP request
     * @return The home page or login error page
     */
    public Result loginrequest(Http.Request request)
    {
        Form<LoginFormData> userLoginForm = formFactory.form(LoginFormData.class)
                                            .bindFromRequest();
        if (userLoginForm.hasErrors()) {
            // redirect user to same login page with some errors.
            return badRequest(userIndex.render(userLoginForm, User.getCurrentUser(request)));
        } else {
            String email = userLoginForm.get().email;
            String userId = Integer.toString(LoginFactory.getUserId(email));

            if (userId.equals("-1")) {
                // happens if somehow there are multiple users with the same email
                // address in the db
                flash("serverError", "Internal Server Error, please try again");
                return internalServerError(userIndex.render(userLoginForm, User.getCurrentUser(request)));
            } else {
                // Check the user has a default album
                User user = UserAccessor.getById(Integer.parseInt(userId));
                user.addMissingData();
                user.getCommandManager().resetUndoRedoStack();

                return redirect(routes.HomeController.mainMapPage())
                           .addingToSession(request, "connected", userId);
            }

        }


    }

    /**
     * Logs the user out by removing them from the session and redirecting them
     * to the user index page.
     *
     * @param request The HTTP request
     * @return The user index page
     */
    public Result logoutrequest(Http.Request request){
        User user = User.getCurrentUser(request);
        if(user != null) {
            List<User> users = User.getCurrentUser(request, true);
            users.get(0).getCommandManager().resetUndoRedoStack();
            if (users.get(0).getUserid() != users.get(1).getUserid()) {
                return redirect(routes.AdminController.setUserBackToAdmin(users.get(1).getUserid()));
            }
            return redirect(routes.UserController.userindex())
                    .removingFromSession(request, "connected");
        } else {
            return redirect(routes.UserController.userindex());
        }
    }
}
