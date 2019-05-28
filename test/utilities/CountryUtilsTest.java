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
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.CountryUtils.*;

public class CountryUtilsTest extends WithApplication {

    @Test
    public void validateValidPassportCountry() {
        CountryUtils.updateCountries();

        Passport p1 = new Passport(CountryUtils.getCountries().get(0));
        p1.save();

        CountryUtils.updateCountries();
        validatePassportCountries();

        Passport passport = Passport.find.byId(p1.getPassportId());

        assertTrue(passport.getCountryValid());
    }

    @Test
    public void validateInvalidPassportCountry() {
        Passport p1 = new Passport("invalid");
        p1.save();

        CountryUtils.updateCountries();
        validatePassportCountries();

        Passport passport = Passport.find.byId(p1.getPassportId());

        assertFalse(passport.getCountryValid());
    }

}