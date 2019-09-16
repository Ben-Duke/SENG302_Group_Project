package utilities;

import accessors.TagAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.ApplicationManager;
import controllers.routes;
import models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static play.mvc.Results.redirect;
import static play.mvc.Results.unauthorized;

/**
 * A class for methods which check user entered data for correctness.
 */
public class UtilityFunctions {

    private static final Logger logger = getLogger();

    /** Get a default logger (application) */
    public static Logger getLogger() {
        return getLogger("application");
    }

    /** Get a new logger */
    public static Logger getLogger(String loggerName) {
        return LoggerFactory.getLogger(loggerName);
    }

    // Private constructor to hide the implicit public one
    private UtilityFunctions() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Check that there is a user logged in
     */
    public static Result checkLoggedIn(Http.Request request) {
        User user = User.getCurrentUser(request);
        if (user == null) {
            return redirect(routes.UserController.userindex());
        }

        return null;
    }

    /**
     * Makes a set of all the users to be retained
     * @param lists A set of user as a list
     * @return A set of all retained users
     */
    public static Set<User> retainFromLists(List<Set<User>> lists) {
        int count = 0;
        Set<User> retainedList = lists.get(count);
        while (count < (lists.size() - 1)) {
            retainedList.retainAll(lists.get(count + 1));
            count += 1;
        }
        return retainedList;
    }

    /**
     * Function that validates a given input string. If the string has more characters than the limit, return false.
     * Else return true.
     *
     * @param max         the character limit
     * @param inputString the input string
     * @return if the string has more characters than the limit return false, else return true.
     */
    public static boolean validateMaxCharLimit(String inputString, Integer max) {
        return inputString.length() <= max;
    }

    /**
     * Function that validates a given input string. If the string has less characters than the limit, return false.
     * Else return true.
     *
     * @param min         the minimum number of characters
     * @param inputString the input string
     * @return A boolean,  if the string has less characters than the limit return false, else return true.
     */
    public static boolean validateMinCharLimit(String inputString, Integer min) {
        return min <= inputString.length();
    }

    /**
     * Checks a String only contains alphabetic characters. Does not support special characters (only a-z & A-Z).
     * <p>
     * Accepts the empty String.
     *
     * @param inputString The input String to validate.
     * @return A boolean, true if String only contains non-special alphabetic characters, false otherwise.
     */
    public static boolean isStringAllAlphabetic(String inputString) {
        String alphabeticRegexString = "^$|[a-zA-Z]*";
        return Pattern.matches(alphabeticRegexString, inputString);
    }

