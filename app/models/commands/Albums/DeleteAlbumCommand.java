package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.commands.General.UndoableCommand;

import java.util.ArrayList;
import java.util.List;


public class DeleteAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> albumsMedia;


    public DeleteAlbumCommand(Album album) {
        this.album = album;
        albumsMedia = new ArrayList<>(album.getMedia());
    }

    public void execute() {

        album.removeAllMedia();
        AlbumAccessor.update(album);
        AlbumAccessor.delete(album);

        for (Media media : albumsMedia) {
            if (media.getAlbums().size() == 0) {
                MediaAccessor.delete(media);
            }
        }

    }

    public void undo() {

    }

    public void redo() {
        execute();
    }


}
