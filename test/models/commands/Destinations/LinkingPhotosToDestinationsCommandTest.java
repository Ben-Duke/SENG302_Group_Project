package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import controllers.ApplicationManager;
import models.Destination;
import models.User;
import models.UserPhoto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import static org.junit.Assert.assertEquals;


public class LinkingPhotosToDestinationsCommandTest extends BaseTestWithApplicationAndDatabase {
    private LinkPhotoDestinationCommand linkCmd;
    private Database database;
    private User user;
    private UserPhoto photo;
    private Destination destination;

    @Override
    public void populateDatabase() {
        TestDatabaseManager.populateDatabase();

        user = User.find().byId(1);

        photo =  new UserPhoto("imagetest.png", false, false, user);
        String unusedPhotoUrl = photo.getUnusedUserPhotoFileName();
        photo.setUrl(unusedPhotoUrl);
        UserPhotoAccessor.insert(photo);


        destination = DestinationAccessor.getDestinationById(1);

        linkCmd = new LinkPhotoDestinationCommand(photo, destination);
    }

    @Test
    public void testExecute() {
        int beforeSize = destination.getUserPhotos().size();
        user.getCommandManager().executeCommand(linkCmd);
        destination = DestinationAccessor.getDestinationById(1);
        int afterSize = destination.getUserPhotos().size();

        assertEquals(beforeSize + 1, afterSize);
    }

    @Test
    public void testUndo() {
        int beforeSize = destination.getUserPhotos().size();
        user.getCommandManager().executeCommand(linkCmd);
        user.getCommandManager().undo();
        destination = DestinationAccessor.getDestinationById(1);
        int afterSize = destination.getUserPhotos().size();

        assertEquals(beforeSize, afterSize);
    }

    @Test
    public void testRedo() {
        int beforeSize = destination.getUserPhotos().size();
        user.getCommandManager().executeCommand(linkCmd);
        user.getCommandManager().undo();
        user.getCommandManager().redo();
        destination = DestinationAccessor.getDestinationById(1);
        int afterSize = destination.getUserPhotos().size();

        assertEquals(beforeSize + 1, afterSize);
    }



}
