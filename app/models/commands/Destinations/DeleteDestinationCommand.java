package models.commands.Destinations;

import accessors.DestinationAccessor;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Command to edit a user's profile */
@Entity
public class DeleteDestinationCommand extends UndoableCommand {
    private Destination destination;
    private Boolean deletedByAdmin;

    // Using sets as the items do not need to be ordered and are unique
    private Set<Visit> deletedVisits = new HashSet<>();
    private Set<TreasureHunt> deletedTreasureHunts = new HashSet<>();

    private final Logger logger = UtilityFunctions.getLogger();

    public DeleteDestinationCommand(Destination destination, Boolean deletedByAdmin) {
        this.destination = destination;
        this.deletedByAdmin = deletedByAdmin;
    }

    public void execute() {
        // If admin, cascade deletion to visits and trips which use the destination
        if (deletedByAdmin) {
            for (Visit visit : destination.getVisits()) {
                deletedVisits.add(visit);
                visit.delete();
            }
            List<TreasureHunt> treasureHunts = TreasureHunt.find.query().where().eq("destination", destination).findList();

            for (TreasureHunt treasureHunt : treasureHunts) {
                deletedTreasureHunts.add(treasureHunt);
                treasureHunt.delete();
            }
        }

        DestinationAccessor.delete(destination);
    }

    public void undo() {
        logger.debug("undoing command in command class");
        destination.save();

        for (TreasureHunt treasureHunt : deletedTreasureHunts) {
            treasureHunt.save();
        }

        for (Visit visit : deletedVisits) {
            visit.save();
        }
    }

    public void redo() {
        //exectute();
    }
}



























