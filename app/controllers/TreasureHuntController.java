package controllers;

import accessors.TreasureHuntAccessor;
import factories.TreasureHuntFactory;
import formdata.TreasureHuntFormData;
import models.Destination;
import models.TreasureHunt;
import models.User;
import models.commands.General.UndoableCommand;
import models.commands.Treasurehunts.DeleteTreasureHuntCommand;
import models.commands.Treasurehunts.TreasureHuntPageCommand;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.users.treasurehunt.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * The Treasure Hunt controller class which handles the viewing, creating, editing and deleting of treasure hunts.
 */
public class TreasureHuntController extends Controller {

    @Inject
    FormFactory formFactory;

    private TreasureHuntFactory treasureHuntFactory = new TreasureHuntFactory();

    /**
     * The option map of public destinations for treasure hunts.
     */
    Map<String, Boolean> destinationMap = new TreeMap<>();

    /**
     * Creates the option map of public destinations for treasure hunts.
     */
    void createPublicDestinationsMap() {
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
    void modifyPublicDestinationsMap(String destName, boolean isTrue) {
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
            user.getCommandManager().setAllowedType(TreasureHuntPageCommand.class); // clear stack
            return ok(indexTreasureHunt.render(user.getTreasureHunts(), getOpenTreasureHunts(), user));
        }
        else{
            return redirect(routes.UserController.userindex());
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
            LocalDate currentDate = LocalDate.now(ZoneId.of("Pacific/Auckland"));
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
            return redirect(routes.UserController.userindex());
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
                // Change the destination map to keep track of the current destination selected in the select
                String destName = incomingForm.rawData().get("destination");
                if (destName != null && !destName.isEmpty()) {
                    destinationMap.put(destName, true);
                }
                return badRequest(createTreasureHunt.render(incomingForm, user, destinationMap));
            }
            for (TreasureHunt tHunt: TreasureHunt.find.all()) {
                if (incomingForm.get().getTitle().equals(tHunt.getTitle())) {
                    return badRequest(createTreasureHunt.render(incomingForm.withError("title", "Another Treasure Hunt with the same title exists in the system."), user, destinationMap));
                }
            }
            TreasureHuntFormData created = incomingForm.get();
            treasureHuntFactory.createTreasureHunt(created, user);
            return redirect(routes.TreasureHuntController.indexTreasureHunt());
        }
        else{
            return redirect(routes.UserController.userindex());
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
                if (treasureHunt.getUser().getUserid() == (user.getUserid())) {
                    TreasureHuntFormData treasureHuntFormData = new TreasureHuntFormData(treasureHunt.getTitle(),
                            treasureHunt.getRiddle(), treasureHunt.getDestination().getDestName(),
                            treasureHunt.getStartDate(), treasureHunt.getEndDate());
                    Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class).fill(treasureHuntFormData);
                    createPublicDestinationsMap();
                    modifyPublicDestinationsMap(treasureHunt.getDestination().getDestName(), true);
                    return ok(editTreasureHunt.render(incomingForm, treasureHunt, user, destinationMap));
                } else {
                    return unauthorized("This Treasure Hunt does not belong to you.");
                }
            } else {
                return notFound("The given Treasure Hunt doesn't exist.");
            }
        }
        return redirect(routes.UserController.userindex());
    }

    /**
     * If the user is logged in, creates and saves the treasure hunt and redirects to the index treasure hunt page
     * If the user is not logged in, returns an error.
     * @param request The HTTP request
     * @return index treasure hunt page or create treasure hunt page if there are validation errors
     */
    public Result editAndSaveTreasureHunt(Http.Request request, Integer treasureHuntId){
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        TreasureHunt treasureHunt = TreasureHunt.find.byId(treasureHuntId);
        if (treasureHunt == null) {
            return notFound("The given Treasure Hunt doesn't exist.");
        }

        if (treasureHunt.getUser().getUserid() != (user.getUserid())) {
            return unauthorized("This Treasure Hunt does not belong to you.");
        }
        Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class).bindFromRequest(request);
        createPublicDestinationsMap();
        if (incomingForm.hasErrors()) {
            // Change the destination map to keep track of the current destination selected in the select
            String destName = incomingForm.rawData().get("destination");
            if (destName !=null && !destName.isEmpty()) {
                destinationMap.put(destName, true);
            }
            return badRequest(editTreasureHunt.render(incomingForm, treasureHunt, user, destinationMap));
        }
        for (TreasureHunt userTreasureHunt: TreasureHunt.find.all()) {
            if (incomingForm.get().getTitle().equals(userTreasureHunt.getTitle())
                    && !incomingForm.get().getTitle().equals(treasureHunt.getTitle())) {
                return badRequest(editTreasureHunt.render(incomingForm.withError("title", "Another Treasure Hunt with the same title exists in the system."), treasureHunt, user, destinationMap));
            }
        }
        TreasureHuntFormData edited = incomingForm.get();
        treasureHuntFactory.editTreasureHunt(user, treasureHuntId, edited);
        return redirect(routes.TreasureHuntController.indexTreasureHunt());
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
            TreasureHunt treasureHunt = TreasureHuntAccessor.getById(treasureHuntId);
            if (treasureHunt != null) {
                if (treasureHunt.getUser().getUserid() == (user.getUserid())) {
                    UndoableCommand cmd = new DeleteTreasureHuntCommand(treasureHunt);
                    user.getCommandManager().executeCommand(cmd);
                    return redirect(routes.TreasureHuntController.indexTreasureHunt());
                } else {
                    return unauthorized("The Treasure Hunt that you are trying to delete does not belong to you.");
                }
            } else {
                return notFound("The Treasure Hunt that you are trying to find does not exist.");
            }
        } else {
            return redirect(routes.UserController.userindex());
        }
    }
}
