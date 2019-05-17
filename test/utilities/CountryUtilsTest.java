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
import play.test.WithApplication;

import java.util.List;

import static org.junit.Assert.assertFalse;

public class CountryUtilsTest extends WithApplication {
    private Database database;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    @Before
    public void setupDatabase() {
        database = TestDatabaseManager.getTestDatabase();
    }

    @After
    public void shutdownDatabase() {
        TestDatabaseManager.shutdownTestDatabase(database);
    }

    @Test
    public void validateInvalidPassportCountry() {
        Passport p1 = new Passport("invalid");
        p1.save();

        CountryUtils.updateCountries();

        Passport passport = Passport.find.byId(p1.getPassportId());

        assertFalse(passport.getCountryValid());
    }

    @Test
    public void validateValidPassportCountry() {
        CountryUtils.updateCountries();

        Passport p1 = new Passport(CountryUtils.getCountries().get(0));
        p1.save();

        CountryUtils.updateCountries();

        Passport passport = Passport.find.byId(p1.getPassportId());

        assertTrue(passport.getCountryValid());
    }

}