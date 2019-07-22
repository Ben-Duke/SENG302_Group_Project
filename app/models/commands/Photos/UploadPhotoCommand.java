package models.commands.Photos;

import accessors.TagAccessor;
import accessors.UserPhotoAccessor;
import controllers.ApplicationManager;
import models.Tag;
import models.UserPhoto;
import models.commands.Profile.HomePageCommand;
import play.libs.Files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class UploadPhotoCommand extends HomePageCommand {

    private UserPhoto userPhoto;
    private Files.TemporaryFile fileObject;
    private Set<Tag> tags;

    public UploadPhotoCommand(UserPhoto photo, Files.TemporaryFile fileObject) {
        this.userPhoto = photo;
        this.fileObject = fileObject;
        this.tags = new HashSet<>();
    }

    public UploadPhotoCommand(UserPhoto photo, Files.TemporaryFile fileObject, Set<Tag> tags) {
        this.userPhoto = photo;
        this.fileObject = fileObject;
        this.tags = tags;
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
        for (Tag tag: tags) {
            userPhoto.addTag(tag);
            TagAccessor.update(tag);
            UserPhotoAccessor.update(userPhoto);

        }
        UserPhotoAccessor.update(userPhoto);

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
