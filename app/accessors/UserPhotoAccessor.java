package accessors;

import models.Tag;
import models.User;
import models.UserPhoto;

/**
 * A class to handle accessing User photos from the database
 */
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
        try {
            for(Tag tag : userPhoto.getTags()){
                userPhoto.removeTag(tag);
                TagAccessor.update(tag);
            }

            userPhoto.delete();
            userPhoto.update();
        } catch (Exception e) { }
    }
    public static void deleteById(int id) { delete(UserPhoto.find().byId(id)); }

    /**
     * update the photo
     * @param userPhoto the photo to update
     */
    public static void update(UserPhoto userPhoto) { userPhoto.update(); }

    /** Return the user photo matching the id passed
     * @param id Id of a user photo to find in the database
     * @return UserPhoto
     */
    public static UserPhoto getUserPhotoById(int id) {
        return UserPhoto.find().byId(id);
    }

    public static UserPhoto getUserPhotoByUrl(String url) {
        return UserPhoto.find().query().where().eq("url", url).findOne();
    }

    public static void unlinkAllTags(UserPhoto photo) {
        photo.getTags().clear();
        photo.update();
    }
}
