package factories;

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
import play.test.WithApplication;

import static org.junit.Assert.*;

/**
 * JUnit 4 tests for DestinationFactory
 */
public class DestinationFactoryTest extends WithApplication {
    private Database database;
    private int testUserId = -1;
    private DestinationFactory destinationFactory;
    private User testUser;
    Destination testPublicDestination;

    @Before
    public void setUpDatabase() {
        destinationFactory = new DestinationFactory();
        database = Databases.inMemory();
        Evolutions.applyEvolutions(database, Evolutions.forDefault(new Evolution(
                1,
                "create table test (id bigint not null, name varchar(255));",
                "drop table test;"
        )));
        //Initialises a test user with name "testUser" and saves it to the database.
        User user = new User("gon12@uclive.ac.nz", "hunter22");
        testUser = user;
        user.save();
        testUserId = user.getUserid();
        testPublicDestination = new Destination("destName",
                "destType", "district", "country",
                45.0, 45.0, testUser, true);
    }

    /**
     * Clears the fake database after each test
     */
    @After
    public void shutdownDatabase() {
        Evolutions.cleanupEvolutions(database);
        database.shutdown();
        database = null;
        testUserId = -1;
        destinationFactory = null;
        testUser = null;
        testPublicDestination = null;
    }


    @Test
    public void userHasPrivateDestinationInvalidUserId() {
        boolean hasDestination = destinationFactory.userHasPrivateDestination(
                                                        -50, testPublicDestination);

        assertFalse(hasDestination);
    }

    @Test
    public void userHasPrivateDestinationUserIdForNoUser() {
        boolean hasDestination = destinationFactory.userHasPrivateDestination(
                9999, testPublicDestination);

        assertFalse(hasDestination);
    }

    @Test
    public void userHasPrivateDestinationWithNoOwnedDestinations() {
        boolean hasDestination = destinationFactory.userHasPrivateDestination(
                testUserId, testPublicDestination);

        assertFalse(hasDestination);
    }

    @Test
    /**
     * Even though the user owns a public destination that matches, the tested
     * method should return false as it only checks private destinations.
     */
    public void userHasPrivateDestinationWithMatchingOwnedPublicDestination() {
        Destination testPublicDestination = new Destination("destName",
                "destType", "district", "country",
                45.0, 45.0, testUser, true);
        testPublicDestination.save();


        boolean hasDestination = destinationFactory.userHasPrivateDestination(
                testUserId, testPublicDestination);

        assertFalse(hasDestination);
    }

    @Test
    public void userHasPrivateDestinationWithMatchingOwnedPrivateDestination() {
        Destination testPrivateDestination = new Destination("destName",
                "destType", "district", "country",
                45.0, 45.0, testUser, false);
        testPrivateDestination.save();


        boolean hasDestination = destinationFactory.userHasPrivateDestination(
                testUserId, testPrivateDestination);

        assertTrue(hasDestination);
    }

    @Test
    public void doesPublicDestinationExistNoPublicDestinations() {
        boolean hasDestination = destinationFactory.doesPublicDestinationExist(
                testPublicDestination);

        assertFalse(hasDestination);
    }

    @Test
    public void doesPublicDestinationExistMatchingPrivateDestinationExists() {
        Destination testPrivateDestination = new Destination("destName",
                "destType", "district", "country",
                45.0, 45.0, testUser, false);
        testPrivateDestination.save();


        boolean hasDestination = destinationFactory.doesPublicDestinationExist(
                testPublicDestination);

        assertFalse(hasDestination);
    }

    @Test
    public void doesPublicDestinationExistMatchingPublicDestinationExistsOwnedByUser() {
        testPublicDestination.save();


        boolean hasDestination = destinationFactory.doesPublicDestinationExist(
                testPublicDestination);

        assertTrue(hasDestination);
    }

    @Test
    public void doesPublicDestinationExistMatchingPublicDestinationExistsOwnedByOtherUser() {
        User user = new User("test@testytest.test", "hunter22");
        user.save();
        Destination testPublicDestination = new Destination("destName",
                "destType", "district", "country",
                45.0, 45.0, user, true);
        testPublicDestination.save();


        boolean hasDestination = destinationFactory.doesPublicDestinationExist(
                testPublicDestination);

        assertTrue(hasDestination);
    }

    @Test
    public void removingDestinationPrivateInformation() {
        UserPhoto userPhoto = new UserPhoto("testurl", false, false, testUser);
        userPhoto.save();
        Destination testPrivateDestination = new Destination("destName",
                "destType", "district", "country",
                45.0, 45.0, testUser, false);
        testPrivateDestination.save();
        userPhoto.addDestination(testPrivateDestination);
        userPhoto.update();
        testPrivateDestination.save();

        testPrivateDestination = Destination.find.byId(testPrivateDestination.getDestId());

        Integer photoCount = testPrivateDestination.getUserPhotos().size();
        destinationFactory.removePrivateInformation(testPrivateDestination);
        testPrivateDestination.setIsPublic(true);
        testPrivateDestination.update();
        assertEquals(photoCount - 1, testPrivateDestination.getUserPhotos().size());
        assertEquals(0, UserPhoto.find.byId(userPhoto.getPhotoId()).destinations.size());
    }
}