    /**
     * Checks if a String represents a double.
     *
     * @param inputString The input String to validate.
     * @return A boolean, true if String represents a Double (can be converted to a double), false otherwise.
     */
    public static boolean isStringADouble(String inputString) {
        try {
            Double.parseDouble(inputString);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Checks if a String represents an int.
     *
     * @param inputString The input String to validate.
     * @return A boolean, true if String represents an int (can be converted to an int), false otherwise.
     */
    static boolean isStringAnInt(String inputString) {
        try {
            Integer.parseInt(inputString);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Checks if a String is alphanumeric (contains only chars a-z && A-Z && 0-9).
     * <p>
     * Accepts the empty String
     *
     * @param inputString The input String to validate.
     * @return A boolean, true if String is alphanumeric, false otherwise.
     */
    static boolean isStringAlphaNumeric(String inputString) {
        String alphanumericRegex = "^$|[a-zA-Z0-9]*"; // regex specifying a word
        return Pattern.matches(alphanumericRegex, inputString);
    }

    /**
     * Method to check if the email entered by the user is a valid email. Is only a basic check with regex, doesn't
     * catch all emails and doesn't check if the email actually exists.
     * <p>
     * Email regex sourced online from here:
     * https://howtodoinjava.com/regex/java-regex-validate-email-address/
     * Courtesy of Lokesh Gupta
     *
     * @param email A String, the email to check.
     * @return A boolean, true if the email is valid, false otherwise.
     */
    public static boolean isEmailValid(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Function that populates the database with the nationalities
     *
     * @return A boolean, true if all nationality are added successfully,
     * false otherwise.
     */
    public static boolean addAllNationalities() {
        boolean isInSuccessState = true;

        CountryUtils.updateCountries();

        if (Nationality.find().all().isEmpty()) {
            try {

                if (CountryUtils.getCountries() == null){

                    logger.error("Countries have not been loaded. " +
                            "Nationalities will not be loaded. " +
                            "Restart Server?");

                } else {
                    for (String country : CountryUtils.getCountries()) {


                        Nationality nationality = new Nationality(country);


                        try {
                            nationality.save();
                        } catch (Exception error) {
                            isInSuccessState = false;
                            logger.error("Failed to save nationality: " +
                                    nationality.getNationalityName() +
                                    " uniqueness contraint failed", error);
                        }

                    }
                }

            } catch (Exception error) {
                logger.error("Unknown error", error);
            }
        } else {
            isInSuccessState = false;
        }
        return isInSuccessState;
    }

    /**
     * Inserts all default passport options into the database.
     *
     * @return A boolean, true if all passports where added successfully.
     */
    public static boolean addAllPassports() {
        boolean isInSuccessState = true;

        CountryUtils.updateCountries();

        if (Passport.find().all().isEmpty()) {
            if (CountryUtils.getCountries() == null){

                logger.error("Countries have not been loaded. " +
                        "Passports will not be loaded. " +
                        "Restart Server?");


            } else {
                for (String country : CountryUtils.getCountries()) {
                    Passport passport = new Passport(country);
                    try {
                        passport.save();
                    } catch (Exception error) {
                        isInSuccessState = false;
                        logger.error("Passport failed to save. name: " +
                                passport.getName() +
                                " uniqueness constraint failed", error);
                    }
                }
            }

        } else {
            isInSuccessState = false;
        }
        return isInSuccessState;
    }

    /**
     * Inserts all default traveler type options into the database.
     *
     * @return A boolean, true if all travelers are successfully added, false otherwise.
     */
    public static boolean addTravellerTypes() {
        ArrayList<String> types = new ArrayList();
        types.add("Groupie");
        types.add("Thrillseeker");
        types.add("Gap Year");
        types.add("Frequent Weekender");
        types.add("Holidaymaker");
        types.add("Business Traveller");
        types.add("Backpacker");
        boolean successfullyAddedAllTravvelers = true;
        if (TravellerType.find().all().isEmpty()) {
            for (String type : types) {
                try {
                    (new TravellerType(type)).save();
                } catch (Exception error) {
                    //Will remove after peer check
                    successfullyAddedAllTravvelers = false;
                    logger.error("Failed to add type: " + type + " Duplicate key", error);
                }
            }
        } else {
            successfullyAddedAllTravvelers = false;
        }

        return successfullyAddedAllTravvelers;
    }

    /**
     * Resizes an image given by a pathname
     *
     * @param pathName the path to the image to be resized
     * @return the resized buffered image
     */
    public static BufferedImage resizeImage(String pathName) {
        try {
            BufferedImage newProfileImage = ImageIO.read(new File(pathName));
            int type = newProfileImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : newProfileImage.getType();
            BufferedImage resizedImage = new BufferedImage(32, 32, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(newProfileImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, 32, 32, null);
            g.dispose();
            return resizedImage;
        } catch (IOException e) {
            logger.error("unknown error", e);
            return null;
        }

    }

    /**
     * This method sends a get request to the countries api and returns a sorted set of these countries
     *
     * @return Map of all countries fom the api
     * @throws Exception Throws exception if error found when using countries api
     */
    public static Map<String, Boolean> CountryUtils() throws IOException {
        Map<String, Boolean> countryMap = new TreeMap<>();
        if (ApplicationManager.isIsTest()) {
            String[] locales = Locale.getISOCountries();
            for (String countryCode : locales) {
                Locale obj = new Locale("", countryCode);
                countryMap.put(obj.getDisplayCountry(), false);
            }
        } else {
            String url = "https://restcountries.eu/rest/v2/all";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            countryMap = new TreeMap<>();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonNode jsonJacksonArray = Json.parse(response.toString());
            //print result

            for (JsonNode node : jsonJacksonArray) {
                countryMap.put(node.get("name").textValue(), false);
            }
        }


        return countryMap;
    }

    /**
     * This method sends a get request to the countries api and returns a sorted set of these countries
     *
     * @return A set of all countries from the api
     * @throws Exception Throws exception if error found when using countries api
     */
    public static Set countriesAsStrings() throws Exception {
        Set<String> countries = new HashSet<String>();
        if (ApplicationManager.isIsTest()) {
                String[] locales = Locale.getISOCountries();
                for (String countryCode : locales) {
                    Locale obj = new Locale("", countryCode);
                    countries.add(obj.getDisplayCountry());
                }
        } else {
            String url = "https://restcountries.eu/rest/v2/all";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000); // 5 seconds

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonNode jsonJacksonArray = Json.parse(response.toString());
            //print result

            for (JsonNode node : jsonJacksonArray) {
                countries.add((node.get("name").textValue()));
            }

        }
        Set countrySet = new TreeSet<String>();
        countrySet.addAll(countries);
        return countrySet;
    }

    /**
     * Deletes a file directory.
     *
     * Taken from : https://www.baeldung.com/java-delete-directory
     *
     * @param directoryToBeDeleted Path to directory to delete
     *
     * @return true if success, else false.
     */
    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * Turn a List of Strings into a set of tags replacing them with existing ones if needed
     * @param tagList the list of strings to make into tags
     * @return a set of tags
     */
    public static Set<Tag> tagLiteralsAsSet(List<String> tagList) {
        Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tagList) {
            Tag existingTag = TagAccessor.getTagByName(tagName.toLowerCase());
            if(existingTag != null) {
                tagSet.add(existingTag);
            } else {
                Tag newTag = new Tag(tagName.toLowerCase());
                tagSet.add(newTag);
                TagAccessor.insert(newTag);
            }
        }
        return tagSet;
    }


    public static ObjectNode quantityError(int maxQuantity) {
        String errorStr = "query parameter 'quantity' exceeded maximum " +
                "allowed int: " + maxQuantity;

        ObjectNode jsonError = (new ObjectMapper()).createObjectNode();
        jsonError.put("error", errorStr);
        jsonError.put("quantityLimit", maxQuantity);
        return jsonError;
    }
}
