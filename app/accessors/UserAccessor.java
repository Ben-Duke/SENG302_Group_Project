package accessors;

import models.Nationality;
import models.Passport;
import models.User;

import java.util.List;

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
}
