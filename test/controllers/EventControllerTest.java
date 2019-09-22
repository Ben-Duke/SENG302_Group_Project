package controllers;

import accessors.DestinationAccessor;
import accessors.EventAccessor;
import accessors.UserAccessor;
import models.Destination;
import models.Event;
import models.User;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.PUT;
import static play.test.Helpers.route;

public class EventControllerTest  extends BaseTestWithApplicationAndDatabase {

    @Ignore
    @Test
    public void linkPhotoToDestinationSuccessful() {
        Destination destination = DestinationAccessor.getDestinationById(3);
        Event event = new Event(1000, LocalDateTime.now(), LocalDateTime.MAX, "testEvent", "event.com",
                destination.getLatitude(), destination.getLongitude(), "test event", destination);
        EventAccessor.insert(event);

        User user = UserAccessor.getById(2);

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri("/events/linkphoto/" + event.getEventId() + "/" + user.getAlbums().get(0).getMedia().get(0).getMediaId()).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        assertEquals(1,event.getPrimaryAlbum().getMedia().size());
    }

}
