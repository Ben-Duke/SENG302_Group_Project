package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.UserPhoto;
import models.UserVideo;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;


public class DeleteAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> albumsMedia;
    private List<Media> deletedMedia;
    private Album defaultAlbum;



    public DeleteAlbumCommand(Album album) {
        super(CommandPage.ALBUM);
        this.album = album;
        albumsMedia = new ArrayList<>(album.getMedia());
        deletedMedia = new ArrayList<>();
        defaultAlbum = new Album(album.getOwner(), "Default", true);
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
        List<Album> albumList = AlbumAccessor.getAlbumsByOwner(album.getOwner());
        for (Album thisAlbum: albumList) {
            if (thisAlbum.getTitle().equals("Default")) {
                defaultAlbum = thisAlbum;
            }
        }
        for (Media media : albumsMedia) {
            defaultAlbum.addMedia(media);
            MediaAccessor.update(media);
        }
        AlbumAccessor.update(defaultAlbum);

    }

    /**
     * Recreates and saves the album to the database.
     * All media is re-added to the album. The media
     * that was deleted is resurrected first.
     */
    public void undo() {
        album = new Album(album.getUser(), album.getTitle(), false);
        AlbumAccessor.insert(album);

        for (Media media : albumsMedia) {

            if (deletedMedia.contains(media)) {
                if (media instanceof UserPhoto) {
                    UserPhoto photo = (UserPhoto)media;
                    media = new UserPhoto(photo);
                    defaultAlbum.removeMedia(media);
                    UserVideo video = (UserVideo)media;
                    media = new UserVideo(video);
                }
                MediaAccessor.insert(media);
                album.addMedia(media);
            } else {
                album.addMedia(media);
            }
            defaultAlbum.removeMedia(media);
            AlbumAccessor.update(defaultAlbum);
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
