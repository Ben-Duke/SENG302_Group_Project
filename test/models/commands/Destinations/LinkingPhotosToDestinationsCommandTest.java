package models.commands.Destinations;

import accessors.DestinationAccessor;
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
    @Before
    public void setUpDatabase() {
        ApplicationManager.setUserPhotoPath("/test/resources/test_photos/user_");
        ApplicationManager.setIsTest(true);
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.populateDatabase();

        user = User.find.byId(1);

        photo =  new UserPhoto("imagetest.png", false, false, user);
        String unusedPhotoUrl = photo.getUnusedUserPhotoFileName();
        photo.setUrl(unusedPhotoUrl);

        destination = DestinationAccessor.getDestinationById(1);

        linkCmd = new LinkPhotoDestinationCommand(photo, destination);
    }

    @Override
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
    }

    @Test
    public void testExecute() {
        int beforeSize = destination.getUserPhotos().size();

        System.out.println("1.1");

        linkCmd.execute();

//        user.getCommandManager().executeCommand(linkCmd);
//        System.out.println(linkCmd);

        System.out.println("1.2");
//
        destination = DestinationAccessor.getDestinationById(1);
//
        System.out.println("1.3");
//
        int afterSize = destination.getUserPhotos().size();
//
        System.out.println("1.4");
//
        assertEquals(beforeSize, afterSize);
    }



}
