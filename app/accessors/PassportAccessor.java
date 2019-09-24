package accessors;

import models.Passport;

import java.util.List;

public class PassportAccessor {
    public static List<Passport> getAll() {
        return Passport.find().all();
    }

    public static Passport getByName(String name) {
        return Passport.find().query().where().eq("passport_name", name).findOne();
    }
}
