package controllers;

import accessors.TripAccessor;
import accessors.VisitAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import factories.TripFactory;
import factories.VisitFactory;
import formdata.TripFormData;
import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.User;
import models.Visit;
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
import utilities.UtilityFunctions;
import views.html.home.mapHome;
import views.html.users.trip.*;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
            return ok(createTrip.render(incomingForm, user));
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
                List<Destination> destinations = user.getDestinations();
                List<Destination> allDestinations = Destination.find().all();
                return ok(AddTripDestinationsTable.render(trip, destinations, allDestinations,user));
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
        if (user != null) {
            Form<TripFormData> incomingForm = formFactory.form(TripFormData.class).bindFromRequest(request);

            if (incomingForm.hasErrors()) {
                return badRequest(createTrip.render(incomingForm, user));
            }
            for (Trip trip: user.getTrips()) {
                if (incomingForm.get().tripName.equals(trip.getTripName())) {
                    return ok(createTrip.render(incomingForm.withError("tripName", "Cannot have duplicate trip names"), user));
                }
            }
            TripFormData created = incomingForm.get();
            int tripid = tripFactory.createTrip(created, user);
            return redirect(routes.TripController.addTripDestinations(tripid));
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
     * Renders the page to add destinations onto a new trip.
     * This has to be separated with existing trips due to the cancel button which deletes the trip.
     *
     * @param request the HTTP request
     * @param tripid the trip id of the trip
     * @return Result Will redirect to userIndex if success or error otherwise
     */
    public Result addTripDestinations(Http.Request request, Integer tripid) {
        Trip trip = Trip.find().byId(tripid);
        User user = User.getCurrentUser(request);
        Date today = new Date();
        today.setTime(today.getTime());
        if (user != null) {
            if (trip != null) {
                if (trip.isUserOwner(user.getUserid())) {
                    List<Visit> visits = trip.getVisits();
                    visits.sort(Comparator.comparing(Visit::getVisitOrder));
                    List<Destination> destinations = user.getDestinations();
                    List<Destination> allDestinations = Destination.find().all();
                    request.flash().getOptional("error").orElse("test");
                    return ok(AddTripDestinationsTable.render(trip, destinations, allDestinations,user)).flashing("error", request.flash().getOptional("error").orElse("test"));

                } else {
                    return unauthorized("Not your trip");
                }
            }
            else{
                return notFound("Oops, invalid trip ID");
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
     * Handles the request to add destinations to a new trip in the newly implemented table form.
     * Destinations with an arrival and departure timestamp are stored in the form of a Visit, HOWEVER, they will default
     * to null as a destination and arrival time is not specified on the table. This can be later edited.
     * The user is then redirected to the edit trip page.
     * If the user is not logged in, an error message is displayed.
     * @param request The HTTP request
     * @param tripid The trip id that the user is editing.
     * @param destid The destination of the trip that the user is editing
     * @return edit trip page or error page
     */
    public Result addVisitFromTable(Http.Request request, Integer tripid, Integer destid){
        User user = User.getCurrentUser(request);

        if(user == null) {
            return redirect(routes.UserController.userindex());
        }
        Trip trip = Trip.find().byId(tripid);
        if (!(trip.isUserOwner(user.getUserid()) || user.userIsAdmin())) {
            return unauthorized("Oops, this is not your trip.");
        }
        Integer visitSize = 0;
        if (trip.getVisits() != null) {
            visitSize = trip.getVisits().size();
        }
        Integer removedVisits = 0;
        if(trip.getRemovedVisits() != null) {
            removedVisits = trip.getRemovedVisits();
        }
        Integer visitOrder = visitSize + 1 + removedVisits;
        List<Visit> visits = trip.getVisits();
        Destination destination = Destination.find().byId(destid);
        if(destination == null) {
            return notFound("Destination not found");
        }
        if(!(destination.getIsPublic() || destination.getUser().getUserid() == user.getUserid() || user.userIsAdmin())) {
            return unauthorized("This private destination is owned by someone else. You may not use it.");
        }
        Visit visit = visitfactory.createVisitTable(trip, destination, visitOrder);
        if (tripFactory.hasRepeatDest(visits, visit, "ADD")) {
            //flash("danger", "You cannot have repeat destinations!");
            return redirect(routes.TripController.addTripDestinations(tripid)).flashing("error", "You cannot have repeat destinations!");
        }
        //if the destination is public but the owner of the destination is not an admin, set the owner of the destination to the default admin
        if (!(destination.getUser().isAdmin()) && destination.getIsPublic() && (destination.getUser().getUserid() != user.getUserid())) {
            User admin = User.find().byId(1);
            destination.setUser(admin);
            destination.update();
        }
        visit.save();
        return redirect(routes.TripController.addTripDestinations(tripid));




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
            if (!destination.getIsPublic() && destination.getUser().getUserid() != user.getUserid()) {
                return forbidden("2");

            }


            VisitFactory visitFactory = new VisitFactory();
            Visit newVisit = visitFactory.createVisitByJSRequest(destination, trip);
            System.out.println(newVisit);
            System.out.println(trip.getVisits());
            System.out.println(newVisit.getVisitOrder());
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
        System.out.println("creating trip with the user id being " + user.getUserid());
        if(user != null) {
            Destination destination = Destination.find().byId(destid);
            if(destination == null) {
                return notFound();
            }
            System.out.println("Destination is public :" + destination.getIsPublic());
            if (!destination.getIsPublic() && destination.getUser().getUserid() != user.getUserid()) {
                System.out.println("Dest forbid");
                return forbidden("2");

            }
            Visit visit = new Visit(null, null, null, destination);
            visit.setVisitorder(1);
            ArrayList<Visit> visits = new ArrayList<>();
            visits.add(visit);
            CreateTripFromVisitsCommand createTripFromVisitsCommand = new CreateTripFromVisitsCommand(visits, "Trip to " + destination.getDestName(), user);
            user.getCommandManager().executeCommand(createTripFromVisitsCommand);

            visit = VisitAccessor.getById(visit.getVisitid());
            Trip trip = visit.getTrip();

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
            System.out.println("Visit trip id is " +  visit.getTrip().getUser().getUserid());
            System.out.println("***********************************************");
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
        System.out.println("visit id is " + visitid);
        Visit visit = VisitAccessor.getById(visitid);
        System.out.println(visit.isTripOwner(user.getUserid()));
        if (visit == null) { return redirect(routes.UserController.userindex()); }

        Trip trip = visit.getTrip();

        System.out.println("User id from the visit trip|" + visit.getTrip().getUser().getUserid());

        System.out.println("User id from request|" + user.getUserid());
        System.out.println("User id from the trip|" +trip.getUser().getUserid());

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

//            ObjectNode tripNode = objectMapper.createObjectNode();

            ArrayNode destinationNodes = objectMapper.createArrayNode();

            for (Visit visit : trip.getOrderedVisits()) {
                ObjectNode destinationNode = objectMapper.createObjectNode();

                Destination destination = visit.getDestination();

                destinationNode.put("lat", destination.getLatitude());
                destinationNode.put("lng", destination.getLongitude());

                destinationNodes.add(destinationNode);
            }

            tripNodes.put(trip.getTripid().toString(), destinationNodes);



//            tripNodes.add(tripNode);
        }

        return ok(tripNodes);

    }


}

