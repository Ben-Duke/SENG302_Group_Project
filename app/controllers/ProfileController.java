package controllers;

import accessors.UserAccessor;
import factories.UserFactory;
import formdata.NatFormData;
import formdata.UpdateUserFormData;
import io.ebean.DuplicateKeyException;
import models.Nationality;
import models.Passport;
import models.User;
import models.UserPhoto;
import models.commands.Profile.EditProfileCommand;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.profile.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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
        List<User> users = User.getCurrentUser(request, true);
        Boolean isAdmin = false;
        if (users.size() != 0) {
            User user = users.get(0);
            if(users.get(0).getUserid() != users.get(1).getUserid()) {
                isAdmin = true;
            }
            UpdateUserFormData updateUserFormData = UserFactory
                                            .getUpdateUserFormDataForm(request);


            Form<UpdateUserFormData> updateUserForm = formFactory
                        .form(UpdateUserFormData.class).fill(updateUserFormData);

            String[] gendersArray = {"Male", "Female", "Other"};
            List gendersList = Arrays.asList(gendersArray);

            return ok(updateProfile.render(updateUserForm, gendersList,user, isAdmin));
        }
        else{
            return redirect(routes.UserController.userindex());
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
                            .form(UpdateUserFormData.class).bindFromRequest(request);
        // checking if a user is logged in.
        List<User> users = User.getCurrentUser(request, true);
        Boolean isAdmin = false;
        if (users.size() != 0) {
            User user = users.get(0);
            if(users.get(0).getUserid() != users.get(1).getUserid())
            {
                isAdmin = true;
            }
            if (! updateProfileForm.hasErrors()) {
                // good update user information request
                // processing it
                this.updateUserProfile(updateProfileForm, user);
                return redirect(routes.HomeController.showhome());

            } else {
                //bad request, errors present
                String[] gendersArray = {"Male", "Female", "Other"};
                List gendersList = Arrays.asList(gendersArray);
                return badRequest(updateProfile.render(updateProfileForm, gendersList, user, isAdmin));
            }
        } else{
            return redirect(routes.UserController.userindex());
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
        String username = updateProfileForm.get().username;
        String passwordPlainText = updateProfileForm.get().password;

        user.setfName(firstName);
        user.setlName(lastName);
        user.setGender(gender);
        user.setDateOfBirth(birthDate);
        user.setEmail(username);

        if (0 < passwordPlainText.length()) {
            user.hashAndSetPassword(passwordPlainText);
        }

        EditProfileCommand editProfileCommand = new EditProfileCommand(user);
        editProfileCommand.execute();
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

        User user = User.getCurrentUser(request);

        if (user != null) {

            User otherUser = User.find.byId(userId);

            if (otherUser == null) {
                return notFound("User does not exist");
            }

            return ok(views.html.users.profile.showProfile.render(otherUser, user));
        }
        return redirect(routes.UserController.userindex());
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
            NatFormData formData = new NatFormData();

            formData.userId = user.getUserid();
            Form<NatFormData> userForm = formFactory.form(NatFormData.class).fill(formData);

            List<Nationality> nationalities = Nationality.find.all();
            List<Passport> passports = Passport.find.all();
            return ok(updateNatPass.render(userForm, nationalities, passports, user.getUserid(), User.getCurrentUser(request)));
        }
        else {
            return redirect(routes.UserController.userindex());
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
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        String nationalityID = userForm.get("nationality");
        User user = User.getCurrentUser(request);
        if (user != null) {

            UserFactory.addNatsOnUser(user.getUserid(), nationalityID);

        }
        else{
            return redirect(routes.UserController.userindex());
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
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        String passportID = userForm.get("passport");
        User user = User.getCurrentUser(request);
        if (user != null) {
            UserFactory.addPassportToUser(user.getUserid(), passportID);
        }
        else{
            return redirect(routes.UserController.userindex());
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
    public Result deleteNationality(Http.Request request){
        Form<NatFormData> userForm = formFactory.form(NatFormData.class).bindFromRequest(request);

        if (userForm.hasErrors()) {

//            int user = UserFactory.getCurrentUserId(request);
//            List<Nationality> nationalities = Nationality.find.all();
//            List<Passport> passports = Passport.find.all();
            flash("error", "Need at least one nationality, " +
                    "please add another nationality before deleting the one you selected");
            //return badRequest(updateNatPass.render(userForm, nationalities, passports, user));

        }else {
            String nationalityID = userForm.get().nationalitydelete;
            User user = User.getCurrentUser(request);
            if (user != null) {
                UserFactory.deleteNatsOnUser(user.getUserid(), nationalityID);
            } else {
                return redirect(routes.UserController.userindex());

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
        DynamicForm userForm = formFactory.form().bindFromRequest(request);
        String passportID = userForm.get("passportdelete");
        User user = User.getCurrentUser(request);
        if (user != null) {
            UserFactory.deletePassportOnUser(user.getUserid(), passportID);
        }
        else{
            return redirect(routes.UserController.userindex());
        }
        return redirect(routes.ProfileController.updateNatPass());
    }

    /**
     * AJAX endpoint to check if profile picture exists
     * @param request the HTTP request
     * @return a HTTP result with a body containing JSON
     */
    public Result isProfilePictureSet(Http.Request request) {
        User user = User.getCurrentUser(request);


        if(user != null) {
            UserPhoto profilePicture = null;
            try {
                profilePicture =  UserAccessor.getProfilePhoto(user);

            } catch (DuplicateKeyException e) {
                System.out.println("ERROR: duplicate profile photos");
            }

            String resultFormat = "{\"isProfilePicSet\": %s}";
            String body = null;

            if (profilePicture == null) {
                body = String.format(resultFormat, false);
            } else {
                body = String.format(resultFormat, true);
            }


            return ok(Json.parse(body));
        } else {
            return unauthorized("Oops! You are not logged in.");
        }

    }

}
