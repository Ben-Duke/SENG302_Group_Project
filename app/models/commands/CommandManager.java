package models.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ebean.Finder;
import models.BaseModel;
import models.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private UndoableCommand undoCommand;
    private UndoableCommand redoCommand;

    public static Finder<Integer, CommandManager> find = new Finder<>(CommandManager.class);


    @OneToOne
    @JoinColumn(name = "user")
    public User user;

    public CommandManager() {

    }

    public void executeCommand(Command command) {
        command.execute();
        if (command instanceof UndoableCommand) {
            undoCommand = (UndoableCommand) command;
        }
    }

    public void undo() {
        undoCommand.undo();
        redoCommand = undoCommand;
    }

    public void redo() {
        redoCommand.redo();
        undoCommand = redoCommand;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
