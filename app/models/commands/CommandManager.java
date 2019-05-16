package models.commands;

import models.BaseModel;

import javax.persistence.Entity;

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

    public void executeCommand(Command command) {
        command.execute();
        if (command instanceof UndoableCommand) {
            UndoableCommand undoableCommand = (UndoableCommand) command;
            undoCommand = undoableCommand;
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
}
