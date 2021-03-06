package models.commands.Destinations;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.User;
import models.UserPhoto;

import org.junit.Test;
import play.db.Database;
import testhelpers.BaseTestWithApplicationAndDatabase;
import static org.junit.Assert.assertEquals;


public class UnlinkingPhotosToDestinationsCommandTest extends BaseTestWithApplicationAndDatabase {
    private UnlinkPhotoDestinationCommand unlinkCmd;
    private Database database;
    private User user;
    private UserPhoto photo;
    private Destination destination;

    @Override
    public void populateDatabase() {
        super.populateDatabase();

        user = User.find().byId(1);

        photo =  new UserPhoto("imagetest.png", false, false, user);
        String unusedPhotoUrl = photo.getUnusedUserPhotoFileName();
        photo.setUrl(unusedPhotoUrl);
        UserPhotoAccessor.insert(photo);

        destination = DestinationAccessor.getDestinationById(1);
        destination.getPrimaryAlbum().getMedia().add(photo);
        AlbumAccessor.update(destination.getPrimaryAlbum());
        UserPhotoAccessor.update(photo);

        unlinkCmd = new UnlinkPhotoDestinationCommand(photo, destination);
    }

    @Test
    public void testExecute() {
        int beforeSize = destination.getPrimaryAlbum().getMedia().size();
        user.getCommandManager().executeCommand(unlinkCmd);
        destination = DestinationAccessor.getDestinationById(1);
        int afterSize = destination.getPrimaryAlbum().getMedia().size();

        assertEquals(beforeSize - 1, afterSize);
    }

    @Test
    public void testUndo() {
        int beforeSize = destination.getPrimaryAlbum().getMedia().size();
        user.getCommandManager().executeCommand(unlinkCmd);
        user.getCommandManager().undo();
        destination = DestinationAccessor.getDestinationById(1);
        int afterSize = destination.getPrimaryAlbum().getMedia().size();

        assertEquals(beforeSize, afterSize);
    }

    @Test
    public void testRedo() {
        int beforeSize = destination.getPrimaryAlbum().getMedia().size();
        user.getCommandManager().executeCommand(unlinkCmd);
        user.getCommandManager().undo();
        user.getCommandManager().redo();
        destination = DestinationAccessor.getDestinationById(1);
        int afterSize = destination.getPrimaryAlbum().getMedia().size();

        assertEquals(beforeSize - 1, afterSize);
    }



}
