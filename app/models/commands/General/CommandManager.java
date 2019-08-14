package models.commands.General;

import accessors.UserAccessor;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
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

    /**
     * Filters the user's stack to only hold one type of command at on time
     * Will be done on the undo and redo stack each time an action is completed
     *
     * @param stack The user's stack of undoable commands
     */
    private void filterStack(Deque<UndoableCommand> stack) {
        for (UndoableCommand cmd : stack) {
            if (!allowedPage.equals(cmd.getCommandPage())) {
                stack.remove(cmd);
            }
        }
    }

    /**
     * Executes the command that has been chosen form the stack.
     * If command can be undone will be pushed onto the undo stack.
     *
     * @param command The undoable command being executed
     */
    public void executeCommand(Command command) {
        command.execute();
        if (command instanceof UndoableCommand) {
            undoStack.push((UndoableCommand) command);
        }
    }

    /**
     * Will execute an undo command  if the stack is not empty
     * and then push this to the redo stack if successful
     *
     * @return The undo command as a string message ot be shown to the user
     */
    public String undo() {
        if (!undoStack.isEmpty()) {
            UndoableCommand undoCommand = undoStack.pop();
            try {
                undoCommand.undo();
                redoStack.push(undoCommand);
                System.out.println(redoStack.isEmpty());
                return undoCommand.toString();
            } catch(Exception exception){
                user.setUndoRedoError(true);
                UserAccessor.update(user);
            }
        }
        return "";
    }

    /**
     * Will execute a redo command  if the stack is not empty
     * and then push this to the uno stack if successful
     *
     * @return The undo command as a string message ot be shown to the user
     */
    public String redo() {
        if (!redoStack.isEmpty()) {
            UndoableCommand redoCommand = redoStack.pop();
            try {
                redoCommand.redo();
                undoStack.push(redoCommand);
                return redoCommand.toString();
            } catch(Exception exception){
                user.setUndoRedoError(true);
                UserAccessor.update(user);
            }
        }
        return "";
    }

    /**
     * Checks if Undo Stack is empty and returns boolean
     *
     * @return True if undo stack is empty,
     * False otherwise
     */
    public boolean isUndoStackEmpty() {
        return undoStack.isEmpty();
    }

    /**
     * Checks if redo Stack is empty and returns boolean
     *
     * @return True if redo stack is empty,
     * False otherwise
     */
    public boolean isRedoStackEmpty() {
        return redoStack.isEmpty();
    }

    /**
     * sets the undo and redo stacks to be empty
     */
    public void resetUndoRedoStack() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }
}
