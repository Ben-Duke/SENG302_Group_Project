package accessors;

import models.Album;

public class AlbumAccessor {

    public static Album getAlbumById(int id) {
        return Album.find.byId(id);
    }

    public static void insert(Album album) { album.save(); }

}
