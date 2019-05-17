package models.commands;

import javax.persistence.*;

/** A command (action) which cannot be undone */
@Entity
public abstract class UndoableCommand extends Command {
    @OneToOne
    @JoinColumn
    private CommandManager commandManager;

    public abstract void undo();
    public abstract void redo();
}
