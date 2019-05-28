package accessors;

import factories.UserFactory;
import models.Nationality;
import models.Passport;
import models.User;
import models.UserPhoto;

import java.util.List;

public class UserAccessor {

    public static User getDefaultAdmin(){
        throw new UnsupportedOperationException();
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
     * for 2 or more users in case our database is in an inconsistent state again.
     *
     * @param email String of the users email to search for.
     * @return A List of User objects with a matching email address.
     */
    public static List<User> getUsersFromEmail(String email) {
        return  User.find.query()
                    .where().eq("email", email.toLowerCase()).findList();
    }

    /**
     * Return the User matching the id passed
     * @param id the id of the user
     */
    public static User getById(int id) {
        return User.find.byId(id);
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
}
