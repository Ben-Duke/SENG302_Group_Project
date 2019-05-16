package models.commands;

import models.Destination;

import javax.persistence.Entity;

/** Command to edit a user's profile */
@Entity
public class DeleteDestinationCommand extends UndoableCommand {
    private Destination destination;

    public DeleteDestinationCommand(Destination destination) {
        this.destination = destination;
    }

    public void execute() {

    }

    public void undo() {

    }

    public void redo() {

    }
}
