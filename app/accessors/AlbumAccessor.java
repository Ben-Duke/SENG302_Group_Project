package accessors;

import models.Album;

public class AlbumAccessor {

    public static Album getAlbumById(int id) {
        return Album.find.byId(id);
    }

}
