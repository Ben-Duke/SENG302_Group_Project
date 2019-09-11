package controllers;

import org.slf4j.Logger;
import play.Application;
import play.db.Database;
import utilities.UtilityFunctions;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum for the two databases used by the application
 *  matches those declared in db {} of application.conf and test.conf
 */
enum DatabaseName {
    DEFAULT
}

/**
 * Application manager used to differentiate between the test environment and main environment
 */
public class ApplicationManager {

    private static String mediaPath;

    // Private constructor to hide the implicit public one
    private ApplicationManager() {
        throw new IllegalStateException("Utility class");
    }

    private static final Logger logger = UtilityFunctions.getLogger();

    private static String userPhotoPath;

    /** Used to determine whether to load countries over http or use local data */
    private static boolean isTest = false;

    /** Current database app is using saved as the name of the database */
    private static DatabaseName databaseName = DatabaseName.DEFAULT;


    /**
     * Method to get the media path.
     *
     * @return A String representing the user photo URL
     */
    public static String getMediaPath(){
        return mediaPath;
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
     * Gets the full url to the default destination photo.
     *
     * Used for new File(fullPath).
     *
     * @return The full url to the placeholder destination image.
     */
    public final static String getDefaultDestinationPhotoFullURL() {
        String urlToRoot = Paths.get(".").toAbsolutePath().normalize().toString();
        return urlToRoot + "/public/images/destinationPlaceHolder.png";
    }

    /**
     * Method to set the user photo path.
     *
     * @param mediaPath A String representing the media URL
     */
    public static void setMediaPath(String mediaPath) {
        ApplicationManager.mediaPath = mediaPath;
    }

    /**
     * Method to check what environment the application is needed to bne run on
     * @retun isTest A boolean representing the environment
     */
    public static boolean isIsTest() {
        return isTest;
    }

    /** Return database name as it is in the .conf files (lowercase) */
    public static String getDatabaseName() {
        return databaseName.toString().toLowerCase();
    }

    /** Set the app to use the testing database */
    public static void setTesting() {
        // H2 testing db accessed through Default
        databaseName = DatabaseName.DEFAULT;
        isTest = true;
    }
}
