package accessors;

import io.ebean.Finder;
import models.Admin;

public class AdminAccessor {

    static public Admin getAdmin(){
        return Admin.find().query().where().eq("isDefault", true).findOne();
    }

    static public Finder<Integer, Admin> AdminFinder(){
        return Admin.find();
    }
}
