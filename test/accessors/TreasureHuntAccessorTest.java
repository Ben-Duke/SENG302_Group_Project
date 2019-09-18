package accessors;

import controllers.ApplicationManager;
import models.Destination;
import models.TreasureHunt;
import models.User;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;
import utilities.exceptions.EbeanDateParseException;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Class to JUnit test the TreasureHuntAccessor
 */
public class TreasureHuntAccessorTest extends BaseTestWithApplicationAndDatabase {

    @Test
    /**
     * Checks the getCountOpenTreasureHunts method returns zero when the database
     * has zero treasure hunts.
     */
    public void getCountOpenTreasureHunts_zeroTreasureHunts_checkReturnValueIsZero() {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();
        long count = TreasureHuntAccessor.getCountOpenTreasureHunts();
        assertEquals(0, count);
    }

    @Test
    /**
     * Checks the getCountOpenTreasureHunts method returns zero when the database
     * contains only closed treasure hunts.
     */
    public void getCountOpenTreasureHunts_onlyClosedTreasureHunts_checkReturnValueIsZero() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test8192398719283791@gmail.com", "test",
                "test", "test", LocalDate.now(), "male");
        UserAccessor.insert(user);

        Destination destination = Destination.find().query()
                .where()
                .eq("destIsPublic", true)
                .findOne();

        TreasureHunt treasureHunt = new TreasureHunt("test hunt",
                "test hunt 222",
                destination,
                "2015-04-17", "2018-12-25",
                user);
        TreasureHuntAccessor.insert(treasureHunt);



        long count = TreasureHuntAccessor.getCountOpenTreasureHunts();
        assertEquals(0, count);
    }

    @Test
    /**
     * Checks the getCountOpenTreasureHunts method returns 1 when the database
     * contains 1 open treasure hunt.
     */
    public void getCountOpenTreasureHunts_oneOpenTreasureHunt_checkReturnValueIsOne() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test8192398719283791@gmail.com", "test",
                "test", "test", LocalDate.now(), "male");
        UserAccessor.insert(user);

        Destination destination = Destination.find().query()
                .where()
                .eq("destIsPublic", true)
                .findOne();

        TreasureHunt treasureHunt = new TreasureHunt("test hunt",
                "test hunt 222",
                destination,
                "2018-04-17", "2180-12-25",
                user);
        TreasureHuntAccessor.insert(treasureHunt);



        long count = TreasureHuntAccessor.getCountOpenTreasureHunts();
        assertEquals(1, count);
    }

    @Test
    /**
     * Checks getCountUsersownTreasureHunts() returns 0 when there are no treasure hunts
     * in the application.
     */
    public void getCountUsersownTreasureHunts_noHunts_checkReturnZero() {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        assertEquals(0, TreasureHuntAccessor.getCountUsersownTreasureHunts(user));
    }

    @Test
    /**
     * Checks getCountUsersownTreasureHunts() returns 0 when there are treasure hunts
     * in the application, but not owned by the user.
     */
    public void getCountUsersownTreasureHunts_noOwnedHunts_checkReturnZero()
                                                throws EbeanDateParseException {

        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        User userOther = new User("other@test.com", "sasdsad");
        UserAccessor.insert(userOther);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", userOther);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(0, TreasureHuntAccessor.getCountUsersownTreasureHunts(user));
    }

    @Test
    /**
     * Checks getCountUsersownTreasureHunts() returns 1 when the users owns 1 treasure hunt.
     */
    public void getCountUsersownTreasureHunts_ownsOne_checkReturnOne()
            throws EbeanDateParseException {

        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        User userOther = new User("other@test.com", "sasdsad");
        UserAccessor.insert(userOther);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", userOther);
        TreasureHuntAccessor.insert(tHunt);

        TreasureHunt ownTHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-12-12", user);
        TreasureHuntAccessor.insert(ownTHunt);

        assertEquals(1, TreasureHuntAccessor.getCountUsersownTreasureHunts(user));
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns zero when there are no treasure
     * hunts in the  application.
     */
    public void getCountOpenTreasureHunts_zeroHunts_checkReturnZero() {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        assertEquals(0, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns zero when the only treasure hunt
     * closed before the present day.
     */
    public void getCountOpenTreasureHunts_onlyWellBeforeTodayHunts_checkReturnZero() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, "2019-01-01", "2019-02-02", user);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(0, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns zero when the only hunt starts
     * and ends in the future.
     */
    public void getCountOpenTreasureHunts_onlyWellAfterTodayHunts_checkReturnZero() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, LocalDate.now().plusYears(1),
                LocalDate.now().plusYears(2), user);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(0, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns zero when the only treasure hunt
     * closed on the present day.
     */
    public void getCountOpenTreasureHunts_huntClosedToday_checkReturnZero() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, LocalDate.now().minusDays(5), LocalDate.now(), user);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(0, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns zero when the only treasure hunt
     * starts after the present day.
     */
    public void getCountOpenTreasureHunts_huntstartsAfterToday_checkReturnZero() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10), user);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(0, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns one when the only treasure hunt
     * starts on the present day.
     */
    public void getCountOpenTreasureHunts_huntStartsToday_checkReturnOne() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, LocalDate.now(),
                LocalDate.now().plusDays(10), user);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(1, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }

    @Test
    /**
     * Checks getCountOpenTreasureHunts returns one when the only treasure hunt
     * starts on the present day.
     */
    public void getCountOpenTreasureHunts_huntStartsBeforeTodayFinishesAfterToday_checkReturnOne() throws EbeanDateParseException {
        TestDatabaseManager dbManager = new TestDatabaseManager();
        dbManager.clearAllData();

        User user = new User("test@test.com", "sasdsad");
        UserAccessor.insert(user);

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);
        DestinationAccessor.insert(destination);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(10), user);
        TreasureHuntAccessor.insert(tHunt);

        assertEquals(1, TreasureHuntAccessor.getCountOpenTreasureHunts());
    }
}