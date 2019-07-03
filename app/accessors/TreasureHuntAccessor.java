package accessors;

import models.Destination;
import models.TreasureHunt;

import java.util.List;

/**
 * A class to handle accessing Treasure Hunts from the database
 */
public class TreasureHuntAccessor {

    // Private constructor to hide the implicit public one
    private TreasureHuntAccessor() {
        throw new IllegalStateException("Utility class");
    }

    /** Return a Treasure Hunt matching the id passed
     * @param id Id value of a treasure hunt in the database
     * @return Treasure Hunt
     */
    public static TreasureHunt getById(int id) {
        return TreasureHunt.find.query().where().eq("thuntid", id).findOne();
    }

    /** Return all Treasure Hunts
     * @return List of Treasure Hunts
     */
    public static List<TreasureHunt> getAll() {
        return TreasureHunt.find.all();
    }

    /** Insert a TreasureHunt
     * @param treasureHunt Treasure Hunt to insert into database
     */
    public static void insert(TreasureHunt treasureHunt) {
        treasureHunt.thuntid = null;
        treasureHunt.save();
    }

    /** Delete a TreasureHunt
     * @param treasureHunt Treasure Hunt to delete from the database
     */
    public static void delete(TreasureHunt treasureHunt) {
        treasureHunt.delete();
    }

    /** Update a TreasureHunt
     * @param treasureHunt Treasure Hunt to update from database
     */
    public static void update(TreasureHunt treasureHunt) {
        treasureHunt.update();
    }

    /** Return a list treasure hunts that match a given destination
     * @param destination Id value of a destination matching a treasure hunt in the database
     * @return List of treasure hunts
     */
    public static List<TreasureHunt> getByDestination(Destination destination) {
        return TreasureHunt.find.query().where().eq(
                "destination", destination).findList();
    }
}
