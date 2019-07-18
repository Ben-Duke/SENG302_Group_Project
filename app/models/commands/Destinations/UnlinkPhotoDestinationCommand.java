package models.commands.Destinations;

import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.UserPhoto;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

public class UnlinkPhotoDestinationCommand extends UndoableCommand {

    private UserPhoto photo;
    private Destination destination;

    /**
     * The destination and the photo that are linked together
     * @param photo
     * @param destination
     */
    public UnlinkPhotoDestinationCommand(UserPhoto photo, Destination destination) {
        super(CommandPage.DESTINATION);
        this.photo = photo;
        this.destination = destination;
    }

    /**
     * Unlink the destination from the photo.
     * Set primary photo to null if the primary photo
     * was unlinked.
     */
    public void execute() {
        photo.removeDestination(destination);
        UserPhotoAccessor.update(photo);
        if ((destination.getPrimaryPhoto() != null) &&
                (photo.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
            destination.setPrimaryPhoto(null);
            DestinationAccessor.update(destination);
        }
    }

    /**
     * Relink the destination and photo together.
     */
    public void undo() {
        photo.addDestination(destination);
        UserPhotoAccessor.update(photo);
    }

    /**
     * Unlink again.
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Photo" + this.photo.getUrl() + " unlinked to" + this.destination.getDestName();
    }
}