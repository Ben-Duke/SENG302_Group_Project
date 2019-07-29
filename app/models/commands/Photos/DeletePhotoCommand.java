package models.commands.Photos;

import accessors.DestinationAccessor;
import accessors.AlbumAccessor;
import accessors.UserPhotoAccessor;
import accessors.VisitAccessor;
import controllers.DestinationController;
import factories.UserFactory;
import models.*;
import models.commands.General.UndoableCommand;

import java.util.HashSet;

/** Command to delete a user's UserPhoto */
public class DeletePhotoCommand extends UndoableCommand {
    private UserPhoto userPhoto;
    private UserPhoto savedUserPhoto;
    private HashSet<Album> refToAlbums = new HashSet<>();
    private HashSet<Album> refToPrimaryPhotoDestinations = new HashSet<>();
    /**
     * Constructor to create an DeleteUserPhotoCommand. Takes the UserPhoto to delete
     * as the parameter.
     * @param userPhoto the UserPhoto to delete
     */
    public DeletePhotoCommand(UserPhoto userPhoto) {
        super(CommandPage.HOME);

        this.userPhoto = userPhoto;

        refToAlbums.addAll(userPhoto.getAlbums());
        refToPrimaryPhotoDestinations.addAll(userPhoto.getPrimaryPhotoDestinations());
    }

    /**
     * Deletes the UserPhoto and unlinks photos to destinations
     */
    public void execute() {
        removeMediaFromAlbums(refToAlbums, userPhoto);
        UserFactory factory = new UserFactory();
        factory.deletePhoto(userPhoto.getMediaId());

    }

    /**
     * Undoes the deletion of the UserPhoto and relinks the photo to destinations
     */
    public void undo() {
        UserPhoto userPhoto = new UserPhoto(this.userPhoto);
        UserPhotoAccessor.insert(userPhoto);
        savedUserPhoto = UserPhotoAccessor.getUserPhotoById(userPhoto.getMediaId());

        for (Album album: refToAlbums){
            album.addMedia(userPhoto);
            AlbumAccessor.update(album);
        }

        for (Album album : refToPrimaryPhotoDestinations) {
            album.setPrimaryPhoto(userPhoto);
            AlbumAccessor.update(album);
        }

    }

    /**
     * Redos the deletion of the UserPhoto
     */
    public void redo() {
        removeMediaFromAlbums(refToAlbums, savedUserPhoto);
        UserFactory factory = new UserFactory();
        factory.deletePhoto(savedUserPhoto.getMediaId());
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return this.userPhoto.getUrl() + "Deleting";
    }

    private void removeMediaFromAlbums(HashSet<Album> albums, Media mediaToRemove) {
        for (Album album : albums) {

            album.removeMedia(mediaToRemove);
            AlbumAccessor.update(album);
            if ((album.getPrimaryPhoto() != null) &&
                    (mediaToRemove.getMediaId().equals(
                            album.getPrimaryPhoto().getMediaId()))) {
                album.setPrimaryPhoto(null);
                AlbumAccessor.update(album);
            }
        }
    }
}