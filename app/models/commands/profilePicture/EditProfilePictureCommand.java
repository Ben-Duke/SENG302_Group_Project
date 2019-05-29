package models.commands.profilePicture;

import accessors.UserAccessor;
import factories.UserFactory;
import models.UserPhoto;
import models.commands.Profile.HomePageCommand;

/**
 * Command to edit a profile picture
 */
public class EditProfilePictureCommand extends HomePageCommand {
    private UserPhoto oldPhoto;
    private UserPhoto editedPhoto;
    private int userId;

    public EditProfilePictureCommand(int userId, UserPhoto editedPhoto) {
        this.oldPhoto = UserAccessor.getUserProfilePictureByUserId(userId);
        editedPhoto.save();
        this.editedPhoto = editedPhoto;
        this.userId = userId;
    }

    /**
     * Undoes the update of the profile picture
     */
    @Override
    public void undo() {
        if (oldPhoto != null) {
            oldPhoto = UserPhoto.find.byId(oldPhoto.photoId);
        }
        UserFactory.replaceProfilePictureLogic(userId, oldPhoto);
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
        if (editedPhoto != null) {
            editedPhoto = UserPhoto.find.byId(editedPhoto.photoId);
        }
        UserFactory.replaceProfilePictureLogic(userId, editedPhoto);
    }
}
