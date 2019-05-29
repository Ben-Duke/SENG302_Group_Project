package models.commands.Treasurehunts;

import accessors.TreasureHuntAccessor;
import models.TreasureHunt;
import models.commands.General.UndoableCommand;

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

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
       return  "Treasure hunt " + this.treasureHunt.getTitle() + " deleting";
    }

}
