package models;

import accessors.AlbumAccessor;
import org.junit.Test;
import testhelpers.BaseTestWithApplicationAndDatabase;

public class AlbumTest extends BaseTestWithApplicationAndDatabase {

    @Test
    public void checkMediaAddsToAlbum() {

        UserPhoto photo = new UserPhoto("/test",false,false,null);
        photo.save();

        Album album = new Album(photo, null);
        album.save();

//        System.out.println(album.getMedia());

        Album newAlbum = AlbumAccessor.getAlbumById(album.getAlbumId());
//

        System.out.println(newAlbum.getMedia().get(0));

        assert(album.getMedia().size() == 1);

    }


}
