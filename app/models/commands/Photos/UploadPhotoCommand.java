package models.commands.Photos;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
import controllers.ApplicationManager;
import models.Album;
import models.Media;
import models.User;
import models.UserPhoto;
import models.commands.Albums.AddMediaToAlbumCommand;
import models.commands.Albums.CreateAlbumCommand;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;
import org.slf4j.Logger;
import play.libs.Files;
import utilities.UtilityFunctions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Command for user photo uploads */

public class UploadPhotoCommand extends UndoableCommand {

    private User user;
    private UserPhoto userPhoto;
    private Files.TemporaryFile fileObject;
    private String albumName;
    private AddMediaToAlbumCommand addMediaToAlbumCommand;
    private CreateAlbumCommand createAlbumCommand;
    private final Logger logger = UtilityFunctions.getLogger();

    public UploadPhotoCommand(UserPhoto photo, Files.TemporaryFile fileObject, User user, String albumName) {
        super(CommandPage.HOME);
        this.user = user;
        this.userPhoto = photo;
        this.fileObject = fileObject;
        this.albumName = albumName;
    }

    /**
     * Execute an upload photo command
     */
    public void execute() {
        try {
            java.nio.file.Files.createDirectories(Paths.get(
                    Paths.get(".").toAbsolutePath().normalize().toString()
                    + ApplicationManager.getUserMediaPath() + userPhoto.getUser().getUserid() + "/"));
        } catch (IOException e) {
            logger.error("IOException on creating directory for photo", e);
        }
        String unusedAbsoluteFilePath = Paths.get(".").toAbsolutePath().normalize().toString()
                + ApplicationManager.getUserMediaPath() + userPhoto.getUser().getUserid() + "/" + userPhoto.getUrl();
        fileObject.copyTo(Paths.get(unusedAbsoluteFilePath), true);
        UserPhotoAccessor.insert(userPhoto);
        userPhoto = UserPhotoAccessor.getUserPhotoByUrl(userPhoto.getUrl());
        addUploadToAlbum(user, userPhoto, albumName);
    }

    public void addUploadToAlbum(User user, UserPhoto media, String albumName) {
        List<Album> albumList = user.getAlbums();
        int albumCount = 0;
        for (Album album : albumList) {
            if (albumName.equals(album.getTitle())) {
                albumCount = 1;
                List<Media> mediaList = new ArrayList<>();
                mediaList.add(media);
                addMediaToAlbumCommand = new AddMediaToAlbumCommand(album, mediaList);
                addMediaToAlbumCommand.execute();
            }
        }
        if (albumCount == 0) {
            createAlbumCommand = new CreateAlbumCommand(albumName, user, media);
            createAlbumCommand.execute();
            user = UserAccessor.getById(user.getUserid());
            addUploadToAlbum(user, media, albumName);
        }
    }

    /**
     * Undo a photo upload
     */
    public void undo() {
        addMediaToAlbumCommand.undo();
        if (AlbumAccessor.getAlbumByTitle(albumName).getMedia().size() == 0) {
            createAlbumCommand.undo();
        };
        File file = new File(userPhoto.getUrlWithPath());
        file.delete();
        UserPhotoAccessor.delete(userPhoto);
    }

    /**
     * Redo a photo upload
     */
    public void redo() {
        userPhoto = new UserPhoto(userPhoto.getUrl(), userPhoto.getIsPublic(), userPhoto.isProfile(), userPhoto.getUser(),
                userPhoto.getAlbums(), userPhoto.getPrimaryPhotoDestinations());
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
