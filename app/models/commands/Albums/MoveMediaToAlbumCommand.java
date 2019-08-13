package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;

public class MoveMediaToAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> medias;

    public MoveMediaToAlbumCommand(Album album, List<Media> medias) {
        super(CommandPage.ALBUM);
        this.album = album;
        this.medias = medias;
    }

    /**
     * Removes each media from all the albums
     * it currently belongs to, then adds it
     * to the given album.
     */
    public void execute() {

        for (Media media : medias) {

            for (Album oldAlbum : media.getAlbums()) {
                oldAlbum.removeMedia(media);
                AlbumAccessor.update(oldAlbum);
            }

            album.addMedia(media);
        }

        AlbumAccessor.update(album);
    }

    /**
     * Adds each media back to the albums
     * it originally belonged to, the removes
     * it from the given album
     */
    public void undo() {

        for (Media media : medias) {

            for (Album oldAlbum : media.getAlbums()) {

                oldAlbum = AlbumAccessor.getAlbumById(oldAlbum.getAlbumId());

                oldAlbum.addMedia(media);
                AlbumAccessor.update(oldAlbum);
            }

            album.removeMedia(media);
        }
        AlbumAccessor.update(album);
    }

    /**
     * Refreshes each media so their album information is
     * update to date. Call execute to move each media to
     * the given album again.
     */
    public void redo() {
        List<Media> refreshedMedia = new ArrayList<>();

        for (Media media : medias) {
            media = MediaAccessor.getMediaById(media.getMediaId());
            refreshedMedia.add(media);
        }

        medias = refreshedMedia;

        execute();
    }
}
