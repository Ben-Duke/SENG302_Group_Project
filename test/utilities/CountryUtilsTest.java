package utilities;

import accessors.UserAccessor;
import cucumber.api.java.bs.I;
import models.Passport;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import play.Application;
import play.db.Database;
import play.inject.guice.GuiceApplicationBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CountryUtilsTest {
    private Database database;

//    @Override
//    protected Application provideApplication() {
//        return new GuiceApplicationBuilder().build();
//    }

    @Before
    public void setupDatabase() {
        database = TestDatabaseManager.getTestDatabase();
    }

    @After
    public void shutdownDatabase() {
        TestDatabaseManager.shutdownTestDatabase(database);
    }

    @Ignore
    @Test
    public void validateInvalidPassportCountry() {
        Passport p1 = new Passport("invalid");
        p1.save();

        CountryUtils.updateCountries();

        assertFalse(p1.getCountryValid());
    }

    @Ignore
    @Test
    public void validateValidPassportCountry() {
        CountryUtils.updateCountries();

        Passport p1 = new Passport(CountryUtils.getCountries().get(0));
        p1.save();

        CountryUtils.updateCountries();

        assertTrue(p1.getCountryValid());
    }

}