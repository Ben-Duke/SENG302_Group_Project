package controllers;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import factories.TripFactory;
import formdata.TripFormData;
import formdata.VisitFormData;
import io.ebean.Transaction;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.trip.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static play.mvc.Results.*;

public class TripController extends Controller {


    @Inject
    FormFactory formFactory;

    TripFactory tripFactory = new TripFactory();



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

    public Result displaytrip(Http.Request request, Integer tripid, String message){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = Trip.find.byId(tripid);
            if(trip.isUserOwner(user.getUserid())) {
                List<Visit> visits = trip.getVisits();
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                //return ok(displayTrip.render(trip, visits));
                return ok(displayTripTable.render(trip,message));
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
            for (Trip trip: user.getTrips()) {
                if (incomingForm.get().tripName.equals(trip.getTripName())) {
                    return ok(createTrip.render(incomingForm, user));
                }
            }
            if (incomingForm.hasErrors()) {
                return badRequest(createTrip.render(incomingForm, user));
            }
            TripFormData created = incomingForm.get();
            Trip createdTrip = Trip.makeInstance(created, user);
            createdTrip.save();
            return redirect(routes.TripController.AddTripDestinations(createdTrip.tripid));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
    }

    public Result editvisit(Http.Request request, Integer visitid){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Visit visit = Visit.find.byId(visitid);
            Form<Visit> visitForm = formFactory.form(Visit.class).fill(visit);
            List<Destination> destinations = user.getDestinations();
            return ok(editVisit.render(visitForm,visit,destinations));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

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
                    if(hasRepeatDest(trip.getVisits(), visit, "SWAP")){
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.update();
                    return ok(displayTripTable.render(trip,""));
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
                    if (hasRepeatDest(visits, visit, "ADD")) {
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.update();
                    visits = trip.getVisits();
                    visits.sort(Comparator.comparing(Visit::getVisitorder));
//                    Visit firstVisit = visits.get(0);
//                    visits.remove(0);
                    //return ok(displayTrip.render(trip,visits));
                    return ok(displayTripTable.render(trip,""));
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

    public Result AddTripDestinations(Http.Request request, Integer tripid) {
        Trip trip = Trip.find.byId(tripid);
        User user = User.getCurrentUser(request);
        Date today = new Date();
        today.setTime(today.getTime());
        if (user != null) {
            if (trip != null) {
                Form<VisitFormData> incomingForm = formFactory.form(VisitFormData.class);
                List<Visit> visits = trip.getVisits();
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                return ok(AddTripDestinations.render(incomingForm, trip, user.getMappedDestinations(), visits, today.toString()));
            }
            else{
                return unauthorized("Oops, invalid trip ID");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
//        return ok("edittrip");
    }
    /**
     * Handles the request to add destinations to a trip.
     * Destinations with an arrival and departure timestamp are stored in the form of a Visit.
     * A visit is created based on the destination, arrival and departure forms filled by the user, and stored into
     * the trip they're editing. The user is then redirected to the edit trip page.
     * If the user is not logged in, an error message is displayed.
     * @param request The HTTP request
     * @param tripid The trip id that the user is editing.
     * @return edit trip page or error page
     */
    public Result addvisit(Http.Request request, Integer tripid){
        Form<VisitFormData> incomingForm = formFactory.form(VisitFormData.class).bindFromRequest(request);
        User user = User.getCurrentUser(request);
        if (user != null) {
            if (incomingForm.hasErrors()) {
                Date today = new Date();
                today.setTime(today.getTime());
                Trip trip = Trip.find.byId(tripid);
                List<Visit> visits = trip.getVisits();
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                return badRequest(AddTripDestinations.render(incomingForm, trip, user.getMappedDestinations(), visits, today.toString()));
            }
            VisitFormData created = incomingForm.get();
            Trip trip = Trip.find.byId(tripid);
            if (trip.isUserOwner(user.getUserid())) {
                Integer visitSize = 0;
                if (trip.getVisits() != null) {
                    visitSize = trip.getVisits().size();
                }
                List<Visit> visits = trip.getVisits();
                Visit visit = new Visit();
                for (Destination destination : user.getDestinations()) {
                    if (destination.getDestName().equals(created.destName)) {
                        visit = Visit.makeInstance(created, destination, trip, visitSize);
                    }
                }

                if (hasRepeatDest(visits, visit, "ADD")) {
                    Date today = new Date();
                    today.setTime(today.getTime());
                    return badRequest(AddTripDestinations.render(incomingForm, trip, user.getMappedDestinations(), visits, today.toString()));
                }
                visit.save();
            } else {
                return unauthorized("Oops, this is not your trip.");
            }

        } else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.TripController.AddTripDestinations(tripid));
    }

    /**
     * If the user is logged in, renders the edit trip page. Users can add, swap or remove destinations from their
     * trip on this page.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @param tripid The trip id that the user is editing.
     * @return edit trip page or error page
     */
    public Result edittrip(Http.Request request, Integer tripid){
        Trip trip = Trip.find.byId(tripid);
        User user = User.getCurrentUser(request);
        if (user != null) {
            List<Destination> destinations = user.getDestinations();
            if (trip != null) {
                Form<VisitFormData> incomingForm = formFactory.form(VisitFormData.class).bindFromRequest(request);
                List<Visit> visits = trip.getVisits();
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                return ok(editTrip.render(incomingForm, trip, destinations, visits));
            }
            else{
                return unauthorized("Oops, invalid trip ID");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
//        return ok("edittrip");
    }


    /**
     * Handles the request to remove destinations from a trip. Removes the destination (which gets converted into a
     * visit) from the trip that the user is editing, then redirects the user to the edit trip page. Displays an error
     * if the user is not logged in.
     * @param request The HTTP request
     * @param tripid The trip ID that the user is editing.
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
                if(hasRepeatDest(visits, visit, "DELETE")){
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
     * Handles the request to swap two destinations from a trip. The two destinations are the two forms selected by the
     * user. Swaps two destination (which gets converted into a visit) from the trip that the user is editing by
     * swapping their visit order, then redirects the user to the edit trip page.
     * Displays an error if the user is not logged in.
     * @param request The HTTP request
     * @param tripid The trip ID that the user is editing.
     * @return edit trip page or error page
     */
    public Result swapvisits(Http.Request request, Integer tripid){
        //System.out.println(request.body().asJson());
        ArrayList<String> list = new ObjectMapper().convertValue(request.body().asJson(), ArrayList.class);
        if(tripFactory.swapVisitsList(list)){
            return ok();
        }
        else{
            return badRequest();
        }
    }

    public boolean hasRepeatDest(List<Visit> visits, Visit visit, String operation){
        if(operation.equalsIgnoreCase("DELETE")) {
            if(visits.size() > 2) {
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                Integer index = visits.indexOf(visit);
                if(index != 0 && (index + 1 != visits.size())) {
                    if (visits.get(index - 1).getVisitName().equalsIgnoreCase(visits.get(index + 1).getVisitName())) {
                                return true;
                    }
                }
            }
        }
        if(operation.equalsIgnoreCase("ADD")){
            if(visits.size() > 0) {
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                if (visits.get(visits.size() - 1).visitName.equalsIgnoreCase(visit.getVisitName())) {
                    //probably the wrong status header
                    return true;
                }
            }
        }
        if(operation.equalsIgnoreCase("SWAP")){
            visits.sort(Comparator.comparing(Visit::getVisitorder));
            Integer index = visits.indexOf(visit);
            if(index != 0){
                if(!(visits.get(index - 1).getVisitName().equalsIgnoreCase(visit.getVisitName()))) {
                    if (visits.size() != index + 1) {
                        if(visits.get(index + 1).getVisitName().equalsIgnoreCase(visit.getVisitName())){
                            return true;
                        }
                    }
                }
                else{
                    return true;
                }
            }
            else{
                if(visits.get(index + 1).getVisitName().equalsIgnoreCase(visit.getVisitName())){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasRepeatDestSwap(List<Visit> visits, Visit visit1, Visit visit2){
        visits.sort(Comparator.comparing(Visit::getVisitorder));
        if(visits.size() > 2) {
            Integer index1 = visits.indexOf(visit1);
            Integer index2 = visits.indexOf(visit2);
            Collections.swap(visits, index1, index2);
            Integer temp1 = index1;
            index1 = index2;
            index2 = temp1;
            visit1 = visits.get(index1);
            visit2 = visits.get(index2);
            if(hasRepeatDest(visits, visit1, "SWAP") || hasRepeatDest(visits, visit2, "SWAP")){
                return true;
            }
        }
        return false;
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
                Integer visit1OrderNumber = visit1.getVisitorder();
                Integer visit2OrderNumber = visit2.getVisitorder();
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