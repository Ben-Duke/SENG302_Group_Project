package models.commands.Destinations;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.UserPhoto;
import models.commands.General.UndoableCommand;

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
        destination.getAlbums().get(0).addMedia(photo);
        AlbumAccessor.update(destination.getAlbums().get(0));
        //photo.addDestination(destination);
        //UserPhotoAccessor.update(photo);
    }

    /**
     * Unlink the destination from the photo.
     * Set primary photo to null if the primary photo
     * was unlinked.
     */
    public void undo() {
        destination.getAlbums().get(0).removeMedia(photo);
        AlbumAccessor.update(destination.getAlbums().get(0));
        //photo.removeDestination(destination);
        //UserPhotoAccessor.update(photo);
        if ((destination.getAlbums().get(0).getPrimaryPhoto() != null) &&
                (photo.getMediaId() == destination.getAlbums().get(0)
                        .getPrimaryPhoto().getMediaId())) {
            destination.getAlbums().get(0).setPrimaryPhoto(null);
            AlbumAccessor.update(destination.getAlbums().get(0));
        }
    }

    /**
     * Relink the photo to the destination.
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
