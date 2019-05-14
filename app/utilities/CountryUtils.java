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

    private static List<String> countries;

    public static List<String> getCountries() {
        return countries;
    }

    public static void updateCountries() {
        try {
            countries = new ArrayList<>(UtilityFunctions.countriesAsStrings());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void validateDestinationCountries() {
        List<Destination> destinations = DestinationAccessor.getAllDestinations();

        for (Destination d : destinations) {
            if (!countries.contains(d.getCountry())) {
                d.setCountryValid(false);
                d.update();
            }
        }
    }

    public static void validateNatPassCountries() {
        List<Passport> passports = UserAccessor.getAllPassports();
        List<Nationality> nationalities = UserAccessor.getAllNationalities();

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
    }



//    public static List<String> fetchCountriesFromApi() {
//        // Fetch the countries from the api
////        List<String> countries = CountryUtils.getCountries();
//
//        // Update nationalities and passports validity
//        List<Passport> passports = UserAccessor.getAllPassports();
//        List<Nationality> nationalities = UserAccessor.getAllNationalities();
//        List<Destination> destinations = DestinationAccessor.getAllDestinations();
//
//        for (Passport passport : passports) {
//            if (!countries.contains(passport.passportName)) {
//                passport.setCountryValid(false);
//                passport.update();
//            }
//        }
//
//        for (Nationality n : nationalities) {
//            if (!countries.contains(n.nationalityName)) {
//                n.setCountryValid(false);
//                n.update();
//            }
//        }
//
//        for (Destination d : destinations) {
////            logger.debug(d.getCountry());
////            logger.debug(countries.get(0));
////            logger.debug(Boolean.toString(countries.get(0).equals(d.getCountry())));
//
//
//            if (!countries.contains(d.getCountry())) {
//                d.setCountryValid(false);
//                d.update();
//            }
//        }
//
//        return countries;
//    }



    /** Return a list of valid countries */
//    private static List<String> getCountries() {
//        try {
//            Set<String> countries = UtilityFunctions.countriesAsStrings();
//            return new ArrayList<>(countries);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /** Return true if the country is valid, false otherwise */
//    public static Boolean isValidCountry(String country) {
//        return CountryUtils.getCountries().contains(country);
//    }
}
