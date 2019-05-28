package models.commands.Photos;

import accessors.UserPhotoAccessor;
import accessors.VisitAccessor;
import controllers.DestinationController;
import models.Destination;
import models.UserPhoto;
import models.Visit;
import models.commands.general.UndoableCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Command to delete a user's UserPhoto */
public class DeletePhotoCommand extends UndoableCommand {
    private UserPhoto userPhoto;
    private UserPhoto savedUserPhoto;
    private HashSet<Destination> refToDestinations = new HashSet<>();
    /**
     * Constructor to create an DeleteUserPhotoCommand. Takes the UserPhoto to delete
     * as the parameter.
     * @param UserPhoto the UserPhoto to delete
     */
    public DeletePhotoCommand(UserPhoto UserPhoto) {
        this.userPhoto = UserPhoto;
        this.savedUserPhoto = UserPhoto;
        for (Destination destination : UserPhoto.getDestinations()) {
            refToDestinations.add(destination);
        }
    }

    /**
     * Deletes the UserPhoto and unlinks photos to destinations
     */
    public void execute() {
        DestinationController destinationController = new DestinationController();
        destinationController.unlinkPhotoFromDestinationAndDelete(null, savedUserPhoto.getPhotoId());
    }

    /**
     * Undoes the deletion of the UserPhoto and relinks the photo to destinations
     */
    public void undo() {
        UserPhoto userPhoto = new UserPhoto(this.userPhoto);
        userPhoto.save();
        savedUserPhoto = userPhoto;
    }

    /**
     * Redos the deletion of the UserPhoto
     */
    public void redo() {
        execute();
    }

    public String toString() {
        return this.userPhoto.getUrl() + "Deleting";
    }
}