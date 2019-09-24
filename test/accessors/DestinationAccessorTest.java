package accessors;

import io.ebean.Ebean;
import models.*;
import org.junit.Test;
import play.db.ebean.EBeanComponents;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Class to JUnit test the DestinationAccessor class.
 */
public class DestinationAccessorTest extends BaseTestWithApplicationAndDatabase {

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method returns an empty list
     * when the database has no destinations.
     */
    public void getPaginatedPublicDestinations_noDestinations_checkEmptyList() {

        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(0, 10);
        assertTrue(destinations.isEmpty());
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method returns an empty list
     * when the only destinations are private.
     */
    public void getPaginatedPublicDestinations_onlyPrivateDestinations_checkEmptyList() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);
        int userId = user.getUserid();

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(0, 10);
        assertTrue(destinations.isEmpty());
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method returns only public destinations
     * when there is a mixture of public and private destinations
     */
    public void getPaginatedPublicDestinations_mixturePublicPrivateDestinations_checkListLength() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);
        int userId = user.getUserid();

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        Destination destinationTwo = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destinationTwo);
        destinationTwo.setIsPublic(true);
        DestinationAccessor.update(destinationTwo);

        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(0, 10);
        assertEquals(1, destinations.size());
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a negative offset
     * acts like an offset of 0.
     */
    public void getPaginatedPublicDestinations_negativeOffset_checkActsLikeOffsetIsZero() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);

        for (int i = 0; i < 20; i++) {
            Destination destination = new Destination("test",
                    "test", "test", "New Zealand",
                    32.2, 22.1, user);
            DestinationAccessor.insert(destination);
            destination.setIsPublic(true);
            DestinationAccessor.update(destination);
        }

        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(-1, 10);
        assertEquals(10, destinations.size());
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a negative quantity
     * returns an empty list.
     */
    public void getPaginatedPublicDestinations_negativeQuantity_checkEmptyList() {
        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(0, -1);
        assertEquals(0, destinations.size());
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with 0 quantity
     * returns an empty list.
     */
    public void getPaginatedPublicDestinations_zeroQuantity_checkEmptyList() {
        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(0, 0);
        assertEquals(0, destinations.size());
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a positive quantity
     * and there are some public destinations returns a list of size upto the positive
     * quantity.
     */
    public void getPaginatedPublicDestinations_positiveQuantity_checkList() {
        List<Destination> destinations = DestinationAccessor.getPaginatedPublicDestinations(0, 5);
        assertEquals(5, destinations.size());
    }

    @Test

    /**
     * Checks that getDestinationsWithKeyword method returns an empty list
     * when the database has no destinations.
     */
    public void getPaginatedDestinations_ByKeyword_noDestinations_checkEmptyList() {

        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();
        User user = new User();
        UserAccessor.insert(user);

        Set<Destination> destinations = DestinationAccessor.getDestinationsWithKeyword("G", 10, 0, user);
        assertTrue(destinations.isEmpty());
    }

    @Test

    /**
     * Checks that getDestinationsWithKeyword method returns an empty list
     * when there no matching destination with the keyword.
     */
    public void getPaginatedDestinations_ByKeyword_noDestinationsWithKeyword_checkEmptyList() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);
        int userId = user.getUserid();

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        Set<Destination> destinations = DestinationAccessor.getDestinationsWithKeyword("I", 10, 0, user);
        assertEquals(0, destinations.size());
    }

    @Test

    /**
     * Checks that getDestinationsWithKeyword method returns the destinations
     * that match the keyword
     */
    public void getPaginatedDestinations_ByKeyword_DestinationsWithKeyword_checkNonEmptyList() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);
        int userId = user.getUserid();


        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);
        destination.setIsPublic(true);
        DestinationAccessor.update(destination);

        Set<Destination> destinations = DestinationAccessor.getDestinationsWithKeyword("e", 10, 0, user);
        assertEquals(1, destinations.size());
    }

    @Test
    /**
     * Checks that getAllPrivateDestinations method returns a non-empty list of the destinations
     * that are private to the given user
     */
    public void getPrivateDestinations_checkNonEmptyList() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);


        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);
        destination.setIsPublic(false);
        DestinationAccessor.update(destination);

        List<Destination> destinations = DestinationAccessor.getAllPrivateDestinations(user);
        assertEquals(1, destinations.size());
    }

    @Test
    /**
     * Checks that getAllPrivateDestinations method returns an empty list of the destinations
     * that are private to the given user
     */
    public void getPrivateDestinations_checkEmptyList() {
        TestDatabaseManager testDatabaseManager = new TestDatabaseManager();
        testDatabaseManager.clearAllData();

        User user = new User();
        UserAccessor.insert(user);


        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);
        destination.setIsPublic(true);
        DestinationAccessor.update(destination);

        List<Destination> destinations = DestinationAccessor.getAllPrivateDestinations(user);
        assertEquals(0, destinations.size());
    }


}