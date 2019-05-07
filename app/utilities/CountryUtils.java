package utilities;

import models.Destination;

import java.util.*;

public class CountryUtils {

    /** Return a list of valid countries */
    public static List<String> getCountries() {
        Set<String> countries = Destination.getIsoCountries().keySet();
        return new ArrayList<>(countries);
    }

    /** Return true if the country is valid, false otherwise */
    public static Boolean isValidCountry(String country) {
        return CountryUtils.getCountries().contains(country);
    }
}
