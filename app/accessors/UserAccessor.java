package accessors;

import io.ebean.Ebean;
import models.Nationality;
import models.Passport;
import models.User;
import models.UserPhoto;

import java.util.List;

public class UserAccessor {

    public static User getDefaultAdmin(){
        throw new UnsupportedOperationException();
    }

    public static User getById(int id) {
        return User.find.byId(id);
    }

    public static Passport getPassport(int id) {
        return Passport.find.query().where().eq("passid", id).findOne();
    }

    public static List<Passport> getAllPassports() {
        return Passport.find.all();
    }

    public static List<Nationality> getAllNationalities() {
        return Nationality.find.all();
    }

    /**
     * Finds a user in the database by their user id
     * @param id the user id
     * @return the user
     */
    public static User getUserById(int id) {
        return User.find.byId(id);
    }

    /**
     * Gets a List of Users with a specific email.
     *
     * It should be a List of length 0 or 1, but you should still check
     * for 2 or more users encase our database is in an inconsistent state again.
     *
     * @param email String of the users email to search for.
     * @return A List of User objects with a matching email address.
     */
    public static List<User> getUsersFromEmail(String email) {
        return  User.find.query()
                    .where().eq("email", email.toLowerCase()).findList();
    }

    public static User getUserByEmail(String email) {
        List<User> users = getUsersFromEmail(email);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    /** Update the user */
    public static void update(User user) { user.update(); }

    /**
     * Gets the profile picture for a User. Returns null if they have no profile
     * photo.
     *
     * @param user The User to get the photo of.
     * @return A UserPhoto representing the users profile picture.
     * @throws io.ebean.DuplicateKeyException If the user has more than 1
     *          profile picture (should never happen).
     */
    public static UserPhoto getProfilePhoto(User user) throws io.ebean.DuplicateKeyException {
        List<UserPhoto> userProfilePhotoList = UserPhoto.find.query()
                .where().eq("user", user)
                .and().eq("isProfile", true)
                .findList();

        if (0 == userProfilePhotoList.size()) {
            return null;
        } else if (1 == userProfilePhotoList.size()) {
            return userProfilePhotoList.get(0);
        } else {
            throw new io.ebean.DuplicateKeyException("Multiple profile photos.",
                    new Throwable("Multiple profile photos."));
        }
    }
}
