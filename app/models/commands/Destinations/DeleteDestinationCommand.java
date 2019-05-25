package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.TreasureHuntAccessor;
import controllers.DestinationController;
import models.Destination;
import models.TreasureHunt;
import models.UserPhoto;
import models.Visit;
import models.commands.CommandManager;
import models.commands.UndoableCommand;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.*;

/** Command to delete a user's destination */
public class DeleteDestinationCommand extends UndoableCommand {
    private Destination destination;
    private Boolean deletedByAdmin;

    // Using sets as the items do not need to be ordered and are unique
    private List<Visit> deletedVisits = new ArrayList<>();
    private List<TreasureHunt> deletedTreasureHunts = new ArrayList<>();
    private List<UserPhoto> destinationPhotos = new ArrayList<>();

    private final Logger logger = UtilityFunctions.getLogger();

    public DeleteDestinationCommand(Destination destination, Boolean deletedByAdmin) {
        this.destination = destination;
        this.deletedByAdmin = deletedByAdmin;
        this.destinationPhotos = destination.getUserPhotos();
    }

    public void execute() {
        // If admin, cascade deletion to visits and trips which use the destination
        if (deletedByAdmin) {
            List<Visit> visitsCopy = new ArrayList<>(destination.getVisits());

            for (Visit visit : visitsCopy) {
                deletedVisits.add(new Visit(visit));
                visit.delete();
            }
            List<TreasureHunt> treasureHunts = TreasureHuntAccessor.getByDestination(destination);

            for (TreasureHunt treasureHunt : treasureHunts) {
                deletedTreasureHunts.add(new TreasureHunt(treasureHunt));
                treasureHunt.delete();
            }

            for (UserPhoto userPhoto : destination.getUserPhotos()){
                DestinationController destinationController = new DestinationController();
                destinationController.unlinkPhotoFromDestination(
                        null, userPhoto.getPhotoId(), destination.getDestId());
            }
        }

        DestinationAccessor.delete(destination);
    }

    public void undo() {
        this.destination = new Destination(destination, deletedVisits);
        destination.setUserPhotos(destinationPhotos);
        destination.save();

        for (TreasureHunt treasureHunt : deletedTreasureHunts) {
            treasureHunt.setDestination(destination);
            treasureHunt.save();
        }

        for (Visit visit : deletedVisits) {
            visit.setDestination(destination);
            visit.save();
        }
    }

    public void redo() {
        execute();
    }
}



























