package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.UserPhoto;
import models.UserVideo;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;


public class DeleteAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> albumsMedia;
    private List<Media> deletedMedia;


    public DeleteAlbumCommand(Album album) {
        this.album = album;
        albumsMedia = new ArrayList<>(album.getMedia());
        deletedMedia = new ArrayList<>();
    }

    /**
     * Removes all media from album, then deletes album.
     * Of the media removed, those belonging to no other
     * albums are deleted. Albums AC10 all media must be
     * in an album.
     */
    public void execute() {

        album.removeAllMedia();
        AlbumAccessor.update(album);
        AlbumAccessor.delete(album);

        for (Media media : albumsMedia) {
            if (media.getAlbums().size() == 0) {
                deletedMedia.add(media);
                MediaAccessor.delete(media);
            }
        }
    }

    /**
     * Recreates and saves the album to the database.
     * All media is re-added to the album. The media
     * that was deleted is resurrected first.
     */
    public void undo() {
        album = new Album(album.getUser(), album.getTitle());
        AlbumAccessor.insert(album);

        for (Media media : albumsMedia) {

            if (deletedMedia.contains(media)) {
                if (media instanceof UserPhoto) {
                    UserPhoto photo = (UserPhoto)media;
                    media = new UserPhoto(photo);
                } else if (media instanceof UserVideo) {
                    UserVideo video = (UserVideo)media;
                    media = new UserVideo(video);
                }
                MediaAccessor.insert(media);
                album.addMedia(media);

            } else {
                album.addMedia(media);
            }
            AlbumAccessor.update(album);
        }
    }

    /**
     * Resets the lists so the next undo will work
     * properly. Calls execute to delete album.
     */
    public void redo() {
        albumsMedia = new ArrayList<>(album.getMedia());
        deletedMedia = new ArrayList<>();
        execute();
    }


}
