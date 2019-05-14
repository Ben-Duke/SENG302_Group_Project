package factories;

import io.ebean.ExpressionList;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * A class to handle accessing the database for login related things.
 */
public class LoginFactory {
    public LoginFactory() {
        // Required default constructor -> not entirely sure, just copying Ben
    }

    /**
     * Method to get a users unique ID from their email (email).
     *
     * @param email A String, the users email(email).
     * @return An int representing the user's userID. Returns -1 if not found.
     */
    public static int getUserId(String email) {
        int userId = -1;

        ExpressionList<User> users = User.find.query()
                            .where().eq("email", email.toLowerCase());

        if (users.findCount() == 1) {
            userId = users.findOne().getUserid();
        }

        return userId;
    }

    /**
     * Method to check if a email/email and password pair matches a user in
     * the database.
     *
     * @param email A String, the email to check.
     * @param passwordPlaintext A String, the password to check
     * @return A boolean, true if there is a user with that email and password,
     *         false otherwise.
     */
    public boolean isPasswordMatch(String email, String passwordPlaintext){
        List<User> users;
        users  = User.find.query().where()
                .eq("email", email.toLowerCase()).findList();

        boolean isMatch = false;
        if (users.size() == 1) {
            User matchedUser = users.get(0);
            String usersDatabasePasswordHash = matchedUser.getPassword();
            isMatch = BCrypt.checkpw(passwordPlaintext, usersDatabasePasswordHash);
        }

        return isMatch;
    }
}