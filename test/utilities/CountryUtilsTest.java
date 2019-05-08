package utilities;

import accessors.UserAccessor;
import models.Passport;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.db.Database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CountryUtilsTest {
    private Database database;

    @Before
    public void setupDatabase() {
        database = TestDatabaseManager.getTestDatabase();
    }

    @After
    public void shutdownDatabase() {
        TestDatabaseManager.shutdownTestDatabase(database);
    }

    @Ignore // Database broken in some way - Noel
    @Test
    public void fetchCountriesFromApi_passportWithInvalidCountry_checkInvalidated() {
        Passport p1 = new Passport("invalid");
        //p1.save();

        CountryUtils.fetchCountriesFromApi();

        p1 = UserAccessor.getPassport(p1.getPassportId());

        assertFalse(p1.getCountryValid());
    }

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