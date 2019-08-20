package models.commands.Profile;

import accessors.AlbumAccessor;
import accessors.UserAccessor;
import models.Album;
import models.User;
import models.commands.Albums.UpdateAlbumCommand;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

/** Command to edit a user profile */
public class EditProfileCommand extends UndoableCommand {
    private User uneditedUser;
    private User editedUser;
    private User actualUser;
    private Album album;
    private String editedTitle;
    private String uneditedTitle;

    /**
     * Constructor to create an EditProfileCommand. Takes an edited user
     * as the parameter which is the user profile to update the current user to.
     * The user with the same user id as the editedUser will be updated
     * to the edited user.
     * @param editedUser the edited user
     */
    public EditProfileCommand(User editedUser) {
        super(CommandPage.HOME);
        this.editedUser = new User();
        actualUser = editedUser;
        this.editedUser.applyEditChanges(actualUser);
        this.uneditedUser =
                UserAccessor.getById(editedUser.getUserid());
        if(!editedUser.getFName().equals(uneditedUser.getFName()) || !editedUser.getLName().equals(uneditedUser.getLName())) {
            if (AlbumAccessor.getAlbumByTitle(uneditedUser.getFName() + " " + uneditedUser.getLName() + "'s "+"Profile Pictures") != null) {
                this.album = AlbumAccessor.getAlbumByTitle(uneditedUser.getFName() + " " + uneditedUser.getLName() + "'s " + "Profile Pictures");
                this.uneditedTitle = album.getTitle();
                this.editedTitle = (editedUser.getFName() + " " + editedUser.getLName() + "'s " + "Profile Pictures");
            }
        }
    }

    /**
     * Updates the user's details
     */
    public void execute() {
        actualUser.applyEditChanges(editedUser);
        UserAccessor.update(actualUser);
        if ((album != null) && (!editedTitle.equals(uneditedTitle))) {
            album.setTitle(editedTitle);
            AlbumAccessor.update(album);
        }
    }

    /**
     * Undoes the update of the user's details
     */
    public void undo() {
        actualUser.applyEditChanges(uneditedUser);
        UserAccessor.update(actualUser);
        if ((album != null) && (!editedTitle.equals(uneditedTitle))) {
            album.setTitle(uneditedTitle);
            AlbumAccessor.update(album);
        }


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
        return this.actualUser.getFName() + " " + this.actualUser.getLName() + " editing";
    }
}



























