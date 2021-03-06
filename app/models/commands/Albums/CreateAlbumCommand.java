package models.commands.Albums;

import accessors.AlbumAccessor;
import models.Album;
import models.AlbumOwner;
import models.Media;
import models.User;
import models.commands.General.CommandPage;
import models.commands.General.UndoableCommand;

public class CreateAlbumCommand extends UndoableCommand {

    private Album album;
    private String title;
    private AlbumOwner owner;
    private Media media;


    public CreateAlbumCommand(String title, AlbumOwner owner, Media media) {
        super(CommandPage.HOME);
        this.title = title;
        this.owner = owner;
        this.media = media;
    }

    public void execute() {
        if (media == null) {
            album = new Album(owner, title, false);
        } else {
            album = new Album(media, owner, title, false);
        }
        AlbumAccessor.insert(album);
        album = AlbumAccessor.getAlbumById(album.getAlbumId());
    }

    public void undo() {
        album.removeAllMedia();
        AlbumAccessor.update(album);
        AlbumAccessor.delete(album);
    }

    public void redo() {
        execute();
    }

    public Album getAlbum() { return album; }
}
