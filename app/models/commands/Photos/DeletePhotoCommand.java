package models.commands.Photos;

import accessors.AlbumAccessor;
import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import accessors.VisitAccessor;
import controllers.DestinationController;
import factories.UserFactory;
import models.Album;
import models.Destination;
import models.UserPhoto;
import models.Visit;
import models.commands.Destinations.UnlinkPhotoDestinationCommand;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Command to delete a user's UserPhoto */
public class DeletePhotoCommand extends UndoableCommand {
    private UserPhoto userPhoto;
    private UserPhoto savedUserPhoto;
    private HashSet<Destination> refToDestinations = new HashSet<>();
    private HashSet<Album> refToPrimaryPhotoDestinations = new HashSet<>();
    /**
     * Constructor to create an DeleteUserPhotoCommand. Takes the UserPhoto to delete
     * as the parameter.
     * @param userPhoto the UserPhoto to delete
     */
    public DeletePhotoCommand(UserPhoto userPhoto) {

        this.userPhoto = userPhoto;

        for (Destination destination : userPhoto.getDestinations()) {
            refToDestinations.add(destination);
        }
        for (Album album : userPhoto.getPrimaryPhotoDestinations()) {
            refToPrimaryPhotoDestinations.add(album);
        }

    }

    /**
     * Deletes the UserPhoto and unlinks photos to destinations
     */
    public void execute() {

        for (Destination destination : refToDestinations) {

            userPhoto.removeDestination(destination);
            UserPhotoAccessor.update(userPhoto);
            if ((destination.getAlbums().get(0).getPrimaryPhoto() != null) &&
                    (userPhoto.getMediaId() == destination.getAlbums()
                            .get(0).getPrimaryPhoto().getMediaId())) {
                destination.getAlbums().get(0).setPrimaryPhoto(null);
                AlbumAccessor.update(destination.getAlbums().get(0));
            }
        }

        UserFactory factory = new UserFactory();
        factory.deletePhoto(userPhoto.getMediaId());


    }

    /**
     * Undoes the deletion of the UserPhoto and relinks the photo to destinations
     */
    public void undo() {
        UserPhoto userPhoto = new UserPhoto(this.userPhoto);

        for(Destination destination: refToDestinations){
            userPhoto.addDestination(destination);
        }
//        System.out.println("Userphoto is " + userPhoto.toString());

        UserPhotoAccessor.insert(userPhoto);
        savedUserPhoto = UserPhotoAccessor.getUserPhotoById(userPhoto.getMediaId());

        for (Album album : refToPrimaryPhotoDestinations) {
            album.setPrimaryPhoto(userPhoto);
            AlbumAccessor.update(album);
        }

    }

    /**
     * Redos the deletion of the UserPhoto
     */
    public void redo() {
        for (Destination destination : refToDestinations) {

            savedUserPhoto.removeDestination(destination);
            UserPhotoAccessor.update(savedUserPhoto);
            if ((destination.getAlbums().get(0).getPrimaryPhoto() != null) &&
                    (savedUserPhoto.getMediaId() ==
                            destination.getAlbums().get(0).getPrimaryPhoto().getMediaId())) {
                destination.getAlbums().get(0).setPrimaryPhoto(null);
                AlbumAccessor.update(destination.getAlbums().get(0));
            }
        }

        UserFactory factory = new UserFactory();
        factory.deletePhoto(savedUserPhoto.getMediaId());
    }

    public String toString() {
        return this.userPhoto.getUrl() + "Deleting";
    }
}