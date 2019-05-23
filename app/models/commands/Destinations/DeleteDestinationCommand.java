package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.TreasureHuntAccessor;
import models.Destination;
import models.TreasureHunt;
import models.Visit;
import models.commands.CommandManager;
import models.commands.UndoableCommand;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.*;

/** Command to delete a destination */
public class DeleteDestinationCommand extends UndoableCommand {
    private Destination destination;
    private Boolean deletedByAdmin;

    // Using sets as the items do not need to be ordered and are unique
    private List<Visit> deletedVisits = new ArrayList<>();
    private List<TreasureHunt> deletedTreasureHunts = new ArrayList<>();

    private final Logger logger = UtilityFunctions.getLogger();

    public DeleteDestinationCommand(Destination destination, Boolean deletedByAdmin) {
        this.destination = destination;
        this.deletedByAdmin = deletedByAdmin;
    }

    /**
     * Deletes the command's destination
     */
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
        }

        DestinationAccessor.delete(destination);
    }

    /**
     * Undoes the deletion of a Destination
     */
    public void undo() {
        this.destination = new Destination(destination, deletedVisits);
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

    /**
     * Redoes the previously executed undo
     */
    public void redo() {
        execute();
    }
}



























