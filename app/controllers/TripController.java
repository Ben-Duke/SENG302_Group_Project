package controllers;

import accessors.DestinationAccessor;
import accessors.TripAccessor;
import accessors.UserAccessor;
import accessors.VisitAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import factories.TripFactory;
import factories.VisitFactory;
import formdata.TripFormData;
import formdata.VisitFormData;
import models.*;
import models.commands.General.CommandPage;
import models.commands.Visits.EditVisitCommand;
import models.commands.Trips.DeleteTripCommand;
import models.commands.Trips.CreateTripFromVisitsCommand;
import models.commands.Visits.DeleteVisitCommand;
import org.slf4j.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.EnvVariableKeys;
import utilities.EnvironmentalVariablesAccessor;
import utilities.UtilityFunctions;
import views.html.users.trip.*;

import javax.inject.Inject;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TripController extends Controller {

    private final Logger logger = UtilityFunctions.getLogger();

    @Inject
    FormFactory formFactory;


    /**
     * The trip factory
     */
    TripFactory tripFactory = new TripFactory();
    VisitFactory visitfactory = new VisitFactory();


    /**
     * If the user is logged in, renders the create trip page.
     * If the user is not logged in, returns an error.
     *
     * @param request The HTTP request
     * @return create profile page or error page if user is not logged in
     */
    public Result createtrip(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TripFormData> incomingForm = formFactory.form(TripFormData.class);
            String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                               EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());
            return ok(createTrip.render(incomingForm, user, googleApiKey));
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }


    /**
     * Create trips from a list of visits. To be used for the save trip button for the map trips story.
     * @param visits a list of visits which are not linked a trip
     * @param name the name of the trip
     * @param user the user that's creating the trip
     * @return created status code
     */
    public Result createTripFromVisits(List<Visit> visits, String name, User user) {

        CreateTripFromVisitsCommand command = new CreateTripFromVisitsCommand(visits, name, user);
        user.getCommandManager().executeCommand(command);
        return created();

    }


    /**
     * Renders the page to display visits of a trip given by the trip id.
     * Users can swap visit destinations by drag and dropping them with their mouse, which should be saved within the
     * database.
     *
     * @param request The HTTP request
     * @param tripId the Id of the trip being displayed
     * @return display visits page
     */
    public Result displaytrip(Http.Request request, Integer tripId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            user.getCommandManager().setAllowedPage(CommandPage.TRIP);

            Trip trip = Trip.find().byId(tripId);
            if (trip == null) {
                return redirect(routes.HomeController.showhome());
            }
            List<Visit> visits = trip.getVisits();
            visits.sort(Comparator.comparing(Visit::getVisitOrder));

            if (trip.isUserOwner(user.getUserid())) {
                return notFound("Page not found");
            } else {
                return ok(displayTrip.render(trip, visits,user));
            }
        }
        else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Handles the create trip request.
     * If the user is logged in, a trip is created for the user based on the form values and stored into the database.
     * The user is then redirected to the create trip page.
     * If the user is not logged in, an error message is displayed.
     *
     * @param request The HTTP request
     * @return create trip page or error page
     */
    public Result savetrip(Http.Request request){
        User user = User.getCurrentUser(request);
        String googleApiKey = EnvironmentalVariablesAccessor.getEnvVariable(
                EnvVariableKeys.GOOGLE_MAPS_API_KEY.toString());

        if (user != null) {
            Form<TripFormData> incomingForm = formFactory.form(TripFormData.class).bindFromRequest(request);

            if (incomingForm.hasErrors()) {

                return badRequest(createTrip.render(incomingForm, user, googleApiKey));
            }
            for (Trip trip: user.getTrips()) {
                if (incomingForm.get().tripName.equals(trip.getTripName())) {
                    return ok(createTrip.render(incomingForm.withError("tripName", "Cannot have duplicate trip names"), user, googleApiKey));
                }
            }
            TripFormData created = incomingForm.get();
            int tripid = tripFactory.createTrip(created, user);


            if (incomingForm.get().tags != null && incomingForm.get().tags.length() > 0) {
                List<String> tags = Arrays.asList(incomingForm.get().tags.split(","));
                Set<Tag> uniqueTags = UtilityFunctions.tagLiteralsAsSet(tags);
                Trip trip = TripAccessor.getTripById(tripid);
                trip.setTags(uniqueTags);
                TripAccessor.update(trip);
            }
            return created();
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Renders the page to edit a visit given by the visit id.
     *
     * @param request the HTTP request
     * @param visitid the visit id
     * @return the edit visit page
     */
    public Result editvisit(Http.Request request, Integer visitid){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Visit visit = Visit.find().byId(visitid);


            VisitFormData visitFormData = new VisitFormData(visit.getArrival(), visit.getDeparture());
            Form<VisitFormData> visitForm = formFactory.form(VisitFormData.class).fill(visitFormData);
            if(visit.getTrip().getUser().getUserid() == user.getUserid() || user.userIsAdmin()) {
                List<Destination> destinations = new ArrayList<>();
                destinations.add(visit.getDestination());
                return ok(editVisit.render(visitForm, visit, destinations,user));
            }
            else{
                return unauthorized("Oops, this is not your trip.");
            }
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }


    /**
     * Handles the update visit request. Updates a visit with the given form details. If the updated visit would cause
     * two of the same destinations to be visited in a row, cancels the update and returns bad request.
     *
     * @param request the HTTP request
     * @param visitid the visitID of the visit being updated
     * @return OK if visit is successfully updated or Bad request otherwise
     */
    public Result updateVisit(Http.Request request, Integer visitid){
        Visit visit = Visit.find().byId(visitid);
        List<Destination> destinations = new ArrayList<>();
        destinations.add(visit.getDestination());
        Form<VisitFormData> visitForm;
        visitForm = formFactory.form(VisitFormData.class).bindFromRequest();
        User user = User.getCurrentUser(request);

        if (user != null) {
            if(visitForm.hasErrors()) {
                return badRequest(editVisit.render(visitForm, visit, destinations,user));
            }
            Integer destID = visit.getDestination().getDestId();
            String arrival = visitForm.get().arrival;
            String departure = visitForm.get().departure;

            visit = Visit.find().byId(visitid);
            Trip trip = visit.getTrip();
            if(trip.isUserOwner(user.getUserid())) {
                Destination dest = Destination.find().byId(destID);
                visit.setDestination(dest);
                //Arrivaldate and departure date TBD
                visit.setArrival(arrival);
                visit.setDeparture(departure);
                if(tripFactory.hasRepeatDest(trip.getVisits(), visit, "SWAP")){
                    return badRequest("You cannot visit the same destination twice in a row!");
                }
                EditVisitCommand editVisitCommand = new EditVisitCommand(visit);
                user.getCommandManager().executeCommand(editVisitCommand);
                return redirect(routes.TripController.displaytrip(trip.getTripid()));
            }
            else{
                return unauthorized("Oops, this is not your trip.");
            }
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Handles the cancellation of a trip on the page to add destinations to a new trip.
     * All visits are removed from the trip and the trip is removed from the database.
     *
     * @param request the HTTP request.
     * @param tripid the trip id of the trip being deleted
     * @return Result Redirects to userIndex if success or error otherwise
     */
    public Result deleteTrip(Http.Request request, Integer tripid) {
        Trip trip = Trip.find().byId(tripid);
        User user = User.getCurrentUser(request);
        if (user != null) {
            if(trip != null) {
                if (trip.getUser().getUserid() == user.getUserid() || user.userIsAdmin()) {
                    DeleteTripCommand deleteTripCommand = new DeleteTripCommand(trip);
                    user.getCommandManager().executeCommand(deleteTripCommand);
                    return redirect(routes.HomeController.mainMapPage());
                } else {
                    return unauthorized("Oops, this is not your trip.");
                }
            }
            else{
                return notFound("Trip not found");
            }
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Add visit based off destination Id to a trip
     * @return
     */
    public Result addVisitToTripJSRequest(Http.Request request, Integer tripId, Integer destId){
        User user = User.getCurrentUser(request);
        if(user != null) {
            Trip trip = Trip.find().byId(tripId);
            Destination destination = Destination.find().byId(destId);
            if (trip == null) {
                return notFound();
            }
            if (!trip.isUserOwner(user.getUserid())) {
                return forbidden("1");
            }
            if(destination == null) {
                return notFound();
            }
            if (!destination.getIsPublic() && !destination.isUserOwner(user)) {
                return forbidden("2");

            }

            // If public dest and user not owner and user not admin
            if (destination.getIsPublic() && !destination.isUserOwner(user) && !user.userIsAdmin()) {
                destination.setUser(UserAccessor.getById(1)); // change ownership to admin
                DestinationAccessor.update(destination);
            }


            VisitFactory visitFactory = new VisitFactory();
            Visit newVisit = visitFactory.createVisitByJSRequest(destination, trip);
            if (tripFactory.hasRepeatDest(trip.getVisits(), newVisit, "ADD")) {
                return badRequest("Trip cannot have two destinations in a row!");
            }
            else {
                VisitAccessor.insert(newVisit);

                trip.addVisit(newVisit);
                TripAccessor.update(trip);
                VisitAccessor.update(newVisit);
                ObjectNode data =  (ObjectNode) Json.toJson(trip);
                data.put("latitude", destination.getLatitude());
                data.put("longitude", destination.getLongitude());
                data.put("visitName", newVisit.getVisitName());
                data.put("visitId", newVisit.getVisitid());
                data.put("destType", destination.getDestType());
                data.put("arrival", newVisit.getArrival());
                data.put("departure", newVisit.getDeparture());
                data.put("tripName", trip.getTripName());
                data.put("tripId", trip.getTripid());


                return ok(Json.toJson(data));
            }

        }
        return unauthorized();
    }

    public Result CreateTripFromJSRequest(Http.Request request, Integer destid) {
        User user = User.getCurrentUser(request);
        if(user != null) {
            Destination destination = Destination.find().byId(destid);
            if(destination == null) {
                return notFound();
            }
            // If private dest and user not owner and user not admin
            if (!destination.getIsPublic() && !destination.isUserOwner(user) && !user.userIsAdmin()) {
                return forbidden("2");

            }

            // If public dest and user not owner and user not admin
            if (destination.getIsPublic() && !destination.isUserOwner(user) && !user.userIsAdmin()) {
                destination.setUser(UserAccessor.getById(1)); // change ownership to admin
                DestinationAccessor.update(destination);
            }

            Trip trip = new Trip("Trip to " + destination.getDestName(),false, user);
            Visit visit = new Visit(null, null, trip, destination);

            TripAccessor.insert(trip);
            VisitAccessor.insert(visit);
            visit.setVisitorder(0);
            TripAccessor.update(trip);
            VisitAccessor.update(visit);

            ObjectNode data =  (ObjectNode) Json.toJson(trip);
            data.put("latitude", destination.getLatitude());
            data.put("longitude", destination.getLongitude());
            data.put("visitName", visit.getVisitName());
            data.put("visitId", visit.getVisitid());
            data.put("destType", destination.getDestType());
            data.put("arrival", visit.getArrival());
            data.put("departure", visit.getDeparture());
            data.put("tripName", trip.getTripName());
            data.put("tripId", trip.getTripid());
            return ok(data);

        }

        return unauthorized();
    }

    /**
     * Handles the request to remove destinations from a trip. Removes the destination (which gets converted into a
     * visit) from the trip that the user is editing, then redirects the user to the edit trip page. Displays an error
     * if the user is not logged in.
     *
     * @param request The HTTP request
     * @param visitid The visit ID that the user is deleting.
     * @return Result edit trip page or error page
     */
    public Result deletevisit(Http.Request request, Integer visitid){

        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }
        Visit visit = VisitAccessor.getById(visitid);
        if (visit == null) { return redirect(routes.UserController.userindex()); }

        Trip trip = visit.getTrip();



        if (!trip.isUserOwner(user.getUserid())) { return unauthorized(); }

        List<Visit> visits = trip.getVisits();

        if (tripFactory.hasRepeatDest(visits, visit, "DELETE")) {
            return badRequest();
        }

        DeleteVisitCommand command = new DeleteVisitCommand(visit);
        user.getCommandManager().executeCommand(command);

        return ok();

    }


    /**
     * Gets new dates from request. Validates the dates. Updates the dates using the command.
     * @param request
     * @param visitId the id of the visit that is being editted
     * @return
     */
    public Result updateVisitDates(Http.Request request, Integer visitId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Visit visit = VisitAccessor.getById(visitId);
        if (visit == null) { return badRequest("Visit does not exits"); }

        Trip trip = visit.getTrip();
        if (!trip.isUserOwner(user.getUserid())) { return unauthorized(); }

        String arrivalDateString = new ObjectMapper().convertValue(request.body().asJson().get("arrival"), String.class);
        String departureDateString = new ObjectMapper().convertValue(request.body().asJson().get("departure"), String.class);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date arrivalDate = null;
        Date departureDate = null;
        Date existingArrivalDate;
        Date existingDepartureDate;

        try {
            if (!arrivalDateString.isEmpty()) {
                arrivalDate = format.parse(arrivalDateString);
            }
            if (!departureDateString.isEmpty()) {
                departureDate = format.parse(departureDateString);
            }

            if (visit.getArrival() != null) {
                existingArrivalDate = format.parse(visit.getArrival());
            }
            else {
                existingArrivalDate = format.parse("0000-01-01");
            }
            if (visit.getDeparture() != null) {
                existingDepartureDate = format.parse(visit.getDeparture());
            } else {
                existingDepartureDate = format.parse("9999-12-31");
            }

        } catch (ParseException e) {
            return badRequest();
        }



        if (!arrivalDateString.isEmpty() && !departureDateString.isEmpty()) {
            if ((arrivalDate.compareTo(departureDate) <= 0)) {

                visit.setArrival(arrivalDateString);
                visit.setDeparture(departureDateString);

                EditVisitCommand command = new EditVisitCommand(visit);
                user.getCommandManager().executeCommand(command);

            }


        } else if (!arrivalDateString.isEmpty()) {

            if ((arrivalDate.compareTo(existingDepartureDate) <= 0)) {

                visit.setArrival(arrivalDateString);

                EditVisitCommand command = new EditVisitCommand(visit);
                user.getCommandManager().executeCommand(command);

            }

        } else if (!departureDateString.isEmpty()) {
            if ((existingArrivalDate.compareTo(departureDate)) <= 0) {

                visit.setArrival(arrivalDateString);

                EditVisitCommand command = new EditVisitCommand(visit);
                user.getCommandManager().executeCommand(command);

            }

        }

        return ok();
    }

    public Result updateTripName(Http.Request request, Integer tripId) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        Trip trip = TripAccessor.getTripById(tripId);
        if (trip == null) { return badRequest("Trip does not exits"); }

        if (!trip.isUserOwner(user.getUserid())) { return unauthorized(); }

        String newTripName = new ObjectMapper().convertValue(request.body().asJson(), String.class);

        if (trip.getTripName().equals(newTripName)) {
            return ok();
        }

        if (newTripName.isEmpty() || newTripName.equals(" ")) {
            return badRequest();
        }

        trip.setTripName(newTripName);
        TripAccessor.update(trip);

        return ok();
    }


    /**
     * Handles the request to swap two destinations from a trip. If the swapped list has repeat destinations or the
     * user is not logged in or they are trying to swap a visit which does not belong to them, sends a bad request.
     * Displays an error if the user is not logged in.
     *
     * @param request The HTTP request
     * @param tripId The trip ID that the user is editing.
     * @return Result edit trip page or error page
     */
    public Result swapvisits(Http.Request request, Integer tripId){
        ArrayList<String> list = new ObjectMapper().convertValue(request.body().asJson(), ArrayList.class);
        User user = User.getCurrentUser(request);
        Trip trip = Trip.find().byId(tripId);
        if (user != null) {
            if(trip.isUserOwner(user.getUserid()) || user.userIsAdmin()) {
                if (tripFactory.swapVisitsList(list, user.getUserid())) {
                    return ok();
                } else {
                    return badRequest();
                }
            } else {
                return unauthorized("Not your trip");
            }
        }
        else{
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Returns the trip as a json based on a trip ID
     *
     * @param request the HTTP request
     * @param tripId  the trip ID
     * @return the trip as a json
     */
    public Result getTrip(Http.Request request, Integer tripId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = Trip.find().byId(tripId);
            if (trip.getUser().getUserid() == user.getUserid() || user.userIsAdmin()) {
                return ok(Json.toJson(trip));
            } else {
                return unauthorized("Oops, this is a private trip and you don't own it.");
            }
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Returns a map of th legnth of each of the user's trip and their is as a json
     *
     * @param request the HTTP request
     * @return Map of the length user's trips and trip id
     */
    public Result getLengthUserTrips(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Map<Integer, Integer> visitList = new HashMap<>();
            for (Trip trip: user.getTrips()) {
                visitList.put(trip.getTripid(), trip.getVisits().size());
            }

            return ok(Json.toJson(visitList));
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Get the trip photo being the photo of the first destination visited (or a placeholder)
     * @param request the http request
     * @param tripId the id of the trip which needs its photo to be retrieved
     * @return
     */
    public Result getTripPhoto(Http.Request request, Integer tripId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = TripAccessor.getTripById(tripId);
            if (trip == null) {
                return notFound();
            }
            Destination startDestination = trip.getOrderedVisits().get(0).getDestination();
            UserPhoto startPhoto = startDestination.getPrimaryAlbum().getPrimaryPhoto();
            if (startPhoto != null) {
                return ok(new File(startPhoto.getUrlWithPath()));
            } else {
                return ok(new File(ApplicationManager.getDefaultDestinationPhotoFullURL()));
            }

        }
        return redirect(routes.UserController.userindex());
    }


    /**
     * Turns a users trips into and array of json node with each destinations
     * coordinates
     * @param request
     * @return an ArrayNode of ArrayNodes of JsonNodes with lat and lng coordinates
     */
    public Result getTripsRoutesJson(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) { return redirect(routes.UserController.userindex()); }

        List<Trip> trips = user.getTrips();

        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode tripNodes = objectMapper.createObjectNode();

        for (Trip trip : trips) {
            ArrayNode destinationNodes = objectMapper.createArrayNode();

            for (Visit visit : trip.getOrderedVisits()) {
                ObjectNode destinationNode = objectMapper.createObjectNode();

                Destination destination = visit.getDestination();

                destinationNode.put("lat", destination.getLatitude());
                destinationNode.put("lng", destination.getLongitude());

                destinationNodes.add(destinationNode);
            }

            tripNodes.put(trip.getTripid().toString(), destinationNodes);
        }

        return ok(tripNodes);
    }

    private ObjectNode convertTripToJson(Trip trip) {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode tripNode = objectMapper.createObjectNode();
        ArrayNode visitNodes = objectMapper.createArrayNode();

        for (Visit visit : trip.getOrderedVisits()) {
            ObjectNode visitNode = objectMapper.createObjectNode();

            Destination destination = visit.getDestination();

            visitNode.put("visitId", visit.getVisitid());
            visitNode.put("visitName", visit.getVisitName());
            visitNode.put("destType", destination.getDestType());
            visitNode.put("lat", destination.getLatitude());
            visitNode.put("lng", destination.getLongitude());
            visitNode.put("arrivalDate", visit.getArrival());
            visitNode.put("departureDate", visit.getDeparture());

            visitNodes.add(visitNode);
        }
        tripNode.put("tripId", trip.getTripid());
        tripNode.put("tripName", trip.getTripName());
        tripNode.put("startDate", trip.getTripStart());
        tripNode.put("visits", visitNodes);

        return tripNode;
    }

    /**
     * Controller function to retrieve a list of trips matching the given name
     * @param request the HTTP request
     * @param name the name of the trip to match
     * @return the list of trips that match the name
     */
    public Result getTripsByName(Http.Request request, String name, int offset, int quantity) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        List<Trip> trips = TripAccessor.getTripsByName(name, user, offset, quantity);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode tripsListNode = objectMapper.createArrayNode();
        if(trips != null && trips.size() > 0) {
            for (Trip trip : trips) {

                ObjectNode tripNode = convertTripToJson(trip);

                tripsListNode.add(tripNode);
            }
            result.put("trips", tripsListNode);
            result.put("tripCount", TripAccessor.getTripsByNameTotalCount(name, user));
            return ok(Json.toJson(result));
        } else {
            result.put("trips", tripsListNode);
            result.put("tripCount", 0);

            return ok(Json.toJson(result));
        }


    }

    /**
     * Get a paginated list of trips for a user based on offset and quantity
     * @param request the HTTP request
     * @param offset the offset being the number of trips to skip before returning results
     * @param quantity an integer indicating the maximum length of the results
     * @return A JSON object with the trip data
     */
    public Result getPaginatedUserTrips(Http.Request request, int offset, int quantity) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        List<Trip> trips = TripAccessor.getPaginatedTrips(offset, quantity, user);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        ArrayNode tripsListNode = objectMapper.createArrayNode();
        for (Trip trip : trips) {

            ObjectNode tripNode = convertTripToJson(trip);

            tripsListNode.add(tripNode);
        }
        result.put("trips", tripsListNode);
        result.put("tripCount", TripAccessor.getTotalUserTripCount(user));
        return ok(Json.toJson(result));
    }



    public Result getTripsAsJson(Http.Request request, Integer tripId) {

        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        Trip trip = TripAccessor.getTripById(tripId);
        if (trip == null) {
            return badRequest();
        }

        if (!trip.isUserOwner(user.getUserid())) {
            return forbidden();
        }

        ObjectNode tripNode = convertTripToJson(trip);

        return ok(tripNode);
    }



}
