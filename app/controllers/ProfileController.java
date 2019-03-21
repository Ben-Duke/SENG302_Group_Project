package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.profile.showProfile;
import views.html.users.profile.createprofile;
import views.html.users.profile.updateNatPass;

import javax.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static play.mvc.Results.*;

public class ProfileController {

    @Inject
    FormFactory formFactory;

    /**
     * If the user is logged in, renders the create profile page.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result createprofile(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<User> userForm = formFactory.form(User.class).fill(user);
            return ok(createprofile.render(userForm));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }


    /**
     * Handles the update profile request.
     * If the user is logged in, their profile is updated with the form values and they are redirected to the home page.
     * If the user is not logged in, an error message is displayed.
     * @param request The HTTP request
     * @return home page or error page
     */
    public Result updateprofile(Http.Request request){
        DynamicForm profileForm = formFactory.form().bindFromRequest();
        String fName = profileForm.get("fName");
        String lName = profileForm.get("lName");
        String gender = profileForm.get("gender");
        String dateofbirth = profileForm.get("dateOfBirth");
        User user = User.getCurrentUser(request);
        if (user != null) {

            if (fName.matches(".*\\d+.*") || fName.length() < 1) {
                return unauthorized("ERROR: First name should only contain alphabets.");
            } else {
                user.setfName(fName);
            }
            if (lName.matches(".*\\d+.*") || lName.length() < 1) {
                return unauthorized("ERROR: Last name should only contain alphabets.");
            } else {
                user.setlName(lName);
            }
            if (gender == null) {
                return unauthorized("ERROR: Please select a gender.");
            } else {
                user.setGender(gender);
            }
            if (dateofbirth.length() < 8) {
                return unauthorized("ERROR: Please enter the date correctly.");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                //convert String to LocalDate
                LocalDate birthDate;
                try {
                    birthDate = LocalDate.parse(dateofbirth, formatter);
                } catch (Exception e) {
                    return badRequest("ERROR: Please enter the date correctly.");
                }
                user.setDateOfBirth(birthDate);
            }

            user.update();
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.HomeController.showhome());
    }

    /**
     * Given a user id this method retrieves the corresponding user profile
     * and renders a page displaying its information.
     *
     * @param request the http request
     * @param userId an Integer id for a given user
     * @return the view profile page or an unauthorized message is no user is logged in.
     */
    public Result showProfile(Http.Request request, Integer userId) {
        String userid = request.session().getOptional("connected").orElse(null);

        if (userid != null) {

            User user = User.find.byId(userId);
            return ok(showProfile.render(user));
        }
        return unauthorized("Oops, you are not logged in");
    }

    /**
     * If the user is logged in, renders the update traveller type page,
     * where the user can add or remove traveller types.
     * If the user is not logged in, returns an error.
     * If the user already has the selected traveller type, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result updateNatPass(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<User> userForm = formFactory.form(User.class).fill(user);
            try {
                addNatandPass();
            } catch (io.ebean.DuplicateKeyException e) {
                // Duplicate nationalities do not get added. No error msg shown.
            }
            List<Nationality> nationalities = Nationality.find.all();
            List<Passport> passports = Passport.find.all();
            return ok(updateNatPass.render(userForm, nationalities, passports, user));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Handles the request from the user to add traveller types to his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, add the selected traveller type into the user's profile.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return update traveller type page or error page
     */
    public Result submitUpdateNationality(Http.Request request){
        DynamicForm userForm = formFactory.form().bindFromRequest();
        String nationalityID = userForm.get("nationality");
        User user = User.getCurrentUser(request);
        if (user != null) {
            Nationality nationality = Nationality.find.byId(Integer.parseInt(nationalityID));
            try {
                user.addNationality(nationality);
                user.update();
            } catch (io.ebean.DuplicateKeyException e) {
                return unauthorized("Oops, you have already have this nationality");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.ProfileController.updateNatPass());
    }

    /**
     * Handles the request from the user to add traveller types to his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, add the selected traveller type into the user's profile.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return update traveller type page or error page
     */
    public Result submitUpdatePassport(Http.Request request){
        DynamicForm userForm = formFactory.form().bindFromRequest();
        String passportID = userForm.get("passport");
        User user = User.getCurrentUser(request);
        if (user != null) {
            Passport passport = Passport.find.byId(Integer.parseInt(passportID));
            try {
                user.addPassport(passport);
                user.update();
            } catch (io.ebean.DuplicateKeyException e) {
                return unauthorized("Oops, you have already have this passport");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.ProfileController.updateNatPass());
    }

    /**
     * Handles the request from the user to delete traveller types from his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, remove the selected traveller type from the user's profile.
     * If the user is not logged in, returns an error.
     * @param request the HTTP request
     * @return update traveller type page or error page
     */
    public Result deleteNationality(Http.Request request){
        DynamicForm userForm = formFactory.form().bindFromRequest();
        String nationalityID = userForm.get("nationalitydelete");
        User user = User.getCurrentUser(request);
        if (user != null) {
            try {
                Nationality nationality = Nationality.find.byId(Integer.parseInt(nationalityID));
                user.deleteNationality(nationality);
                user.update();
            } catch (NumberFormatException e) {
                return  unauthorized("Oops, you do not have any nationalities to delete");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.ProfileController.updateNatPass());
    }

    /**
     * Handles the request from the user to delete traveller types from his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, remove the selected traveller type from the user's profile.
     * If the user is not logged in, returns an error.
     * @param request the HTTP request
     * @return update traveller type page or error page
     */
    public Result deletePassport(Http.Request request){
        DynamicForm userForm = formFactory.form().bindFromRequest();
        String passportID = userForm.get("passportdelete");
        User user = User.getCurrentUser(request);
        if (user != null) {
            try {
                Passport passport = Passport.find.byId(Integer.parseInt(passportID));
                user.deletePassport(passport);
                user.update();
            } catch (NumberFormatException e) {
                return  unauthorized("Oops, you do not have any passports to delete");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.ProfileController.updateNatPass());
    }

    /**
     * adds all of the following traveller types to the database
     * @throws io.ebean.DuplicateKeyException if a type has already been added to the database
     */
    public void addNatandPass() throws io.ebean.DuplicateKeyException {
        String[] locales = Locale.getISOCountries();
        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            Nationality nationality = new Nationality(obj.getDisplayCountry());
            nationality.save();
            Passport passport = new Passport(obj.getDisplayCountry());
            passport.save();
        }

    }

}
