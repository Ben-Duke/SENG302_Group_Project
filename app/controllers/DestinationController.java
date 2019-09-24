package controllers;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.TreasureHuntAccessor;
import accessors.UserPhotoAccessor;
import accessors.UserPhotoAccessor;
import accessors.TreasureHuntAccessor;
import accessors.UserPhotoAccessor;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import factories.DestinationFactory;
import factories.TravellerTypeFactory;
import factories.UserFactory;
import formdata.DestinationFormData;
import io.ebean.Ebean;
import models.*;


import models.commands.Albums.CreateAlbumCommand;
import models.commands.Destinations.*;
import models.commands.General.CommandPage;
import models.commands.Photos.DeletePhotoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import utilities.CountryUtils;
import utilities.EnvVariableKeys;
import utilities.EnvironmentalVariablesAccessor;
import views.html.users.destination.*;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;


import utilities.UtilityFunctions;


/**
 * A controller class for handing destination actions..
 */
public class DestinationController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    DestinationFactory destFactory;

    private final Logger logger = LoggerFactory.getLogger("application");

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
            return notAcceptable("ERROR: Destination name must not be empty.");
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

    public Result getPlacesDetailsRequest(Http.Request request, String placeId) throws Exception {
        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+ placeId +
                "&key=" + googleApiKey;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();


        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JsonNode jsonJacksonArray = Json.parse(response.toString());



        return ok(jsonJacksonArray);
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
            user.getCommandManager().setAllowedPage(CommandPage.DESTINATION);

            CountryUtils.updateCountries();

            List<Destination> destinations = user.getDestinations();

            List<Destination> allDestinations = Destination.find().all();

            return ok();


        }
        return redirect(routes.UserController.userindex());
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
        if (user == null) { return redirect(routes.UserController.userindex()); }
