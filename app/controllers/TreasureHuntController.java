package controllers;

import accessors.TreasureHuntAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import factories.TreasureHuntFactory;
import formdata.TreasureHuntFormData;
import models.Destination;
import models.TreasureHunt;
import models.User;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;
import models.commands.Treasurehunts.DeleteTreasureHuntCommand;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.UtilityFunctions;
import utilities.exceptions.EbeanDateParseException;
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
        List<Destination> allDestinations = Destination.find().query().where().eq("dest_is_public", true).findList();
        for (Destination destination : allDestinations) {
            destinationMap.put(destination.getDestName(), false);
        }
    }

    /**
     * Modifies the given destination name's (key) boolean value.
     *
     * @param destName Name of the destination
     * @param isTrue   Boolean value to be changed to.
     */
    void modifyPublicDestinationsMap(String destName, boolean isTrue) {
        destinationMap.replace(destName, isTrue);
    }

    /**
     * If the user is logged in, renders the treasure hunt index page
     * If the user is not logged in, returns an error.
     *
     * @param request The HTTP request
     * @return index treasure hunt page or error page
     */
    public Result indexTreasureHunt(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            user.getCommandManager().setAllowedPage(CommandPage.TREASURE_HUNT); // clear stack
            return ok(indexTreasureHunt.render(user.getTreasureHunts(), getOpenTreasureHunts(), user)); //TODO remove lists
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * Gets a paginated jsonArray of public treasure hunts based on an offset and quantity
     *
     * @param request  the HTTP request
     * @param offset   an integer representing the number of TreasureHunts to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return a Result object containing the TreasureHunt lis JSON in it's body
     */
    public Result getPaginatedOpenTreasureHunts(Http.Request request, int offset, int quantity) {
        final int MAX_QUANTITY = 1000;
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        if (quantity > MAX_QUANTITY) {
            return badRequest(Json.toJson(UtilityFunctions.quantityError(MAX_QUANTITY)));
        }

        List<TreasureHunt> treasureHunts = TreasureHuntAccessor
                .getPaginatedOpenTreasureHunts(offset, quantity);
        long countTotalOpenHunts = TreasureHuntAccessor.getCountOpenTreasureHunts();

        ObjectNode result = (new ObjectMapper()).createObjectNode();
        result.set("openTreasureHunts", getJsonForOpenTreasureHunts(treasureHunts, user));
        result.put("totalCountOpenTreasureHunts", countTotalOpenHunts);

        return ok(Json.toJson(result));
    }

    /**
     * Get's JSON (ArrayNode) containing a list of treasure hunts, edited with
     * an extra isHidden attribute and if isHidden is true then destName is always
     * "Hidden".
     *
     * @param treasureHunts List of treasure hunts to convert to json.
     * @param user User to check owns the hunts or is an admin.
     * @return An ArrayNode representing the json to send to client.
     */
    private ArrayNode getJsonForOpenTreasureHunts(List<TreasureHunt> treasureHunts, User user) {
        ArrayNode treasureHuntsJson = (new ObjectMapper()).createArrayNode();

        for (TreasureHunt treasureHunt: treasureHunts) {
            ObjectNode treasureHuntJson = (new ObjectMapper()).createObjectNode();
            treasureHuntJson.put("title", treasureHunt.getTitle());
            treasureHuntJson.put("id", treasureHunt.getThuntid());
            treasureHuntJson.put("endDate", UtilityFunctions.getStringFromLocalDate(treasureHunt.getEndDate()));
            treasureHuntJson.put("riddle", treasureHunt.getRiddle());

            if (user.userIsAdmin() || treasureHunt.getUser().getUserid() == user.getUserid()) {
                treasureHuntJson.put("destName", treasureHunt.getDestination().getDestName());
                treasureHuntJson.put("isHidden", false);
            } else {
                treasureHuntJson.put("destName", "Hidden");
                treasureHuntJson.put("isHidden", true);
            }

            treasureHuntsJson.add(treasureHuntJson);
        }

        return treasureHuntsJson;
    }

    /**
     * Gets a paginated jsonArray of a user's treasure hunts based on an offset and quantity
     *
     * @param request  the HTTP request, with the user logged in
     * @param offset   an integer representing the number of TreasureHunts to skip before sending
     * @param quantity an integer representing the maximum length of the jsonArray
     * @return a Result object containing the TreasureHunt list JSON in it's body
     */
    public Result getPaginatedUserTreasureHunts(Http.Request request, int offset, int quantity) {
        final int MAX_QUANTITY = 1000;
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        if (quantity > MAX_QUANTITY) {
            return badRequest(Json.toJson(UtilityFunctions.quantityError(MAX_QUANTITY)));
        }

        List<TreasureHunt> treasureHunts = TreasureHuntAccessor
                .getPaginatedUsersTreasurehunts(user, offset, quantity);
        int totalCountOwn = TreasureHuntAccessor.getCountUsersownTreasureHunts(user);

        ObjectNode result = (new ObjectMapper()).createObjectNode();
        result.set("ownTreasureHunts", this.getJsonForTreasureHunts(treasureHunts));
        result.put("totalCountOpenTreasureHunts", totalCountOwn);
        return ok(Json.toJson(result));
    }

    /**
     * Get's JSON (ArrayNode) containing a paginated list of treasuer hunts.
     * No isAdmin or isOwner checking is done.
     *
     * @param treasureHunts List of treasure hunts to convert to json.
     * @return An ArrayNode representing the json to send to client.
     */
    private ArrayNode getJsonForTreasureHunts(List<TreasureHunt> treasureHunts) {
        ArrayNode treasureHuntsJson = (new ObjectMapper()).createArrayNode();

        for (TreasureHunt treasureHunt: treasureHunts) {
            ObjectNode treasureHuntJson = (new ObjectMapper()).createObjectNode();
            treasureHuntJson.put("id", treasureHunt.getThuntid());
            treasureHuntJson.put("title", treasureHunt.getTitle());
            treasureHuntJson.put("isOpen", treasureHunt.isOpen());
            treasureHuntJson.put("startDate", UtilityFunctions.getStringFromLocalDate(treasureHunt.getStartDate()));
            treasureHuntJson.put("endDate", UtilityFunctions.getStringFromLocalDate(treasureHunt.getEndDate()));
            treasureHuntJson.put("riddle", treasureHunt.getRiddle());
            treasureHuntJson.put("destName", treasureHunt.getDestination().getDestName());


            treasureHuntsJson.add(treasureHuntJson);
        }

        return treasureHuntsJson;
    }

    /**
     * Gets a list of open treasure hunts. A treasure hunt is open if its start date is before the current date
     * and the end date is after the current date.
     *
     * @return list of open treasure hunts
     */
    public List<TreasureHunt> getOpenTreasureHunts() {
        List<TreasureHunt> treasureHunts = TreasureHunt.find().all();
        List<TreasureHunt> openTreasureHunts = new ArrayList<>();
        for (TreasureHunt treasureHunt : treasureHunts) {
            if (treasureHunt.isOpen()) {
                openTreasureHunts.add(treasureHunt);
            }
        }
        return openTreasureHunts;
    }

    /**
     * If the user is logged in, renders the create treasure hunt page
     * If the user is not logged in, returns an error.
     *
     * @param request The HTTP request
     * @return create treasure hunt page or error page
     */
    public Result createTreasureHunt(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            Form<TreasureHuntFormData> incomingForm = formFactory.form(TreasureHuntFormData.class);
            createPublicDestinationsMap();
            return ok(createTreasureHunt.render(incomingForm, user, destinationMap));
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * If the user is logged in, creates and saves the treasure hunt and redirects to the index treasure hunt page
     * If the user is not logged in, returns an error.
     *
     * @param request The HTTP request
     * @return index treasure hunt page or create treasure hunt page if there are validation errors
     */
    public Result createAndSaveTreasureHunt(Http.Request request) {
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
            for (TreasureHunt tHunt : TreasureHunt.find().all()) {
                if (incomingForm.get().getTitle().equals(tHunt.getTitle())) {
                    return badRequest(createTreasureHunt.render(incomingForm.withError("title", "Another Treasure Hunt with the same title exists in the system."), user, destinationMap));
                }
            }
            TreasureHuntFormData created = incomingForm.get();
            try {
                treasureHuntFactory.createTreasureHunt(created, user);
            } catch (EbeanDateParseException e) {
                UtilityFunctions.getLogger().error(e.getMessage());
                return badRequest("Date formatted incorrectly");
            }
            return redirect(routes.TreasureHuntController.indexTreasureHunt());
        } else {
            return redirect(routes.UserController.userindex());
        }
    }

    /**
     * If the user is logged in, renders the edit treasure hunt page
     * If the user is not logged in, returns an error.
     *
     * @param request        The HTTP request
     * @param treasureHuntId The Id of the treasure hunt being edited
     * @return edit treasure hunt page or error page
     */
    public Result editTreasureHunt(Http.Request request, Integer treasureHuntId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            TreasureHunt treasureHunt = TreasureHunt.find().byId(treasureHuntId);
            if (treasureHunt != null) {
                if (treasureHunt.getUser().getUserid() == (user.getUserid())) {
                    TreasureHuntFormData treasureHuntFormData = new TreasureHuntFormData(treasureHunt.getTitle(),
                            treasureHunt.getRiddle(), treasureHunt.getDestination().getDestName(),
                            UtilityFunctions.getStringFromLocalDate(treasureHunt.getStartDate()),
                            UtilityFunctions.getStringFromLocalDate(treasureHunt.getEndDate()));
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
     *
     * @param request        The HTTP request
     * @param treasureHuntId The Id of the treasure hunt being updated
     * @return index treasure hunt page or create treasure hunt page if there are validation errors
     */
    public Result editAndSaveTreasureHunt(Http.Request request, Integer treasureHuntId) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }
        TreasureHunt treasureHunt = TreasureHunt.find().byId(treasureHuntId);
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
            if (destName != null && !destName.isEmpty()) {
                destinationMap.replace(destName, true);
            }
            return badRequest(editTreasureHunt.render(incomingForm, treasureHunt, user, destinationMap));
        }
        for (TreasureHunt userTreasureHunt : TreasureHunt.find().all()) {
            if (incomingForm.get().getTitle().equals(userTreasureHunt.getTitle())
                    && !incomingForm.get().getTitle().equals(treasureHunt.getTitle())) {
                return badRequest(editTreasureHunt.render(incomingForm.withError("title", "Another Treasure Hunt with the same title exists in the system."), treasureHunt, user, destinationMap));
            }
        }
        TreasureHuntFormData edited = incomingForm.get();
        try {
            treasureHuntFactory.editTreasureHunt(user, treasureHuntId, edited);
        } catch (EbeanDateParseException e) {
            UtilityFunctions.getLogger().error(e.getMessage());
            return badRequest("Time parse error");
        }
        return redirect(routes.TreasureHuntController.indexTreasureHunt());
    }

    /**
     * If the user is logged in, deletes the treasure hunt and renders the index treasure hunt page.
     * If the user is not logged in, returns an error.
     *
     * @param request        The HTTP request
     * @param treasureHuntId The Id of the treasure hunt being deleted
     * @return index treasure hunt page or error page
     */
    public Result deleteTreasureHunt(Http.Request request, Integer treasureHuntId) {
        User user = User.getCurrentUser(request);
        if (user != null) {
            TreasureHunt treasureHunt = TreasureHuntAccessor.getById(treasureHuntId);
            if (treasureHunt != null) {
                if ((treasureHunt.getUser().getUserid() == (user.getUserid())) ||
                        (user.userIsAdmin())) {
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
