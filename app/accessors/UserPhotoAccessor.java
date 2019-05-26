package accessors;

import models.User;
import models.UserPhoto;

public class UserPhotoAccessor {

    /**
     * Insert the photo
     * @param userPhoto the photo to add
     */
    public static void insert(UserPhoto userPhoto) {
        userPhoto.save();
    }

    /**
     * Delete the photo
     * @param userPhoto the photo to delete
     */
    public static void delete(UserPhoto userPhoto) {
        userPhoto.delete();
    }
}
