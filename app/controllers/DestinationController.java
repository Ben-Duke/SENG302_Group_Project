package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import factories.DestinationFactory;
import formdata.DestinationFormData;
import formdata.UpdateUserFormData;
import models.*;


import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.util.*;


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

            return ok(createdestination.render(destFormData, Destination.getIsoCountries(), Destination.getTypeList(),user));
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
        Form<DestinationFormData> destinationFormData;
        destinationFormData = formFactory.form(DestinationFormData.class)
                                                                .bindFromRequest();
        User user = User.getCurrentUser(request);

        if (user != null) { // checks if a user is logged in
            if (! destinationFormData.hasErrors()) {
                // no form errors
                // processing it now
                DynamicForm destForm = formFactory.form().bindFromRequest();

                //If program gets past this point then inputted destination is valid

                Destination newDestination = formFactory.form(Destination.class)
                                                        .bindFromRequest().get();

                // checking if private and public destinations already exist. -----------
                DestinationFactory destinationFactory = new DestinationFactory();
                int userId = user.getUserid();

                boolean hasError = false;
                if (destinationFactory.userHasPrivateDestination(userId, newDestination)) {
                    flash("privateDestinationExists",
                            "You already have a matching private destination!");
                    hasError = true;
                }

                if (destinationFactory.doesPublicDestinationExist(newDestination)) {
                    flash("publicDestinationExists",
                            "A matching public destination already exists!");
                    hasError = true;
                }

                if (hasError) {
                    return badRequest(createdestination.render(destinationFormData,
                            Destination.getIsoCountries(), Destination.getTypeList(),user));
                } else {
                    newDestination.setUser(user);
                    newDestination.save();
                    return redirect(routes.DestinationController.indexDestination());
                }
            } else {
                return badRequest(createdestination.render(destinationFormData,
                        Destination.getIsoCountries(), Destination.getTypeList(),user));
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
     * @param destId the id of the given destination
     * @return renders the edit destination page, or an unauthorized message is no user is logged in, or
     * a not found error, or an unauthorized message if the destination does not belong to the user.
     */
    public Result editDestination(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);

        if (user != null) {
            Destination destination = Destination.find.query().where().eq("destid", destId).findOne();

            if (destination != null) {
                if (destination.isUserOwner(user.getUserid()) || user.userIsAdmin()) {

                    Form<Destination> destForm = formFactory.form(Destination.class).fill(destination);

                    Map<String, Boolean> typeList = Destination.getTypeList();
                    typeList.replace(destination.getDestType(), true);

                    Map<String, Boolean> countryList = Destination.getIsoCountries();
                    countryList.replace(destination.getCountry(), true);

                    return ok(editDestination.render(destForm, destination, countryList, typeList,user));

                } else {
                    return unauthorized("Not your destination. You can't edit.");
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

                Map<String, Boolean> countryList = Destination.getIsoCountries();
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

            Destination newDestination = formFactory.form(Destination.class).bindFromRequest().get();
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

                    List<TravellerType> travellerTypes = new ArrayList<>();
                    for (TravellerType travellerType : modReq.getNewTravellerTypes()) {
                        travellerTypes.add(travellerType);
                    }

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
                if (destination.isUserOwner(user.userid)) {
                    if(destination.visits.isEmpty()) {
                        destination.delete();
                        return redirect(routes.DestinationController.indexDestination());
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
                    //-----------checking if a public destination equivalent
                    // ----------already exists
                    DestinationFactory destFactory = new DestinationFactory();
                    if (destFactory.doesPublicDestinationExist(destination)) {
                        // public matching destination already exists
                        // show error
                        destFactory.removePrivateInformation(destination);
                        destination.setIsPublic(true);
                        destination.update();
                        return redirect(routes.DestinationController.indexDestination());
                    } else {
                        //no matching pub destination exists, making public now
                        //sets the destination to public, sets the owner to the default admin and updates the destination
                        destFactory.removePrivateInformation(destination);
                        destination.setIsPublic(true);
                        destination.update();
                        return redirect(routes.DestinationController.indexDestination());
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

    /**
     * Links a photo with a photo id to a destination with a destination id.
     * @param request the HTTP request
     * @param destId the destination that the photo should be linked to
     * @return success if the linking was successful, not found if destination or photo not found, unauthorized otherwise.
     */
    public Result linkPhotoToDestination(Http.Request request, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null) {
            JsonNode node = request.body().asJson().get("photoid");
            String photoid = node.textValue();
            photoid = photoid.replace("\"", "");
            UserPhoto photo = UserPhoto.find.byId(Integer.parseInt(photoid));
            Destination destination = Destination.find.byId(destId);
            if(destination != null || photo != null) {
                if (photo.getUser().getUserid() == user.getUserid()) {
                    //add checks for private destinations here once destinations have been merged in.
                    //You can only link a photo to a private destination if you own the private destination.
                    if(!photo.getDestinations().contains(destination)) {
                        photo.addDestination(destination);
                        photo.update();
                        System.out.println("SUCCESS!");
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
        return ok();
    }

    /**
     * Returns a json list of traveller types associated to a destination given by a destination id
     * @param request the HTTP request
     * @param destId the destination id
     * @return a json list of traveller types associated to the destination
     */
    public Result getTravellerTypes(Http.Request request, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null){
            return ok(Json.toJson(Destination.find.byId(destId).travellerTypes));
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns a json list of photos associated to a destination given by a destination id
     * @param request the HTTP request
     * @param destId the destination id
     * @return a json list of traveller types associated to the destination
     */
    public Result getPhotos(Http.Request request, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null){
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
     * @param request the HTTP request
     * @return the photo file
     */
    public Result getPhoto(Http.Request request, Integer photoId){
        User user = User.getCurrentUser(request);
        if(user != null){
            UserPhoto photo = UserPhoto.find.byId(photoId);
            if(photo.getUser().getUserid() == user.getUserid() || photo.isPublic() || user.userIsAdmin()) {
                return ok(Json.toJson(photo));
            } else{
                return unauthorized("Oops, you do not have the rights to view this photo");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns the destination as a json based on a destination ID
     * @param request the HTTP request
     * @param destId the destination ID
     * @return the destination as a json
     */
    public Result getDestination(Http.Request request, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null){
            Destination destination = Destination.find.byId(destId);
            if(destination.getIsPublic() || destination.getUser().getUserid() == user.getUserid() || user.userIsAdmin()) {
                return ok(Json.toJson(destination));
            }
            else{
                return unauthorized("Oops, this is a private destination and you don't own it.");
            }
        } else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Returns the destination owner's id as a json based on a destination ID
     * @param request the HTTP request
     * @param destId the destination ID
     * @return the destination as a json
     */
    public Result getDestinationOwner(Http.Request request, Integer destId){
        Destination destination = Destination.find.byId(destId);
        User user = User.find.query().where().eq("userid", destination.getUser().getUserid()).findOne();
        if(user != null){
            return ok(Json.toJson(user.getUserid()));
        } else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Sets the primary photo of a destination given by the destination ID.
     * @param request the HTTP request
     * @param destId the id of the destination to be updated
     * @return success if it worked, error otherwise
     */
    public Result setPrimaryPhoto(Http.Request request, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null) {
            JsonNode node = request.body().asJson().get("photoid");
            String photoid = node.textValue();
            photoid = photoid.replace("\"", "");
            UserPhoto photo = UserPhoto.find.byId(Integer.parseInt(photoid));
            Destination destination = Destination.find.byId(destId);
            if(destination != null || photo != null) {
                if ((destination.getUser().getUserid() == user.getUserid() && destination.getUserPhotos().contains(photo)) || user.userIsAdmin()) {
                    //add checks for private destinations here once destinations have been merged in.
                    //You can only link a photo to a private destination if you own the private destination.
                    destination.setPrimaryPhoto(photo);
                    destination.update();
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
        return ok();
    }

    /**
     * Gets JSON of all visible (public + the logged in users private photos)
     * Destinations avaliable to the user.
     *
     * @param request the HTTP request
     * @return a Result object containing the destinations JSON in it's body
     */
    public Result getVisibleDestinationMarkersJSON(Http.Request request) {
        User user = User.getCurrentUser(request);
        if(user != null) {
            int userId = user.getUserid();

            DestinationFactory destinationFactory = new DestinationFactory();

            List<Destination> publicDestinations;
            List<Destination> privateDestinations;
            publicDestinations = destinationFactory.getPublicDestinations();
            privateDestinations = destinationFactory.getUsersPrivateDestinations(userId);

            List<Destination> allVisibleDestination = new ArrayList<Destination>();
            allVisibleDestination.addAll(publicDestinations);
            allVisibleDestination.addAll(privateDestinations);

            return ok(Json.toJson(allVisibleDestination));
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }
}
