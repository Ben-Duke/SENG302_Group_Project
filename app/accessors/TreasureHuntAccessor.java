package accessors;

import io.ebean.Ebean;
import models.Destination;
import models.TreasureHunt;
import models.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        return TreasureHunt.find().query().where().eq("thuntid", id).findOne();
    }

    /** Return all Treasure Hunts
     * @return List of Treasure Hunts
     */
    public static List<TreasureHunt> getAll() {
        return TreasureHunt.find().all();
    }

    public static List<TreasureHunt> getAllByDestination(Destination destination) {
        return TreasureHunt.find().query().where().eq("destination", destination).findList();
    }

    /** Insert the TreasureHunt  */
    /** Insert a TreasureHunt
     * @param treasureHunt Treasure Hunt to insert into database
     */
    public static void insert(TreasureHunt treasureHunt) {
        treasureHunt.setThuntIdNull();
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
        return TreasureHunt.find().query().where().eq(
                "destination", destination).findList();
    }

    /**
     * Gets a paginated list of treasure hunts that are currently open, with an offset and quantity to fetch.
     *
     * @param offset an integer representing the number of treasure hunts to skip before sending
     * @param quantity an integer representing the maximum length of the list
     * @return A List<TreasureHunt> of treasure hunts.
     */
    public static List<TreasureHunt> getPaginatedOpenDestinations(int offset, int quantity) {
        if (quantity < 1) {
            return new ArrayList<TreasureHunt>();
        }

        if (offset < 0) {
            offset = 0;
        }

        List<TreasureHunt> allTreasureHunts = TreasureHunt.find()
                .query()
                .setFirstRow(offset)
                .setMaxRows(quantity)
                .findList();

        return allTreasureHunts.stream()
                // filter out the closed treasure hunts
                .filter(TreasureHunt::isOpen).collect(Collectors.toList());
    }

    /**
     * Gets the total count of all open treasure hunts.
     *
     * @return a long representing the number of open treasure hunts.
     */
    public static long getCountOpenTreasureHunts() {
        return Ebean.find(TreasureHunt.class)
                .findList()
                .stream()
                .filter(TreasureHunt::isOpen)
                .count();
    }

    /**
     * Gets a paginated list of treasure hunts that match the current user, with an offset and quantity to fetch.
     *
     * @param user the user that the treasure hunts belong to
     * @param offset an integer representing the number of treasure hunts to skip before sending
     * @param quantity an integer representing the maximum length of the list
     * @return A List<TreasureHunt> of treasure hunts.
     */
    public static List<TreasureHunt> getPaginatedUsersTreasurehunts(User user, int offset, int quantity) {
        return TreasureHunt.find()
                .query()
                .where()
                .eq("user", user)
                .setFirstRow(offset)
                .setMaxRows(quantity)
                .findList();
    }
}
