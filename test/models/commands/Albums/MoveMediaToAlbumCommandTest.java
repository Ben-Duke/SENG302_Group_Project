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

public class MoveMediaToAlbumCommandTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void executeTest() {

        User user = new User("email1234");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);
        Album album3 = new Album(user, "testAlbum3", false);
        AlbumAccessor.insert(album3);

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
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        List<Media> mediaToMove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToMove.add(media1);
        media2 = MediaAccessor.getMediaById(media2.getMediaId());
        mediaToMove.add(media2);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToMove.add(media3);

        int albumBeforeSize = album.getMedia().size();
        int album2BeforeSize = album2.getMedia().size();
        int album3BeforeSize = album3.getMedia().size();

        MoveMediaToAlbumCommand command = new MoveMediaToAlbumCommand(album3, mediaToMove);
        command.execute();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int album2AfterSize = album2.getMedia().size();
        int album3AfterSize = album3.getMedia().size();

        assert (album3AfterSize == album3BeforeSize + 3);
        assert (album2AfterSize == album2BeforeSize - 1);
        assert (albumAfterSize == albumBeforeSize - 3);

    }

    @Test
    public void undoTest() {

        User user = new User("email1234");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);
        Album album3 = new Album(user, "testAlbum3", false);
        AlbumAccessor.insert(album3);

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
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        List<Media> mediaToMove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToMove.add(media1);
        media2 = MediaAccessor.getMediaById(media2.getMediaId());
        mediaToMove.add(media2);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToMove.add(media3);

        int albumBeforeSize = album.getMedia().size();
        int album2BeforeSize = album2.getMedia().size();
        int album3BeforeSize = album3.getMedia().size();

        MoveMediaToAlbumCommand command = new MoveMediaToAlbumCommand(album3, mediaToMove);
        command.execute();
        command.undo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int album2AfterSize = album2.getMedia().size();
        int album3AfterSize = album3.getMedia().size();

        assert (album3AfterSize == album3BeforeSize);
        assert (album2AfterSize == album2BeforeSize);
        assert (albumAfterSize == albumBeforeSize);

    }

    @Test
    public void redoTest() {

        User user = new User("email1234");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);
        Album album3 = new Album(user, "testAlbum3", false);
        AlbumAccessor.insert(album3);

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
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        List<Media> mediaToMove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToMove.add(media1);
        media2 = MediaAccessor.getMediaById(media2.getMediaId());
        mediaToMove.add(media2);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToMove.add(media3);

        int albumBeforeSize = album.getMedia().size();
        int album2BeforeSize = album2.getMedia().size();
        int album3BeforeSize = album3.getMedia().size();

        MoveMediaToAlbumCommand command = new MoveMediaToAlbumCommand(album3, mediaToMove);
        command.execute();
        command.undo();
        command.redo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int album2AfterSize = album2.getMedia().size();
        int album3AfterSize = album3.getMedia().size();


        assert (album3AfterSize == album3BeforeSize + 3);
        assert (album2AfterSize == album2BeforeSize - 1);
        assert (albumAfterSize == albumBeforeSize - 3);

    }

    @Test
    public void redoThenUndoTest() {

        User user = new User("email1234");
        UserAccessor.insert(user);

        Album album = new Album(user, "testAlbum", false);
        AlbumAccessor.insert(album);
        Album album2 = new Album(user, "testAlbum2", false);
        AlbumAccessor.insert(album2);
        Album album3 = new Album(user, "testAlbum3", false);
        AlbumAccessor.insert(album3);

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
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        List<Media> mediaToMove = new ArrayList<>();
        media1 = MediaAccessor.getMediaById(media1.getMediaId());
        mediaToMove.add(media1);
        media2 = MediaAccessor.getMediaById(media2.getMediaId());
        mediaToMove.add(media2);
        media3 = MediaAccessor.getMediaById(media3.getMediaId());
        mediaToMove.add(media3);

        int albumBeforeSize = album.getMedia().size();
        int album2BeforeSize = album2.getMedia().size();
        int album3BeforeSize = album3.getMedia().size();

        MoveMediaToAlbumCommand command = new MoveMediaToAlbumCommand(album3, mediaToMove);
        command.execute();
        command.undo();
        command.redo();
        command.undo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());
        album2 = AlbumAccessor.getAlbumById(album2.getAlbumId());
        album3 = AlbumAccessor.getAlbumById(album3.getAlbumId());

        int albumAfterSize = album.getMedia().size();
        int album2AfterSize = album2.getMedia().size();
        int album3AfterSize = album3.getMedia().size();


        assert (album3AfterSize == album3BeforeSize);
        assert (album2AfterSize == album2BeforeSize);
        assert (albumAfterSize == albumBeforeSize);

    }



}
