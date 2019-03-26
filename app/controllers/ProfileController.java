package controllers;

import factories.UserFactory;
import formdata.NatFormData;
import formdata.UserFormData;
import models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import factories.UserFactory;
import formdata.UpdateUserFormData;
import models.Nationality;
import models.Passport;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.profile.*;

import javax.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A Class to handle interactions from the client to the frontend.
 */
public class ProfileController extends Controller {
    @Inject
    FormFactory formFactory;
    private String notLoggedInErrorStr = "Oops, you are not logged in";

    /**
     * If the user is logged in, renders the update profile page.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return update profile page or error page
     */
    public Result updateProfile(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            UpdateUserFormData updateUserFormData = UserFactory
                                            .getUpdateUserFormDataForm(request);

            Form<UpdateUserFormData> updateUserForm = formFactory
                        .form(UpdateUserFormData.class).fill(updateUserFormData);

            String[] gendersArray = {"Male", "Female", "Other"};
            List gendersList = Arrays.asList(gendersArray);

            return ok(updateProfile.render(updateUserForm, gendersList));
        }
        else{
            return unauthorized(notLoggedInErrorStr);
        }
    }


    /**
     * Handles the update profile request.
     * If the user is logged in, their profile is updated with the form values
     * and they are redirected to the home page.
     * If the user is not logged in, an error message is displayed.
     * @param request The HTTP request
     * @return home page or error page
     */
    public Result updateProfileRequest(Http.Request request){
        Form<UpdateUserFormData> updateProfileForm = formFactory
                            .form(UpdateUserFormData.class).bindFromRequest();

        // checking if a user is logged in.
        User user = User.getCurrentUser(request);
        if (user != null) {
            if (! updateProfileForm.hasErrors()) {
                // good update user information request
                // processing it
                this.updateUserProfile(updateProfileForm, user);
                return redirect(routes.HomeController.showhome());


            } else {
                //bad request, errors present
                String[] gendersArray = {"Male", "Female", "Other"};
                List gendersList = Arrays.asList(gendersArray);
                return badRequest(updateProfile.render(updateProfileForm, gendersList));
            }
        } else{
            return unauthorized(notLoggedInErrorStr);
        }
    }

    /**
     * Method to update a user using a UpdateUserFormData and user object.
     *
     * @param updateProfileForm A Form<UpdateUserFormData> containing the users
     *                          new information.
     * @param user The User to update.
     */
    public void updateUserProfile(Form<UpdateUserFormData> updateProfileForm,
                                                                    User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String firstName = updateProfileForm.get().firstName;
        String lastName = updateProfileForm.get().lastName;
        String gender = updateProfileForm.get().gender;
        String dateOfBirth = updateProfileForm.get().dateOfBirth;
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);

        user.setfName(firstName);
        user.setlName(lastName);
        user.setGender(gender);
        user.setDateOfBirth(birthDate);

        user.update();
        // Show the user their home page
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
        return unauthorized(notLoggedInErrorStr);
    }

    /**
     * If the user is logged in, renders the update traveller type page,
     * where the user can add or remove traveller types.
     * If the user is not logged in, returns an error.
     * If the user already has the selected traveller type, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result updateNatPass(Http.Request request ){
       // User user = User.getCurrentUser(request);

        int userId = UserFactory.getCurrentUserId(request);
        if (userId != -1) {
            NatFormData formData = new NatFormData();

            formData.userId = userId;
            Form<NatFormData> userForm = formFactory.form(NatFormData.class).fill(formData);


            try {
                addNatandPass();
            } catch (io.ebean.DuplicateKeyException e) {
                // Duplicate nationalities do not get added. No error msg shown.
                // Front end allows you to add one but wont get saved unless it is not on the
                //user already.
            }
            List<Nationality> nationalities = Nationality.find.all();
            List<Passport> passports = Passport.find.all();




            return ok(updateNatPass.render(userForm, nationalities, passports, userId));
        }
        else{
            return unauthorized(notLoggedInErrorStr);
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
        int user = UserFactory.getCurrentUserId(request);
        if (user != -1) {


            UserFactory.addNatsOnUser(user, nationalityID);

        }
        else{
            return unauthorized(notLoggedInErrorStr);
        }

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
        int userId = UserFactory.getCurrentUserId(request);
        if (userId != -1) {
            UserFactory.addPassportToUser(userId, passportID);

        }
        else{
            return unauthorized(notLoggedInErrorStr);
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

        Form<NatFormData> userForm = formFactory.form(NatFormData.class).bindFromRequest();




        if (userForm.hasErrors()) {

            int user = UserFactory.getCurrentUserId(request);
            List<Nationality> nationalities = Nationality.find.all();
            List<Passport> passports = Passport.find.all();

            return badRequest(updateNatPass.render(userForm, nationalities, passports, user));

        }else {
            String nationalityID = userForm.get().nationalitydelete;
            int userId = UserFactory.getCurrentUserId(request);
            if (userId != -1) {
                UserFactory.deleteNatsOnUser(userId, nationalityID);
            } else {
                return unauthorized("Oops, you are not logged in");
            }
        }
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
        int userId = UserFactory.getCurrentUserId(request);
        if (userId != -1) {
            UserFactory.deletePassportOnUser(userId, passportID);
        }
        else{
            return unauthorized(notLoggedInErrorStr);
        }

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
