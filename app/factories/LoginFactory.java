package factories;

import io.ebean.ExpressionList;
import formdata.LoginFormData;

import play.data.Form;
import play.data.FormFactory;
import models.User;

public class LoginFactory {
    public LoginFactory() {

    }

    public boolean isPasswordMatch(String email, String password){
        ExpressionList<User> usersExpressionList;
        usersExpressionList = User.find.query()
                              .where().eq("username", email.toLowerCase());

        return usersExpressionList.findCount() > 1;
    }

    public int getUserId(Form<User> loginFormData) {
        int userId;
        User user = loginFormData.get();
        ExpressionList<User> usersExpressionList;
        usersExpressionList = User.find.query()
                .where().eq("username", user.getUsername().toLowerCase());

        if (usersExpressionList.findCount() > 1) {
            User user = usersExpressionList.findOne();
            userId = user.getUserid();
        }

        return userId;
    }
}