package factories;

import io.ebean.ExpressionList;
import formdata.LoginFormData;

import play.data.Form;
import play.data.FormFactory;
import models.User;

import java.util.List;

public class LoginFactory {
    public LoginFactory() {

    }

    public boolean isPasswordMatch(String email, String password){
        ExpressionList<User> usersExpressionList;
        usersExpressionList = User.find.query()
                              .where().eq("username", email.toLowerCase());

        return usersExpressionList.findCount() > 1;
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

    public static int getUserId(String userName) {
        int userId = -1;

        List<User> users = User.find.all();

        for(int i = 0; i < users.size();i++){
            if(users.get(i).username.equals(userName)){
                userId = users.get(i).getUserid();
            }
        }

        return userId;
    }
}