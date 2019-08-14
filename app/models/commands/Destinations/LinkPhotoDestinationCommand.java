package models.commands.Destinations;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import models.Destination;
import models.UserPhoto;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

public class LinkPhotoDestinationCommand extends UndoableCommand {

    private UserPhoto photo;
    private Destination destination;

    /**
     * A user photo and a destination is needed to link the two together
     * @param photo The photo being linked
     * @param destination The destination being linked
     */
    public LinkPhotoDestinationCommand(UserPhoto photo, Destination destination) {
        super(CommandPage.DESTINATION);
        this.photo = photo;
        this.destination = destination;
    }

    /**
     * Link the destination to the photo
     */
    public void execute() {
        destination.getPrimaryAlbum().addMedia(photo);
        AlbumAccessor.update(destination.getPrimaryAlbum());
        //photo.addDestination(destination);
        //UserPhotoAccessor.update(photo);
    }

    /**
     * Unlink the destination from the photo.
     * Set primary photo to null if the primary photo
     * was unlinked.
     */
    public void undo() {
        destination.getPrimaryAlbum().removeMedia(photo);
        AlbumAccessor.update(destination.getPrimaryAlbum());
        //photo.removeDestination(destination);
        //UserPhotoAccessor.update(photo);
        if ((destination.getPrimaryAlbum().getPrimaryPhoto() != null) &&
                (photo.getMediaId() == destination.getPrimaryAlbum()
                        .getPrimaryPhoto().getMediaId())) {
            destination.getPrimaryAlbum().setPrimaryPhoto(null);
            AlbumAccessor.update(destination.getPrimaryAlbum());
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
        return "Photo" + this.photo.getUrl() + " unlinked to" + this.destination.getDestName();
    }
}
