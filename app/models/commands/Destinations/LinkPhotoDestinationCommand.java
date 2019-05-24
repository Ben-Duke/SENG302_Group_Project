package models.commands.Destinations;

import models.Destination;
import models.UserPhoto;
import models.commands.UndoableCommand;

public class LinkPhotoDestinationCommand extends UndoableCommand {

    private UserPhoto photo;
    private Destination destination;

    public LinkPhotoDestinationCommand(UserPhoto photo, Destination destination) {
        this.photo = photo;
        this.destination = destination;
    }

    public void execute() {
        photo.addDestination(destination);
        photo.update();
    }

    public void undo() {

    }

    public void redo() {
        execute();
    }
}
