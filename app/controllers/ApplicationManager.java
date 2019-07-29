package controllers;

import org.slf4j.Logger;
import utilities.UtilityFunctions;

import java.nio.file.Paths;

/**
 * Enum for the two databases used by the application
 *  matches those declared in db {} of application.conf and test.conf
 */
enum DatabaseName {
    PROD, TEST
}

/**
 * Application manager used to differentiate between the test environment and main environment
 */
public class ApplicationManager {

    private static final Logger logger = UtilityFunctions.getLogger();

    private static String userPhotoPath;

    /** Used to determine whether to load countries over http or use local data */
    private static boolean isTest = false;

    /** Current database app is using saved as the name of the database */
    private static DatabaseName databaseName = DatabaseName.PROD;

    /**
     * Method to get the user photo path.
     *
     * @return A String representing the user photo URL
     */
    public static String getUserPhotoPath(){
        return userPhotoPath;
    }

    /**
     * Gets the full url to the default user photo.
     *
     * Used for new File(fullPath).
     *
     * @return The full url to the placeholder profile image.
     */
    public final static String getDefaultUserPhotoFullURL() {
        String urlToRoot = Paths.get(".").toAbsolutePath().normalize().toString();
        return urlToRoot + "/public/images/Generic.png";
    }

    /**
     * Method to set the user photo path.
     *
     * @param userPhotoPath A String representing the user photo URL
     */
    public static void setUserPhotoPath(String userPhotoPath) {
        ApplicationManager.userPhotoPath = userPhotoPath;
    }

    public static boolean isIsTest() {
        return isTest;
    }

    /** Return database name as it is in the .conf files (lowercase) */
    public static String getDatabaseName() {
        return databaseName.toString().toLowerCase();
    }

    /** Set the app to use the testing database */
    public static void setTesting() {
        databaseName = DatabaseName.TEST;
        isTest = true;
    }
}
