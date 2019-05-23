package models.commands.treasurehunts;

import accessors.TreasureHuntAccessor;
import models.TreasureHunt;
import models.commands.UndoableCommand;

/** Command to delete a destination */
public class EditTreasureHuntCommand extends UndoableCommand {
    private TreasureHunt unEditedTreasureHunt;
    private TreasureHunt editedTreasureHunt;
    private TreasureHunt actualTreasureHunt;

    public EditTreasureHuntCommand(TreasureHunt newTreasureHunt) {
        this.editedTreasureHunt = new TreasureHunt(newTreasureHunt);
        this.actualTreasureHunt = newTreasureHunt;
        this.unEditedTreasureHunt =
                TreasureHuntAccessor.getById(newTreasureHunt.getThuntid());
    }

    /**
     * Edits the command's TreasureHunt
     */
    @Override
    public void execute() {
        actualTreasureHunt = new TreasureHunt(editedTreasureHunt);
        TreasureHuntAccessor.update(actualTreasureHunt);
    }

    /**
     * Undoes the editing of a TreasureHunt
     */
    @Override
    public void undo() {
        actualTreasureHunt = new TreasureHunt(unEditedTreasureHunt);
        TreasureHuntAccessor.update(actualTreasureHunt);
    }

    /**
     * Redoes the previously executed undo
     */
    @Override
    public void redo() {
        execute();
    }
}
