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

        Album album = new Album(user, "testAlbum");
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

        assert (media1 == null);
        assert (media2 == null);
        assert (media3 != null);

    }




}
