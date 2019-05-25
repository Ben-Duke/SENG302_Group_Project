package models.commands.Photos;

import accessors.UserPhotoAccessor;
import accessors.VisitAccessor;
import models.Destination;
import models.UserPhoto;
import models.Visit;
import models.commands.UndoableCommand;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/** Command to delete a user's UserPhoto */
public class DeleteUserPhotoCommand extends UndoableCommand {
    private UserPhoto UserPhoto;
    private UserPhoto savedUserPhoto;
    private HashSet<Destination> refToDestinations = new HashSet<>();
    /**
     * Constructor to create an DeleteUserPhotoCommand. Takes the UserPhoto to delete
     * as the parameter.
     * @param UserPhoto the UserPhoto to delete
     */
    public DeleteUserPhotoCommand(UserPhoto UserPhoto) {
        this.UserPhoto = UserPhoto;
        this.savedUserPhoto = UserPhoto;
        for (Destination destination : UserPhoto.getDestinations()) {
            refToDestinations.add(new Destination(destination));
        }
    }

    /**
     * Deletes the UserPhoto
     */
    public void execute() {
        for (Destination visit : savedUserPhoto.getVisits()) {
            VisitAccessor.delete(visit);
        }
        UserPhotoAccessor.delete(savedUserPhoto);
    }

    /**
     * Undoes the deletion of the UserPhoto
     */
    public void undo() {
        UserPhoto UserPhoto = new UserPhoto(this.UserPhoto, deletedVisits);
        UserPhoto.save();
        savedUserPhoto = UserPhoto;
        for (Visit visit : deletedVisits) {
            visit.setUserPhoto(UserPhoto);
            VisitAccessor.insert(visit);
        }
    }

    /**
     * Redos the deletion of the UserPhoto
     */
    public void redo() {
        execute();
    }
}