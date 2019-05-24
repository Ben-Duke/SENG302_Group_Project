package accessors;

import models.User;
import models.UserPhoto;

public class UserPhotoAccessor {

    /**
     * Insert the photo
     * @param userPhoto the photo to add
     */
    public static void insert(UserPhoto userPhoto) {
        System.out.println();
        System.out.println("---Inserting photo " + userPhoto.getPhotoId() + "   :" +  userPhoto.getUrlWithPath() + "---");
        System.out.println("All photos before:");
        for(UserPhoto userPhoto1 : UserPhoto.find.all()) {
            System.out.println("User Photo : " + userPhoto1.getPhotoId() + "   ||| url = " + userPhoto1.getUrlWithPath());
        }
        System.out.println("-----------------------------");
        userPhoto.save();
        System.out.println("All photos after:");
        for(UserPhoto userPhoto1 : UserPhoto.find.all()) {
            System.out.println("User Photo : " + userPhoto1.getPhotoId() + "   ||| url = " + userPhoto1.getUrlWithPath());
        }
        System.out.println("-----------------------------");
        System.out.println();

    }

    /**
     * Delete the photo
     * @param userPhoto the photo to delete
     */
    public static void delete(UserPhoto userPhoto) {
        System.out.println();
        System.out.println("---Deleting photo " + userPhoto.getUrl() + "---");
        System.out.println("All photos before:");
        for(UserPhoto userPhoto1 : UserPhoto.find.all()) {
            System.out.println("User Photo : " + userPhoto1.getPhotoId() + "||| url = " + userPhoto1.getUrlWithPath());
        }
        System.out.println("-----------------------------");
        userPhoto.delete();
        System.out.println("All photos after:");
        for(UserPhoto userPhoto1 : UserPhoto.find.all()) {
            System.out.println("User Photo : " + userPhoto1.getPhotoId() + "||| url = " + userPhoto1.getUrlWithPath());
        }
        System.out.println("-----------------------------");
        System.out.println();
    }
}
