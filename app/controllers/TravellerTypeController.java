package controllers;

import models.Destination;
import models.TravellerType;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.destination.updateDestinationTraveller;
import views.html.users.travellertype.updatetraveller;

import javax.inject.Inject;

import java.util.List;

import static play.mvc.Controller.flash;
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
            List<TravellerType> travellerTypes = TravellerType.find.all();
            travellerTypes.removeAll(user.getTravellerTypes());

            return ok(updatetraveller.render(userForm, travellerTypes, user));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * If the user is logged in, renders the update traveller type page,
     * where the user can add or remove traveller types.
     * If the user is not logged in, returns an error.
     * If the user already has the selected traveller type, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result updateDestinationTravellerType(Http.Request request, Integer destid){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Destination destination = Destination.find.byId(destid);
            if(destination != null) {
                if(destination.getUser().getUserid() == user.getUserid()) {
                    Form<Destination> destForm = formFactory.form(Destination.class).fill(destination);
                    List<TravellerType> travellerTypes = TravellerType.find.all();
                    travellerTypes.removeAll(destination.getTravellerTypes());
                    return ok(updateDestinationTraveller.render(destForm, travellerTypes, destination));
                }
                else{
                    return unauthorized("You do not own this destination!");
                }
            }else{
                return notFound("Destination not found!");
            }
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
        return redirect(routes.TravellerTypeController.updateTravellerType());
    }

    /**
     * Handles the request from the user to add traveller types to his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, add the selected traveller type into the user's profile.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return update traveller type page or error page
     */
    public Result submitUpdateDestinationTravellerType(Http.Request request, Integer destid){
        DynamicForm destForm = formFactory.form().bindFromRequest();
        String travellerID = destForm.get("travellertypes");
        User user = User.getCurrentUser(request);
        Destination destination = Destination.find.byId(destid);
        if (user != null) {
            TravellerType travellerType = TravellerType.find.byId(Integer.parseInt(travellerID));
            try {
                if(destination != null) {
                    if(destination.getUser().getUserid() == user.getUserid()) {
                        destination.addTravellerType(travellerType);
                        destination.update();
                    }
                    else{
                        return unauthorized("You do not own this destination!");
                    }
                } else{
                    return notFound("Destination not found!");
                }
            } catch (io.ebean.DuplicateKeyException e) {
                return unauthorized("Oops, you have already selected this activity type");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        return redirect(routes.TravellerTypeController.updateDestinationTravellerType(destid));
    }

    /**
     * Handles the request from the user to delete traveller types from his profile
     * and redirects them to the update traveller page.
     * If the user is logged in, remove the selected traveller type from the user's profile.
     * If the user is not logged in, returns an error.
     * @param request the HTTP request
     * @return update traveller type page or error page
     */
    public Result deleteUpdateTravellerType(Http.Request request, Integer typeId){
        User user = User.getCurrentUser(request);
        if (user == null) {
            return unauthorized("Oops, you are not logged in");
        } else if (! (user.getTravellerTypes().size() > 1)) {
            flash("error", "Need at least one traveller type, " +
                    "please add another traveller type before deleting the one you selected");
        } else {
            try {
                TravellerType travellerType = TravellerType.find.byId(typeId);
                user.deleteTravellerType(travellerType);
                user.update();

            } catch (NumberFormatException e) {
                return unauthorized("Oops, you do not have any traveller types to delete");
            }
        }
        return redirect(routes.TravellerTypeController.updateTravellerType());
    }

    /**
     * Destination and users could potentially be turned into interfaces to avoid duplicate code/methods
     * Handles the request from the user to delete traveller types from a destination
     * and redirects them to the update traveller page.
     * If the user is logged in and the user owns the destination, remove the selected traveller type from the destination.
     * If the user is not logged in, returns an error.
     * @param request the HTTP request
     * @return update traveller type page or error page
     */
    public Result deleteUpdateDestinationTravellerType(Http.Request request, Integer destId, Integer typeId){
        User user = User.getCurrentUser(request);
        Destination destination = Destination.find.byId(destId);
        if (user == null) {
            return unauthorized("Oops, you are not logged in");
        }  else {
            try {
                TravellerType travellerType = TravellerType.find.byId(typeId);
                destination.deleteTravellerType(travellerType);
                destination.update();
            } catch (NumberFormatException e) {
                return unauthorized("Oops, you do not have any traveller types to delete");
            }
        }
        return redirect(routes.TravellerTypeController.updateDestinationTravellerType(destId));
    }
}
