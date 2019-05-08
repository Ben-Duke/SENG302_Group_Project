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


}
