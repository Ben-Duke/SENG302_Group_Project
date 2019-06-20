package models.commands.Albums;

import accessors.AlbumAccessor;
import models.Album;
import models.Media;
import models.commands.General.UndoableCommand;

import java.util.List;

public class AddMediaToAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> medias;

    public AddMediaToAlbumCommand(Album album, List<Media> medias) {
        this.album = album;
        this.medias = medias;
    }

    public void execute() {
        for (Media media : medias) {
            album.addMedia(media);
        }
        AlbumAccessor.update(album);
    }

    public void undo() {
        for (Media media : medias) {
            album.removeMedia(media);
            AlbumAccessor.update(album);
        }
    }

    public void redo() {
        execute();
    }


}
