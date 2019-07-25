package models.commands.Destinations;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.UserPhoto;
import models.commands.General.UndoableCommand;

public class UnlinkPhotoDestinationCommand extends DestinationPageCommand  {

    private UserPhoto photo;
    private Destination destination;

    /**
     * The destination and the photo that are linked together
     * @param photo
     * @param destination
     */
    public UnlinkPhotoDestinationCommand(UserPhoto photo, Destination destination) {
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
        if ((destination.getAlbums().get(0).getPrimaryPhoto() != null) &&
                (photo.getMediaId() ==
                        destination.getAlbums().get(0).getPrimaryPhoto().getMediaId())) {
            destination.getAlbums().get(0).setPrimaryPhoto(null);
            AlbumAccessor.update(destination.getAlbums().get(0));
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
        return "Photo" + this.photo.getUrl() + " unlinked to" + this.destination.destName;
    }
}