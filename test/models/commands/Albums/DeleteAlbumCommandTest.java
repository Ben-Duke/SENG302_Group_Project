package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import accessors.UserAccessor;
import models.Album;
import models.Media;
import models.User;
import models.UserPhoto;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

public class DeleteAlbumCommandTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void executeTest() {
        User user = UserAccessor.getById(1);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);

        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize - 1);
    }

    @Test
    public void executeTestWithMultipleMedia() {

        User user = UserAccessor.getById(1);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        album.addMedia(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        album.addMedia(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);
        album.addMedia(media3);
        album2.addMedia(media3);

        AlbumAccessor.update(album);
        AlbumAccessor.update(album2);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());


        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize - 1);

        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        media2 = MediaAccessor.getMediaById(media2.getMediaId());
        media3 = MediaAccessor.getMediaById(media3.getMediaId());

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (album == null);

        assert (media1 != null);
        assert (media2 != null);
        assert (media3 != null);

    }


    @Test
    public void undoTest() {
        User user = UserAccessor.getById(1);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);

        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();
        command.undo();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize);
    }

    @Test
    public void undoTestWithMultipleMedia() {

        User user = UserAccessor.getById(1);

        user.save();

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        album.addMedia(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        album.addMedia(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);
        album.addMedia(media3);
        album2.addMedia(media3);

        AlbumAccessor.update(album);
        AlbumAccessor.update(album2);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        user = UserAccessor.getById(user.getUserid());


        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();
        command.undo();

        user = UserAccessor.getById(user.getUserid());
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize);

        user = UserAccessor.getById(user.getUserid());
        album = user.getAlbums().get(2);

        assert (album != null);
        System.out.println(album.getMedia().size());
        assert (album.getMedia().size() == 3);

    }

    @Test
    public void redoTest() {
        User user = UserAccessor.getById(1);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);

        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();
        command.undo();
        command.redo();

        user = UserAccessor.getById(1);
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize - 1);
    }

    @Test
    public void redoTestWithMultipleMedia() {

        User user = UserAccessor.getById(1);
        user.save();

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        album.addMedia(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        album.addMedia(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);
        album.addMedia(media3);
        album2.addMedia(media3);

        AlbumAccessor.update(album);
        AlbumAccessor.update(album2);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        user = UserAccessor.getById(user.getUserid());


        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();
        command.undo();
        command.redo();

        user = UserAccessor.getById(user.getUserid());
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize - 1);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        assert (album == null);

    }

    @Test
    public void redoThenUndoTestWithMultipleMedia() {

        User user = UserAccessor.getById(1);
        user.save();

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        album.addMedia(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        album.addMedia(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);
        album.addMedia(media3);
        album2.addMedia(media3);

        AlbumAccessor.update(album);
        AlbumAccessor.update(album2);

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        user = UserAccessor.getById(user.getUserid());


        int beforeSize = user.getAlbums().size();

        DeleteAlbumCommand command = new DeleteAlbumCommand(album);
        command.execute();
        command.undo();
        command.redo();
        command.undo();

        user = UserAccessor.getById(user.getUserid());
        int afterSize = user.getAlbums().size();

        assert (afterSize == beforeSize);

        user = UserAccessor.getById(user.getUserid());
        album = user.getAlbums().get(2);

        assert (album != null);
        assert (album.getMedia().size() == 3);

    }




}
