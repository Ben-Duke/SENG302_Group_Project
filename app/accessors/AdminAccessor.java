package accessors;

import models.Admin;

public class AdminAccessor {

    static public Admin getAdmin(){
        return Admin.find().query().where().eq("isDefault", true).findOne();
    }

}
