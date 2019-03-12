package controllers;

import io.ebean.ExpressionList;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Controller;
import views.html.home.home;
import views.html.users.createUser;
import views.html.users.*;
import javax.inject.Inject;

import java.util.regex.Pattern;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;
import static play.mvc.Results.redirect;

/**
 * A Controller class for the user registration page.
 */
public class RegisterController {

    @Inject
    FormFactory formFactory;

    /**
     * Renders the create user page where users can register.
     * @return create user page
     */
    public Result createuser(){
        Form<User> userForm = formFactory.form(User.class);
        return ok(createUser.render(userForm));
    }

    /**
     * Handles the register request
     * When the user registers, stores the form data in to the database, stores their login session and redirects them
     * to the home page (where they'll be redirected to the profile creation page).
     *
     * Checks the username does not exist already, and that the username is a valid email address and the password
     * meets some basic length requirements.
     *
     * @param request the HTTP request
     * @return the home page
     */
    public Result saveuser(Http.Request request){
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        User user = userForm.get();

        String email = user.getUsername().toLowerCase();
        String password = user.getPassword();

        // checking if the username exists already in db
        ExpressionList<User> usersExpressionList = User.find.query().where().eq("username", user.getUsername().toLowerCase());
        if (usersExpressionList.findCount() > 0) {
            return badRequest("Http error 400: Username already taken");
        }

        if (! this.isEmailValid(email)) {
            return badRequest("Http error 400: The username entered is not a valid email address.");
        }

        if (! this.isValidPassword(password)) {
            return badRequest("Http error 400: The password entered is invalid. The password length must be between (inclusive) " +
                    "8 and 128.");
        }

        user.save();
        //return redirect(routes.UserController.userindex());
        return redirect(routes.HomeController.showhome()).addingToSession(request, "connected", Integer.toString(user.getUserid()));
    }

    /**
     * Method to check if the email entered by the user is a valid email. Is only a basic check with regex, doesn't
     * catch all emails and doesn't check if the email actually exists.
     *
     * Email regex sourced online from here:
     * https://howtodoinjava.com/regex/java-regex-validate-email-address/
     * Courtesy of Lokesh Gupta
     *
     * @param email A String, the email to check.
     * @return A boolean, true if the email is valid, false otherwise.
     */
    public boolean isEmailValid(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * A method to check whether the user entered password is valid.
     *
     * Currently this is just a basic check if it's length.
     *
     * Password length must be: 8 <= length <= 128
     *
     * @param password A String, the password to check.
     * @return A boolean, true if the password is valid, false otherwise.
     */
    public boolean isValidPassword(String password) {
        int passwordLength = password.length();

        int minLength = 8;
        int maxLength = 128;

        return minLength <= passwordLength && passwordLength <= maxLength;
    }
}
