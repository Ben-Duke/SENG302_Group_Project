package utilities;

import accessors.DestinationAccessor;
import accessors.UserAccessor;
import models.CountryItem;
import models.Destination;
import models.Nationality;
import models.Passport;
import org.slf4j.Logger;

import java.util.*;


public class CountryUtils {

    private static final Logger logger = UtilityFunctions.getLogger();

    private static Date lastUpdated;
    private static List<String> countries;

    public static List<String> getCountries() {
        return countries;
    }

    public static void updateCountries() {

        try {

            if (lastUpdated == null || countries == null) {

                countries = new ArrayList<>(UtilityFunctions.countriesAsStrings());
                lastUpdated = new Date();

                validateUsedCountries();

            } else {
                Date yesterdayDate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));

                if (lastUpdated.compareTo(yesterdayDate) < 0) {
                    countries = new ArrayList<>(UtilityFunctions.countriesAsStrings());
                    lastUpdated = new Date();
                }
                
                validateUsedCountries();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void validateUsedCountries() {
        List<Passport> passports = UserAccessor.getAllPassports();
        List<Nationality> nationalities = UserAccessor.getAllNationalities();
        List<Destination> destinations = DestinationAccessor.getAllDestinations();

        for (Passport passport : passports) {
            if (!countries.contains(passport.getName())) {
                passport.setCountryValid(false);
                passport.update();
            }
        }

        for (Nationality n : nationalities) {
            if (!countries.contains(n.getNationalityName())) {
                n.setCountryValid(false);
                n.update();
            }
        }

        for (Destination d : destinations) {
            if (!countries.contains(d.getCountry())) {
                d.setCountryValid(false);
                d.update();
            }
        }
    }


}
