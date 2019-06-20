package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import models.Album;
import models.Media;
import models.commands.General.UndoableCommand;

import java.util.List;

public class MoveMediaToAlbumCommand extends UndoableCommand {

    private Album album;
    private List<Media> medias;

    public MoveMediaToAlbumCommand(Album album, List<Media> medias) {
        this.album = album;
        this.medias = medias;
    }

    /**
     *
     */
    public void execute() {


    }

    /**
     *
     */
    public void undo() {


    }

    /**
     *
     */
    public void redo() {
        execute();
    }
}
