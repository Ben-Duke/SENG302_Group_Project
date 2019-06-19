package models.commands.Albums;

import accessors.UserAccessor;
import models.Media;
import models.User;
import models.UserPhoto;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

public class CreateAlbumCommandTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void executeTest() {

        String title = "testAlbum";
        User user = UserAccessor.getById(1);
        Media media = new UserPhoto("/test", false, false, user);
        media.save();

        int beforeSize = user.getAlbums().size();

        CreateAlbumCommand command = new CreateAlbumCommand(title, user, media);
        command.execute();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize + 1);
    }

    @Test
    public void undoTest() {
        String title = "testAlbum";
        User user = UserAccessor.getById(1);
        Media media = new UserPhoto("/test", false, false, user);
        media.save();

        int beforeSize = user.getAlbums().size();

        CreateAlbumCommand command = new CreateAlbumCommand(title, user, media);
        command.execute();

        command.undo();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize);

    }

    @Test
    public void redoTest() {
        String title = "testAlbum";
        User user = UserAccessor.getById(1);
        Media media = new UserPhoto("/test", false, false, user);
        media.save();

        int beforeSize = user.getAlbums().size();

        CreateAlbumCommand command = new CreateAlbumCommand(title, user, media);
        command.execute();
        command.undo();
        command.redo();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize + 1);

    }

    @Test
    public void executeWithoutMediaTest() {

        String title = "testAlbum";
        User user = UserAccessor.getById(1);

        int beforeSize = user.getAlbums().size();

        CreateAlbumCommand command = new CreateAlbumCommand(title, user, null);
        command.execute();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize + 1);
    }

    @Test
    public void undoWithoutMediaTest() {
        String title = "testAlbum";
        User user = UserAccessor.getById(1);

        int beforeSize = user.getAlbums().size();

        CreateAlbumCommand command = new CreateAlbumCommand(title, user, null);
        command.execute();

        command.undo();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize);

    }

    @Test
    public void redoWithoutMediaTest() {
        String title = "testAlbum";
        User user = UserAccessor.getById(1);

        int beforeSize = user.getAlbums().size();

        CreateAlbumCommand command = new CreateAlbumCommand(title, user, null);
        command.execute();
        command.undo();
        command.redo();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize + 1);

    }




}
