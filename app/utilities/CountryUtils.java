package utilities;

import accessors.DestinationAccessor;
import accessors.UserAccessor;
import models.Destination;
import models.Nationality;
import models.Passport;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


public class CountryUtils {

    private static final Logger logger = UtilityFunctions.getLogger();

    private static Date lastUpdated;
    private static List<String> countries;


    public static List<String> getCountries() {
        updateCountries();
        return countries;
    }

    public static Map<String, Boolean> getCountriesMap() {
        updateCountries();

        Map<String, Boolean> countryMap = new TreeMap<>();

        for (String country : countries) {
            countryMap.put(country, false);
        }

         return countryMap;
    }


    /**
     * Updates countries if the list of countries does not exist or has not
     * been updated for greater than one day or the backup locales are
     * currently loaded in place.
     */
    public static void updateCountries() {
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
     * Runs the methods for validating passports
     * nationalities and destinations.
     */
    public static void validateUsedCountries() {
        validatePassportCountries();
        validateNationalityCountries();
        validateDestinationCountries();
    }

    /**
     * Make api call. Set last updated date. Revalidate used countries
     */
    private static void reloadCountries() {

        try {
            printLoadingCountriesMessage("IN PROGRESS...");

            countries = new ArrayList<>(UtilityFunctions.countriesAsStrings());

            lastUpdated = new Date();

            validateUsedCountries();

            logger.info("SUCCEEDED");

        } catch (Exception e) {

            logger.error("FAILED");

            if (countries == null) {
                countries = new ArrayList<>();

                Locale[] locales = Locale.getAvailableLocales();
                for (Locale locale : locales) {
                    if (!locale.getDisplayCountry().equals("") &&
                            !countries.contains(locale.getDisplayCountry())) {

                        countries.add(locale.getDisplayCountry());
                    }
                }

                lastUpdated = new Date();
                printLoadingCountriesMessage("Locales loaded in place");

            }

        }
    }

    private static void printLoadingCountriesMessage(String message) {

        LocalDateTime dateNowUTC = LocalDateTime.now(ZoneId.of("UTC"));
        String format = "Reloading countries at (UTC): %s | " +
                "lastUpdated: %s | " +
                "countries loaded: %s | %s";
        String formattedStr = String.format(format,
                dateNowUTC,
                lastUpdated,
                countries != null,
                message);

        logger.info(formattedStr);
    }

    /**
     * For passports check if the country associated is contained
     * in the list of valid countries.
     */
    static void validatePassportCountries() {
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
