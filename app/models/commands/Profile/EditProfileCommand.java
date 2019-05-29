package models.commands.Profile;

import accessors.UserAccessor;
import models.User;
import models.commands.general.UndoableCommand;

/** Command to edit a user profile */
public class EditProfileCommand extends HomePageCommand {
    private User uneditedUser;
    private User editedUser;
    private User actualUser;

    /**
     * Constructor to create an EditProfileCommand. Takes an edited user
     * as the parameter which is the user profile to update the current user to.
     * The user with the same user id as the editedUser will be updated
     * to the edited user.
     * @param editedUser the edited user
     */
    public EditProfileCommand(User editedUser) {
        this.editedUser = new User();
        actualUser = editedUser;
        this.editedUser.applyEditChanges(actualUser);
        this.uneditedUser =
                UserAccessor.getById(editedUser.getUserid());
    }

    /**
     * Updates the user's details
     */
    public void execute() {
        actualUser.applyEditChanges(editedUser);
        UserAccessor.update(actualUser);
    }

    /**
     * Undoes the update of the user's details
     */
    public void undo() {
        actualUser.applyEditChanges(uneditedUser);
        UserAccessor.update(actualUser);
    }

    /**
     * Redos the update of the user's details
     */
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return this.actualUser.getfName() + this.actualUser.lName + " editing";
    }
}



























