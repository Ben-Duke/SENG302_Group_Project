package models.commands.treasurehunts;

import accessors.TreasureHuntAccessor;
import accessors.UserAccessor;
import models.TreasureHunt;
import models.commands.UndoableCommand;

public class DeleteTreasureHuntCommand extends UndoableCommand {
    private TreasureHunt treasureHunt;

    public DeleteTreasureHuntCommand(TreasureHunt treasureHunt) {
        this.treasureHunt = treasureHunt;
    }

    @Override
    public void execute() {
        TreasureHuntAccessor.delete(treasureHunt);
    }

    @Override
    public void undo() {
        treasureHunt = new TreasureHunt(treasureHunt);
        TreasureHuntAccessor.insert(treasureHunt);
    }

    @Override
    public void redo() {
        execute();
    }
}
