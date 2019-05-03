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

/**
 * The Treasure Hunt controller class which handles the viewing, creating, editing and deleting of treasure hunts.
 */
public class TreasureHuntController extends Controller {

    @Inject
    FormFactory formFactory;

    TreasureHuntFactory treasureHuntFactory = new TreasureHuntFactory();

    /**
     * The option map of public destinations for treasure hunts.
     */
    Map<String, Boolean> destinationMap = new TreeMap<>();

    /**
     * Creates the option map of public destinations for treasure hunts.
     */
    public void createPublicDestinationsMap() {
        List<Destination> allDestinations = Destination.find.query().where().eq("is_public", true).findList();
        for (Destination destination: allDestinations) {
            destinationMap.put(destination.getDestName(), false);
        }
    }

    /**
     * Modifies the given destination name's (key) boolean value.
     * @param destName Name of the destination
     * @param isTrue Boolean value to be changed to.
     */
    public void modifyPublicDestinationsMap(String destName, boolean isTrue) {
        destinationMap.replace(destName, isTrue);
    }

    /**
     * If the user is logged in, renders the treasure hunt index page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return index treasure hunt page or error page
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
     * @return list of open treasure hunts
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

    /**
     * If the user is logged in, renders the create treasure hunt page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return create treasure hunt page or error page
     */
    public Result createTreasureHunt(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class);
            createPublicDestinationsMap();
            return ok(createTreasureHunt.render(incomingForm, user, destinationMap));
        }
        else{
            return unauthorized("Oops, you are not logged in");
        }
    }

    /**
     * If the user is logged in, creates and saves the treasure hunt and redirects to the index treasure hunt page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return index treasure hunt page or create treasure hunt page if there are validation errors
     */
    public Result createAndSaveTreasureHunt(Http.Request request){
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class).bindFromRequest(request);
            createPublicDestinationsMap();
            if (incomingForm.hasErrors()) {
                return badRequest(createTreasureHunt.render(incomingForm, user, destinationMap));
            }
            for (TreasureHunt tHunt: user.getTreasureHunts()) {
                if (incomingForm.get().getTitle().equals(tHunt.getTitle())) {
                    return badRequest(createTreasureHunt.render(incomingForm.withError("title", "Cannot have duplicate Treasure Hunt titles"), user, destinationMap));
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

    /**
     * If the user is logged in, renders the edit treasure hunt page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return edit treasure hunt page or error page
     */
    public Result editTreasureHunt(Http.Request request, Integer treasureHuntId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            TreasureHunt treasureHunt = TreasureHunt.find.byId(treasureHuntId);
            if (treasureHunt != null) {
                TreasureHuntFormData treasureHuntFormData = new TreasureHuntFormData(treasureHunt.getTitle(),
                        treasureHunt.getRiddle(), treasureHunt.getDestination().getDestName(),
                        treasureHunt.getStartDate(), treasureHunt.getEndDate());
                Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class).fill(treasureHuntFormData);
                createPublicDestinationsMap();
                modifyPublicDestinationsMap(treasureHunt.getDestination().getDestName(), true);
                return ok(editTreasureHunt.render(incomingForm, treasureHunt, user, destinationMap));
            } else {
                notFound("The given Treasure Hunt doesn't exist.");
            }
        }
        return unauthorized("Oops, you are not logged in");
    }

    /**
     * If the user is logged in, creates and saves the treasure hunt and redirects to the index treasure hunt page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return index treasure hunt page or create treasure hunt page if there are validation errors
     */
    public Result editAndSaveTreasureHunt(Http.Request request, Integer treasureHuntId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            TreasureHunt treasureHunt = TreasureHunt.find.byId(treasureHuntId);
            if (treasureHunt != null) {
                if (treasureHunt.getUser().getUserid() == (user.getUserid())) {
                    Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class).bindFromRequest(request);
                    createPublicDestinationsMap();
                    if (incomingForm.hasErrors()) {
                        return badRequest(editTreasureHunt.render(incomingForm, treasureHunt, user, destinationMap));
                    }
                    for (TreasureHunt userTreasureHunt: user.getTreasureHunts()) {
                        if (incomingForm.get().getTitle().equals(userTreasureHunt.getTitle())
                                && !incomingForm.get().getTitle().equals(treasureHunt.getTitle())) {
                            return ok(editTreasureHunt.render(incomingForm.withError("title", "Cannot have duplicate Treasure Hunt titles"), treasureHunt, user, destinationMap));
                        }
                    }
                    TreasureHuntFormData edited = incomingForm.get();
                    treasureHuntFactory.editTreasureHunt(treasureHuntId, edited);
                    return redirect(routes.TreasureHuntController.indexTreasureHunt());
                } else {
                    unauthorized("This Treasure Hunt does not belong to you.");
                }
            } else {
                notFound("The given Treasure Hunt doesn't exist.");
            }
        }
        return unauthorized("Oops, you are not logged in");
    }

    /**
     * If the user is logged in, deletes the treasure hunt and renders the index treasure hunt page.
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return index treasure hunt page or error page
     */
    public Result deleteTreasureHunt(Http.Request request, Integer treasureHuntId){
        User user = User.getCurrentUser(request);
        if (user != null) {
            TreasureHunt treasureHunt = TreasureHunt.find.byId(treasureHuntId);
            if (treasureHunt != null) {
                if (treasureHunt.getUser().getUserid() == (user.getUserid())) {
                    treasureHuntFactory.deleteTreasureHunt(treasureHunt);
                    return ok(indexTreasureHunt.render(user.getTreasureHunts(), getOpenTreasureHunts(), user));
                } else {
                    return unauthorized("The Treasure Hunt that you are trying to delete does not belong to you.");
                }
            } else {
                return notFound("The Treasure Hunt that you are trying to find does not exist.");
            }
        } else {
            return unauthorized("Oops, you are not logged in");
        }
    }
}
