package controllers;

import accessors.DestinationAccessor;
import accessors.UserAccessor;
import models.Destination;
import models.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import play.mvc.Http;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

public class UndoRedoControllerTest extends BaseTestWithApplicationAndDatabase {

    private final Logger logger = UtilityFunctions.getLogger();

    private int concurrencyDestId = 1;
    private String concurrencyUserId = "2";

    private String adminId = "1";

    private void undo(String userId) {
        Http.RequestBuilder undoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/undo").session("connected", userId);
        route(app, undoRequest);
    }

    private void redo(String userId) {
        // redo the deletion
        Http.RequestBuilder redoRequest = Helpers.fakeRequest()
                .method(PUT)
                .uri("/redo").session("connected", userId);
        route(app, redoRequest);
    }

    private void deleteDestination(int destId, String userId) {
        // delete the destination
        Http.RequestBuilder deleteRequest = Helpers.fakeRequest()
                .method(GET)
                .uri("/users/destinations/delete/" + destId).session("connected", userId);
        route(app, deleteRequest);
    }

    private void editDestination(int destId, String userId) {
        // Have to include all fields due to using a post request
        Map<String, String> formData = new HashMap<>();
        formData.put("destName", "TestCity");
        formData.put("destType", "Yes");
        formData.put("district", "Demacia");
        formData.put("country", "Angola");
        formData.put("latitude", "50.0");
        formData.put("longitude", "-50.0");
        Http.RequestBuilder editRequest = Helpers.fakeRequest()
                .bodyForm(formData)
                .method(POST)
                .uri("/users/destinations/update/" + destId).session("connected", userId);
        route(app, editRequest);
    }


    /** Check that multiple commands can be undone */
    @Test
    public void undo_undoTwoCommands_checkBothUndone() {
        int destinationSize = DestinationAccessor.getAllDestinations().size();
        String adminId = "1";   // admin@admin.com

        deleteDestination(1, adminId);
        deleteDestination(2, adminId);

        undo(adminId);
        undo(adminId);

        // check that no destinations have been deleted
        assertEquals(destinationSize, DestinationAccessor.getAllDestinations().size());
    }

    /** Check that multiple commands can be redone */
    @Test
    public void redo_redoTwoCommands_checkBothRedone() {
        int destinationSize = DestinationAccessor.getAllDestinations().size();
        String adminId = "1";   // admin@admin.com

        deleteDestination(1, adminId);
        deleteDestination(2, adminId);

        undo(adminId);
        undo(adminId);

        redo(adminId);
        redo(adminId);

        // check that the destinations have been deleted
        assertEquals(destinationSize - 2, DestinationAccessor.getAllDestinations().size());
    }

    /** Edit a public destination as a user then delete it as an admin */
    private void editThenDeleteDestinationConcurrently(String userId) {
        String adminId = "1";
        int destId = 1;

        editDestination(destId, userId);
        deleteDestination(destId, adminId);
    }

    /** Edit a dest as a user then delete it as an admin
     *  Undo as user then check error flag was set
     * */
    @Test
    public void undo_concurrency_checkErrorFlagSet() {
        editThenDeleteDestinationConcurrently(concurrencyUserId);
        undo(concurrencyUserId);
        User user = UserAccessor.getUserById(Integer.parseInt(concurrencyUserId));
        assertTrue(user.isUndoRedoError());
    }

    @Test
    public void undo_concurrency_checkRemovedFromStack() {
        editThenDeleteDestinationConcurrently(concurrencyUserId);
        undo(concurrencyUserId);
        User user = UserAccessor.getUserById(Integer.parseInt(concurrencyUserId));

        assertTrue(user.getCommandManager().isUndoStackEmpty());
    }

    private void editUndoDeleteRedo() {
        // edit and undo as user
        editDestination(concurrencyDestId, concurrencyUserId);
        undo(concurrencyUserId);

        //delete as admin
        deleteDestination(concurrencyDestId, adminId);

        // redo as user - should fail as dest no longer exists
        redo(concurrencyUserId);
    }

    @Test
    public void redo_concurrency_checkErrorFlagSet() {
        editUndoDeleteRedo();

        User user = UserAccessor.getUserById(Integer.parseInt(concurrencyUserId));

        assertTrue(user.isUndoRedoError());
    }

    @Test
    public void redo_concurrency_checkRemovedFromStack() {
        editUndoDeleteRedo();

        User user = UserAccessor.getUserById(Integer.parseInt(concurrencyUserId));

        assertTrue(user.getCommandManager().isRedoStackEmpty());
    }
}
