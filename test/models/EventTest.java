package models;

import accessors.EventAccessor;
import accessors.UserAccessor;
import org.junit.Test;
import org.slf4j.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

public class EventTest extends BaseTestWithApplicationAndDatabase {

    private Logger logger = UtilityFunctions.getLogger();

    /* Remove all event responses */
    private void removeAllResponses() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearData(Collections.singletonList(TableName.event_response));
    }

    @Test
    public void getLimitedResponses_userNotResponded_noOtherFollowerResponses() {
        removeAllResponses();

        Event event = EventAccessor.getEventById(1);
        User user = UserAccessor.getById(2);

        List<EventResponse> responses = event.getLimitedResponses(user);

        assertEquals(0, responses.size());
    }

    private Result respondGoingToEvent(User user, Event event) {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/" + event.getExternalId() + "/Going").session("connected",
                        Integer.toString(user.getUserid()));
        return route(app, request);
    }

    @Test
    public void getLimitedResponses_userResponded_noOtherFollowerResponses_checkNoResponsesFound() {
        removeAllResponses();

        Event event = EventAccessor.getEventById(1);
        User user = UserAccessor.getById(2);

        // respond to the event
        Result result = respondGoingToEvent(user, event);
        assertEquals(OK, result.status());

        List<EventResponse> responses = event.getLimitedResponses(user);

        // check the list contains only the user response
        assertEquals(0, responses.size());
    }

    /* Test data contains 8 responses of users user 2 is following */
    @Test
    public void getLimitedResponses_userResponded_LessThan9FollowerResponses() {
        Event event = EventAccessor.getEventById(1);
        User user = UserAccessor.getById(2);

        // respond to the event
        Result result = respondGoingToEvent(user, event);
        assertEquals(OK, result.status());

        List<EventResponse> responses = event.getLimitedResponses(user);

        assertEquals(5, responses.size());

        // check all other responses are from users that the user is following
        for (EventResponse response : responses) {    // skip first user
            assertTrue(user.isFollowing(response.getUser()));
        }
    }
}
