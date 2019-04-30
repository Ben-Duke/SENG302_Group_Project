package controllers;

import factories.TreasureHuntFactory;
import formdata.TreasureHuntFormData;
import models.Destination;
import models.TreasureHunt;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.treasurehunt.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class TreasureHuntController extends Controller {

    @Inject
    FormFactory formFactory;

    TreasureHuntFactory treasureHuntFactory = new TreasureHuntFactory();

    /**
     * If the user is logged in, renders the treasure hunt index page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return create profile page or error page
     */
    public Result indexTreasureHunt(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            return ok(indexTreasureHunt.render(user.getTreasureHunts(), getOpenTreasureHunts(), user));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * Gets a list of open treasure hunts. A treasure hunt is open if its start date is before the current date
     * and the end date is after the current date.
     * @return
     */
    public List<TreasureHunt> getOpenTreasureHunts(){
        List<TreasureHunt> treasureHunts = TreasureHunt.find.all();
        List<TreasureHunt> openTreasureHunts = new ArrayList<>();
        for(TreasureHunt treasureHunt : treasureHunts){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(treasureHunt.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(treasureHunt.getEndDate(), formatter);
            LocalDate currentDate = LocalDate.now();
            if(startDate.isBefore(currentDate) && endDate.isAfter(currentDate)){
                openTreasureHunts.add(treasureHunt);
            }
        }
        return openTreasureHunts;
    }


    public Result createTreasureHunt(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class);
            Map<String, Boolean> destinationMap = new TreeMap<>();
            List<Destination> allDestinations= Destination.find.all();
            for (Destination destination: allDestinations) {
                if(destination.getIsPublic()) {
                    destinationMap.put(destination.getDestName(), false);
                }
            }
            return ok(createTreasureHunt.render(incomingForm, user, destinationMap));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }


    public Result saveTreasureHunt(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class).bindFromRequest(request);
            Map<String, Boolean> destinationMap = new TreeMap<>();
            List<Destination> allDestinations= Destination.find.all();
            for (Destination destination: allDestinations) {
                if(destination.getIsPublic()) {
                    destinationMap.put(destination.getDestName(), false);
                }
            }
            if (incomingForm.hasErrors()) {
                return badRequest(createTreasureHunt.render(incomingForm, user, destinationMap));
            }
            for (TreasureHunt tHunt: user.getTreasureHunts()) {
                if (incomingForm.get().getTitle().equals(tHunt.getTitle())) {
                    return ok(createTreasureHunt.render(incomingForm.withError("title", "Cannot have duplicate Treasure Hunt titles"), user, destinationMap));
                }
            }
            TreasureHuntFormData created = incomingForm.get();
            treasureHuntFactory.createTreasureHunt(created, user);
            return redirect(routes.TreasureHuntController.indexTreasureHunt());
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    public Result editTreasureHunt(Http.Request request, Integer treasureHuntId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class);
            Map<String, Boolean> destinationMap = new TreeMap<>();
            List<Destination> allDestinations= Destination.find.all();
            for (Destination destination: allDestinations) {
                if(destination.getIsPublic()) {
                    destinationMap.put(destination.getDestName(), false);
                }
            }
            return ok(createTreasureHunt.render(incomingForm, user, destinationMap));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    public Result deleteTreasureHunt(Http.Request request, Integer treasureHuntId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class);
            Map<String, Boolean> destinationMap = new TreeMap<>();
            List<Destination> allDestinations= Destination.find.all();
            for (Destination destination: allDestinations) {
                if(destination.getIsPublic()) {
                    destinationMap.put(destination.getDestName(), false);
                }
            }
            return ok(createTreasureHunt.render(incomingForm, user, destinationMap));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }
}
