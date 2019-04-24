package factories;

import formdata.TreasureHuntFormData;
import models.TreasureHunt;
import models.User;

public class TreasureHuntFactory {

    public TreasureHuntFactory() { // Instantiate
    }

    /**
     * The method to create and save the Treasure Hunt.
     * @param treasureHuntFormData TreasureHuntFormData
     * @param user The user who is the creator of this treasure hunt
     */
    public void createTreasureHunt(TreasureHuntFormData treasureHuntFormData, User user) {
        TreasureHunt treasureHunt = new TreasureHunt(treasureHuntFormData.title, treasureHuntFormData.riddle,
                treasureHuntFormData.destination, treasureHuntFormData.startDate, treasureHuntFormData.endDate, user);
        treasureHunt.save();
    }

    /**
     * The method to update and save the Treasure Hunt.
     * @param treasureHunt The Treasure Hunt to be edited
     * @param treasureHuntFormData TreasureHuntFormData
     * @param user The user who wants to edit this treasure hunt
     */
    public void editTreasureHunt(TreasureHunt treasureHunt, TreasureHuntFormData treasureHuntFormData, User user) {
        if (user.equals(treasureHunt.getUser())) {
            treasureHunt.setTitle(treasureHuntFormData.title);
            treasureHunt.setRiddle(treasureHuntFormData.riddle);
            treasureHunt.setDestination(treasureHuntFormData.destination);
            treasureHunt.setStartDate(treasureHuntFormData.startDate);
            treasureHunt.setEndDate(treasureHuntFormData.endDate);
            treasureHunt.save();
        } else {
            System.out.println("ERROR: You cannot edit this Treasure hunt as you are not it's owner");
        }
    }

    /**
     * The method to delete the Treasure Hunt.
     * @param treasureHunt The Treasure Hunt to be edited
     * @param user The user who wants to delete this treasure hunt
     */
    public void deleteTreasureHunt(TreasureHunt treasureHunt, User user) {
        if (user.equals(treasureHunt.getUser())) {
            treasureHunt.delete();
            treasureHunt.save();
        } else {
            System.out.println("ERROR: You cannot delete this Treasure hunt as you are not it's owner");
        }
    }

    /**
     * The method to view the Treasure Hunt.
     * @param tHuntId The id of Treasure Hunt to be viewed
     * @param user The user who wants to view this treasure hunt
     */
    public TreasureHunt viewTreasureHunt(int tHuntId, User user) {
        if (User.find.byId(user.getUserid()) != null) {
            TreasureHunt treasureHunt = TreasureHunt.find.byId(tHuntId);
            return treasureHunt;
        } else {
            System.out.println("ERROR: You cannot delete this Treasure hunt as you do not belong here.");
            return null;
        }
    }
}
