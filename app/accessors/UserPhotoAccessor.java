package accessors;

import models.User;
import models.UserPhoto;

public class UserPhotoAccessor {

    // Private constructor to hide the implicit public one
    private UserPhotoAccessor() {
        throw new IllegalStateException("Utility class");
    }

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

    public static void update(UserPhoto userPhoto) { userPhoto.update(); }

    public static UserPhoto getUserPhotoById(int id) {
        return UserPhoto.find.byId(id);
    }
}
