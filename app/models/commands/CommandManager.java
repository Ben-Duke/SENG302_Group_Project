package models.commands;

import accessors.CommandManagerAccessor;
import io.ebean.Finder;
import models.BaseModel;
import models.User;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;

import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/** Invoker in the command pattern used for undo/redo
 *
 *  Holds undo/redo stacks.
 *  All commands pass through this class
 *  Singleton
 **/
@Entity
public class CommandManager extends BaseModel {
    /* Used Deque not Stack as a stack is synchronised (thread safe) but slower
     than a de-synchronized Deque - Sonarlint gave warning for stack
    */
    @OneToOne(mappedBy = "command_manager")
    private UndoableCommand undoCommand;

    //@OneToOne(mappedBy = "commandManager")
    private UndoableCommand redoCommand;

    public static Finder<Integer, CommandManager> find = new Finder<>(CommandManager.class);

    private final Logger logger = UtilityFunctions.getLogger();

    @OneToOne
    @JoinColumn
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UndoableCommand getUndoCommand() {
        return undoCommand;
    }

    public void setUndoCommand(UndoableCommand undoCommand) {
        this.undoCommand = undoCommand;
    }

    public UndoableCommand getRedoCommand() {
        return redoCommand;
    }

    public void setRedoCommand(UndoableCommand redoCommand) {
        this.redoCommand = redoCommand;
    }

    @Override
    public String toString() {
        return "CommandManager{" +
                "undoCommand=" + undoCommand +
                ", redoCommand=" + redoCommand +
                '}';
    }

    public void executeCommand(Command command) {
        command.execute();
        logger.debug("destinaton deleted");
        if (command instanceof UndoableCommand) {
            undoCommand = (UndoableCommand) command;
            int id = this.getId();
            CommandManagerAccessor.update(this);
            CommandManager fromDb = CommandManager.find.byId(id);

            logger.debug(fromDb.toString());

            logger.debug("set undo to " + undoCommand);
        }
    }

    public void undo() {
        logger.debug("command manager undo called. Undo is " + undoCommand);
        // To prevent a crash if a redo is requested before an undoable action occurs
        if (undoCommand != null) {
            logger.debug("undoing");
            undoCommand.undo();
            redoCommand = undoCommand;
        }
    }

    public void redo() {
        // To prevent a crash if a redo is requested before an undoable action occurs
        if (redoCommand != null) {
            redoCommand.redo();
            undoCommand = redoCommand;
        }
    }
}
