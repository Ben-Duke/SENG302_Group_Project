package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.UserPhoto;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;

public class AddMediaToAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> medias;
    private List<Media> savedMediaList;

    public AddMediaToAlbumCommand(Album album, List<Media> medias) {
        super(CommandPage.HOME);
        this.album = album;
        this.medias = medias;
    }

    public void execute() {
        for (Media media : medias) {
            if(!album.getMedia().contains(media)) {
                album.addMedia(media);
            }
            MediaAccessor.update(media);
        }
        AlbumAccessor.update(album);
    }

    public void undo() {
        savedMediaList = new ArrayList<>();
        for (Media media : medias) {
            if(media instanceof UserPhoto) {
                UserPhoto userPhoto = (UserPhoto) media;
                userPhoto = new UserPhoto(userPhoto.getUrl(), userPhoto.getIsPublic(), userPhoto.isProfile(), userPhoto.getUser(),
                        userPhoto.getAlbums(), userPhoto.getPrimaryPhotoDestinations());
                savedMediaList.add(userPhoto);
            }
            album.removeMedia(media);
            AlbumAccessor.update(album);
        }
    }



    public void redo() {
//        for (Media media : savedMediaList) {
//            if(!album.getMedia().contains(media)) {
//                album.addMedia(media);
//            }
//        }
//        AlbumAccessor.update(album);
        execute();
    }


}
