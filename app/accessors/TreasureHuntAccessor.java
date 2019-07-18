package accessors;

import models.Destination;
import models.TreasureHunt;

import java.util.List;

public class TreasureHuntAccessor {

    // Private constructor to hide the implicit public one
    private TreasureHuntAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return the TreasureHunt matching the id passed */
    public static TreasureHunt getById(int id) {
        return TreasureHunt.find().query().where().eq("thuntid", id).findOne();
    }

    public static List<TreasureHunt> getAll() {
        return TreasureHunt.find().all();
    }

    /** Insert the TreasureHunt  */
    public static void insert(TreasureHunt treasureHunt) {
        treasureHunt.setThuntIdNull();
        treasureHunt.save();
    }

    /** delete the TreasureHunt */
    public static void delete(TreasureHunt treasureHunt) {
        treasureHunt.delete();
    }

    public static void update(TreasureHunt treasureHunt) {
        treasureHunt.update();
    }

    public static List<TreasureHunt> getByDestination(Destination destination) {
        return TreasureHunt.find().query().where().eq(
                "destination", destination).findList();
    }
}
