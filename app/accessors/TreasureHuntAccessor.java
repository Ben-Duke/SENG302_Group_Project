package accessors;

import models.TreasureHunt;

import java.util.List;

public class TreasureHuntAccessor {

    /** Return the destination matching the id passed */
    public static TreasureHunt getById(int id) {
        return TreasureHunt.find.query().where().eq("thuntid", id).findOne();
    }

    public static List<TreasureHunt> getAll() {
        return TreasureHunt.find.all();
    }

    /** Insert the destination */
    public static void insert(TreasureHunt treasureHunt) {
        treasureHunt.save();
    }

    /** delete the destination */
    public static void delete(TreasureHunt treasureHunt) {
        treasureHunt.delete();
    }
}
