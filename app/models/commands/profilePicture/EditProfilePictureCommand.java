package models.commands.profilePicture;

import accessors.UserAccessor;
import factories.UserFactory;
import models.UserPhoto;
import models.commands.UndoableCommand;

/**
 * Command to edit a profile picture
 */
public class EditProfilePictureCommand extends UndoableCommand {
    private UserPhoto oldPhoto;
    private UserPhoto editedPhoto;
    private int userId;

    public EditProfilePictureCommand(int userId, UserPhoto editedPhoto) {
        this.editedPhoto = editedPhoto;
        this.userId = userId;
        this.oldPhoto = UserAccessor.getUserProfilePictureByUserId(userId);
    }

    /**
     * Undoes the update of the profile picture
     */
    @Override
    public void undo() {
        UserFactory.replaceProfilePicture(userId, oldPhoto);
    }

    /**
     * Redoes the update of the profile picture
     */
    @Override
    public void redo() {
        execute();
    }

    /**
     * Edits the profile picture
     */
    @Override
    public void execute() {
        UserFactory.replaceProfilePicture(userId, editedPhoto);
    }
}
