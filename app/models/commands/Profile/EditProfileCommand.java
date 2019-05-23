package models.commands.Profile;

import accessors.UserAccessor;
import models.User;
import models.commands.UndoableCommand;

/** Command to edit a user profile */
public class EditProfileCommand extends UndoableCommand {
    private User uneditedUser;
    private User editedUser;

    /**
     * Constructor to create an EditProfileCommand. Takes an edited user
     * as the parameter which is the user profile to update the current user to.
     * The user with the same user id as the editedUser will be updated
     * to the edited user.
     * @param editedUser the edited user
     */
    public EditProfileCommand(User editedUser) {
        this.editedUser = editedUser;
        this.uneditedUser =
                UserAccessor.getUserById(editedUser.getUserid());
    }

    /**
     * Updates the user's details
     */
    public void execute() {
        UserAccessor.update(editedUser);
    }

    /**
     * Undoes the update of the user's details
     */
    public void undo() {
        editedUser.applyEditChanges(uneditedUser);
        UserAccessor.update(editedUser);
    }

    public void redo() {
        execute();
    }
}



























