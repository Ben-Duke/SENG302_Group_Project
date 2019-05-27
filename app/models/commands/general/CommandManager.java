package models.commands.general;

import models.BaseModel;
import models.User;
import org.slf4j.Logger;
import utilities.UtilityFunctions;

import java.util.ArrayDeque;
import java.util.Deque;

/** Invoker in the command pattern used for undo/redo
 *
 *  Holds undo/redo stacks.
 *  All commands pass through this class
 *  Singleton
 **/
public class CommandManager extends BaseModel {
    /* Deque is the accepted java implementation for a stack */
    private Deque<UndoableCommand> undoStack = new ArrayDeque<>();

    private Deque<UndoableCommand> redoStack = new ArrayDeque<>();

    private final Logger logger = UtilityFunctions.getLogger();

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public void executeCommand(Command command) {
        command.execute();
        if (command instanceof UndoableCommand) {
            undoStack.push((UndoableCommand) command);
        }
    }

    public String undo() {
        if (!undoStack.isEmpty()) {
            UndoableCommand undoCommand = undoStack.pop();
            undoCommand.undo();
            redoStack.push(undoCommand);
            return undoCommand.toString();
        }
        return null;
    }

    public String redo() {
        if (!redoStack.isEmpty()) {
            UndoableCommand redoCommand = redoStack.pop();
            redoCommand.redo();
            undoStack.push(redoCommand);
            return redoCommand.toString();
        }
        return null;
    }
}
