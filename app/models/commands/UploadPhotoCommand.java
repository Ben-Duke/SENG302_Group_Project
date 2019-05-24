package models.commands;

import accessors.UserPhotoAccessor;
import controllers.ApplicationManager;
import models.User;
import models.UserPhoto;
import models.commands.UndoableCommand;
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

    public void undo() {
        System.out.println("Undo");
        File file = new File(userPhoto.getUrlWithPath());
        file.delete();
        UserPhotoAccessor.delete(userPhoto);
    }

    public void redo() {
        System.out.println("Redo");
        userPhoto = new UserPhoto(userPhoto.getUrl(), userPhoto.isPublic(), userPhoto.isProfile(), userPhoto.getUser(),
                userPhoto.getDestinations(), userPhoto.getPrimaryPhotoDestinations());
        execute();

    }
}
