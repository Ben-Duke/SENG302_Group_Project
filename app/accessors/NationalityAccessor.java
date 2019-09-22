package accessors;

import models.Nationality;

import java.util.List;

public class NationalityAccessor {

    public static List<Nationality> getAll() {
        return Nationality.find().all();
    }
}
