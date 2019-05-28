package models.commands.treasurehunts;

import accessors.TreasureHuntAccessor;
import models.TreasureHunt;
import models.commands.general.UndoableCommand;

/** Command to delete a destination */
public class EditTreasureHuntCommand extends UndoableCommand {
    private TreasureHunt unEditedTreasureHunt;
    private TreasureHunt editedTreasureHunt;
    private TreasureHunt actualTreasureHunt;

    public EditTreasureHuntCommand(TreasureHunt editedTreasureHunt) {
        this.editedTreasureHunt = new TreasureHunt();
        this.actualTreasureHunt = editedTreasureHunt;
        this.editedTreasureHunt.applyEditChanges(actualTreasureHunt);
        this.unEditedTreasureHunt =
                TreasureHuntAccessor.getById(editedTreasureHunt.getThuntid());
    }

    /**
     * Edits the command's TreasureHunt
     */
    @Override
    public void execute() {
        actualTreasureHunt.applyEditChanges(editedTreasureHunt);
        TreasureHuntAccessor.update(actualTreasureHunt);
    }

    /**
     * Undoes the editing of a TreasureHunt
     */
    @Override
    public void undo() {
        actualTreasureHunt.applyEditChanges(unEditedTreasureHunt);
        TreasureHuntAccessor.update(actualTreasureHunt);
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
        return "Trip " + this.actualTreasureHunt.getTitle() + " editing";
    }
}
