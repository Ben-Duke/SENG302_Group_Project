package models.commands.Photos;

import accessors.DestinationAccessor;
import accessors.UserPhotoAccessor;
import accessors.VisitAccessor;
import controllers.DestinationController;
import factories.UserFactory;
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
    private HashSet<Destination> refToPrimaryPhotoDestinations = new HashSet<>();
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
        for (Destination destination : userPhoto.getPrimaryPhotoDestinations()) {
            refToPrimaryPhotoDestinations.add(destination);
        }

    }

    /**
     * Deletes the UserPhoto and unlinks photos to destinations
     */
    public void execute() {

        for (Destination destination : refToDestinations) {

            userPhoto.removeDestination(destination);
            UserPhotoAccessor.update(userPhoto);
            if ((destination.getPrimaryPhoto() != null) &&
                    (userPhoto.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
                destination.setPrimaryPhoto(null);
                DestinationAccessor.update(destination);
            }
        }

        UserFactory factory = new UserFactory();
        factory.deletePhoto(userPhoto.getPhotoId());


    }

    /**
     * Undoes the deletion of the UserPhoto and relinks the photo to destinations
     */
    public void undo() {
        UserPhoto userPhoto = new UserPhoto(this.userPhoto);
        for(Destination destination: refToDestinations){
            userPhoto.addDestination(destination);
        }
        System.out.println("Userphoto is " + userPhoto.toString());

        UserPhotoAccessor.insert(userPhoto);
        savedUserPhoto = UserPhotoAccessor.getUserPhotoById(userPhoto.getPhotoId());

        for (Destination destination : refToPrimaryPhotoDestinations) {
            destination.setPrimaryPhoto(userPhoto);
            DestinationAccessor.update(destination);
        }


    }

    /**
     * Redos the deletion of the UserPhoto
     */
    public void redo() {
        for (Destination destination : refToDestinations) {

            savedUserPhoto.removeDestination(destination);
            UserPhotoAccessor.update(savedUserPhoto);
            if ((destination.getPrimaryPhoto() != null) &&
                    (savedUserPhoto.getPhotoId() == destination.getPrimaryPhoto().getPhotoId())) {
                destination.setPrimaryPhoto(null);
                DestinationAccessor.update(destination);
            }
        }

        UserFactory factory = new UserFactory();
        factory.deletePhoto(savedUserPhoto.getPhotoId());
    }

    public String toString() {
        return this.userPhoto.getUrl() + "Deleting";
    }
}