package accessors;

import controllers.ApplicationManager;
import models.Destination;
import models.TreasureHunt;
import models.User;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;
import utilities.TestDatabaseManager;

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
    public void getCountOpenTreasureHunts_onlyClosedTreasureHunts_checkReturnValueIsZero() {
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
    public void getCountOpenTreasureHunts_oneOpenTreasureHunt_checkReturnValueIsOne() {
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
}