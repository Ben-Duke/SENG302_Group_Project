package factories;

import io.ebean.ExpressionList;
import formdata.LoginFormData;

import play.data.Form;
import play.data.FormFactory;
import models.User;

import java.util.List;

/**
 * A class to handle accessing the database for login related things.
 */
public class LoginFactory {
    public LoginFactory() {

    }

    /**
     * Method to check if a username/email and password pair matches a user in
     * the database.
     *
     * @param email A String, the email to check.
     * @param password A String, the password to check
     * @return A boolean, true if there is a user with that email and password,
     *         false otherwise.
     */
    public boolean isPasswordMatch(String email, String password){
        ExpressionList<User> usersExpressionList;
        usersExpressionList = User.find.query()
                              .where().eq("username", email.toLowerCase())
        .and().eq("password", password);

        return usersExpressionList.findCount() > 0;
    }

//    public static int getUserId(String userName) {
//        int userId = -1;
//
//        ExpressionList<User> usersExpressionList;
//        usersExpressionList = User.find.query()
//                .where().eq("username", userName.toLowerCase());
//
//        if (usersExpressionList.findCount() > 1) {
//            User userfound = usersExpressionList.findOne();
//            userId = userfound.getUserid();
//        }
//
//        return userId;
//    }

    /**
     * Method to get a users unique ID from their username (email).
     *
     * @param userName A String, the users username(email).
     * @return An int representing the user's userID. Returns -1 if not found.
     */
    public static int getUserId(String userName) {
        int userId = -1;

        ExpressionList<User> users = User.find.query()
                            .where().eq("username", userName.toLowerCase());

        if (users.findCount() == 1) {
            userId = users.findOne().getUserid();
        }

        return userId;
    }
}