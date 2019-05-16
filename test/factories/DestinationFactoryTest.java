package factories;

import models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.db.Database;
import play.db.Databases;
import play.db.evolutions.Evolution;
import play.db.evolutions.Evolutions;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.List;

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
    public void testGetOtherUsersMatchingPrivateDestinationsNoMatches() {
        User user = new User("test@testytest.test", "hunter22");
        user.save();
        Destination testPrivateDestination = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, user, false);
        testPrivateDestination.save();

        List<Destination> matchingDests = destinationFactory
                .getOtherUsersMatchingPrivateDestinations(user.getUserid(), testPrivateDestination);

        assertTrue(matchingDests.isEmpty());
    }

    @Test
    public void testGetOtherUsersMatchingPrivateDestinations1Match() {
        User user1 = new User("test@testytest.test", "hunter22");
        user1.save();
        Destination testPrivateDestination1 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, user1, false);
        testPrivateDestination1.save();

        User user2 = new User("test@testers.org", "hunter27");
        user2.save();
        Destination testPrivateDestination2 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, user2, false);
        testPrivateDestination2.save();

        List<Destination> matchingDests = destinationFactory
                .getOtherUsersMatchingPrivateDestinations(user1.getUserid(), testPrivateDestination1);

        assertEquals(1, matchingDests.size());

    }

    @Test
    public void testGetOtherUsersMatchingPrivateDestinations2Matches() {
        User user1 = new User("test@testytest.test", "hunter22");
        user1.save();
        Destination testPrivateDestination1 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, user1, false);
        testPrivateDestination1.save();

        User user2 = new User("test@testers.org", "hunter27");
        user2.save();
        Destination testPrivateDestination2 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, user2, false);
        testPrivateDestination2.save();

        User user3 = new User("test@testing.net", "hunter360");
        user3.save();
        Destination testPrivateDestination3 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, user3, false);
        testPrivateDestination3.save();

        List<Destination> matchingDests = destinationFactory
                .getOtherUsersMatchingPrivateDestinations(user1.getUserid(), testPrivateDestination1);

        assertEquals(2, matchingDests.size());
    }

    @Test
    public void testGetOtherUsersMatchingPrivateDestinationWithPublicMatch() {
        User privateUser = new User("test@testytest.test", "hunter22");
        privateUser.save();
        Destination testPrivateDestination = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, privateUser, false);
        testPrivateDestination.save();

        User publicUser = new User("test@testers.org", "hunter27");
        publicUser.save();
        Destination testPublicDestination = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, publicUser, true);
        testPublicDestination.save();

        List<Destination> matchingDests = destinationFactory
                .getOtherUsersMatchingPrivateDestinations(privateUser.getUserid(), testPrivateDestination);

        assertTrue(matchingDests.isEmpty());
    }

    @Test
    public void testMergingTwoPrivateDestinations() {
        User adminUser = new User("test@testytest.test", "hunter22");
        adminUser.save();
        Admin admin = new Admin(adminUser.userid, true);
        admin.save();
        Destination testPrivateDestination = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, adminUser, false);
        testPrivateDestination.save();
        List<Visit> visitList = new ArrayList();
        testPrivateDestination.setVisits(visitList);

        User privateUser = new User("test@testers.org", "hunter27");
        privateUser.save();
        Destination testPrivateDestination1 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, privateUser, false);
        testPrivateDestination1.save();
        testPrivateDestination1.setVisits(visitList);


        List<Destination> matchingDests = destinationFactory
                .getOtherUsersMatchingPrivateDestinations(privateUser.getUserid(), testPrivateDestination1);

        destinationFactory.mergeDestinations(matchingDests, testPrivateDestination1);
        List<Destination> destinationsWithSameName = Destination.find.query().where()
                .eq("destName", "Rotherham").findList();
        assertEquals(1, destinationsWithSameName.size());
    }

    @Test
    public void testMergingTwoPrivateDestinations2() {
        User adminUser = new User("test@testytest.test", "hunter22");
        adminUser.save();
        Admin admin = new Admin(adminUser.userid, true);
        admin.save();
        Destination testPrivateDestination = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, adminUser, false);
        testPrivateDestination.save();
        List<Visit> visitList = new ArrayList();
        testPrivateDestination.setVisits(visitList);

        User privateUser2 = new User("test@testers.org", "hunter27");
        privateUser2.save();
        Destination testPrivateDestination2 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, privateUser2, false);
        testPrivateDestination2.save();
        testPrivateDestination2.setVisits(visitList);

        User privateUser3 = new User("test@testerstest.org", "hunter29");
        privateUser3.save();
        Destination testPrivateDestination3 = new Destination("Rotherham",
                "Town", "North Canterbury", "New Zealand",
                -42.699000, 172.943667, privateUser3, false);
        testPrivateDestination3.save();
        testPrivateDestination3.setVisits(visitList);


        List<Destination> matchingDests = destinationFactory
                .getOtherUsersMatchingPrivateDestinations(privateUser2.getUserid(), testPrivateDestination2);
        destinationFactory.mergeDestinations(matchingDests, testPrivateDestination2);
        List<Destination> destinationsWithSameName = Destination.find.query().where()
                .eq("destName", "Rotherham").findList();
        assertEquals(1, destinationsWithSameName.size());
    }


    @Test
    public void removingDestinationPrivatePhotos() {
        ArrayList<UserPhoto> userPhotos = new ArrayList<UserPhoto>();
        Boolean isPublic = true;
        for (int i = 0; i< 10; i ++) {
            UserPhoto userPhoto = new UserPhoto("testurl" + i, isPublic, false, testUser);
            userPhotos.add(userPhoto);
            isPublic = !isPublic ;
        }
        Integer userPhotosSize = userPhotos.size();
        destinationFactory.removePrivatePhotos(userPhotos, testUser.getUserid() + 1);
        assertEquals(userPhotosSize - 5, userPhotos.size());

    }
}