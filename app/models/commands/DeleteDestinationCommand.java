package models.commands;

import models.Destination;
import models.TreasureHunt;
import models.Visit;

import javax.persistence.Entity;
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

        destination.delete();
    }

    public void undo() {

    }

    public void redo() {

    }
}
