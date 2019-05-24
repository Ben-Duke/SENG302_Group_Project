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
public class CommandManager extends BaseModel {
    /* Used Deque not Stack as a stack is synchronised (thread safe) but slower
     than a de-synchronized Deque - Sonarlint gave warning for stack
    */
    private UndoableCommand undoCommand;

    //@OneToOne(mappedBy = "commandManager")
    private UndoableCommand redoCommand;

    private final Logger logger = UtilityFunctions.getLogger();

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
        if (command instanceof UndoableCommand) {
            undoCommand = (UndoableCommand) command;
        }
    }

    public void undo() {
        // To prevent a crash if a redo is requested before an undoable action occurs
        if (undoCommand != null) {
            System.out.println("undo edit");
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
