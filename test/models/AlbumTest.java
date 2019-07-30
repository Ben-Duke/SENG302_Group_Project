package models;

import accessors.AlbumAccessor;
import accessors.MediaAccessor;
import accessors.UserAccessor;
import models.commands.Albums.AddMediaToAlbumCommand;
import models.commands.Albums.CreateAlbumCommand;
import models.commands.Albums.RemoveMediaFromAlbumCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

import java.util.ArrayList;
import java.util.List;

public class AlbumTest extends BaseTestWithApplicationAndDatabase {



    @Test
    public void checkMediaAddsToAlbum() {

        UserPhoto photo = new UserPhoto("/test",false,false,null);
        photo.save();


        Media media = MediaAccessor.getMediaById(photo.getMediaId());
        System.out.println(media);


//        Album album = new Album(photo, null, "title");
//        album.save();

//        System.out.println(album.getMedia());

//        Album newAlbum = AlbumAccessor.getAlbumById(album.getAlbumId());
////
//
//        System.out.println(newAlbum.getMedia().get(0));
//
//        assert(album.getMedia().size() == 1);

    }

    @Test
    public void ViewPublicAlbum() {
        User user = new User();
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle", false);
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", true, false, user);
        MediaAccessor.insert(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);

        List<Media> medias = new ArrayList<>();
        medias.add(media1);
        medias.add(media2);
        medias.add(media3);

        AddMediaToAlbumCommand command = new AddMediaToAlbumCommand(album, medias);
        command.execute();

        List<Media> mediaList = album.viewAllMedia(true);
        Assert.assertEquals(1, mediaList.size());
        Assert.assertTrue(mediaList.get(0).isMediaPublic);
    }

    @Test
    public void ViewMixedAlbum() {
        User user = new User();
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle", false);
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", true, false, user);
        MediaAccessor.insert(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);

        List<Media> medias = new ArrayList<>();
        medias.add(media1);
        medias.add(media2);
        medias.add(media3);

        AddMediaToAlbumCommand command = new AddMediaToAlbumCommand(album, medias);
        command.execute();

        List<Media> mediaList = album.viewAllMedia(false);
        Assert.assertEquals(3, mediaList.size());
        Assert.assertFalse(mediaList.get(0).isMediaPublic);
    }

    @Test
    public void AlbumVisibilityChange() {
        User user = new User();
        UserAccessor.insert(user);

        Album album = new Album(user, "testTitle", false);
        AlbumAccessor.insert(album);

        Media media1 = new UserPhoto("/test", false, false, user);
        MediaAccessor.insert(media1);
        Media media2 = new UserPhoto("/test2", true, false, user);
        MediaAccessor.insert(media2);
        Media media3 = new UserPhoto("/test3", false, false, user);
        MediaAccessor.insert(media3);

        List<Media> medias = new ArrayList<>();
        medias.add(media1);
        medias.add(media2);
        medias.add(media3);

        AddMediaToAlbumCommand command = new AddMediaToAlbumCommand(album, medias);
        command.execute();

        Assert.assertTrue(album.isPublic());

        medias.remove(media1);
        medias.remove(media3);

        RemoveMediaFromAlbumCommand removeCommand = new RemoveMediaFromAlbumCommand(album, medias);
        removeCommand.execute();

        Assert.assertFalse(album.isPublic());
    }


}
