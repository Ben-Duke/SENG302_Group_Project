package models.commands.General;

import accessors.UserAccessor;
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

    private CommandPage allowedPage;

    private final Logger logger = UtilityFunctions.getLogger();

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAllowedPage(CommandPage allowedPage) {
        this.allowedPage = allowedPage;
        filterStack(undoStack);
        filterStack(redoStack);
    }

    private void filterStack(Deque<UndoableCommand> stack) {
        for (UndoableCommand cmd : stack) {
            if (!allowedPage.equals(cmd.getCommandPage())) {
                stack.remove(cmd);
            }
        }
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
            try {
                undoCommand.undo();
                redoStack.push(undoCommand);
                return undoCommand.toString();
            } catch(Exception exception){
                user.setUndoRedoError(true);
                UserAccessor.update(user);
            }
        }
        return "";
    }

    public String redo() {

        if (!redoStack.isEmpty()) {
            UndoableCommand redoCommand = redoStack.pop();
            try {
                redoCommand.redo();
                undoStack.push(redoCommand);
                return redoCommand.toString();
            } catch (Exception exception){
                user.setUndoRedoError(true);
                UserAccessor.update(user);
            }
        }
        return "";
    }

    public boolean isUndoStackEmpty() {
        return undoStack.isEmpty();
    }

    public boolean isRedoStackEmpty() {
        return redoStack.isEmpty();
    }

    public void resetUndoRedoStack() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }
}
