package factories;
import formdata.UserFormData;
import io.ebean.ExpressionList;
import models.User;
import play.data.Form;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserFactory {

    public UserFactory(){}



    public boolean checkpassword(String email, String password){
        ExpressionList<User> usersExpressionList = User.find.query()
                .where().eq("username", userLoggingInFormData.getUsername().toLowerCase());

        return 1;
    }

    public int checkusername(String username){
        //todo ask back end if this exists
        return 1;
    }


    public int createUser(UserFormData userForm){
        //todo make back end create a user
        String username = userForm.username;
        String password = userForm.password;
        String firstName = userForm.firstName;
        String lastName = userForm.lastName;
        String gender = userForm.gender;
        //ArrayList<List>(passports);
        //ArrayList<>(nationalities);
        String dob =  userForm.dob;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dob, formatter);


        User user = new User(username, password, firstName, lastName, date, gender);
        user.save();
        return user.getUserid();
    }

    //todo check backend to see if there is any user with this username

    public static int checkUsername(String username) {
        return User.checkUser(username);
    }


    //todo validate user with backend
    public static int getCurrentUser(Http.Request request) {
        return User.getCurrentUser(request, "");
    }
    //
}
