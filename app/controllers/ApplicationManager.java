package controllers;

/**
 * Application manager used to differentiate between the test environment and main environment
 */
public class ApplicationManager {


    private static String userPhotoPath;

    public static String getUserPhotoPath() {
        return userPhotoPath;
    }

    public static void setUserPhotoPath(String userPhotoPath) {
        ApplicationManager.userPhotoPath = userPhotoPath;
    }
}
