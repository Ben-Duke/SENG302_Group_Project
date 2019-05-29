package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.UserPhoto;
import models.commands.general.UndoableCommand;

public class LinkPhotoDestinationCommand extends DestinationPageCommand  {

    private UserPhoto photo;
    private Destination destination;

    /**
     * A user photo and a destination is needed to link the two together
     * @param photo
     * @param destination
     */
    public LinkPhotoDestinationCommand(UserPhoto photo, Destination destination) {
        this.photo = photo;
        this.destination = destination;
    }

    /**
     * Link the destination to the photo
     */
    public void execute() {
        photo.addDestination(destination);
        UserPhotoAccessor.update(photo);
    }

    /**
     * Unlink the destination from the photo.
     * Set primary photo to null if the primary photo
     * was unlinked.
     */
    public void undo() {
        photo.removeDestination(destination);
        UserPhotoAccessor.update(photo);
        if ((destination.getPrimaryPhoto() != null) &&
                (photo.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
            destination.setPrimaryPhoto(null);
            DestinationAccessor.update(destination);
        }
    }

    /**
     * Relink the photo to the destination.
     */
    public void redo() {
        execute();
    }
}
