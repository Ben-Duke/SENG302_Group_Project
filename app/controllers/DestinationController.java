package controllers;

import accessors.DestinationAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import factories.DestinationFactory;
import formdata.DestinationFormData;
import models.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.destination.*;

import javax.inject.Inject;
import java.io.File;
import java.util.*;


import utilities.UtilityFunctions;
import views.html.users.destination.*;

public class DestinationController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    DestinationFactory destFactory;

    private final Logger logger = LoggerFactory.getLogger("application");
    UtilityFunctions utilityFunctions = new UtilityFunctions();
    /**
     * Performs validation tests on each on the users input for
     * each destination attribute.
     *
     * @param destForm all of the inputs from the user
     * @return notAcceptable error messages for failed test, or null if all valid
     */
    @Deprecated
    private Result validateDestination(DynamicForm destForm) {


        String destName = destForm.get("destName").trim();
        String destType = destForm.get("destType");
        String district = destForm.get("district").trim();
        String latitude = destForm.get("latitude");
        String longitude = destForm.get("longitude");
        String country = destForm.get("country");

        if (destName.length() < 1) {
            return notAcceptable("ERROR: Destination name  must not be empty.");
        }

        if (district.length() < 1) {
            return notAcceptable("ERROR: District must not be empty.");
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
        if (destType.length() < 1) {
            return notAcceptable("ERROR: Destination type must not be empty.");
        }
        if (country.length() < 1) {
            return notAcceptable("ERROR: Destination country must not be empty.");
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
        DestinationFactory destFactory = new DestinationFactory();

        if (user != null) {
            List<Destination> destinations = user.getDestinations();
            List<Destination> allDestinations = Destination.find.all();

            try {
                Set<String> countryList = UtilityFunctions.countriesAsStrings();

                for (Destination destination : destinations) {
                    destination.updateIsCountryValidGivenCountries(countryList);
                }
                for (Destination destination : allDestinations) {
                    destination.updateIsCountryValidGivenCountries(countryList);
                }

            } catch (Exception e) {
                //Do nothing
            }

            return ok(indexDestination.render(destinations, allDestinations, destFactory, user));


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
            Form<DestinationFormData> destFormData;
            destFormData = formFactory.form(DestinationFormData.class);

            Map<String, Boolean> countries = null;

            try{
                countries = utilityFunctions.CountryUtils();
            }catch(Exception error){
                System.out.println(error);
                System.out.println("Error getting countries");
            }

            return ok(createEditDestination.render(destFormData, null, countries , Destination.getTypeList(),user));
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
    public Result saveDestinationFromRequest(Http.Request request) {
        User user = User.getCurrentUser(request);

        if (user != null) { // checks if a user is logged in
                Result errorForm = validateEditCreateForm(request, user, null);
                if (errorForm != null) {
                    return errorForm;
                } else {
                    Destination newDestination = formFactory.form(Destination.class)
                            .bindFromRequest(request).get();
                    newDestination.setUser(user);
                    newDestination.updateIsCountryValid();
                    newDestination.save();


                    return redirect(routes.DestinationController.indexDestination());
                }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Takes in the id of a given destination, retrieves that destination from the database.
     * A page is rendered with the information of the destination loaded ready for editing.
     *
     * @param request the http request
     * @param destId  the id of the given destination
     * @return renders the edit destination page, or an unauthorized message is no user is logged in, or
     * a not found error, or an unauthorized message if the destination does not belong to the user.
     */
    public Result editDestination(Http.Request request, Integer destId) {
        Result unauthorised = UtilityFunctions.checkLoggedIn(request);
        if (unauthorised != null) {
            return unauthorised;
        }

        User user = User.getCurrentUser(request);
        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) {
            return notFound("Destination does not exist");
        } else if (!(user != null && (destination.isUserOwner(user.getUserid()) || user.userIsAdmin()))) {
            return unauthorized("Not your destination. You can't edit.");
        }

        return renderDestinationForm(user, destination, destId);
    }

    /** Render the destination form */
    private Result renderDestinationForm(User user, Destination destination, Integer destId) {
        DestinationFormData formData = destFactory.makeDestinationFormData(destination);

        Form<DestinationFormData> destForm = formFactory.form(
                DestinationFormData.class).fill(formData);

        // Select the dropdown values which were selected at form submission
        Map<String, Boolean> typeList = Destination.getTypeList();
        typeList.replace(destination.getDestType(), true);

        Map<String, Boolean> countryList = null;
        try{
            countryList = utilityFunctions.CountryUtils();
        }catch(Exception error){
            System.out.println(error);
        }
        countryList.replace(destination.getCountry(), true);

        return ok(createEditDestination.render(destForm, destId, countryList, typeList, user));
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

            // Validate form
            Result errorForm = validateEditCreateForm(request, user, destId);
            if (errorForm != null) {
                return errorForm;
            }

            // Save form
            Destination newDestination = formFactory.form(Destination.class).
                    bindFromRequest(request).get();
            Destination oldDestination = DestinationAccessor.getDestinationById(destId);

            if (oldDestination != null) {
                if (oldDestination.isUserOwner(user.userid)) {
                    oldDestination.applyEditChanges(newDestination);
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

    /** Perform form validation for the edit/create destination form
     *  Returns a request containing the form with errors if errors found,
     *  null if no errors found */
    private Result validateEditCreateForm(Http.Request request, User user, Integer destId) {
        Form<DestinationFormData> destForm;
        destForm = formFactory.form(DestinationFormData.class).bindFromRequest(request);

        // check if private and public destinations already exist.
        // Cannon use .get() on form unless there are no errors
        boolean hasError = false;
        if (!destForm.hasErrors()) {
            Destination destination = formFactory.form(Destination.class).
                    bindFromRequest(request).get();

            DestinationFactory destinationFactory = new DestinationFactory();
            int userId = user.getUserid();

            if (destinationFactory.userHasPrivateDestination(userId, destination)) {
                flash("privateDestinationExists",
                        "You already have a matching private destination!");
                hasError = true;
            } else if (destinationFactory.doesPublicDestinationExist(destination)) {
                flash("publicDestinationExists",
                        "A matching public destination already exists!");
                hasError = true;
            }
        }

        // Use a normal form to trigger validation
        if (destForm.hasErrors() || hasError) {

            Map<String, Boolean> typeList = Destination.getTypeList();
            Map<String, Boolean> countryList = null;
            try{
                utilityFunctions.CountryUtils();
            }catch(Exception error){
                System.out.println(error);
            }

            // Use a dynamic form to get the values of the dropdown inputs
            DynamicForm dynamicDestForm = formFactory.form().bindFromRequest(request);

            // Select the dropdown values which were selected at form submission
            typeList.replace(dynamicDestForm.get("destType"), true);
            countryList.replace(dynamicDestForm.get("country"), true);

            return badRequest(createEditDestination.render(destForm, destId, countryList,
                    typeList, user));
        } else {
            return null;    // no errors
        }
    }

    /**
     * Takes in the id of a given public destination, retrieves that destination from the database.
     * A page is rendered with the information of the destination loaded ready for editing.
     *
     * @param request the http request
     * @param destId the id of the destination that is to be edited
     * @return renders the editPublicDestination page, or an unauthorized message is no user is logged in, or
     * a not found error.
     */
    public Result editPublicDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            Destination destination = Destination.find.query().where().eq("destid", destId).findOne();

            if (destination != null) {
                Form<Destination> destForm = formFactory.form(Destination.class).fill(destination);

                Map<String, Boolean> typeList = Destination.getTypeList();
                typeList.replace(destination.getDestType(), true);

                Map<String, Boolean> countryList = null;

                try{
                    countryList = utilityFunctions.CountryUtils();
                }
                catch(Exception error){

                }
                countryList.replace(destination.getCountry(), true);

                List<TravellerType> travellerTypes = TravellerType.find.all();
                Map<String, Boolean> travellerTypesMap = new TreeMap<>();
                for (TravellerType travellerType : travellerTypes) {
                    if (destination.getTravellerTypes().contains(travellerType)) {
                        travellerTypesMap.put(travellerType.getTravellerTypeName(), true);
                    } else {
                        travellerTypesMap.put(travellerType.getTravellerTypeName(), false);
                    }
                }

                return ok(editPublicDestination.render(destForm, destination, countryList, typeList, travellerTypesMap, user));

            } else {
                return notFound("Destination does not exist");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Creates a new destination object from the edit page form, checks if inputs make a valid destination.
     * then using the given destination, checks if any changes have been made. If so, a request to the admins is
     * sent with the info of the old and new destinations awaiting their acceptance of the modification.
     *
     * @param request http request
     * @param destId the id of the destination that is being updated
     * @return redirects to view the updated destination if successful, or
     * a not found error.
     */
    public Result updatePublicDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            DynamicForm destForm = formFactory.form().bindFromRequest();
            Result validationResult = validateDestination(destForm);

            if (validationResult != null) {
                return validationResult;
            }
            //If program gets past this point then inputted destination is valid

            //Takes the request body and forms a custom map for binding, gets past Play not liking sets
            Map<String, String> map = new HashMap<>();
            fillDataWith(map, request.body().asFormUrlEncoded());
            Destination newDestination = formFactory.form(Destination.class).bind(map).get();
            if (newDestination.getTravellerTypes().isEmpty()) {
                newDestination.setTravellerTypes(new HashSet<>());
            }

            Destination oldDestination = Destination.find.query().where().eq("destid", destId).findOne();
            if (oldDestination != null) {
                if (newDestination.equals(oldDestination)) {

                    return badRequest("No changes suggested");
                } else {
                    DestinationModificationRequest newModReq = new DestinationModificationRequest(oldDestination, newDestination, user);
                    newModReq.save();

                    return redirect(routes.DestinationController.indexDestination());
                }

            } else {
                return notFound("The destination you are trying to update no longer exists");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * This method handles when an admin rejects a destination modification request
     * the modification request is deleted and the admin is redirected to the index
     * admin page.
     *
     * @param request
     * @param destModReqId the id of the destination modification request under review
     * @return given proper authorisation redirect to index admin page
     */
    public Result destinationModificationReject(Http.Request request, Integer destModReqId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.userid).findOne();
            if (currentAdmin != null) {
                DestinationModificationRequest modReq = DestinationModificationRequest.find.query().where().eq("id", destModReqId).findOne();
                if (modReq != null) {

                    modReq.delete();
                    return redirect(routes.AdminController.indexAdmin());
                } else {
                    return badRequest("Destination Modification Request does not exist");
                }
            } else {
                return unauthorized("Oops, you are not authorised.");
            }
        } else {
            return unauthorized("Oops, you are not logged in.");
        }
    }

    /**
     * This method handles when an admin accepts a destination modification request
     * the new values for the destination in the destination modification request
     * are used to update the destination, then the modification request is
     * is deleted and the admin is redirected to the index admin page.
     *
     * @param request
     * @param destModReqId the id of the destination modification request under review
     * @return given proper authorisation redirect to index admin page
     */
    public Result destinationModificationAccept(Http.Request request, Integer destModReqId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find.query().where().eq("userId", currentUser.userid).findOne();
            if (currentAdmin != null) {
                DestinationModificationRequest modReq = DestinationModificationRequest.find.query().where().eq("id", destModReqId).findOne();
                if (modReq != null) {

                    Destination oldDestination = modReq.getOldDestination();

                    oldDestination.setDestName(modReq.getNewDestName());
                    oldDestination.setDestType(modReq.getNewDestType());
                    oldDestination.setCountry(modReq.getNewDestCountry());
                    oldDestination.setDistrict(modReq.getNewDestDistrict());
                    oldDestination.setLatitude(modReq.getNewDestLatitude());
                    oldDestination.setLongitude(modReq.getNewDestLongitude());

                    Set<TravellerType> travellerTypes = new TreeSet<>();
                    travellerTypes.addAll(modReq.getNewTravellerTypes());

                    oldDestination.setTravellerTypes(travellerTypes);

                    oldDestination.update();

                    modReq.delete();

                    return redirect(routes.AdminController.indexAdmin());
                } else {
                    return badRequest("Destination Modification Request does not exist");
                }
            } else {
                return unauthorized("Oops, you are not authorised.");
            }
        } else {
            return unauthorized("Oops, you are not logged in.");
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
                if (destination.isUserOwner(user.userid) || user.userIsAdmin()) {
                    if(destination.visits.isEmpty()) {
                        List<TreasureHunt> treasureHunts = TreasureHunt.find.query().where().eq("destination", destination).findList();
                        if (treasureHunts.isEmpty()) {
                            destination.delete();
                            return redirect(routes.DestinationController.indexDestination());
                        } else {
                            return preconditionRequired("You cannot delete destinations while they are being used by the treasure hunts.");
                        }
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

    }

    /**
     * Makes a private destination from the database public, given its id.
     *
     * @param request the http request
     * @param destId the id of the destination that is being made public
     * @return redirects to the index page if successful, or a not found error,
     * or an unauthorized message if the destination does not belong to the user.
     */
    public Result makeDestinationPublic(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            Destination destination = Destination.find.query().where().eq("destid", destId).findOne();

            if (destination != null) {
                if (destination.isUserOwner(user.userid)) {
                    destination.updateIsCountryValid();
                    if (destination.getIsCountryValid()) {

                        //-----------checking if a public destination equivalent
                        // ----------already exists
                        DestinationFactory destFactory = new DestinationFactory();
                        if (destFactory.doesPublicDestinationExist(destination)) {
                            // public matching destination already exists
                            // show error
                            destination.setIsPublic(true);
                            destination.update();
                            return redirect(routes.DestinationController.indexDestination());
                        } else {
                            //no matching pub destination exists, making public now
                            //sets the destination to public, sets the owner to the default admin and updates the destination
                            List<Destination> matchingDests = destFactory.getOtherUsersMatchingPrivateDestinations(user.userid, destination);
                            if (matchingDests.size() == 0) {
                                destination.setIsPublic(true);
                                destination.update();
                            }
                            return redirect(routes.DestinationController.indexDestination());
                        }

                    } else {
                        return badRequest("The country for this destination is not valid. The destination can not be made public");
                    }
                } else {
                    return unauthorized("HEY!, not yours. You cant make public. How you get access to that anyway?... FBI!!! OPEN UP!");
                }
            } else {
                return notFound("Destination does not exist");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    public Result makeDestinationsMerge(Http.Request request, int destId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            Destination destination = Destination.find.query().where().eq("destid", destId).findOne();
            if (destination != null) {
                DestinationFactory destFactory = new DestinationFactory();
                List<Destination> matchingDests = destFactory.getOtherUsersMatchingPrivateDestinations(user.userid, destination);
                if (destination.isUserOwner(user.userid) || user.userIsAdmin()) {
                    if(!destFactory.mergeDestinations(matchingDests, destination)) {
                        flash("visitExists",
                                "This destination is used in a trip!");
                    }
                    return redirect(routes.DestinationController.indexDestination());
                } else {
                    return unauthorized("HEY!, not yours. You cant delete. How you get access to that anyway?... FBI!!! OPEN UP!");
                }
            } else {
                return notFound("Destination does not exist");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }


    /**
     * Links a photo with a photo id to a destination with a destination id.
     *
     * @param request the HTTP request
     * @param destId the destination that the photo should be linked to
     * @return success if the linking was successful, not found if destination or photo not found, unauthorized otherwise.
     */
    public Result linkPhotoToDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            JsonNode node = request.body().asJson().get("photoid");
            String photoid = node.textValue();
            photoid = photoid.replace("\"", "");
            UserPhoto photo = UserPhoto.find.byId(Integer.parseInt(photoid));
            Destination destination = Destination.find.byId(destId);
            if (destination != null || photo != null) {
                if (photo.getUser().getUserid() == user.getUserid()) {
                    //add checks for private destinations here once destinations have been merged in.
                    //You can only link a photo to a private destination if you own the private destination.
                    if (!photo.getDestinations().contains(destination)) {
                        photo.addDestination(destination);
                        photo.update();
                    } else {
                        return badRequest("You have already linked the photo to this destination.");
                    }
                } else {
                    return unauthorized("Oops, this is not your photo!");
                }
            } else {
                return notFound();
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
        return ok();
    }

    /**
     * Returns a json list of traveller types associated to a destination given by a destination id
     *
     * @param request the HTTP request
     * @param destId the destination id
     * @return a json list of traveller types associated to the destination
     */
    public Result getTravellerTypes(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            return ok(Json.toJson(Destination.find.byId(destId).travellerTypes));
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns a json list of photos associated to a destination given by a destination id
     *
     * @param request the HTTP request
     * @param destId the destination id
     * @return a json list of photos associated to the destination
     */
    public Result getPhotos(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            //To add: filter between private and public, but that's another task
            List<UserPhoto> photos = Destination.find.byId(destId).userPhotos;
            ObjectNode result = Json.newObject();
//            for(UserPhoto photo: photos){
//                result.put(Integer.toString(photo.getPhotoId()), new java.io.File(photo.getUrlWithPath()).toString());
//            }
            return ok(Json.toJson(Destination.find.byId(destId).userPhotos));
            //return ok(result);
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns a photo file based on a photo with a given photo id
     *
     * @param request the HTTP request
     * @return the photo file
     */
    public Result getPhoto(Http.Request request, Integer photoId) {
        User user = User.getCurrentUser(request);
        if(user != null){
            UserPhoto photo = UserPhoto.find.byId(photoId);
            if (photo.getUser().getUserid() == user.getUserid() || photo.isPublic() || user.userIsAdmin()) {
                return ok(Json.toJson(photo));
            } else {
                return unauthorized("Oops, you do not have the rights to view this photo");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns the destination as a json based on a destination ID
     *
     * @param request the HTTP request
     * @param destId  the destination ID
     * @return the destination as a json
     */
    public Result getDestination(Http.Request request, Integer destId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Destination destination = Destination.find.byId(destId);
            if (destination.getIsPublic() || destination.getUser().getUserid() == user.getUserid() || user.userIsAdmin()) {
                return ok(Json.toJson(destination));
            } else {
                return unauthorized("Oops, this is a private destination and you don't own it.");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns the destination owner's id as a json based on a destination ID
     *
     * @param request the HTTP request
     * @param destId the destination ID
     * @return the destination as a json
     */
    public Result getDestinationOwner(Http.Request request, Integer destId) {
        Destination destination = Destination.find.byId(destId);
        User user = User.find.query().where().eq("userid", destination.getUser().getUserid()).findOne();
        if (user != null) {
            return ok(Json.toJson(user.getUserid()));
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Sets the primary photo of a destination given by the destination ID.
     *
     * @param request the HTTP request
     * @param destId the id of the destination to be updated
     * @return success if it worked, error otherwise
     */
    public Result setPrimaryPhoto(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            JsonNode node = request.body().asJson().get("photoid");
            String photoid = node.textValue();
            photoid = photoid.replace("\"", "");
            UserPhoto photo = UserPhoto.find.byId(Integer.parseInt(photoid));
            Destination destination = Destination.find.byId(destId);
            if (destination != null || photo != null) {
                if ((destination.getUser().getUserid() == user.getUserid() && destination.getUserPhotos().contains(photo)) || user.userIsAdmin()) {
                    //add checks for private destinations here once destinations have been merged in.
                    //You can only link a photo to a private destination if you own the private destination.
                    destination.setPrimaryPhoto(photo);
                    destination.update();
                } else {
                    return unauthorized("Oops, this is not your photo!");
                }
            } else {
                return notFound();
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
        return ok();
    }

    /**
     * Gets List of all destinations visible to the given user
     * @param userId the user to look for private destinations of
     * @return The list of all public destinations and all private destinations that the user can see
     */
    private List<Destination> getVisibleDestinations(int userId) {
        DestinationFactory destinationFactory = new DestinationFactory();

        return destinationFactory.getAllVisibleDestinations(userId);
    }

    /**
     * Gets JSON of all visible (public + the logged in users private)
     * Destinations available to the user.
     *
     * @param request the HTTP request
     * @return a Result object containing the destinations JSON in it's body
     */
    public Result getVisibleDestinationMarkersJSON(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            int userId = user.getUserid();

            List<Destination> allVisibleDestination = getVisibleDestinations(userId);

            return ok(Json.toJson(allVisibleDestination));
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Adds a photo with a photo id to a destination with a destination id.
     * @param request the HTTP request
     * @param photoId the photoId of the photo to e added
     * @param destId the destination that the photo should be linked to
     * @return success if the linking was successful, not found if destination or photo not found, unauthorized otherwise.
     */
    public Result addPhotoToDestination(Http.Request request, Integer photoId, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null) {
            UserPhoto photo = UserPhoto.find.byId(photoId);
            Destination destination = Destination.find.byId(destId);
            if(destination != null && photo != null) {
                if (photo.getUser().getUserid() == user.getUserid()) {
                    //add checks for private destinations here once destinations have been merged in.
                    //You can only link a photo to a private destination if you own the private destination.
                    if(!photo.getDestinations().contains(destination)) {
                        photo.addDestination(destination);
                        photo.update();
                        System.out.println("SUCCESS!");
                        return redirect(routes.DestinationController.indexDestination());
                    }
                    else{
                        return badRequest("You have already linked the photo to this destination.");
                    }
                } else {
                    return unauthorized("Oops, this is not your photo!");
                }
            }
            else{
                return notFound();
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }


    /**
     * Returns an image file to the requester, accepts the UserPhoto id to send back the correct image.
     * @param request
     * @param destId
     * @return
     */
    public Result servePrimaryPicture(Http.Request request, Integer destId) {
        // User user = httpRequest.session().getOptional("connected").orElse(null);
        if(destId != null) {
            UserPhoto primaryPicture = DestinationFactory.getprimaryProfilePicture(destId);
            System.out.println("Path is " + primaryPicture.getUrlWithPath());
            if (primaryPicture != null) {
                System.out.println("Sending image back");
                return ok(new File(primaryPicture.getUrlWithPath()));
            } else {
                //should be 404 but then console logs an error
                return ok();
            }
        }
        else{
            return unauthorized("Oops, you're not logged in.");
        }
    }


    /**
     * Taken from Play framework
     * Takes an empty Map to fill with data from http body, this method helps replace the default way
     * of binding from request, which does not deal with sets, only lists
     * @param data The data map to add data from the http body to. Contains info about 1 Object
     * @param urlFormEncoded the data from the http request body, with details about the Object to bind
     */
    private void fillDataWith(Map<String, String> data, Map<String, String[]> urlFormEncoded) {
        urlFormEncoded.forEach((key, values) -> {
            if (key.endsWith("[]")) {
                String k = key.substring(0, key.length() - 2);
                Set<String> subData = new HashSet<>(Arrays.asList(values));
                data.put(k, subData.toString());
            } else if (values.length > 0) {
                data.put(key, values[0]);
            }
        });
    }

}
