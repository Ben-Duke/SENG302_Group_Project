package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
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
        UserPhotoAccessor.update(photo);
    }

    public void undo() {
        photo.removeDestination(destination);
        UserPhotoAccessor.update(photo);
        if ((destination.getPrimaryPhoto() != null) &&
                (photo.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
            destination.setPrimaryPhoto(null);
            DestinationAccessor.update(destination);
        }
    }

    public void redo() {
        execute();
    }
}
