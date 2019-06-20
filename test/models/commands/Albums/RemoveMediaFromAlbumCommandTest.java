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

import java.util.ArrayList;
import java.util.List;

public class RemoveMediaFromAlbumCommandTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void executeTest() {

        User user = new User("email123");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum");
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2");
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

        List<Media> mediaToRemove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToRemove.add(media1);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToRemove.add(media3);

        int beforeSize = album.getMedia().size();

        RemoveMediaFromAlbumCommand command = new RemoveMediaFromAlbumCommand(album, mediaToRemove);
        command.execute();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize - 2);

        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        media2 = MediaAccessor.getMediaById(media2.getMediaId());

        assert (media1 == null);
        assert (media2 != null);
        assert (media2.getAlbums().size() == 1);

    }

    @Test
    public void undoTest() {

        User user = new User("email123");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum");
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2");
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

        List<Media> mediaToRemove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToRemove.add(media1);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToRemove.add(media3);

        int beforeSize = album.getMedia().size();

        RemoveMediaFromAlbumCommand command = new RemoveMediaFromAlbumCommand(album, mediaToRemove);
        command.execute();
        command.undo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize);

    }

    @Test
    public void redoTest() {

        User user = new User("email123");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum");
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2");
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

        List<Media> mediaToRemove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToRemove.add(media1);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToRemove.add(media3);

        int beforeSize = album.getMedia().size();

        RemoveMediaFromAlbumCommand command = new RemoveMediaFromAlbumCommand(album, mediaToRemove);
        command.execute();
        command.undo();
        command.redo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize - 2);

    }

    @Test
    public void redoThenUndoTest() {

        User user = new User("email123");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum");
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2");
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

        List<Media> mediaToRemove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToRemove.add(media1);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToRemove.add(media3);

        int beforeSize = album.getMedia().size();

        RemoveMediaFromAlbumCommand command = new RemoveMediaFromAlbumCommand(album, mediaToRemove);
        command.execute();
        command.undo();
        command.redo();
        command.undo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize);

    }


}
