package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.TreasureHuntAccessor;
import models.Destination;
import models.TreasureHunt;
import models.Visit;
import models.commands.UndoableCommand;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;

/** Command to edit a user's profile */
public class EditDestinationCommand extends UndoableCommand {
    private Destination uneditedDestination;
    private Destination editedDestination;

    public EditDestinationCommand(Destination editedDestination) {
        this.editedDestination = editedDestination;
        this.uneditedDestination =
                DestinationAccessor.getDestinationById(editedDestination.getDestId());
    }

    public void execute() {
        DestinationAccessor.update(editedDestination);
    }

    public void undo() {
        DestinationAccessor.update(uneditedDestination);
    }

    public void redo() {
        execute();
    }
}



























