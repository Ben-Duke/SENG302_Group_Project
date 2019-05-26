package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
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
        UserPhotoAccessor.update(photo);
        if ((destination.getPrimaryPhoto() != null) &&
                (photo.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
            destination.setPrimaryPhoto(null);
            DestinationAccessor.update(destination);
        }
    }

    public void undo() {
        photo.addDestination(destination);
        UserPhotoAccessor.update(photo);
    }

    public void redo() {
        execute();
    }
}