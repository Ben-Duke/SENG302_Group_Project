package controllers;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import factories.TripFactory;
import java.util.Date;

import factories.VisitFactory;
import formdata.TripFormData;
import formdata.VisitFormData;
import models.Destination;
import models.Trip;
import models.User;
import models.Visit;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.trip.createTrip;
import views.html.users.trip.displayTrip;
import views.html.users.trip.*;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

public class TripController extends Controller {


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
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result createtrip(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TripFormData> incomingForm = formFactory.form(TripFormData.class);
            return ok(createTrip.render(incomingForm, user));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Renders the page to display visits of a trip given by the trip id.
     * Users can swap visit destinations by drag and dropping them with their mouse, which should be saved within the
     * database.
     * @param request
     * @param tripId the trip id
     * @param message an error message if there is one
     * @return display visits page
     */
    public Result displaytrip(Http.Request request, Integer tripId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = Trip.find.byId(tripId);
            List<Visit> visits = trip.getVisits();
            visits.sort(Comparator.comparing(Visit::getVisitOrder));

            if(trip.isUserOwner(user.getUserid())) {
                List<Destination> destinations = user.getDestinations();
                List<Destination> allDestinations = Destination.find.all();
                return ok(AddTripDestinationsTable.render(trip, destinations, allDestinations));
            }
            else{
                return ok(displayTrip.render(trip, visits));
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Handles the create trip request.
     * If the user is logged in, a trip is created for the user based on the form values and stored into the database.
     * The user is then redirected to the create trip page.
     * If the user is not logged in, an error message is displayed.
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
            return redirect(routes.TripController.AddTripDestinations(tripid));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
    }

    /**
     * Renders the page to edit a visit given by the visit id.
     * @param request the HTTP request
     * @param visitid the visit id
     * @return the edit visit page
     */
    public Result editvisit(Http.Request request, Integer visitid){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Visit visit = Visit.find.byId(visitid);
            Form<Visit> visitForm = formFactory.form(Visit.class).fill(visit);
            if(visit.getTrip().getUser().getUserid() == user.getUserid() || user.userIsAdmin()) {
                List<Destination> destinations = user.getDestinations();
                return ok(editVisit.render(visitForm, visit, destinations));
            }
            else{
                return unauthorized("Oops, this is not your trip.");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Handles the update visit request. Updates a visit with the given form details. If the updated visit would cause
     * two of the same destinations to be visited in a row, cancels the update and returns bad request.
     * @param request the HTTP request
     * @param visitid the vistID of the visit
     * @return OK or Bad request
     */
    public Result updateVisit(Http.Request request, Integer visitid){
        DynamicForm visitForm = formFactory.form().bindFromRequest();
        User user = User.getCurrentUser(request);
        if (user != null) {
            String destID = visitForm.get("destination");
            String arrival = visitForm.get("arrival");
            String departure = visitForm.get("departure");
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
//            LocalDate arrivalDate;
//            LocalDate departureDate;
            try {
                Visit visit = Visit.find.byId(visitid);
                //arrivalDate = LocalDate.parse(arrival, formatter);
                //departureDate = LocalDate.parse(departure, formatter);
                Trip trip = visit.getTrip();
                if(trip.isUserOwner(user.getUserid())) {
                    Destination dest = Destination.find.byId(Integer.parseInt(destID));
                    visit.setDestination(dest);
                    //Arrivaldate and departure date TBD
                    visit.setArrival(arrival);
                    visit.setDeparture(departure);
                    if(tripFactory.hasRepeatDest(trip.getVisits(), visit, "SWAP")){
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.update();
                    return redirect(routes.TripController.displaytrip(trip.getTripid()));
                }
                else{
                    return unauthorized("Oops, this is not your trip.");
                }
            } catch (Exception e) {
                Visit visit = Visit.find.byId(visitid);
                Trip trip = visit.getTrip();
                if (trip.isUserOwner(user.getUserid())) {
                    Destination dest = Destination.find.byId(Integer.parseInt(destID));
                    List<Visit> visits = trip.getVisits();
                    visit.setDestination(dest);
                    if (tripFactory.hasRepeatDest(visits, visit, "ADD")) {
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.update();
                    visits = trip.getVisits();
                    visits.sort(Comparator.comparing(Visit::getVisitOrder));
//                    Visit firstVisit = visits.get(0);
//                    visits.remove(0);
                    //return ok(displayTrip.render(trip,visits));
                    return redirect(routes.TripController.displaytrip(trip.getTripid()));
                }
                else{
                    return unauthorized("Oops, this is not your trip.");
                }
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Renders the page to add destinations onto a new trip.
     * This has to be separated with existing trips due to the cancel button which deletes the trip.
     * @param request the HTTP request
     * @param tripid the trip id of the trip
     * @return
     */
    public Result AddTripDestinations(Http.Request request, Integer tripid) {
        Trip trip = Trip.find.byId(tripid);
        User user = User.getCurrentUser(request);
        Date today = new Date();
        today.setTime(today.getTime());
        if (user != null) {
            if (trip != null) {
                if (trip.isUserOwner(user.getUserid())) {
                    Form<VisitFormData> incomingForm = formFactory.form(VisitFormData.class);
                    List<Visit> visits = trip.getVisits();
                    visits.sort(Comparator.comparing(Visit::getVisitOrder));
                    List<Destination> destinations = user.getDestinations();
                    List<Destination> allDestinations = Destination.find.all();
                    //return ok(AddTripDestinations.render(incomingForm, trip, user.getMappedDestinations(), visits, today.toString()));
                    System.out.println(request.flash().getOptional("error").orElse("test"));
                    return ok(AddTripDestinationsTable.render(trip, destinations, allDestinations)).flashing("error", request.flash().getOptional("error").orElse("test"));

                } else {
                    return unauthorized("Not your trip");
                }
            }
            else{
                return unauthorized("Oops, invalid trip ID");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Handles the cancellation of a trip on the page to add destinations to a new trip.
     * All visits are removed from the trip and the trip is removed from the database.
     * @param request the HTTP request
     * @param tripid the trip id of the trip
     * @return
     */
    public Result cancelTrip(Http.Request request, Integer tripid) {
        Trip trip = Trip.find.byId(tripid);
        User user = User.getCurrentUser(request);
        if (user != null) {
                if (trip.hasVisit()) {
                    List<Visit> visits = trip.getVisits();
                    for (Visit visit : visits) {
                        visit.delete();
                    }
                }
                trip.delete();
                return redirect(routes.TripController.createtrip());
        }
        else{
            return unauthorized("Oops, you are not logged in");
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
        if(user != null) {
            Trip trip = Trip.find.byId(tripid);
            if (trip.isUserOwner(user.getUserid())) {
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
                Destination destination = Destination.find.byId(destid);
                if(destination != null) {
                    Visit visit = visitfactory.createVisitTable(trip, destination, visitOrder);
                    if (tripFactory.hasRepeatDest(visits, visit, "ADD")) {
                        //flash("danger", "You cannot have repeat destinations!");
                        return redirect(routes.TripController.AddTripDestinations(tripid)).flashing("error", "You cannot have repeat destinations!");
                    }
                    if(!(destination.getUser().isAdmin()) && destination.getIsPublic() && !(destination.getUser().getUserid() == user.getUserid())){
                        User admin = User.find.byId(1);
                        destination.setUser(admin);
                        destination.update();
                    }
                    visit.save();
                    return redirect(routes.TripController.AddTripDestinations(tripid));
                }
            } else {
                return unauthorized("Oops, this is not your trip.");
            }
        }
        return badRequest();
    }

    /**
     * Handles the request to remove destinations from a trip. Removes the destination (which gets converted into a
     * visit) from the trip that the user is editing, then redirects the user to the edit trip page. Displays an error
     * if the user is not logged in.
     * @param request The HTTP request
     * @param visitid The visit ID that the user is deleting.
     * @return edit trip page or error page
     */
    public Result deletevisit(Http.Request request, Integer visitid){
        //Form<VisitFormData> incomingForm = formFactory.form(VisitFormData.class).bindFromRequest(request);
        //VisitFormData created = incomingForm.get();
        Visit visit = Visit.find.byId(visitid);
        User user = User.getCurrentUser(request);
        //change later
        if (user != null && visit != null) {
            Trip trip = visit.getTrip();
            if(trip.isUserOwner(user.getUserid())) {
                List<Visit> visits = trip.getVisits();
                if(tripFactory.hasRepeatDest(visits, visit, "DELETE")){
                    //flash("danger", "You cannot visit the same destination twice in a row!");
                    return badRequest();
                }
                visit.delete();
                Integer removedVisits = 0;
                if (trip.getRemovedVisits() != null) {
                    removedVisits = trip.getRemovedVisits();
                }
                trip.setRemovedVisits(removedVisits + 1);
                trip.update();
            }
            else{
                //flash("danger", "You are not the owner of this trip.");
                return unauthorized();
            }
        }
        else{
            //flash("danger", "You are not logged in.");
            return unauthorized();
        }
        //flash("success", "Destination deleted.");
        return ok();
    }


    /**
     * Handles the request to swap two destinations from a trip. If the swapped list has repeat destinations or the
     * user is not logged in or they are trying to swap a visit which does not belong to them, sends a bad request.
     * Displays an error if the user is not logged in.
     * @param request The HTTP request
     * @param tripId The trip ID that the user is editing.
     * @return edit trip page or error page
     */
    public Result swapvisits(Http.Request request, Integer tripId){
        //System.out.println(request.body().asJson());
        ArrayList<String> list = new ObjectMapper().convertValue(request.body().asJson(), ArrayList.class);
        User user = User.getCurrentUser(request);
        Trip trip = Trip.find.byId(tripId);
        if (user != null) {
            if(trip.isUserOwner(user.getUserid())) {
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
            return unauthorized();
        }
    }


}

//SWAP VISITS WITHOUT THE TABLE (don't delete might come in handy in the future)
        /*
        Form<VisitFormData> incomingForm = formFactory.form(VisitFormData.class).bindFromRequest(request);
        VisitFormData created = incomingForm.get();
        String visitID1 = created.visitName;
        String visitID2 = created.visitName;
        User user = User.getCurrentUser(request);
        if (user != null) {
            Visit visit1 = Visit.find.byId(Integer.parseInt(visitID1));
            Visit visit2 = Visit.find.byId(Integer.parseInt(visitID2));
            if(visit1.isTripOwner(tripid) && visit2.isTripOwner(tripid)) {
                //check for back to back trips to be added here?
                Trip trip = Trip.find.query().where().eq("tripid", tripid).findOne();
                List<Visit> visits = trip.getVisits();
                if(hasRepeatDestSwap(visits, visit1, visit2)){
                    return badRequest("You cannot visit the same destination twice in a row!");
                }
                Integer visit1OrderNumber = visit1.getVisitOrder();
                Integer visit2OrderNumber = visit2.getVisitOrder();
                visit1.setVisitorder(visit2OrderNumber);
                visit2.setVisitorder(visit1OrderNumber);
                visit1.update();
                visit2.update();
            }
            else{
                return unauthorized("Oops, this is not your trip");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        */