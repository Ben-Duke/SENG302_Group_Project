package controllers;

import accessors.EventAccessor;
import accessors.EventResponseAccessor;
import accessors.UserAccessor;
import models.Event;
import models.EventResponse;
import org.junit.Ignore;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;
import com.fasterxml.jackson.databind.JsonNode;



public class EventResponseControllerTest extends BaseTestWithApplicationAndDatabase {

    private Integer LOGIN_USER_ID = 2;


    /**
     * Inserts an event response. The event response's responseDateTime is set to the current date
     * @param eventEndTime
     */
    private void insertEventResponse(LocalDateTime eventEndTime) {
        Event event = new Event(LocalDateTime.MIN, eventEndTime, "test");
        EventAccessor.insert(event);
        event = EventAccessor.getEventById(event.getEventId());
        EventResponse eventResponse = new EventResponse();
        EventResponseAccessor.save(eventResponse);
        EventResponse savedEventResponse = EventResponseAccessor.getById(eventResponse.getEventResponseId());
        savedEventResponse.setUser(UserAccessor.getById(LOGIN_USER_ID));
        savedEventResponse.setEvent(event);
        savedEventResponse.setResponseType("Going");
        EventResponseAccessor.update(savedEventResponse);
    }

    /**
     * Overload method where the event response's response date time can be set
     * @param eventEndTime
     * @param responseDateTime
     */
    private void insertEventResponse(LocalDateTime eventEndTime, LocalDateTime responseDateTime) {
        Event event = new Event(LocalDateTime.MIN, eventEndTime, "test");
        EventAccessor.insert(event);
        event = EventAccessor.getEventById(event.getEventId());
        EventResponse eventResponse = new EventResponse();
        EventResponseAccessor.save(eventResponse);
        EventResponse savedEventResponse = EventResponseAccessor.getById(eventResponse.getEventResponseId());
        savedEventResponse.setUser(UserAccessor.getById(LOGIN_USER_ID));
        savedEventResponse.setEvent(event);
        savedEventResponse.setResponseType("Going");
        EventResponseAccessor.update(savedEventResponse);
        savedEventResponse = EventResponseAccessor.getById(savedEventResponse.getEventResponseId());
        savedEventResponse.setResponseDateTime(responseDateTime);
        savedEventResponse.update();
    }

    private Result getEventResponses(int offset, int limit, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy%20HH:mm:ss");
        String formattedString = time.format(formatter);
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/events/responses/newsfeed/" + offset + "/" + limit + "/" +formattedString)
                .session("connected", Integer.toString(LOGIN_USER_ID));
        return route(app, request);
    }


    @Ignore
    @Test
    public void respondToEventGoing() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/594788/Going").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Ignore
    @Test
    public void respondToEventInvalidEvent() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/1/Going").session("connected", "1");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Ignore
    @Test
    public void respondToEventInterested() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/594788/Interested").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Ignore
    @Test
    public void respondToEventNotInterested() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/594788/NotInterested").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Ignore
    @Test
    public void respondToEventWent() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/594788/Went").session("connected", "1");
        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
    @Ignore
    @Test
    public void respondToEventLoggedOut() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/respond/594788/Going").session("connected", null);
        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

    @Ignore
    @Test
    public void getAllResponses() {
    }

    @Test
    public void testGetEventResponsesWithThreeValidEvents() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        int offset = 0;
        int limit = 100000;

        Result result = getEventResponses(offset, limit, time);
        JsonNode response = Json.parse(contentAsString(result));
        int beforeSize = response.get("responses").size();

        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(1));

        result = getEventResponses(offset, limit, time);
        response = Json.parse(contentAsString(result));
        int afterSize = response.get("responses").size();

        assertEquals(beforeSize + 3, afterSize);
    }

    @Test
    public void testGetEventResponsesWithEventsTestingLimit() {
        LocalDateTime time = LocalDateTime.now().plusDays(1);
        int limit = 2;
        int offset = 0;

        //This assumes that there are initially no event responses. This might have to be
        //changed if event responses are added to the database.
        Result result = getEventResponses(offset, limit, time);

        JsonNode response = Json.parse(contentAsString(result));
        int beforeSize = response.get("responses").size();

        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(1));

        result = getEventResponses(offset, limit, time);

        response = Json.parse(contentAsString(result));
        int afterSize = response.get("responses").size();

        assertEquals(beforeSize + 2, afterSize);
    }

    @Test
    public void testGetEventResponsesWithEventsTestingOffset() {
        int offset = 0;
        int limit = 2;
        LocalDateTime time = LocalDateTime.now().plusDays(1);

        //This assumes that there are initially no event responses. This might have to be
        //changed if event responses are added to the database.

        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(1));

        Result result = getEventResponses(offset, limit, time);

        JsonNode response = Json.parse(contentAsString(result));

        int beforeSize = response.get("responses").size();
        assertEquals(2, beforeSize);

        offset = 2;

        result = getEventResponses(offset, limit, time);

        response = Json.parse(contentAsString(result));
        int afterSize = response.get("responses").size();
        System.out.println(response.get("responses"));
        assertEquals(1, afterSize);

        offset = 3;

        result = getEventResponses(offset, limit, time);

        response = Json.parse(contentAsString(result));
        afterSize = response.get("responses").size();
        System.out.println(response.get("responses"));
        assertEquals(0, afterSize);
    }

    @Test
    public void testGetEventResponsesWithEventsTestingEventRespondTime() {
        LocalDateTime time = LocalDateTime.now();
        int limit = 100000;
        int offset = 0;
        LocalDateTime eventResponseTime = LocalDateTime.now().minusDays(1);
        insertEventResponse(LocalDateTime.now().plusDays(1), eventResponseTime);
        insertEventResponse(LocalDateTime.now().plusDays(1), eventResponseTime);
        insertEventResponse(LocalDateTime.now().plusDays(1), eventResponseTime);

        Result result = getEventResponses(offset, limit, time);

        JsonNode response = Json.parse(contentAsString(result));
        int beforeSize = response.get("responses").size();

        time = LocalDateTime.now().minusDays(2);

        result = getEventResponses(offset, limit, time);

        response = Json.parse(contentAsString(result));
        int afterSize = response.get("responses").size();

        assertEquals(beforeSize - 3, afterSize);
    }
}