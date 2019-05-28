package models.commands.treasurehunts;

import accessors.TreasureHuntAccessor;
import accessors.UserAccessor;
import models.TreasureHunt;
import models.commands.general.UndoableCommand;

/** Command to delete a treasure hunt */
public class DeleteTreasureHuntCommand extends UndoableCommand {
    private TreasureHunt treasureHunt;

    public DeleteTreasureHuntCommand(TreasureHunt treasureHunt) {
        this.treasureHunt = treasureHunt;
    }

    /**
     * Executes the deletion of the treasure hunt
     */
    @Override
    public void execute() {
        TreasureHuntAccessor.delete(treasureHunt);
    }

    /**
     * Undoes the deletion of the treasure hunt
     */
    @Override
    public void undo() {
        treasureHunt = new TreasureHunt(treasureHunt);
        TreasureHuntAccessor.insert(treasureHunt);
    }

    /**
     * Redoes the deletion of the previously undone treasure hunt
     */
    @Override
    public void redo() {
        execute();
    }
}
