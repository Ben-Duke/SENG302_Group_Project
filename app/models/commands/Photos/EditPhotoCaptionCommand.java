package models.commands.Photos;

import accessors.UserPhotoAccessor;
import models.UserPhoto;
import models.commands.Profile.HomePageCommand;

public class EditPhotoCaptionCommand extends HomePageCommand {
    private UserPhoto uneditedPhoto;
    private UserPhoto editedPhoto;
    private UserPhoto actualPhoto;

    public EditPhotoCaptionCommand(UserPhoto editedPhoto) {
        this.editedPhoto = new UserPhoto();
        this.actualPhoto = editedPhoto;
        this.editedPhoto.applyEditChanges(actualPhoto);
        this.uneditedPhoto =
                UserPhotoAccessor.getUserPhotoById(editedPhoto.getPhotoId());
    }

    /**
     * Edits the command's TreasureHunt
     */
    @Override
    public void execute() {
        actualPhoto.applyEditChanges(editedPhoto);
        UserPhotoAccessor.update(actualPhoto);
    }

    /**
     * Undoes the editing of a TreasureHunt
     */
    @Override
    public void undo() {
        actualPhoto.applyEditChanges(uneditedPhoto);
        UserPhotoAccessor.update(actualPhoto);
    }

    /**
     * Redoes the previously executed undo
     */
    @Override
    public void redo() {
        execute();
    }

    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "User Photo " + this.actualPhoto.getUrl() + " editing";
    }
}
