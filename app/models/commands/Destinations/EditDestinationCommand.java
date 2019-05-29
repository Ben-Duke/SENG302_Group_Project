package models.commands.Destinations;

import accessors.DestinationAccessor;
import models.Destination;
import models.commands.general.UndoableCommand;

/** Command to edit a destination */
public class EditDestinationCommand extends DestinationPageCommand {
    private Destination uneditedDestination;
    private Destination editedDestination;
    private Destination actualDestination;

    /**
     * Constructor to create an EditDestinationCommand. Takes an edited destination
     * as the parameter which is the destination to update the current destination to.
     * The destination with the same dest id as the editedDestination will be updated
     * to the edited destination.
     * @param editedDestination the edited destination
     */
    public EditDestinationCommand(Destination editedDestination) {
        this.editedDestination = new Destination();
        actualDestination = editedDestination;
        this.editedDestination.applyEditChanges(actualDestination);
        this.uneditedDestination =
                DestinationAccessor.getDestinationById(editedDestination.getDestId());
    }

    /**
     * Updates the destination's details
     */
    public void execute() {
        actualDestination.applyEditChanges(editedDestination);
        DestinationAccessor.update(actualDestination);
    }

    /**
     * Undoes the update of the destination's details
     */
    public void undo() {
        actualDestination.applyEditChanges(uneditedDestination);
        DestinationAccessor.update(actualDestination);
    }

    /**
     * Redos the update of the destination's details
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return this.actualDestination.getDestName() + " editing";
    }
}



























