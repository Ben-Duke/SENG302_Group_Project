package controllers;

import java.nio.file.Paths;

/**
 * Application manager used to differentiate between the test environment and main environment
 */
public class ApplicationManager {

    // Private constructor to hide the implicit public one
    private ApplicationManager() {
        throw new IllegalStateException("Utility class");
    }

    private static String userPhotoPath;

    private static boolean isTest = false;

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

    /**
     * Method to check what environment the application is needed to bne run on
     * @retun isTest A boolean representing the environment
     */
    public static boolean isIsTest() {
        return isTest;
    }

    /**
     * Method to check set what environment the application is running on
     * @param isTest A boolean representing the environment
     */
    public static void setIsTest(boolean isTest) {
        ApplicationManager.isTest = isTest;
    }
}
