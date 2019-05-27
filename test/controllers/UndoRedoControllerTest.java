package controllers;

import accessors.DestinationAccessor;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import play.mvc.Http;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.UtilityFunctions;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.GET;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

public class UndoRedoControllerTest extends BaseTestWithApplicationAndDatabase {

    private final Logger logger = UtilityFunctions.getLogger();

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
}
