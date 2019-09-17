package accessors;

import io.ebean.Finder;
import io.ebean.Query;
import models.Admin;

import java.util.List;

public class AdminAccessor {

    static public Admin getAdmin(){
        return Admin.find().query().where().eq("isDefault", true).findOne();
    }

    static public Finder<Integer, Admin> AdminFinder(){
        return Admin.find();
    }
}
