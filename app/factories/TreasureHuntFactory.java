package factories;

import formdata.TreasureHuntFormData;
import models.Destination;
import models.TreasureHunt;
import models.User;
import models.commands.General.UndoableCommand;
import models.commands.Treasurehunts.EditTreasureHuntCommand;
import utilities.exceptions.EbeanDateParseException;

import java.util.List;

public class TreasureHuntFactory {

    public TreasureHuntFactory() { // Instantiate
    }

    /**
     * The method to create and save the Treasure Hunt.
     * @param treasureHuntFormData TreasureHuntFormData
     * @param user The user who is the creator of this treasure hunt
     */
    public void createTreasureHunt(TreasureHuntFormData treasureHuntFormData, User user) throws EbeanDateParseException {
        List<Destination> destinations = Destination.find().all();
        for (Destination destination: destinations) {
            if (destination.getIsPublic() && destination.getDestName().equals(treasureHuntFormData.destination)) {
                TreasureHunt treasureHunt = new TreasureHunt(treasureHuntFormData.title, treasureHuntFormData.riddle,
                        destination, treasureHuntFormData.startDate, treasureHuntFormData.endDate, user);
                treasureHunt.save();
            }
        }

    }

    /**
     * The method to update and save the Treasure Hunt.
     * @param treasureHuntId The id of the Treasure Hunt to be edited
     * @param treasureHuntFormData TreasureHuntFormData
     */
    public void editTreasureHunt(User user, Integer treasureHuntId, TreasureHuntFormData treasureHuntFormData) throws EbeanDateParseException {
        TreasureHunt treasureHunt = TreasureHunt.find().byId(treasureHuntId);
        if (treasureHunt != null) {
            treasureHunt.setTitle(treasureHuntFormData.title);
            treasureHunt.setRiddle(treasureHuntFormData.riddle);
            List<Destination> destinations = Destination.find().query().where().eq("destIsPublic", true).findList();
            for (Destination destination: destinations) {
                if (destination.getDestName().equals(treasureHuntFormData.destination)) {
                    treasureHunt.setDestination(destination);
                }
            }
            treasureHunt.setStartDate(treasureHuntFormData.startDate);
            treasureHunt.setEndDate(treasureHuntFormData.endDate);
            UndoableCommand cmd = new EditTreasureHuntCommand(treasureHunt);
            user.getCommandManager().executeCommand(cmd);
        }
    }
}
