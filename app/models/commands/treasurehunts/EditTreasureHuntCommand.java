package models.commands.treasurehunts;

import models.TreasureHunt;
import models.commands.UndoableCommand;

public class EditTreasureHuntCommand extends UndoableCommand {
    private TreasureHunt treasureHunt;

    EditTreasureHuntCommand(TreasureHunt treasureHunt) {
        this.treasureHunt = treasureHunt;
    }

    @Override
    public void execute() {

    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {
        execute();
    }
}