;
        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) { return notFound("Destination does not exist"); }

        if (!destination.isUserOwner(user) && !user.userIsAdmin()) {
            if (!destination.getIsPublic()) {
                return unauthorized("Not your destination");
            }
        }

        boolean inEditMode = false;

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        return ok(destinationPage.render(user, destination, inEditMode,
                null, null, null, null,
                 googleApiKey));
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
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) { return notFound("Destination does not exist"); }


        if (!destination.isUserOwner(user) && !user.userIsAdmin()) {
            if (!destination.getIsPublic()) {
                return unauthorized("Not your destination");
            }
        }

        DestinationFormData formData = destFactory.makeDestinationFormData(destination);

        Form<DestinationFormData> destForm = formFactory.form(
                DestinationFormData.class).fill(formData);

        return renderEditDestination(user, destination, destForm);
    }

    /**
     * Creates maps for country, type, traveller types present in the given
     * destination. Renders the edit destination page.
     * @param user
     * @param destination
     * @param destForm
     * @return
     */
    private Result renderEditDestination(User user, Destination destination, Form<DestinationFormData> destForm) {

        boolean inEditMode = true;

        Map<String, Boolean> countries = CountryUtils.getCountriesMap();
        countries.replace(destination.getCountry(), true);

        Map<String, Boolean> types = Destination.getTypeList();
        types.replace(destination.getDestType(), true);

        Map<String, Boolean> travellerTypes = TravellerType.getTravellerTypeMap();
        for (TravellerType travellerType : destination.getTravellerTypes()) {
            String travellerTypeName = travellerType.getTravellerTypeName();
            if (travellerTypes.containsKey(travellerTypeName)) {
                travellerTypes.replace(travellerTypeName, true);
            }
        }

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        return ok(destinationPage.render(user, destination, inEditMode,
                destForm, countries, types, travellerTypes, googleApiKey));
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
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Destination oldDestination = DestinationAccessor.getDestinationById(destId);
        if (oldDestination == null) { return notFound("Destination not found"); }

        if (!oldDestination.isUserOwner(user) && !user.userIsAdmin()) {
            return unauthorized("Not your destination. You cant edit.");
        }

        // Validate form
        Result errorForm = validateDestinationEdit(request, user, destId);
        if (errorForm != null) {
            return errorForm;
        }

        Destination newDestination = getDestinationFromRequest(request);

        DestinationFactory destinationFactory = new DestinationFactory();

        List<Destination> matchingDestinations = destinationFactory.getMatching(newDestination);

        if(!matchingDestinations.isEmpty()) {
            destinationFactory.editDestinationMerge(matchingDestinations.get(0), oldDestination);
        } else {

            oldDestination.applyEditChanges(newDestination);

            EditDestinationCommand editDestinationCommand =
                    new EditDestinationCommand(oldDestination);
            user.getCommandManager().executeCommand(editDestinationCommand);


            return redirect(routes.DestinationController.viewDestination(destId));
        }
        return redirect(routes.HomeController.mainMapPage());

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
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) { return notFound("Destination not found"); }

        if (!destination.getIsPublic()) {
            return unauthorized("Not your destination. You cant edit.");
        }

        // Validate form
        Result errorForm = validateDestinationEdit(request, user, destId);
        if (errorForm != null) {
            return errorForm;
        }

        Destination newDestination = getDestinationFromRequest(request);

        if (newDestination.isSame(destination)) {
            return badRequest("No changes suggested");
        }

        DestinationModificationRequest newModReq = new DestinationModificationRequest(destination, newDestination, user);
        newModReq.save();

        boolean inEditMode = false;

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        return ok(destinationPage.render(user, destination, inEditMode,
                null, null, null, null,
                googleApiKey));
    }

    /**
     *
     * @param request
     * @param user
     * @param destId
     * @return
     */
    private Result validateDestinationEdit(Http.Request request, User user, Integer destId) {
        Form<DestinationFormData> destForm;
        destForm = formFactory.form(DestinationFormData.class).bindFromRequest(request);

        if (destForm.hasErrors()) {

            Destination destination = DestinationAccessor.getDestinationById(destId);

            return renderEditDestination(user, destination, destForm);
        }

        return null;
    }

    /**
     * Given an request creates a destination from the form.
     * @param request
     * @return
     */
    private Destination getDestinationFromRequest(Http.Request request) {
        Map<String, String> map = new HashMap<>();
        fillDataWith(map, request.body().asFormUrlEncoded());

        Destination destination = formFactory.form(Destination.class).bind(map).get();

        Set<TravellerType> travellerTypes = TravellerTypeFactory
                .formNewTravellerTypes(destination.getTravellerTypes());

        destination.setTravellerTypes(travellerTypes);

        return destination;
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
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) { return notFound("Destination not found"); }

        if (!destination.isUserOwner(user) && !user.userIsAdmin()) {
            return unauthorized("Not your destination. You can't Delete.");
        }

        if (user.userIsAdmin()) {

            DeleteDestinationCommand cmd = new DeleteDestinationCommand(
                    destination, true);
            user.getCommandManager().executeCommand(cmd);

        } else {

            if (!destination.getVisits().isEmpty()) {
                return preconditionRequired("You cannot delete destinations " +
                        "while you're using them for your trips. Delete them from" +
                        " your trip first!");
            }

            if (!TreasureHuntAccessor.getAllByDestination(destination).isEmpty()) {
                return preconditionRequired("You cannot delete destinations" +
                        " while they are being used by the treasure hunts.");
            }

            DeleteDestinationCommand cmd = new DeleteDestinationCommand(
                    destination, false);
            user.getCommandManager().executeCommand(cmd);

        }

        return redirect(routes.HomeController.mainMapPage());

    }


    /**
     * Makes a private destination from the database public, given its id.
     * Will merge other matching private destinations.
     *
     * @param request the http request
     * @param destId the id of the destination that is being made public
     * @return redirects to the index page if successful, or a not found error,
     * or an unauthorized message if the destination does not belong to the user.
     */
    public Result makeDestinationPublic(Http.Request request, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) {
            return notFound("Destination not found");
        }
        if (!destination.isUserOwner(user) && !user.userIsAdmin()) {
            return unauthorized("Not your destination to make public.");
        }
        if (destination.getIsPublic()) {
            return badRequest("Destination is already public");
        }
        if (!destination.getIsCountryValid()) {
            return badRequest("The country for this destination is not valid. " +
                    "The destination can not be made public");
        }


        DestinationFactory destFactory = new DestinationFactory();

        List<Destination> matchingDestinations = destFactory
                .getOtherUsersMatchingPrivateDestinations(user.getUserid(), destination);

        if (matchingDestinations.size() == 0) {
            destination.setIsPublic(true);
            DestinationAccessor.update(destination);

        } else {
            destFactory.mergeDestinations(matchingDestinations, destination);
        }

        // set the owner to be null since no one owns public destinations
        destination.setUser(null);
        destination.save();

        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());

        return ok(destinationPage.render(user, destination, false,
                null, null, null, null,
                googleApiKey));

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

            Map<String, Boolean> countryList = CountryUtils.getCountriesMap();

            return ok();
        }
        return redirect(routes.UserController.userindex());
    }

    /**
     * AJAX request.
     *
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
        logger.info("start");
        User user = User.getCurrentUser(request);

        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        Result errorForm = validateEditCreateForm(request, user, null);
        if (errorForm != null) {
            return errorForm;
        }

        Destination newDestination = formFactory.form(Destination.class)
                .bindFromRequest(request).get();
        logger.info("start get all dests");
        List<Destination> allDestinations = DestinationAccessor.getAllDestinations();
        logger.info("end get all dests");
        List<Destination> userAccessibleDestinations = new ArrayList<>();

        logger.info("start loop all dests");
        for (Destination existingDestination : allDestinations) {
            if (existingDestination.isUserOwner(user) ||
                    newDestination.getIsPublic()) {
                userAccessibleDestinations.add(existingDestination);
            }
        }
        logger.info("finish loop all dests");

        for (Destination existingDestination : userAccessibleDestinations) {
            if (newDestination.isSimilar(existingDestination) || newDestination.isSame(existingDestination)) {
                return badRequest();
            }
        }

        Form<DestinationFormData> destinationForm = formFactory.form(DestinationFormData.class).bindFromRequest();
        newDestination.setTags(new HashSet<>());
        newDestination.setUser(user);
        newDestination.setCountryValid(true);

        if (destinationForm.get().getTags() != null && destinationForm.get().getTags().length() > 0) {
            List<String> tags = Arrays.asList(destinationForm.get().getTags().split(","));
            Set uniqueTags = UtilityFunctions.tagLiteralsAsSet(tags);
            newDestination.setTags(uniqueTags);
        }
        newDestination.save();
        CreateAlbumCommand cmd = new CreateAlbumCommand(
                newDestination.getDestName(),
                newDestination,
                null);
        cmd.execute();

        logger.info("end");
        return ok(Json.toJson(newDestination));
    }






    public Result doesDestinationExist(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        String name = new ObjectMapper().convertValue(request.body().asJson().get("name"), String.class);
        String country = new ObjectMapper().convertValue(request.body().asJson().get("country"), String.class);
        String district = new ObjectMapper().convertValue(request.body().asJson().get("district"), String.class);
        Double latitude = new ObjectMapper().convertValue(request.body().asJson().get("latitude"), Double.class);
        Double longitude = new ObjectMapper().convertValue(request.body().asJson().get("longitude"), Double.class);

        Destination destination = new Destination();
        destination.setDestName(name);
        destination.setCountry(country);
        destination.setDistrict(district);
        destination.setLatitude(latitude);
        destination.setLongitude(longitude);

        List<Destination> allDestinations = DestinationAccessor.getAllDestinations();
        List<Destination> userAccessibleDestinations = new ArrayList<>();

        for (Destination existingDestination : allDestinations) {
            if (existingDestination.isUserOwner(user) ||
                    destination.getIsPublic()) {
                userAccessibleDestinations.add(existingDestination);
            }
        }

        for (Destination existingDestination : userAccessibleDestinations) {
            if (destination.isSimilar(existingDestination)) {
                return ok(Json.toJson(existingDestination.getDestId()));
            }
        }
        return created();
    }


    /** Perform form validation for the edit/create destination form
     *  Returns a request containing the form with errors if errors found,
     *  null if no errors found
     * @param destId the id of the given destination
     * @param user the id of the user
     * @param request the http request
     * @return validates the destination form and checks for errors and if there are matching destinations,
     * flashes messages if any are found.
     */
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
            Map<String, Boolean> countryList = CountryUtils.getCountriesMap();


            // Use a dynamic form to get the values of the dropdown inputs
            DynamicForm dynamicDestForm = formFactory.form().bindFromRequest(request);

            // Select the dropdown values which were selected at form submission
            typeList.replace(dynamicDestForm.get("destType"), true);
            if (countryList != null) {
                countryList.replace(dynamicDestForm.get("country"), true);
            }
            return badRequest(destForm.errorsAsJson());
        } else {
            return null;    // no errors
        }
    }


    /**
     * This method handles when an admin rejects a destination modification request
     * the modification request is deleted and the admin is redirected to the index
     * admin page.
     *
     * @param request the http request
     * @param destModReqId the id of the destination modification request under review
     * @return given proper authorisation redirect to index admin page
     */
    public Result destinationModificationReject(Http.Request request, Integer destModReqId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find().query().where().eq("userId", currentUser.getUserid()).findOne();
            if (currentAdmin != null) {
                DestinationModificationRequest modReq = DestinationModificationRequest.find().query().where().eq("id", destModReqId).findOne();
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
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * This method handles when an admin accepts a destination modification request
     * the new values for the destination in the destination modification request
     * are used to update the destination, then the modification request is
     * is deleted and the admin is redirected to the index admin page.
     *
     * @param request the http request
     * @param destModReqId the id of the destination modification request under review
     * @return given proper authorisation redirect to index admin page
     */
    public Result destinationModificationAccept(Http.Request request, Integer destModReqId) {
        User currentUser = User.getCurrentUser(request);
        if (currentUser != null) {
            Admin currentAdmin = Admin.find().query().where().eq("userId", currentUser.getUserid()).findOne();
            if (currentAdmin != null) {
                DestinationModificationRequest modReq = DestinationModificationRequest.find().query().where().eq("id", destModReqId).findOne();
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
            return redirect(routes.UserController.userindex());
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
            UserPhoto photo = UserPhoto.find().byId(Integer.parseInt(photoid));
            Destination destination = Destination.find().byId(destId);
            if (destination != null || photo != null) {
                if (photo.getUser().getUserid() == user.getUserid()) {
                    //add checks for private destinations here once destinations have been merged in.
                    //You can only link a photo to a private destination if you own the private destination.
                    if (!photo.getAlbums().contains(destination.getPrimaryAlbum())) {

                        LinkPhotoDestinationCommand cmd = new LinkPhotoDestinationCommand(photo, destination);
                        user.getCommandManager().executeCommand(cmd);


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
            return redirect(routes.UserController.userindex());
        }
        return ok();
    }

    public Result unlinkAndDelete(Http.Request request, int photoId){
        UserPhoto photo = UserPhoto.find().byId(photoId);
        DeletePhotoCommand deletePhotoCommand = new DeletePhotoCommand(photo);
        User user = User.getCurrentUser(request);
        if(user != null){
            if(photo.getUser().getUserid() == user.getUserid()) {
                user.getCommandManager().executeCommand(deletePhotoCommand);
                return ok();
            }
            else{
                return forbidden("Oops, this is not your photo");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Unlinks the UserPhoto from any destinations and then deletes it
     * @param request The HTTP request
     * @param photoId The Id of the photo being unlinked
     * @return success if the unlinking was successful.
     */
    public Result unlinkPhotoFromDestinationAndDelete(Http.Request request, int photoId) {
        UserPhoto photo = UserPhoto.find().byId(photoId);
            if (photo != null) {
                for (Destination destination : photo.getDestinations()) {
                    unlinkPhotoFromDestination(request, photoId, destination.getDestId());
                }
                UserPhoto.deletePhoto(photoId);
            }

        return ok();
    }


    /**
     * Removes the given destination from the list of destinations in the photos
     * @param request unused http request information
     * @param photoId the id of the photo to unlink
     * @param destId the id of the destination to unlink
     * @return response ok if the removal worked
     *         notFound if the destination or photo does not exist
     *         badRequest if the destination and photo were not linked     *
     */
    public Result unlinkPhotoFromDestination(Http.Request request, int photoId, int destId) {
        User user = User.getCurrentUser(request);
        UserPhoto photo = UserPhoto.find().byId(photoId);
        Destination destination = Destination.find().byId(destId);

        if (user == null) return redirect(routes.UserController.userindex());
        if (photo == null) return notFound("No photo found with that id");
        if (destination == null) return notFound("No destination found with that id");
        // This block checks if the user is the owner of either the photo or the destination.
        // If not the owner then returns an unauthorized error else proceeds as usual.
        if (!destination.isUserOwner(user)
                && photo.getUser().getUserid() != user.getUserid()) {
            return unauthorized("You cannot unlink this photo from this destination as neither of those belong to you.");
        }
        if (!photo.getAlbums().contains(destination.getPrimaryAlbum()))
            return badRequest("The destination was not linked to this photo");

        UnlinkPhotoDestinationCommand cmd = new UnlinkPhotoDestinationCommand(photo, destination);
        user.getCommandManager().executeCommand(cmd);

        return redirect(routes.DestinationController.viewDestination(destId));
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
            return ok(Json.toJson(Destination.find().byId(destId).getTravellerTypes()));
        } else {
            return redirect(routes.UserController.userindex());
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
        if(user != null){
            Destination destination = Destination.find().byId(destId);
            if (destination.getAlbums().isEmpty()) {
                Album album = new Album(destination, destination.getDestName(), false);
                AlbumAccessor.insert(album);
            }
            List<Media> photos = Destination.find().byId(destId).getPrimaryAlbum().getMedia();
            if(destination.getIsPublic() && !user.userIsAdmin()) {
                DestinationFactory destinationFactory = new DestinationFactory();
                destinationFactory.removePrivateMedia(photos, user.getUserid());
            }
            return ok(Json.toJson(photos));
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Returns a photo file based on a photo with a given photo id
     *
     * @param request the HTTP request
     * @param photoId the id for a given photo
     * @return the photo file
     */
    public Result getPhoto(Http.Request request, Integer photoId) {
        User user = User.getCurrentUser(request);
        if(user != null){
            Media photo = Media.find.byId(photoId);
            if (photo.getUser().getUserid() == user.getUserid() || photo.getIsPublic() || user.userIsAdmin()) {
                return ok(Json.toJson(photo));
            } else {
                return unauthorized("Oops, you do not have the rights to view this photo");
            }
        } else {
            return redirect(routes.UserController.userindex());
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
            Destination destination = Destination.find().byId(destId);
            if (destination.getIsPublic() || destination.isUserOwner(user) || user.userIsAdmin()) {
                return ok(Json.toJson(destination));
            } else {
                return unauthorized("Oops, this is a private destination and you don't own it.");
            }
        } else {
            return redirect(routes.UserController.userindex());
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
        Destination destination = Destination.find().byId(destId);
        User user = User.find().query().where().eq("userid", destination.getUser().getUserid()).findOne();
        if (user != null) {
            return ok(Json.toJson(user.getUserid()));
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Sets the primary photo of a destination given by the destination ID.
     *
     * @param request the HTTP request
     * @param destId the id of the destination to be updated
     * @return success if it worked, error otherwise
     */
    public Result setPrimaryPhoto(Http.Request request, Integer photoId, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) { return notFound("Destination not found"); }

        UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
        if (photo == null) { return notFound("Photo not found"); }

        if (!destination.getAlbums().get(0).getMedia().contains(photo)) {
            return badRequest("Photo not linked to destination");
        }

        if (!destination.isUserOwner(user) && !user.userIsAdmin()) {
            return unauthorized("Not your destination");
        }

        destination.getPrimaryAlbum().setPrimaryPhoto(photo);
        AlbumAccessor.update(destination.getPrimaryAlbum());


        return redirect(routes.DestinationController.viewDestination(destId));
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
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Gets a paginated jsonArray of public destinations based on an offset and quantity
     * @param request the HTTP request
     * @param offset an integer representing the number of destinations to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return a Result object containing the destinations JSON in it's body
     */
    public Result getAllDestinationForUserPaginated(Http.Request request, int offset, int quantity) {
        int MAX_QUANTITY = 1000;

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        if (MAX_QUANTITY < quantity) {
            String errorStr = "query parameter 'quantity' exceeded maximum " +
                    "allowed int: " + MAX_QUANTITY;

            ObjectNode jsonError = (new ObjectMapper()).createObjectNode();
            jsonError.put("error", errorStr);
            jsonError.put("quantityLimit", MAX_QUANTITY);
            return badRequest(Json.toJson(jsonError));
        }

        List<Destination> publicDestinations = DestinationAccessor
                .getPaginatedPublicDestinations(offset, quantity);


        List<Destination> privateDestinations = DestinationAccessor
                .getAllPrivateDestinations(user);
        publicDestinations.addAll(privateDestinations);

        ObjectNode result = (new ObjectMapper()).createObjectNode();
        result.set("destinations", Json.toJson(publicDestinations));
        result.put("totalCountPublic", Ebean.find(Destination.class).where()
                .eq("destIsPublic", true) .findCount());

        return ok(Json.toJson(result));
    }
    /**
     * Gets a paginated jsonArray of public destinations based on an offset and quantity
     * @param request the HTTP request
     * @param offset an integer representing the number of destinations to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return a Result object containing the destinations JSON in it's body
     */
    public Result getPaginatedPublicDestinations(Http.Request request, int offset, int quantity) {
        int MAX_QUANTITY = 1000;

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        if (MAX_QUANTITY < quantity) {
            String errorStr = "query parameter 'quantity' exceeded maximum " +
                    "allowed int: " + MAX_QUANTITY;

            ObjectNode jsonError = (new ObjectMapper()).createObjectNode();
            jsonError.put("error", errorStr);
            jsonError.put("quantityLimit", MAX_QUANTITY);
            return badRequest(Json.toJson(jsonError));
        }

        List<Destination> destinations = DestinationAccessor
                .getPaginatedPublicDestinations(offset, quantity);

        ObjectNode result = (new ObjectMapper()).createObjectNode();
        result.set("destinations", Json.toJson(destinations));
        result.put("totalCountPublic", Ebean.find(Destination.class).where()
                .eq("destIsPublic", true) .findCount());

        return ok(Json.toJson(result));
    }

    /**
     * Gets a paginated jsonArray of public destinations based on an offset and quantity
     * @param request the HTTP request
     * @param offset an integer representing the number of destinations to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return a Result object containing the destinations JSON in it's body
     */
    public Result getPaginatedPrivateDestinations(Http.Request request, int offset, int quantity) {
        int MAX_QUANTITY = 1000;

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        if (MAX_QUANTITY < quantity) {
            String errorStr = "query parameter 'quantity' exceeded maximum " +
                    "allowed int: " + MAX_QUANTITY;

            ObjectNode jsonError = (new ObjectMapper()).createObjectNode();
            jsonError.put("error", errorStr);
            jsonError.put("quantityLimit", MAX_QUANTITY);
            return badRequest(Json.toJson(jsonError));
        }

        List<Destination> destinations = DestinationAccessor
                .getAllPrivateDestinationsPaginated(user, offset, quantity);

        ObjectNode result = (new ObjectMapper()).createObjectNode();
        result.set("destinations", Json.toJson(destinations));
        result.put("totalCountPrivate", Destination.find().query().where().eq("user", user)
                .and().eq("destIsPublic", false)
                .findCount());

        return ok(Json.toJson(result));
    }

    /**
     * Adds a photo with a photo id to a destination with a destination id.
     * @param request the HTTP request
     * @param photoId the photoId of the photo to e added
     * @param destId the destination that the photo should be linked to
     * @return success if the linking was successful, not found if destination or photo not found, unauthorized otherwise.
     */
    public Result addPhotoToDestination(Http.Request request, Integer photoId, Integer destId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        UserPhoto photo = UserPhotoAccessor.getUserPhotoById(photoId);
        if (photo == null) { return notFound("Photo not found"); }

        Destination destination = DestinationAccessor.getDestinationById(destId);
        if (destination == null) { return notFound("Destination not found"); }

        if (photo.getUser().getUserid() != user.getUserid()) {
            return unauthorized("Not your photo.");
        }

        if (photo.getAlbums().contains(destination.getPrimaryAlbum())) {
            return badRequest("You have already linked the photo to this destination.");
        }


        LinkPhotoDestinationCommand cmd = new LinkPhotoDestinationCommand(photo, destination);
        user.getCommandManager().executeCommand(cmd);

        return redirect(routes.DestinationController.viewDestination(destId));
    }


    /**
     * Returns an image file to the requester, accepts the UserPhoto id to send back the correct image.
     * @param request the photo
     * @param destId the id of the destination tht is having it's primary photo set
     * @return success if setting primary photo was successful, not found if destination or photo not found, unauthorized otherwise.
     */
    public Result servePrimaryPicture(Http.Request request, Integer destId) {
        if (destId != null) {
            UserPhoto primaryPicture = DestinationFactory.getPrimaryPicture(destId);
            if (primaryPicture != null) {
                return ok(new File(primaryPicture.getUrlWithPath()));
            } else {
                //should be 404 but then console logs an error
                return ok(new File(ApplicationManager.getDefaultDestinationPhotoFullURL()));
            }
        } else {
            return redirect(routes.UserController.userindex());
        }
    }





    public Result renderMap(Http.Request request) {
        User user = User.getCurrentUser(request);
//        return ok(googlePlacesMapDocumentationExample.render(user));
        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
        return ok(googlePlacesMapDocumentationExample.render(user, googleApiKey));
    }

    /**
     * Controller function to retrieve a list of trips matching the given name
     * @param request the HTTP request
     * @param name the name of the trip to match
     * @return the list of trips that match the name
     */
    public Result getDestinationsByName(Http.Request request, String name, int offset, int quantity) {
        int MAX_QUANTITY = 1000;
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        if (MAX_QUANTITY < quantity) {
            String errorStr = "query parameter 'quantity' exceeded maximum " +
                    "allowed int: " + MAX_QUANTITY;

            ObjectNode jsonError = (new ObjectMapper()).createObjectNode();
            jsonError.put("error", errorStr);
            jsonError.put("quantityLimit", MAX_QUANTITY);
            return badRequest(Json.toJson(jsonError));
        }

        Set<Destination> destinations = DestinationAccessor
                .getDestinationsWithKeyword(name, quantity ,offset, user);

        ObjectNode result = (new ObjectMapper()).createObjectNode();
        result.set("destinations", Json.toJson(destinations));
        result.put("totalCountPublic", Destination.find().query().where().like("destName", "%" + name + "%").where().eq("destIsPublic", true).findCount());
        result.put("totalCountPrivate", Destination.find().query().where().like("destName", "%" + name + "%").where().eq("destIsPublic", false).where().eq("user", user).findCount());


        return ok(Json.toJson(result));
    }
}

