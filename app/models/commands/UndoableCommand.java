package models.commands;

import javax.persistence.MappedSuperclass;

/** A command (action) which cannot be undone */
@MappedSuperclass
public abstract class UndoableCommand extends Command {
    public abstract void undo();
    public abstract void redo();
}
