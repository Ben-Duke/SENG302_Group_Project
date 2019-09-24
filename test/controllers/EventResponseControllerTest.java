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


    private void insertEventResponse(LocalDateTime endTime) {
        Event event = new Event(LocalDateTime.MIN, endTime, "test");
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy%20HH:mm:ss");
        String formattedString = time.format(formatter);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/events/responses/newsfeed/0/100000/"+formattedString)
                .session("connected", Integer.toString(LOGIN_USER_ID));
        Result result = route(app, request);
        JsonNode response = Json.parse(contentAsString(result));
        int beforeSize = response.get("responses").size();

        insertEventResponse(LocalDateTime.now().plusDays(1));
        insertEventResponse(LocalDateTime.now().plusDays(2));
        insertEventResponse(LocalDateTime.now().plusDays(3));

        request = Helpers.fakeRequest()
                .method(GET)
                .uri("/events/responses/newsfeed/0/100000/"+formattedString)
                .session("connected", Integer.toString(LOGIN_USER_ID));
        result = route(app, request);
        response = Json.parse(contentAsString(result));
        int afterSize = response.get("responses").size();

        assertEquals(beforeSize + 3, afterSize);
    }
}