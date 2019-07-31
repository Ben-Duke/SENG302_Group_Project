package accessors;

import models.TravellerType;

public class TravellerTypeAccessor {

    public static TravellerType getByName(String name) {
        return TravellerType.find().query().where().eq("traveller_type_name", name).findOne();
    }
}
