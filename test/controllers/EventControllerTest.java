package controllers;

import accessors.*;
import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import models.commands.Albums.CreateAlbumCommand;
import models.commands.General.Command;
import org.junit.Ignore;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static play.test.Helpers.route;

@SuppressWarnings("ALL")
public class EventControllerTest  extends BaseTestWithApplicationAndDatabase {

    private Event populateDatabaseWithEventThenReturnEvent() {
        Destination destination = DestinationAccessor.getDestinationById(3);
        User user = UserAccessor.getById(2);

        Event event = new Event(1000, LocalDateTime.now(), LocalDateTime.MAX, "testEvent", "type","event.com", "imageurl",
                destination.getLatitude(), destination.getLongitude(), "", "test event");
        EventAccessor.insert(event);
        event = EventAccessor.getByInternalId(event.getEventId());
        Command albumCommand = new CreateAlbumCommand(event.getName(), event, null);
        albumCommand.execute();
        EventAccessor.update(event);
        Album userDefaultAlbum = user.getAlbums().get(0);
        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        userDefaultAlbum.addMedia(media1);
        AlbumAccessor.update(userDefaultAlbum);
        return EventAccessor.getByInternalId(event.getEventId());
    }

    private void linkMediaToEvent(User user, Event event, Media media) {
        String url = "/events/linkphoto/" + media.getMediaId() + "/" + event.getEventId();


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri(url).session("connected", "2");
        route(app, request);
    }

    private Event linkPhotoToEventThenReturnEvent(User user, Event event) {
        String url = "/events/linkphoto/" + user.getAlbums().get(0).getMedia().get(0).getMediaId() + "/" + event.getEventId();


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri(url).session("connected", "2");
        route(app, request);

        event = EventAccessor.getByInternalId(event.getEventId());

        return event;
    }

    @Test
    public void linkPhotoToEventSuccessful() {

        Event event = populateDatabaseWithEventThenReturnEvent();

        int eventMediaSizeBefore = event.getPrimaryAlbum().getMedia().size();


        User user = UserAccessor.getById(2);


        String url = "/events/linkphoto/" + user.getAlbums().get(0).getMedia().get(0).getMediaId() + "/" + event.getEventId();


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        event = EventAccessor.getByInternalId(event.getEventId());

        assertEquals(eventMediaSizeBefore + 1,event.getPrimaryAlbum().getMedia().size());
    }


    @Test
    public void unlinkPhotoToEventSuccessful() {

        User user = UserAccessor.getById(2);

        Event event = populateDatabaseWithEventThenReturnEvent();

        int eventMediaSizeBefore = event.getPrimaryAlbum().getMedia().size();

        event = linkPhotoToEventThenReturnEvent(user, event);

        assertEquals(eventMediaSizeBefore + 1,event.getPrimaryAlbum().getMedia().size());

        String url = "/events/unlinkphoto/" + user.getAlbums().get(0).getMedia().get(0).getMediaId() + "/" + event.getEventId();


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        event = EventAccessor.getByInternalId(event.getEventId());

        assertEquals(eventMediaSizeBefore, event.getPrimaryAlbum().getMedia().size());
    }

    @Test
    public void testGetEventPhotosSuccessful() {
        User user = UserAccessor.getById(2);
        Event event = populateDatabaseWithEventThenReturnEvent();
        int eventSize = event.getPrimaryAlbum().getMedia().size();

        Album userDefaultAlbum = user.getAlbums().get(0);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        userDefaultAlbum.addMedia(media2);

        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);
        userDefaultAlbum.addMedia(media3);

        Media media4 = new UserPhoto("/test4", false, false, user);
        MediaAccessor.insert(media4);
        userDefaultAlbum.addMedia(media4);

        AlbumAccessor.update(userDefaultAlbum);


        media2 = MediaAccessor.getMediaById(media2.getMediaId());
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        media4 = MediaAccessor.getMediaById(media4.getMediaId());
        linkMediaToEvent(user, event, media2);
        event = EventAccessor.getByInternalId(event.getEventId());
        linkMediaToEvent(user, event, media3);
        event = EventAccessor.getByInternalId(event.getEventId());
        linkMediaToEvent(user, event, media4);
        event = EventAccessor.getByInternalId(event.getEventId());

        String url = "/events/photos/" + event.getEventId();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri(url).session("connected", "2");
        Result result = route(app, request);

        JsonNode jsonJacksonArray = Json.parse(contentAsString(result));
        JsonNode eventPhotosJSONArray = jsonJacksonArray.get("eventPhotos");
        String firstPhotoUrl = eventPhotosJSONArray.get(0).get("urlWithPath").asText();
        assertEquals(media2.getUrlWithPath(), firstPhotoUrl);
        assertEquals(eventSize + 3, eventPhotosJSONArray.size());
    }
}
