package models.commands.Albums;

import accessors.AlbumAccessor;
import models.Album;
import models.Media;
import models.User;
import models.commands.General.UndoableCommand;

public class CreateAlbumCommand extends UndoableCommand {

    private Album album;
    private String title;
    private User user;
    private Media media;


    public CreateAlbumCommand(String title, User user, Media media) {
        this.title = title;
        this.user = user;
        this.media = media;
    }

    public void execute() {

        if (media == null) {
            album = new Album(user, title);
        } else {
            album = new Album(media, user, title);
        }

        AlbumAccessor.insert(album);
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
