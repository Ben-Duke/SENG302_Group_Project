package controllers;

import models.Nationality;
import models.TravellerType;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.profile.createprofile;
import views.html.users.travellertype.updatetraveller;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static play.mvc.Results.*;

public class TravellerTypeController {
    @Inject
    FormFactory formFactory;

    /**
     * If the user is logged in, renders the update traveller type page,
     * where the user can add or remove traveller types.
     * If the user is not logged in, returns an error.
     * If the user already has the selected traveller type, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result updateTravellerType(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<User> userForm = formFactory.form(User.class).fill(user);
            try {
                addTravelTypes();
            } catch (io.ebean.DuplicateKeyException e) {
                // Duplicate traveller types do not get added. No error msg shown.
            }
            List<TravellerType> travellerTypes = TravellerType.find.all();
            return ok(updatetraveller.render(userForm, travellerTypes, user));
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
    public Result submitUpdateTravellerType(Http.Request request){
        DynamicForm userForm = formFactory.form().bindFromRequest();
        String travellerID = userForm.get("travellertypes");
        User user = User.getCurrentUser(request);
        if (user != null) {
            TravellerType travellerType = TravellerType.find.byId(Integer.parseInt(travellerID));
            try {
                user.addTravellerType(travellerType);
                user.update();
            } catch (io.ebean.DuplicateKeyException e) {
                return unauthorized("Oops, you have already selected this activity type");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.TravellerTypeController.updateTravellerType());
    }

    /**
     * Handles the request from the user to delete traveller types from his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, remove the selected traveller type from the user's profile.
     * If the user is not logged in, returns an error.
     * @param request the HTTP request
     * @return update traveller type page or error page
     */
    public Result deleteUpdateTravellerType(Http.Request request){
        DynamicForm userForm = formFactory.form().bindFromRequest();
        String travellerID = userForm.get("travellertypesdelete");
        User user = User.getCurrentUser(request);
        if (user != null) {
            try {
                TravellerType travellerType = TravellerType.find.byId(Integer.parseInt(travellerID));
                user.deleteTravellerType(travellerType);
                user.update();
            } catch (NumberFormatException e) {
                return  unauthorized("Oops, you do not have any traveller types to delete");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.TravellerTypeController.updateTravellerType());
    }

    /**
     * adds all of the following traveller types to the database
     * @throws io.ebean.DuplicateKeyException if a type has already been added to the database
     */
    public void addTravelTypes() throws io.ebean.DuplicateKeyException {
        (new TravellerType("Groupie")).save();
        (new TravellerType("Thrillseeker")).save();
        (new TravellerType("Gap Year")).save();
        (new TravellerType("Frequent Weekender")).save();
        (new TravellerType("Holidaymaker")).save();
        (new TravellerType("Business Traveller")).save();
        (new TravellerType("Backpacker")).save();
    }
}
