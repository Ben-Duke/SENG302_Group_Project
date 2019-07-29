package models.commands.Albums;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import accessors.UserAccessor;
import accessors.UserPhotoAccessor;
import models.Album;
import models.Media;
import models.User;
import models.UserPhoto;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddMediaToAlbumCommandTest extends BaseTestWithApplicationAndDatabase {


    @Test
    public void testExecute() {

        User user = new User();
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);

        List<Media> medias = new ArrayList<>();
        medias.add(media1);
        medias.add(media2);
        medias.add(media3);

        int beforeSize = album.getMedia().size();

        AddMediaToAlbumCommand command = new AddMediaToAlbumCommand(album, medias);
        command.execute();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize + 3);
    }

    @Test
    public void testUndo() {

        User user = new User();
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);

        List<Media> medias = new ArrayList<>();
        medias.add(media1);
        medias.add(media2);
        medias.add(media3);

        int beforeSize = album.getMedia().size();

        AddMediaToAlbumCommand command = new AddMediaToAlbumCommand(album, medias);
        command.execute();
        command.undo();

        album = AlbumAccessor.getAlbumById(album.getAlbumId());

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize);
    }

    @Test
    public void testRedo() {

        User user = new User();
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle");
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", false, false, user);
        MediaAccessor.insert(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);

        List<Media> medias = new ArrayList<>();
        medias.add(UserPhotoAccessor.getUserPhotoByUrl("/test"));
        medias.add(UserPhotoAccessor.getUserPhotoByUrl("/test2"));
        medias.add(UserPhotoAccessor.getUserPhotoByUrl("/test3"));

        int beforeSize = album.getMedia().size();

        AddMediaToAlbumCommand command = new AddMediaToAlbumCommand(album, medias);
        command.execute();
        command.undo();
        command.redo();

        album = AlbumAccessor.getAlbumByTitle("testTitle");

        int afterSize = album.getMedia().size();

        assert (afterSize == beforeSize + 3);
    }



}
