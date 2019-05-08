package controllers;

/**
 * Application manager used to differentiate between the test environment and main environment
 */
public class ApplicationManager {

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

    public static void setIsTest(boolean isTest) {
        ApplicationManager.isTest = isTest;
    }
}
