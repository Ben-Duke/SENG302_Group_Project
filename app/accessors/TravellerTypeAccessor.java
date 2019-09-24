package accessors;

import models.TravellerType;

import java.util.List;
import java.util.Set;

public class TravellerTypeAccessor {

    public static TravellerType getByName(String name) {
        return TravellerType.find().query().where().eq("traveller_type_name", name).findOne();
    }

    public static List<TravellerType> getAll() {
        return TravellerType.find().all();
    }
}
