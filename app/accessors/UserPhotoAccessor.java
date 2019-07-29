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
        try {
            userPhoto.delete();
            userPhoto.update();
        } catch (Exception e) { }
    }
    public static void deleteById(int id) { delete(UserPhoto.find.byId(id)); }

    public static void update(UserPhoto userPhoto) { userPhoto.update(); }

    public static UserPhoto getUserPhotoById(int id) {
        return UserPhoto.find.byId(id);
    }

    public static UserPhoto getUserPhotoByUrl(String url) {
        return UserPhoto.find.query().where().eq("url", url).findOne();
    }
}
