package models.commands.general;

/** A command (action) which cannot be undone */
public abstract class UndoableCommand extends Command {

    public abstract void undo();
    public abstract void redo();
}

