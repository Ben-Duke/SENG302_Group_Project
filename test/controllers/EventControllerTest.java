package controllers;

import accessors.*;
import models.*;
import models.commands.Albums.CreateAlbumCommand;
import models.commands.Events.CreateEventCommand;
import models.commands.General.Command;
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


        Event event = new Event(1000, LocalDateTime.now(), LocalDateTime.MAX, "testEvent", "type","event.com", "imageurl",
                destination.getLatitude(), destination.getLongitude(), "", "test event");
        EventAccessor.insert(event);
        event = EventAccessor.getByInternalId(event.getEventId());
        Command albumCommand = new CreateAlbumCommand(event.getName(), event, null);
        albumCommand.execute();
        EventAccessor.update(event);
        User user = UserAccessor.getById(2);



        Album userDefaultAlbum = user.getAlbums().get(0);
        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        userDefaultAlbum.addMedia(media1);
        AlbumAccessor.update(userDefaultAlbum);
        String url = "/events/linkphoto/" + user.getAlbums().get(0).getMedia().get(0).getMediaId() + "/" + event.getEventId();


        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(PUT)
                .uri(url).session("connected", "2");
        Result result = route(app, request);
        assertEquals(OK, result.status());

        assertEquals(1,event.getPrimaryAlbum().getMedia().size());
    }

}
