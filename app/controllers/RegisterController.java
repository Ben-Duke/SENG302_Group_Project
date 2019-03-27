package controllers;

import formdata.UserFormData;
import io.ebean.ExpressionList;
import models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Controller;
import views.html.users.profile.*;
import factories.UserFactory;

import javax.inject.Inject;
import factories.UserFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import static play.mvc.Results.*;

/**
 * A Controller class for the user registration page.
 */
public class RegisterController {
    public static Logger logger = LoggerFactory.getLogger("application");

    @Inject
    FormFactory formFactory;
    Logger logger = new Logger() {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void trace(String msg) {

        }

        @Override
        public void trace(String format, Object arg) {

        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {

        }

        @Override
        public void trace(String format, Object... arguments) {

        }

        @Override
        public void trace(String msg, Throwable t) {

        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return false;
        }

        @Override
        public void trace(Marker marker, String msg) {

        }

        @Override
        public void trace(Marker marker, String format, Object arg) {

        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {

        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String msg) {

        }

        @Override
        public void debug(String format, Object arg) {

        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {

        }

        @Override
        public void debug(String format, Object... arguments) {

        }

        @Override
        public void debug(String msg, Throwable t) {

        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return false;
        }

        @Override
        public void debug(Marker marker, String msg) {

        }

        @Override
        public void debug(Marker marker, String format, Object arg) {

        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void debug(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String msg) {

        }

        @Override
        public void info(String format, Object arg) {

        }

        @Override
        public void info(String format, Object arg1, Object arg2) {

        }

        @Override
        public void info(String format, Object... arguments) {

        }

        @Override
        public void info(String msg, Throwable t) {

        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return false;
        }

        @Override
        public void info(Marker marker, String msg) {

        }

        @Override
        public void info(Marker marker, String format, Object arg) {

        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void info(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public void warn(String msg) {

        }

        @Override
        public void warn(String format, Object arg) {

        }

        @Override
        public void warn(String format, Object... arguments) {

        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {

        }

        @Override
        public void warn(String msg, Throwable t) {

        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return false;
        }

        @Override
        public void warn(Marker marker, String msg) {

        }

        @Override
        public void warn(Marker marker, String format, Object arg) {

        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void warn(Marker marker, String msg, Throwable t) {

        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public void error(String msg) {

        }

        @Override
        public void error(String format, Object arg) {

        }

        @Override
        public void error(String format, Object arg1, Object arg2) {

        }

        @Override
        public void error(String format, Object... arguments) {

        }

        @Override
        public void error(String msg, Throwable t) {

        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return false;
        }

        @Override
        public void error(Marker marker, String msg) {

        }

        @Override
        public void error(Marker marker, String format, Object arg) {

        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {

        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {

        }

        @Override
        public void error(Marker marker, String msg, Throwable t) {

        }
    };

    UserFactory factory = new UserFactory();
    /**
     * Renders the create user page where users can register.
     * @return create user page
     */
    public Result createuser(){
        Form<UserFormData> userForm = formFactory.form(UserFormData.class);
        String[] gendersArray = {"Male", "Female", "Other"};
        UserFactory.addNatandPass();
        try {
            UserFactory.addTravelTypes();
        }catch (Exception error){
            //Do nothing as data is in database, otherwise error would not have happended.
        }
        Map<String, Boolean> tTypes = UserFactory.getTTypesList();
        Map<String, Boolean> passports = UserFactory.getPassports();
        Map<String, Boolean> nationalities = UserFactory.getNatList();
        return ok(createprofile_.render(userForm, Arrays.asList(gendersArray), tTypes, passports, nationalities));
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

        Form<UserFormData> userForm = formFactory.form(UserFormData.class).bindFromRequest();

         if (userForm.hasErrors()) {
            Map<String, Boolean> tTypes = UserFactory.getTTypesList();
            Map<String, Boolean> passports = UserFactory.getPassports();
            Map<String, Boolean> nationalities = UserFactory.getNatList();
            String[] gendersArray = {"Male", "Female", "Other"};

            return badRequest(createprofile_.render(userForm, Arrays.asList(gendersArray), tTypes, passports, nationalities));
        }
        else{
            UserFormData user = userForm.get();

                int userid = factory.createUser(user);
                return redirect(routes.HomeController.showhome()).addingToSession(request, "connected", Integer.toString(userid));

        }

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
