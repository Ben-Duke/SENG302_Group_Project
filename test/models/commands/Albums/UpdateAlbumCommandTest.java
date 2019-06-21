package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.UserAccessor;
import models.Album;
import models.User;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

public class UpdateAlbumCommandTest extends BaseTestWithApplicationAndDatabase {


    @Test
    public void executeTest() {

        User user = new User("email12345");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        UpdateAlbumCommand command = new UpdateAlbumCommand(album, "newTitle");
        command.execute();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (album.getTitle().equals("newTitle"));
    }

    @Test
    public void undoTest() {

        User user = new User("email12345");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        UpdateAlbumCommand command = new UpdateAlbumCommand(album, "newTitle");
        command.execute();
        command.undo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (album.getTitle().equals("testTitle"));
    }

    @Test
    public void redoTest() {

        User user = new User("email12345");
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        UpdateAlbumCommand command = new UpdateAlbumCommand(album, "newTitle");
        command.execute();
        command.undo();
        command.redo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (album.getTitle().equals("newTitle"));
    }

}
