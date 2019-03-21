package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.trip.createTrip;
import views.html.users.trip.displayTrip;
import views.html.users.trip.editTrip;
import views.html.users.trip.editVisit;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static play.mvc.Results.*;

public class TripController {

    @Inject
    FormFactory formFactory;

    /**
     * If the user is logged in, renders the create trip page.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result createtrip(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<Trip> tripForm = formFactory.form(Trip.class);
            return ok(createTrip.render(tripForm, user));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    public Result displaytrip(Http.Request request, Integer tripid){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = Trip.find.byId(tripid);
            if(trip.isUserOwner(user.getUserid())) {
                List<Visit> visits = trip.getVisits();
                visits.sort(Comparator.comparing(Visit::getVisitorder));
//                Visit firstVisit = visits.get(0);
//                visits.remove(0);
                return ok(displayTrip.render(trip, visits));
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
        Form<Trip> tripForm = formFactory.form(Trip.class).bindFromRequest();
        Trip trip = tripForm.get();
        User user = User.getCurrentUser(request);
        if (user != null) {
            trip.setUser(user);
            trip.save();
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.TripController.createtrip());
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
            LocalDate arrivalDate;
            LocalDate departureDate;
            try {
                Visit visit = Visit.find.byId(visitid);
                arrivalDate = LocalDate.parse(arrival, formatter);
                departureDate = LocalDate.parse(departure, formatter);
                Trip trip = visit.getTrip();
                if(trip.isUserOwner(user.getUserid())) {
                    Destination dest = Destination.find.byId(Integer.parseInt(destID));
                    visit.setDestination(dest);
                    visit.setArrival(arrivalDate);
                    visit.setDeparture(departureDate);
                    if(hasRepeatDest(trip.getVisits(), visit, "ADD")){
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.update();
                    List<Visit> visits = trip.getVisits();
                    visits.sort(Comparator.comparing(Visit::getVisitorder));
//                    Visit firstVisit = visits.get(0);
//                    visits.remove(0);
                    return ok(displayTrip.render(trip,visits));
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
                    return ok(displayTrip.render(trip,visits));
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
        DynamicForm visitForm = formFactory.form().bindFromRequest();
        User user = User.getCurrentUser(request);
        if (user != null) {
            String destID = visitForm.get("destination");
            String arrival = visitForm.get("arrival");
            String departure = visitForm.get("departure");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
            LocalDate arrivalDate;
            LocalDate departureDate;
            try {
                arrivalDate = LocalDate.parse(arrival, formatter);
                departureDate = LocalDate.parse(departure, formatter);
                Trip trip = Trip.find.byId(tripid);
                if(trip.isUserOwner(user.getUserid())) {
                    Integer visitSize = 0;
                    if (trip.getVisits() != null) {
                        visitSize = trip.getVisits().size();
                    }
                    Integer removedVisits = 0;
                    if (trip.getRemovedVisits() != null) {
                        removedVisits = trip.getRemovedVisits();
                    }
                    Integer visitOrder = visitSize + 1 + removedVisits;
                    Destination dest = Destination.find.byId(Integer.parseInt(destID));
                    List<Visit> visits = trip.getVisits();
                    Visit visit = new Visit(dest, trip, visitOrder, arrivalDate, departureDate);
                    if(hasRepeatDest(visits, visit, "ADD")){
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.save();
                }
                else{
                    return unauthorized("Oops, this is not your trip.");
                }
            } catch (Exception e) {
                Trip trip = Trip.find.byId(tripid);
                if(trip.isUserOwner(user.getUserid())) {
                    Integer visitSize = 0;
                    if (trip.getVisits() != null) {
                        visitSize = trip.getVisits().size();
                    }
                    Integer removedVisits = 0;
                    if (trip.getRemovedVisits() != null) {
                        removedVisits = trip.getRemovedVisits();
                    }
                    Integer visitOrder = visitSize + 1 + removedVisits;
                    Destination dest = Destination.find.byId(Integer.parseInt(destID));
                    List<Visit> visits = trip.getVisits();
                    Visit visit = new Visit(dest, trip, visitOrder);
                    if(hasRepeatDest(visits, visit, "ADD")){
                        return badRequest("You cannot visit the same destination twice in a row!");
                    }
                    visit.save();
                }
                else{
                    return unauthorized("Oops, this is not your trip.");
                }
                //return badRequest("ERROR: Please enter the date correctly.");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.TripController.edittrip(tripid));
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
                Form<Visit> visitForm = formFactory.form(Visit.class);
                List<Visit> visits = trip.getVisits();
                visits.sort(Comparator.comparing(Visit::getVisitorder));
                return ok(editTrip.render(visitForm, trip, destinations, visits));
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
    public Result deletevisit(Http.Request request, Integer tripid){
        DynamicForm visitForm = formFactory.form().bindFromRequest();
        String visitID = visitForm.get("visitid");
        User user = User.getCurrentUser(request);
        if (user != null) {
            Trip trip = Trip.find.query().where().eq("tripid", tripid).findOne();
            if(trip.isUserOwner(user.getUserid())) {
                Visit visit = Visit.find.query().where().eq("visitid", Integer.parseInt(visitID)).findOne();
                List<Visit> visits = trip.getVisits();
                if(hasRepeatDest(visits, visit, "DELETE")){
                    return badRequest("You cannot visit the same destination twice in a row!");
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
                return unauthorized("Oops, this is not your trip. ya-yeet.");
            }
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
        //return redirect(routes.UserController.userindex());
        return redirect(routes.TripController.edittrip(tripid));
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
        DynamicForm visitForm = formFactory.form().bindFromRequest();
        String visitID1 = visitForm.get("visitid1");
        String visitID2 = visitForm.get("visitid2");
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
        return redirect(routes.TripController.edittrip(tripid));
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

/*
                <script>
                    var text = "";
                    var i = 1;
                    while (i <= @visits.size){
                        text += "<li data-target=\"#myslider\" data-slide-to=\"" + i + "\"></li>";
                        console.log(text);
                        i++;
                    }
                    document.getElementById("myslider").innerHTML = text;
                </script>
 */
