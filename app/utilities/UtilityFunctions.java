package utilities;

import models.User;

import java.util.*;
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
     * Validates if the input string is an actual nationality.
     * @param inputString
     * @return
     */
    public static boolean validateInvalidNationality(String inputString){

        return false;
    }

    /**
     * Validates if the input string is an actual passport type.
     * @param inputString
     * @return
     */
    public static boolean validateInvalidPassport(String inputString){

        return false;
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

//    public static boolean isStringDate TODO

}
