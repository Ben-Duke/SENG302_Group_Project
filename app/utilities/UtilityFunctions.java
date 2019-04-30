package utilities;

import models.Nationality;
import models.Passport;
import models.TravellerType;
import models.User;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A class for methods which check user entered data for correctness.
 */
public class UtilityFunctions {
    public static List<User> retainFromLists(List<List<User>> lists){
        int count = 0;
        List<User> retainedList = lists.get(count);
        while(count < (lists.size() - 1)){
            retainedList.retainAll(lists.get(count + 1));
            count += 1;
        }
        return retainedList;
    }

    /**
     * Function that validates a given input string. If the string has more characters than the limit, return false.
     * Else return true.
     * @param max the character limit
     * @param inputString the input string
     * @return
     */
    public static boolean validateMaxCharLimit(String inputString, Integer max){
        return inputString.length() <= max;
    }

    /**
     * Function that validates a given input string. If the string has less characters than the limit, return false.
     * Else return true.
     * @param min the minimum number of characters
     * @param inputString the input string
     * @return
     */
    public static boolean validateMinCharLimit(String inputString, Integer min){
        return min <= inputString.length();
    }

    /**
     * Checks a String only contains alphabetic characters. Does not support special characters (only a-z & A-Z).
     *
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
            double doubleFromInput = Double.parseDouble(inputString);
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
    public static boolean isStringAnInt(String inputString) {
        try {
            int intFromInput = Integer.parseInt(inputString);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Checks if a String is alphanumeric (contains only chars a-z && A-Z && 0-9).
     *
     * Accepts the empty String
     *
     * @param inputString The input String to validate.
     * @return A boolean, true if String is alphanumeric, false otherwise.
     */
    public static boolean isStringAlphaNumeric(String inputString) {
        String alphanumericRegex = "^$|[a-zA-Z0-9]*"; // regex specifying a word
        return Pattern.matches(alphanumericRegex, inputString);
    }


    /**
     * Function that validates a given input string to check if it can be converted to a given type
     * If the condition is "date", check that the string can be converted to a Date
     * If the condition is "datetime", check that the string can be converted to a datetime.
     * If it can't, return false.
     * Format should be dd/MM/yyyy
     * @param type "date", "datetime" (more in the future)
     * @param inputString the input string
     * @return
     */
    public static boolean validateType(String inputString, String type){

        return false; // TODO
    }

    /**
     * Method to check if the email entered by the user is a valid email. Is only a basic check with regex, doesn't
     * catch all emails and doesn't check if the email actually exists.
     *
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
     */
    public static void addAllNationalities() {
        if (Nationality.find.all().isEmpty()) {
            String[] locales = Locale.getISOCountries();
            for (String countryCode : locales) {
                Locale obj = new Locale("", countryCode);
                Nationality nationality = new Nationality(obj.getDisplayCountry());
                try{
                nationality.save();
                }catch(Exception error){
                    System.out.println("Failed to save nationality: " + nationality );

                }
            }
        }
    }

    /**
     * Inserts all default passport options into the database.
     */
    public static void addAllPassports() {
        if (Passport.find.all().isEmpty()) {
            String[] locales = Locale.getISOCountries();

            for (String countryCode : locales) {
                Locale obj = new Locale("", countryCode);

                Passport passport = new Passport(obj.getDisplayCountry());
                try {
                    passport.save();
                }catch(Exception error){
                    System.out.println("Passport failed to save. name: " + passport);
                }
            }
        }
    }

    /**
     * Inserts all default traveler type options into the database.
     */
    public static void addTravellerTypes() {
        ArrayList<String> types = new ArrayList();
        types.add("Groupie");
        types.add("Thrillseeker");
        types.add("Gap Year");
        types.add("Frequent Weekender");
        types.add("Holidaymaker");
        types.add("Business Traveller");
        types.add("Backpacker");
        if (TravellerType.find.all().isEmpty()) {
            System.out.println("Adding types");
            for(String type : types){
                try{
                    (new TravellerType(type)).save();
                }catch(Exception error){
                    //Will remove after peer check
                    System.out.println("Failed to add type: " + type + "Duplicate key");
                }
            }
        }
    }

    /**
     * Resizes an image given by a pathname
     * @param pathName the path to the image to be resized
     * @return the resized buffered image
     */
    public static BufferedImage resizeImage(String pathName){
        try {
            BufferedImage newProfileImage = ImageIO.read(new File(pathName));
            int type = newProfileImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : newProfileImage.getType();
            BufferedImage resizedImage = new BufferedImage(32, 32, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(newProfileImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH), 0, 0, 32, 32, null);
            g.dispose();
            return resizedImage;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }


    }

}
