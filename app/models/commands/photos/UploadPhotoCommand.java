package models.commands.photos;

import accessors.UserPhotoAccessor;
import controllers.ApplicationManager;
import models.UserPhoto;
import models.commands.general.UndoableCommand;
import play.libs.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class UploadPhotoCommand extends UndoableCommand {

    private UserPhoto userPhoto;
    private Files.TemporaryFile fileObject;

    public UploadPhotoCommand(UserPhoto photo, Files.TemporaryFile fileObject) {
        this.userPhoto = photo;
        this.fileObject = fileObject;
    }

    /**
     * Execute an upload photo command
     */
    public void execute() {
        try {
            java.nio.file.Files.createDirectories(Paths.get(
                    Paths.get(".").toAbsolutePath().normalize().toString()
                    + ApplicationManager.getUserPhotoPath() + userPhoto.getUser().getUserid() + "/"));
        } catch (IOException e) {

        }
        String unusedAbsoluteFilePath = Paths.get(".").toAbsolutePath().normalize().toString()
                + ApplicationManager.getUserPhotoPath() + userPhoto.getUser().getUserid() + "/" + userPhoto.getUrl();
        fileObject.copyTo(Paths.get(unusedAbsoluteFilePath), true);
        UserPhotoAccessor.insert(userPhoto);

    }

    /**
     * Undo a photo upload
     */
    public void undo() {
        File file = new File(userPhoto.getUrlWithPath());
        file.delete();
        UserPhotoAccessor.delete(userPhoto);
    }

    /**
     * Redo a photo upload
     */
    public void redo() {
        userPhoto = new UserPhoto(userPhoto.getUrl(), userPhoto.isPublic(), userPhoto.isProfile(), userPhoto.getUser(),
                userPhoto.getDestinations(), userPhoto.getPrimaryPhotoDestinations());
        execute();

    }
    /**
     * Returns result from the undo/redo command as a string
     * @return String result of command
     */
    public String toString() {
        return "Photo " + this.userPhoto.getUrl() + " uploading";
    }

}
