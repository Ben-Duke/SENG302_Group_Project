package models.commands.Albums;

import accessors.AlbumAccessor;
import models.Album;
import models.commands.General.UndoableCommand;

public class UpdateAlbumCommand extends UndoableCommand {

    private Album album;
    private String newTitle;
    private String oldTitle;

    public UpdateAlbumCommand(Album album, String title) {
        this.album = album;
        this.newTitle = title;
        this.oldTitle = album.getTitle();
    }

    public void execute() {
        album.setTitle(newTitle);
        AlbumAccessor.update(album);
    }

    public void undo() {
        album.setTitle(oldTitle);
        AlbumAccessor.update(album);
    }

    public void redo() {
        execute();
    }
}
