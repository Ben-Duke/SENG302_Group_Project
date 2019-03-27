package controllers;

import models.Destination;
import models.User;


import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.*;


import utilities.UtilityFunctions;
import views.html.users.destination.*;

public class DestinationController extends Controller {

    @Inject
    FormFactory formFactory;

    /**
     * Performs validation tests on each on the users input for
     * each destination attribute.
     *
     * @param destForm all of the inputs from the user
     * @return notAcceptable error messages for failed test, or null if all valid
     */
    private Result validateDestination(DynamicForm destForm) {


        String destName = destForm.get("destName").trim();
        String destType = destForm.get("destType");
        String country = destForm.get("country");
        String district = destForm.get("district").trim();
        String latitude = destForm.get("latitude");
        String longitude = destForm.get("longitude");

        //todo add a validation method that works for destNames with apostrophes and spaces
//        if (! UtilityFunctions.isStringAllAlphabetic(destName) || destName.length() < 1) {
//            return notAcceptable("ERROR: Destination name should only contain alphanumeric characters and must not be empty.");
//        }

        if (! UtilityFunctions.isStringAllAlphabetic(district) || district.length() < 1) {
            return notAcceptable("ERROR: District should only contain alphanumeric characters and must not be empty.");
        }
        try {
            Double.parseDouble(latitude);
        } catch (NumberFormatException e) {
            return notAcceptable("ERROR: Entered latitude is not a number");
        }
        if (! (Double.parseDouble(latitude) >= -90 && Double.parseDouble(latitude) <= 90)) {
            return notAcceptable("ERROR: Entered latitude must be between -90 and 90");
        }
        try {
            Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            return notAcceptable("ERROR: Entered longitude is not a number");
        }
        if (! (Double.parseDouble(longitude) >= -180 && Double.parseDouble(longitude) <= 180)) {
            return notAcceptable("ERROR: Entered longitude must be between -180 and 180");
        }
        if (! destType.matches("^[a-zA-Z0-9]+$") || destType.length() < 1) {
            return notAcceptable("ERROR: Destination type should only contain alphanumeric characters and must not be empty.");
        }

        return null;
    }


    /**
     * Gets all the current users destinations renders the index page displaying them.
     *
     * @param request the http request
     * @return the destinations index page which displays all the users destinations
     * or an unauthorized message is no user is logged in.
     */
    public Result indexDestination(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            List<Destination> destinations = user.getDestinations();
            return ok(indexDestination.render(destinations));


        }
        return unauthorized("Oops, you are not logged in");
    }

    /**
     * Given a destination id this method retrieves the corresponding destination
     * and renders a page displaying its information.
     *
     * @param request the http request
     * @param destId an Integer id for a given destination
     * @return the view destination page or an unauthorized message is no user is logged in.
     */
    public Result viewDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {

            Destination destination = Destination.find.byId(destId);
            return ok(viewDestination.render(destination));
        }
        return unauthorized("Oops, you are not logged in");
    }

    /**
     * This method renders a page where a user can create a destination.
     *
     * @param request the http request
     * @return the create destination page or an unauthorized message is no user is logged in.
     */
    public Result createDestination(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            Form<Destination> destForm = formFactory.form(Destination.class);

            return ok(createdestination.render(destForm, Destination.getIsoCountries(), Destination.getTypeList()));
        }
        return unauthorized("Oops, you are not logged in");
    }




    /**
     * Extracts a destination object from the form user fills out.
     * Checks if input makes a valid destination.
     * Associates the current user with that destination.
     * Saves the destination to the database.
     * Takes the user back to the index page.
     *
     * @param request the http request
     * @return renders the index page or an unauthorized message is no user is logged in.
     */
    public Result saveDestination(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            DynamicForm destForm = formFactory.form().bindFromRequest();
            Result validationResult = validateDestination(destForm);

            if (validationResult != null) {
                return validationResult;
            }
            //If program gets past this point then inputted destination is valid

            Destination destination = formFactory.form(Destination.class).bindFromRequest().get();

            destination.setUser(user);
            destination.save();
        } else {
            return unauthorized("Oops, you are not logged in");
        }

        return redirect(routes.DestinationController.indexDestination());
    }

    /**
     * Takes in the id of a given destination, retrieves that destination from the database.
     * A page is rendered with the information of the destination loaded ready for editing.
     *
     * @param request the http request
     * @param destId the id of the given destination
     * @return renders the edit destination page, or an unauthorized message is no user is logged in, or
     * a not found error, or an unauthorized message if the destination does not belong to the user.
     */
    public Result editDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            Destination destination = Destination.find.query().where().eq("destid", destId).findOne();

            if (destination != null) {
                if (destination.isUserOwner(user.userid)) {

                    Form<Destination> destForm = formFactory.form(Destination.class).fill(destination);

                    Map<String, Boolean> typeList = Destination.getTypeList();
                    typeList.replace(destination.getDestType(), true);

                    Map<String, Boolean> countryList = Destination.getIsoCountries();
                    typeList.replace(destination.getCountry(), true);

                    return ok(editDestination.render(destForm, destination, countryList, typeList));

                } else {
                    return unauthorized("Not your destination. You cant edit.");
                }
            } else {
                return notFound("Destination does not exist");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Creates a new destination object from the edit page form, checks if inputs make a valid destination.
     * then using the given destination, all the attributes of the old destination are updated with the new attributes.
     * The old destination is then updated in the database.
     *
     * @param request http request
     * @param destId the id of the destination that is being updated
     * @return redirects to view the updated destination if successful, or
     * a not found error, or an unauthorized message if the destination does not belong to the user.
     */
    public Result updateDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            DynamicForm destForm = formFactory.form().bindFromRequest();
            Result validationResult = validateDestination(destForm);

            if (validationResult != null) {
                return validationResult;
            }
            //If program gets past this point then inputted destination is valid

            Destination newDestination = formFactory.form(Destination.class).bindFromRequest().get();


            Destination oldDestination = Destination.find.query().where().eq("destid", destId).findOne();

            if (oldDestination != null) {

                if (oldDestination.isUserOwner(user.userid)) {

                    oldDestination.setDestName(newDestination.getDestName());
                    oldDestination.setDestType(newDestination.getDestType());
                    oldDestination.setCountry(newDestination.getCountry());
                    oldDestination.setDistrict(newDestination.getDistrict());
                    oldDestination.setLatitude(newDestination.getLatitude());
                    oldDestination.setLongitude(newDestination.getLongitude());

                    oldDestination.update();

                    return redirect(routes.DestinationController.indexDestination());

                } else {
                    return unauthorized("Not your destination. You cant edit.");
                }
            } else {
                return notFound("The destination you are trying to update no longer exists");
            }

        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Deletes a destination from the database given its id.
     *
     * @param request the http request
     * @param destId the id of the destination that is being deleted
     * @return redirects to the index page if successful, or a not found error,
     * or an unauthorized message if the destination does not belong to the user.
     */
    public Result deleteDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            Destination destination = Destination.find.query().where().eq("destid", destId).findOne();

            if (destination != null) {
                if (destination.isUserOwner(user.userid)) {
                    if(destination.visits.isEmpty()) {
                        destination.delete();
                    }
                    else{
                        return preconditionRequired("You cannot delete destinations while you're using them for your trips. Delete them from your trip first!");
                    }
                } else {
                    return unauthorized("HEY!, not yours. You cant delete. How you get access to that anyway?... FBI!!! OPEN UP!");
                }
            } else {
                return notFound("Destination does not exist");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }

        return redirect(routes.DestinationController.indexDestination());
    }

}
