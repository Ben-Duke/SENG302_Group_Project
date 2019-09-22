package models;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * JUnit tests for TreasureHunt model class.
 */
public class TreasureHuntTest {

    /**
     * Helper method to build a treasure hunt using only a start and end date.
     *
     * @param startDate Date to start
     * @param endDate Date to end
     * @return A TreasureHunt
     */
    private TreasureHunt getTreasureHunt(LocalDate startDate, LocalDate endDate) {
        User user = new User("test@test.com", "sasdsad");

        Destination destination = new Destination("test",
                "test", "test", "New Zealand",
                32.2, 22.1, user);

        TreasureHunt tHunt = new TreasureHunt("test", "test",
                destination, startDate, endDate, user);
        return tHunt;
    }

    @Test
    /**
     * Checks isOpen returns false for a treasure hunt that starts and ends before
     * the present day.
     */
    public void isOpen_startsEndsBeforeToday_checkFalse() {
        TreasureHunt tHunt = this.getTreasureHunt(LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(5));
        assertFalse(tHunt.isOpen());
    }

    @Test
    /**
     * Checks isOpen returns false for a treasure hunt that starts and ends after
     * the present day.
     */
    public void isOpen_startsEndsAfterToday_checkFalse() {
        TreasureHunt tHunt = this.getTreasureHunt(LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10));
        assertFalse(tHunt.isOpen());
    }

    @Test
    /**
     * Checks isOpen returns true for a treasure hunt that starts before the present
     * day and ends after the present day.
     */
    public void isOpen_startsBeforeTodayEndsAfterToday_checkTrue() {
        TreasureHunt tHunt = this.getTreasureHunt(LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(10));
        assertTrue(tHunt.isOpen());
    }

    @Test
    /**
     * Checks isOpen returns false for a treasure hunt that starts before the present
     * day and ends on the present day.
     */
    public void isOpen_endsToday_checkFalse() {
        TreasureHunt tHunt = this.getTreasureHunt(LocalDate.now().minusDays(5),
                LocalDate.now());
        assertFalse(tHunt.isOpen());
    }

    @Test
    /**
     * Checks isOpen returns true for a treasure hunt that starts on the present
     * day.
     */
    public void isOpen_startsToday_checkTrue() {
        TreasureHunt tHunt = this.getTreasureHunt(LocalDate.now(),
                LocalDate.now().plusDays(100));
        assertTrue(tHunt.isOpen());
    }

    @Test
    /**
     * Checks isOpen returns false for a treasure hunt that starts and ends on the present
     * day.
     */
    public void isOpen_startsEndsToday_checkFalse() {
        TreasureHunt tHunt = this.getTreasureHunt(LocalDate.now(), LocalDate.now());
        assertFalse(tHunt.isOpen());
    }
}