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

    public static List<String> getCountries() { return countries; }


    /**
     * Updates countries if the list of countries does not exist or has not
     * been updated for greater than one day.
     */
    public static void updateCountries() {

        countries = null;

        if (lastUpdated == null || countries == null) {
            reloadCountries();

        } else {
            Date yesterdayDate = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));

            if (lastUpdated.compareTo(yesterdayDate) < 0) {
                reloadCountries();
            }
        }
    }

    /**
     * Make api call. Set last updated date. Revalidate used countries
     */
    private static void reloadCountries() {
        try {
            countries = new ArrayList<>(UtilityFunctions.countriesAsStrings());
            lastUpdated = new Date();

            countries.add("Czechoslovakia");

            validatePassportCountries();
            validateNationalityCountries();
            validateDestinationCountries();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For passports check if the country associated is contained
     * in the list of valid countries.
     */
    private static void validatePassportCountries() {
        List<Passport> passports = UserAccessor.getAllPassports();

        for (Passport passport : passports) {
            if (!countries.contains(passport.getName())) {
                passport.setCountryValid(false);
                passport.update();

            } else {
                if (!passport.getCountryValid()) {
                    passport.setCountryValid(true);
                    passport.update();
                }
            }

        }
    }

    /**
     * For nationalities check if the country associated is contained
     * in the list of valid countries.
     */
    private static void validateNationalityCountries() {
        List<Nationality> nationalities = UserAccessor.getAllNationalities();

        for (Nationality nationality : nationalities) {
            if (!countries.contains(nationality.getNationalityName())) {
                nationality.setCountryValid(false);
                nationality.update();

            } else {
                if (!nationality.getCountryValid()) {
                    nationality.setCountryValid(true);
                    nationality.update();
                }
            }
        }
    }

    /**
     * For destinations check if the country associated is contained
     * in the list of valid countries.
     */
    private static void validateDestinationCountries() {
        List<Destination> destinations = DestinationAccessor.getAllDestinations();

        for (Destination destination : destinations) {
            if (!countries.contains(destination.getCountry())) {
                destination.setCountryValid(false);
                destination.update();

            } else {
                if (!destination.getIsCountryValid()) {
                    destination.setCountryValid(true);
                    destination.update();
                }
            }
        }
    }


}
