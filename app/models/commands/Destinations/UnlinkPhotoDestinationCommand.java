package models.commands.Destinations;

import models.Destination;
import models.UserPhoto;
import models.commands.UndoableCommand;

public class UnlinkPhotoDestinationCommand extends UndoableCommand {

    private UserPhoto photo;
    private Destination destination;

    public UnlinkPhotoDestinationCommand(UserPhoto photo, Destination destination) {
        this.photo = photo;
        this.destination = destination;
    }

    public void execute() {
        photo.removeDestination(destination);
        photo.update();
        if ((destination.getPrimaryPhoto() != null) &&
                (photo.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
            destination.setPrimaryPhoto(null);
            destination.update();
        }
    }

    public void undo() {

    }

    public void redo() {
        execute();
    }
}