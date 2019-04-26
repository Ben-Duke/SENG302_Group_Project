package controllers;

/**
 * Application manager used to differentiate between the test environment and main environment
 */
public class ApplicationManager {


    private static String userPhotoPath;

    public ApplicationManager(String mainOrTest){
        if(mainOrTest.equalsIgnoreCase("test")){
            userPhotoPath = "/test/resources/test_photos/user_";
        }
    }

    public static String getUserPhotoPath() {
        return userPhotoPath;
    }

    public static void setUserPhotoPath(String userPhotoPath) {
        ApplicationManager.userPhotoPath = userPhotoPath;
    }
}
