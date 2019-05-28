package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.TreasureHuntAccessor;
import accessors.VisitAccessor;
import models.Destination;
import models.TreasureHunt;
import models.Visit;
import models.commands.UndoableCommand;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    public void execute() {
        // If admin, cascade deletion to visits and trips which use the destination
        if (deletedByAdmin) {
            List<Visit> visitsCopy = new ArrayList<>(destination.getVisits());

            for (Visit visit : visitsCopy) {
                deletedVisits.add(new Visit(visit));
                VisitAccessor.delete(visit);
            }
            List<TreasureHunt> treasureHunts = TreasureHuntAccessor.getByDestination(destination);

            for (TreasureHunt treasureHunt : treasureHunts) {
                deletedTreasureHunts.add(new TreasureHunt(treasureHunt));
                TreasureHuntAccessor.delete(treasureHunt);
            }
        }

        DestinationAccessor.delete(destination);
    }

    /**
     * Undoes the deletion of a Destination
     */
    @Override
    public void undo() {
        this.destination = new Destination(destination, deletedVisits);
        destination.save();

        for (TreasureHunt treasureHunt : deletedTreasureHunts) {
            treasureHunt.setDestination(destination);
            TreasureHuntAccessor.insert(treasureHunt);
        }

        for (Visit visit : deletedVisits) {
            visit.setDestination(destination);
            VisitAccessor.insert(visit);
        }
    }

    /**
     * Redoes the previously executed undo
     */
    @Override
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Destination " + this.destination.getDestName() + " deletion";
    }
}



























