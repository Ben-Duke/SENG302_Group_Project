package accessors;

import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

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
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method returns an empty list
     * when the only destinations are private.
     */
    public void getPaginatedPublicDestinations_onlyPrivateDestinations_checkEmptyList() {
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method returns only public destinations
     * when there is a mixture of public and private destinations
     */
    public void getPaginatedPublicDestinations_mixturePublicPrivateDestinations_checkListLength() {
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a negative offset
     * acts like an offset of 0.
     */
    public void getPaginatedPublicDestinations_negativeOffset_checkActsLikeOffsetIsZero() {
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a negative quantity
     * returns an empty list.
     */
    public void getPaginatedPublicDestinations_negativeQuantity_checkEmptyList() {
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with 0 quantity
     * returns an empty list.
     */
    public void getPaginatedPublicDestinations_zeroQuantity_checkEmptyList() {
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a positive quantity
     * and there are some public destinations returns a list of size upto the positive
     * quantity.
     */
    public void getPaginatedPublicDestinations_positiveQuantity_checkList() {
        fail();
    }

    @Test
    /**
     * Checks that getPaginatedPublicDestinations method with a positive quantity
     * and a positive offset correctly skips and limits results.
     */
    public void getPaginatedPublicDestinations_offsetAndLimit_checkList() {
        fail();
    }
}