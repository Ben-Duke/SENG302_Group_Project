package controllers;

import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

import static org.junit.Assert.*;

/**
 * JUnit4 test class for the ApplicationManager class.
 */
public class ApplicationManagerTest extends BaseTestWithApplicationAndDatabase {

    /**
     * Tests the getDefaultUserPhotoFullURL method returns the correct
     * placeholder user photo string.
     *
     * Does not test the full string, only the end as the full string contains the
     * absolute path which is different depending on the machine running the test.
     */
    @Test
    public void getDefaultUserPhotoFullURL() {
        assertTrue(ApplicationManager.getDefaultUserPhotoFullURL().endsWith("/public/images/Generic.png"));
    }
}