package controllers;

import accessors.EventAccessor;
import accessors.UserAccessor;
import models.Event;
import models.EventResponse;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

public class EventResponseControllerTest extends BaseTestWithApplicationAndDatabase {


    private void initialiseEventResponse(LocalDateTime endTime) {
        Event event = new Event(LocalDateTime.MIN, endTime, "test");
        EventAccessor.insert(event);
        event = EventAccessor.getEventById(event.getEventId());
        EventResponse eventResponse = new EventResponse("Going", event, UserAccessor.getById(2));
        eventResponse.insert();
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
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/responses/newsfeed/0/100000/").session("connected", null);
    }
}