package models;

import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

public class AlbumTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void checkMediaAddsToAlbum() {

        UserPhoto photo = new UserPhoto("/test",false,false,null);
        photo.save();

        Album album = new Album();
        album.save();

        album.addMedia(photo);
        album.update();
//
        assert(album.getMedia().size() == 1);

    }


}
