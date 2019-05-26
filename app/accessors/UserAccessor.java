package accessors;

import factories.UserFactory;
import models.Nationality;
import models.Passport;
import models.User;
import models.UserPhoto;

import java.io.File;
import java.util.List;

import static play.mvc.Results.ok;

public class UserAccessor {

    public User getDefaultAdmin(){
        return null;
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
     * Finds a user's profile picture in the database by their user id.
     * The user must exist in the database.
     * @param userId the user id
     * @throws IllegalArgumentException if the user doesn't exist
     * @return the user's profile picture or null if no profile picture exists
     */
    public static UserPhoto getUserProfilePictureByUserId(int userId) {
        User user = getUserById(userId);
        if (user != null) {
            return UserFactory.getUserProfilePicture(userId);
        } else {
            throw new IllegalArgumentException("The User must exist in the database.");
        }

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

    /** Update the user */
    public static void update(User user) { user.update(); }
}
