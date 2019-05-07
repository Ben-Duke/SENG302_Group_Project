package utilities;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CountryUtilsTest {

    @Test
    public void isValidCountry_validCountry() {
        Boolean result = CountryUtils.isValidCountry("New Zealand");
        assertTrue(result);
    }

    @Test
    public void isValidCountry_invalidCountry() {
        Boolean result = CountryUtils.isValidCountry("xxx");
        assertFalse(result);
    }
}